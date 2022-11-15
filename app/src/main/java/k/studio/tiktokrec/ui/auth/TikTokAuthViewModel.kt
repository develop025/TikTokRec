package k.studio.tiktokrec.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import k.studio.tiktokrec.R
import k.studio.tiktokrec.data.error.ErrorsResources
import k.studio.tiktokrec.data.source.AppRepository
import k.studio.tiktokrec.data.vo.Status
import k.studio.tiktokrec.ui.main.ScreenState
import k.studio.tiktokrec.utils.Event
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TikTokAuthViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _navigationDestination = MutableLiveData<Event<NavigationDestination>>()
    val navigationDestination: LiveData<Event<NavigationDestination>> = _navigationDestination

    private val _errorMessageRes = MutableLiveData<Event<Int>>()
    val errorMessageRes: LiveData<Event<Int>> = _errorMessageRes

    private val _screenState = MutableLiveData<Event<ScreenState>>()
    val screenState: LiveData<Event<ScreenState>> = _screenState

    /**
     * Deprecated
     * TikTok block get request
     */
    fun checkUsername(username: String) {
        if (username.isEmpty())
            showDialog(R.string.empty_username)
        else {
            viewModelScope.launch {
                appRepository.isTikTokUserExist(username).collectLatest { result ->
                    setScreenState(result.status)

                    when (result.status) {
                        Status.SUCCESS -> {
                            appRepository.saveTikTokUsername(username)
                            checkAuthAndNavigate()
                        }
                        Status.ERROR -> showDialog(ErrorsResources.getResource(result.getError()))
                        Status.LOADING -> {}
                    }
                }
            }
        }
    }

    fun onUsernameValidated(username: String) {
        viewModelScope.launch {
            appRepository.saveTikTokUsername(username)
            checkAuthAndNavigate()
        }
    }

    private fun setScreenState(status: Status) {
        val state = if (status == Status.LOADING)
            ScreenState.NOT_TOUCHABLE
        else
            ScreenState.TOUCHABLE

        _screenState.postValue(Event(state))
    }

    private fun showDialog(messageRes: Int) {
        _errorMessageRes.postValue(Event(messageRes))
    }

    private fun checkAuthAndNavigate() {
        auth = Firebase.auth
        if (auth.currentUser == null)
            _navigationDestination.postValue(Event(NavigationDestination.AUTHORIZATION))
        else if (auth.currentUser?.isEmailVerified != true)
            _navigationDestination.postValue(Event(NavigationDestination.EMAIL_VERIFICATION))
        else
            _navigationDestination.postValue(Event(NavigationDestination.GET_STARS))
    }
}

enum class NavigationDestination {
    AUTHORIZATION,
    EMAIL_VERIFICATION,
    GET_STARS
}
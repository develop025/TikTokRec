package k.studio.tiktokrec.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import k.studio.tiktokrec.data.source.AppRepository
import k.studio.tiktokrec.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {

    private val _navigationDestination = MutableLiveData<Event<NavigationDestination>>()
    val navigationDestination: LiveData<Event<NavigationDestination>> = _navigationDestination
    private lateinit var auth: FirebaseAuth

    init {
        viewModelScope.launch {
            checkAuthAndPermissions()
        }
    }

    private fun checkAuthAndPermissions() {
        auth = Firebase.auth
        viewModelScope.launch(Dispatchers.IO) {
            if (appRepository.getUserState().tikTokUsername.isNullOrEmpty())
                _navigationDestination.postValue(Event(NavigationDestination.TIK_TOK_AUTH))
            else if (auth.currentUser == null)
                _navigationDestination.postValue(Event(NavigationDestination.EMAIL_REGISTER))
            else if (auth.currentUser?.isEmailVerified != true)
                _navigationDestination.postValue(Event(NavigationDestination.EMAIL_VERIFICATION))
            else
                _navigationDestination.postValue(Event(NavigationDestination.GET_STARS))
        }
    }
}

enum class NavigationDestination {
    EMAIL_REGISTER,
    EMAIL_VERIFICATION,
    TIK_TOK_AUTH,
    GET_STARS
}
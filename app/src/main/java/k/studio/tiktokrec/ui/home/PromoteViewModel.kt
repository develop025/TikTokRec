package k.studio.tiktokrec.ui.home

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import k.studio.tiktokrec.data.error.ErrorsResources
import k.studio.tiktokrec.data.source.AppRepository
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.utils.Event
import k.studio.tiktokrec.utils.logD
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoteViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _navigationDestination = MutableLiveData<Event<NavigationDestination>>()
    val navigationDestination: LiveData<Event<NavigationDestination>> = _navigationDestination

    private val _tikTokUsername = MutableLiveData<String>()

    private val _errorMessageRes = MutableLiveData<Event<Int>>()
    val errorMessageRes: LiveData<Event<Int>> = _errorMessageRes

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private var subscribeOrdersJob: CompletableJob? = null

    val flowOrdersHearts = Pager(
        PagingConfig(pageSize = 20)
    ) {
        appRepository.getOrdersHeartsByCurrentUserPagingSource()
    }.flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.getTikTokUsername()?.let { tikTokUsername ->
                _tikTokUsername.postValue(tikTokUsername)
            } ?: run {
                _navigationDestination.postValue(Event(NavigationDestination.TIK_TOK_AUTH))
            }
        }
    }

    fun subscribeRemoteOrders() {
        "PromoteViewModel.subscribeRemoteOrders".logD()
        viewModelScope.launch(Dispatchers.IO) {
            _tikTokUsername.asFlow().collectLatest { tikTokUsername ->
                "PromoteViewModel.subscribeRemoteOrders $tikTokUsername".logD()
                viewModelScope.launch(Dispatchers.IO) {
                    appRepository.getOrderHeartsByUsername(tikTokUsername, { orderHeart ->
                        "PromoteViewModel.subscribeRemoteOrders getOrderHeartsByUsername onAdded $orderHeart".logD()
                        viewModelScope.launch(Dispatchers.IO) {
                            appRepository.save(orderHeart)
                        }
                    }, { orderHeart ->
                        "PromoteViewModel.subscribeRemoteOrders getOrderHeartsByUsername onRemoved $orderHeart".logD()
                        viewModelScope.launch(Dispatchers.IO) {
                            appRepository.delete(orderHeart)
                        }
                    }, { error ->
                        showErrorDialog(error.message)
                    })
                }
            }
        }
    }

    fun unsubscribeRemoteOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeOrdersJob?.cancelAndJoin()
            appRepository.unsubscribeOrdersHearts()
        }
    }

    fun windUpLikes(videoLink: String, heartsNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _tikTokUsername.asFlow().collectLatest { username ->
                val stars = appRepository.getRequiredAmountOfStars(heartsNumber)
                if (appRepository.enoughStarNumber(heartsNumber)) {
                    val orderHeart = OrderHeart(
                        username = username,
                        videoLink = videoLink,
                        heartsNumber = heartsNumber.toLong()
                    )
                    saveLocalOrder(orderHeart)
                    createOrderTask(orderHeart, stars)
                }
            }
        }
    }

    private fun saveLocalOrder(orderHeart: OrderHeart) {
        appRepository.save(orderHeart)
    }

    private fun createOrderTask(
        orderHeart: OrderHeart,
        stars: Int
    ) {
        appRepository.createWindUpTask(
            orderHeart,
            stars.toLong(),
            {},
            { errorCode ->
                showErrorDialog(ErrorsResources.getResource(errorCode))
            }
        )
    }

    private fun showErrorDialog(messageRes: Int) {
        _errorMessageRes.postValue(Event(messageRes))
    }

    private fun showErrorDialog(message: String) {
        _errorMessage.postValue(Event(message))
    }

    enum class NavigationDestination {
        TIK_TOK_AUTH
    }
}
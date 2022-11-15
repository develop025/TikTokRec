package k.studio.tiktokrec.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import k.studio.tiktokrec.bot.AppBot
import k.studio.tiktokrec.data.source.AppRepository
import k.studio.tiktokrec.utils.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetStarsViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    fun clearUniqueActions() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.clearUniqueActions()
        }
    }

    fun clearOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.clearOrders()
        }
    }

    fun startAction() {
        "GetStarsViewModel.startAction".logD("TikTokMediator")
        AppBot.getScreenInteract().startAction()
    }

    fun stopAction() {
        "GetStarsViewModel.stopAction".logD()
        AppBot.getScreenInteractOrNull()?.stopAction()
    }

    override fun onCleared() {
        super.onCleared()
        "GetStarsViewModel.onCleared".logD()
    }

    fun resetOrderAt() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.resetOrderAt()
        }
    }
}
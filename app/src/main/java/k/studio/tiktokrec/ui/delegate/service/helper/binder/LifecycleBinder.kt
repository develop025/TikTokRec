package k.studio.tiktokrec.ui.delegate.service.helper.binder

import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.StateFlow

/**
 * Delegate interface to bind/unbind service
 */
interface LifecycleBinder<S : Service, B : ServiceBinder<S>> {
    val boundLiveData: LiveData<Boolean>
    val bindingServiceFlow: StateFlow<S?>

    fun unbindService(context: Context)
    fun bindService(context: Context, intent: Intent)
    fun bindService(context: Context, clazz: Class<S>)
}

package k.studio.tiktokrec.ui.delegate.service.helper.binder

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Delegate to bind/unbind service
 * Require call [bindService]
 */
class DelegateLifecycleBinder<S : Service, B : ServiceBinder<S>> : LifecycleBinder<S, B> {

    //TODO: deprecated
    private val _boundLiveData = MutableLiveData(false)
    override val boundLiveData: LiveData<Boolean> = _boundLiveData

    private val _bindingServiceFlow: MutableStateFlow<S?> = MutableStateFlow(null)
    override var bindingServiceFlow: StateFlow<S?> = _bindingServiceFlow

    private val connection = object : ServiceConnection {
        @Suppress("UNCHECKED_CAST")
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: ServiceBinder<S> = service as ServiceBinder<S>
            val bindingService = binder.getService()
            _bindingServiceFlow.tryEmit(bindingService)
            _boundLiveData.postValue(true)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            _bindingServiceFlow.tryEmit(null)
            _boundLiveData.postValue(false)
        }
    }

    /**
     * Bind service if created
     * @param clazz - Service::class.java
     */
    override fun bindService(context: Context, clazz: Class<S>) {
        val intent = Intent(context, clazz)
        context.bindService(intent, connection, 0)
    }

    /**
     * Bind service if created
     */
    override fun bindService(context: Context, intent: Intent) {
        context.bindService(intent, connection, 0)
    }

    override fun unbindService(context: Context) {
        _boundLiveData.postValue(false)
        context.unbindService(connection)
    }
}

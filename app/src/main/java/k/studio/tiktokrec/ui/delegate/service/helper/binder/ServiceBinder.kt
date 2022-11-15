package k.studio.tiktokrec.ui.delegate.service.helper.binder

import android.app.Service
import android.os.Binder

/**
 * Binder wrapper
 */
abstract class ServiceBinder<S : Service> : Binder() {
    abstract fun getService(): S
}
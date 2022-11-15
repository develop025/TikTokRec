package k.studio.tiktokrec.ui.delegate.service.helper

import android.app.Service
import android.content.Intent
import android.os.IBinder
import k.studio.tiktokrec.ui.delegate.service.helper.binder.ServiceBinder
import k.studio.tiktokrec.ui.delegate.service.helper.notification.DelegateServiceNotification
import k.studio.tiktokrec.ui.delegate.service.helper.notification.ServiceNotification
import k.studio.tiktokrec.ui.delegate.service.helper.stayawake.DelegateStayAwake
import k.studio.tiktokrec.ui.delegate.service.helper.stayawake.StayAwake
import k.studio.tiktokrec.utils.logD

/**
 * Layer for manage start/stop Accessibility service
 */
abstract class BotHelper : Service(),
    ServiceNotification by DelegateServiceNotification(),
    StayAwake by DelegateStayAwake() {

    abstract val binder: ServiceBinder<BotHelper>

    override fun onCreate() {
        super.onCreate()
        "BotHelper.onCreate".logD()
        createServiceNotification(this)
        initView(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        "BotHelper onBind".logD()
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.getBooleanExtra(START_ID, false)?.let { start ->
            "BotHelper.onStartCommand start:$start".logD()
//            if (start)
//                initView(this)
//            else
//                stopSelf()

            if (!start){
                "BotHelper.onStartCommand stopSelf".logD()
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        removeView()
        super.onDestroy()
    }

    abstract inner class BotManagerBinder : ServiceBinder<BotHelper>() {
        override fun getService(): BotHelper = this@BotHelper
    }

    companion object {
        const val START_ID = "START_ID"
    }
}
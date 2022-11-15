package k.studio.tiktokrec.ui.delegate.service.ui.lifecyclebothelper

import android.content.Context
import android.content.Intent
import android.os.Build
import k.studio.tiktokrec.ui.delegate.service.helper.BotHelper
import k.studio.tiktokrec.utils.logD

/**
 * Manages any BotManagerService
 * For using binding call [setStartServiceListener]
 */
class DelegateLifecycleBotHelper<S : BotHelper> : LifecycleBotHelper<S> {

    override var parentInteract: LifecycleBotHelper.IParentInteract? = null

    override fun startService(context: Context, clazz: Class<S>) {
        "DelegateLifecycleBotHelper startService".logD()
        Intent(context, clazz).let { intentService ->
            intentService.putExtra(BotHelper.START_ID, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intentService)
            else
                context.startService(intentService)

            parentInteract?.bindService(intentService)
        }
    }

    override fun stopService(context: Context, clazz: Class<S>) {
        "DelegateLifecycleBotHelper stopService".logD()
        Intent(context, clazz).let { intentService ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intentService)
            else
                context.startService(intentService)
        }
    }

    override fun setStartServiceListener(parentInteract: LifecycleBotHelper.IParentInteract) {
        this.parentInteract = parentInteract
    }
}

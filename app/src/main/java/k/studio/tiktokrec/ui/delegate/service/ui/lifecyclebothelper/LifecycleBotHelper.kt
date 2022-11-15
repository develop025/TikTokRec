package k.studio.tiktokrec.ui.delegate.service.ui.lifecyclebothelper

import android.content.Context
import android.content.Intent
import k.studio.tiktokrec.ui.delegate.service.helper.BotHelper

/**
 * Delegate interface to manage any BotManagerService
 */
interface LifecycleBotHelper<S : BotHelper> {

    var parentInteract: IParentInteract?

    fun startService(context: Context, clazz: Class<S>)

    fun stopService(context: Context, clazz: Class<S>)

    fun setStartServiceListener(parentInteract: IParentInteract)

    interface IParentInteract {
        fun bindService(intentService: Intent)
    }
}

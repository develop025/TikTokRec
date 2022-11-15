package k.studio.tiktokrec.service

import k.studio.tiktokrec.ui.delegate.service.helper.BotHelper
import k.studio.tiktokrec.ui.delegate.service.helper.binder.ServiceBinder

/**
 * Implements service for manage Accessibility service with Helper
 */
class AppForegroundService : BotHelper() {

    override val binder: ServiceBinder<BotHelper> = BackgroundBinder()

    /**
     * Implements wrapper binder [BotHelper]
     */
    inner class BackgroundBinder : BotManagerBinder()

}
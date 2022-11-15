package k.studio.tiktokrec.bot

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.res.Resources
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import dagger.hilt.android.AndroidEntryPoint
import k.studio.screen_driver.screeninteract.ScreenDriver
import k.studio.tiktokrec.bot.behaviour.ITikTokActionManager
import k.studio.tiktokrec.bot.behaviour.TikTokActionManager
import k.studio.tiktokrec.data.vo.datasource.HeartOrderDataSource
import k.studio.tiktokrec.data.vo.datasource.UniqueActionDataSource
import k.studio.tiktokrec.service.AppForegroundService
import k.studio.tiktokrec.ui.delegate.service.ui.lifecyclebothelper.DelegateLifecycleBotHelper
import k.studio.tiktokrec.ui.delegate.service.ui.lifecyclebothelper.LifecycleBotHelper
import k.studio.tiktokrec.utils.logD
import k.studio.tiktokrec.utils.logW
import javax.inject.Inject

/**
 * Impl init accessibility service
 */
//LifecycleBinder<AppForegroundService, ServiceBinder<AppForegroundService>> by DelegateLifecycleBinder()
@AndroidEntryPoint
class AppBot :
    AccessibilityService(),
    ScreenInteract,
    LifecycleBotHelper<AppForegroundService> by DelegateLifecycleBotHelper() {

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: ScreenInteract? = null

        //TODO: impl specific exception
        @Throws(Exception::class)
        fun getScreenInteract(): ScreenInteract {
            return instance ?: synchronized(this) {
                instance ?: run {
                    "AppBot bot == null".logW()
                    throw Exception("bot == null")
                }
            }
        }

        fun getScreenInteractOrNull(): ScreenInteract? {
            synchronized(this) {
                return instance
            }
        }

        private fun setScreenInteract(instance: ScreenInteract?) {
            "AppBot.setScreenInteract:$instance".logD()
            synchronized(this) {
                Companion.instance = instance
            }
        }
    }

    //TODO: impl custom scope
    //https://medium.com/mindful-engineering/more-on-hilt-custom-components-custom-scope-f66c441c40c9
    @Inject
    lateinit var heartOrderDataSource: HeartOrderDataSource

    @Inject
    lateinit var uniqueActionDataSource: UniqueActionDataSource

    private val interact = object : ScreenDriver.Interact {
        override fun getContext(): Context {
            return this@AppBot
        }

        override fun getRootInActiveWindow(): AccessibilityNodeInfo {
            return rootInActiveWindow
        }

        override fun getResources(): Resources {
            return resources
        }

        //Call from mediator
       /* override fun onStop() {
            "AppBot.interact.onStop tikTokMediator = null".logD()
            tikTokMediator = null
        }*/
    }

    @Volatile
    var tikTokActionManager: ITikTokActionManager? = null

    override fun onCreate() {
        super.onCreate()
        "!!!!!!!!!!!!!!!!!!!AppBot.onCreate".logD()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        "!!!!!!!!!!!!!!!!!!!AppBot.onServiceConnected".logD()
        setScreenInteract(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        "!!!!!!!!!!!!!!!!!!!AppBot.onDestroy".logD()
        setScreenInteract(null)
        stopAction()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    override fun startAction() {
        if (tikTokActionManager != null) {
            "AppBot.startAction action already started".logD()
            return
        }

        startService(this@AppBot, AppForegroundService::class.java)

//        setStartServiceListener(object : LifecycleBotHelper.IParentInteract {
//            override fun bindService(intentService: Intent) {
//                bindService(this@AppBot, intentService)
//            }
//        })

        tikTokActionManager = TikTokActionManager(
            interact,
            heartOrderDataSource,
            uniqueActionDataSource
        ).apply {
            startActions {
                stopAction()
            }
        }
    }

    override fun stopAction() {
//        if (boundLiveData.value == true)
//            stopService(this, AppForegroundService::class.java)
        stopService(this, AppForegroundService::class.java)
        "AppBot.stopAction".logD()
        tikTokActionManager?.stopActions()
        tikTokActionManager = null
    }
}
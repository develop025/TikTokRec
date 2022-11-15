package k.studio.tiktokrec.bot.behaviour

import k.studio.screen_driver.screeninteract.ScreenDriver
import k.studio.tiktokrec.data.vo.datasource.HeartOrderDataSource
import k.studio.tiktokrec.data.vo.datasource.UniqueActionDataSource
import k.studio.tiktokrec.utils.logD
import k.studio.tiktokrec.utils.logW
import kotlinx.coroutines.*

/**
 * Implements screen interaction for execution actions
 * On stop:
 * - stop foreground service
 */
class TikTokActionManager(
    private val interact: ScreenDriver.Interact,
    private val heartOrderDataSource: HeartOrderDataSource,
    private val uniqueActionDataSource: UniqueActionDataSource
) : ITikTokActionManager,
    TikTokApi by TikTokApiDelegate(interact) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        "TikTokMediator.init".logD("TikTokMediator")
    }

    override fun stopActions() {
        "TikTokMediator.stopActions".logD("TikTokMediator")
        scope.launch {
            job.cancelAndJoin()
            "TikTokMediator.stopActions job.cancelAndJoin".logD("TikTokMediator")
        }

        "TikTokMediator.stopActions scope.isActive:${scope.isActive}".logD("TikTokMediator")
    }

    protected fun finalize() {
        "TikTokMediator.finalize".logD("TikTokMediator")
    }

    override fun startActions(onResult: () -> Unit) {
        "TikTokMediator.startActions scope.isActive:${scope.isActive}".logD("TikTokMediator")

        //0. Detect foreground application between all steps
        //1. Get order
        //2. Pause
        //3. Save unique
        //4. Check TikTok window is active
        //5. Click like

        try {
            "TikTokMediator.startActions try".logD("TikTokMediator")
            scope.launch {
                "TikTokMediator.startActions scope.launch".logD("TikTokMediator")
                while (true) {
                    run loop@{
                        "TikTokMediator.startActions while (true)".logD("TikTokMediator")
                        heartOrderDataSource.getOrderHeartForActionWithTimeout()
                            ?.let { orderHeart ->
                                "TikTokMediator.start getOrderHeart success ${orderHeart.username}".logD(
                                    "TikTokMediator"
                                )
                                val result = openUrl(orderHeart.videoLink)
                                "TikTokMediator.startActions openUrl:$result".logD("TikTokMediator")
                                "TikTokMediator.startActions delay 10s".logD("TikTokMediator")
                                delay(10000)
                                if (!result) {
                                    //TODO show user error
                                    "TikTokMediator.start continue@loop".logD("TikTokMediator")
                                    uniqueActionDataSource.saveUniqueAction(orderHeart)
                                    "TikTokMediator.startActions return@loop".logD("TikTokMediator")
                                    return@loop
                                }
                                //5. Click like
                                foundLikeButton()?.let { node ->
                                    val clickResult = click(node)
                                    "TikTokMediator.startActions click:$clickResult".logD("TikTokMediator")
                                    if (!clickResult) {
                                        //TODO show user error
                                        "TikTokMediator.startActions continue@loop".logD("TikTokMediator")
                                        uniqueActionDataSource.saveUniqueAction(orderHeart)
                                        "TikTokMediator.startActions return@loop".logD("TikTokMediator")
                                        return@loop
                                    }
                                }
                                "TikTokMediator.startActions delay 10s".logD("TikTokMediator")
                                delay(10000)
                                uniqueActionDataSource.saveUniqueAction(orderHeart)
                                "TikTokMediator.startActions delay 5s".logD("TikTokMediator")
                                delay(5000)
                            } ?: run {
                            //TODO: orders empty
                            //TODO: stop action. error saved in DataSource
                            "TikTokMediator.startActions orderHeart empty".logD("TikTokMediator")
                            "TikTokMediator.startActions return@launch".logD("TikTokMediator")
//                            stopActions()
                            onResult()
                            return@launch
                        }
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> "TikTokMediator.start startActions job closed Exception:${e.message}, ${e.javaClass.name}".logD(
                    "TikTokMediator"
                )
                else -> "TikTokMediator.start Exception:${e.message}, ${e.javaClass.name}".logW()
            }
        }
        "TikTokMediator.startActions finish".logD("TikTokMediator")
    }
    /*private fun openErrorDialog() {
        //TODO
    }

    private fun openReport() {
        //TODO
    }*/
}

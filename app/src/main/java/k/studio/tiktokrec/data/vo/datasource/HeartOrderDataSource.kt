package k.studio.tiktokrec.data.vo.datasource

import k.studio.realtime_ktx.vo.DataResponse
import k.studio.tiktokrec.data.source.AppRepository
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.utils.logD
import kotlinx.coroutines.*
import java.lang.Long.max
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class HeartOrderDataSource @Inject constructor(private val appRepository: AppRepository) {

    private var job: CompletableJob? = null
    private var scope: CoroutineScope? = null

    suspend fun getOrderHeartForActionWithTimeout(): OrderHeart? {
        "HeartOrderDataSource.getOrderHeartForActionWithTimeout".logD()
        // Init new scope if not active
        //TODO: impl single call
        /*if (job?.isActive == true && scope?.isActive == true) {
            "HeartOrderDataSource.getOrderHeartForActionWithTimeout init new job and scope".logD()

        }*/
        SupervisorJob().let {
            job = it
            scope = CoroutineScope(Dispatchers.IO + it)
        }

        // Start timer
        return suspendCoroutine { continuation ->
            scope?.launch {
                withTimeout(60000L) {
                    val orderHeart = getOrderHeartForAction()
                    continuation.resume(orderHeart)
                    /*{
                        "HeartOrderDataSource.getOrderHeartForActionWithTimeout suspendCancellableCoroutine continuation.resume Throwable:${it.message}".logD()
                    }*/
                    //continuation.invokeOnCancellation { }
                }
            }
        }
    }

    suspend fun stop() {
        job?.cancelAndJoin()
    }

    suspend fun getOrderHeartForAction(): OrderHeart? {
        "HeartOrderDataSource.getOrderHeartForAction get from local".logD()
        // First of all get from local, and if empty result get from remote
        appRepository.getOrderHeartForAction()?.let { order ->
            "HeartOrderDataSource.getOrderHeartForAction return from local".logD()
            return order
        } ?: run {
            "HeartOrderDataSource.getOrderHeartForAction get from remote".logD()
            val userState = appRepository.getUserState()
            "HeartOrderDataSource.getOrderHeartForAction orderHeartAt:${userState.orderHeartAt}".logD()

            val dataResponse = appRepository.subscribeOnceOrderHeart(userState.orderHeartAt)
            when (dataResponse) {
                is DataResponse.Complete -> {
                    "HeartOrderDataSource.getOrderHeartForAction DataResponse.Complete size:${dataResponse.data.size}".logD()

                    if (dataResponse.data.isEmpty())
                        return null

                    appRepository.save(*dataResponse.data.toTypedArray())
                    var maxOrderAt = appRepository.getOrderHeartAt()
                    dataResponse.data.forEach { orderHeart ->
                        maxOrderAt = max(orderHeart.orderAt, maxOrderAt)
                    }
                    appRepository.setOrderHeartAt(maxOrderAt)
                }
                is DataResponse.Error -> {
                    "HeartOrderDataSource DataResponse.Error ${dataResponse.error.message}".logD()
                    //TODO: handle error. save to error table
                    return null
                }
            }
            delay(1000)
            // To return result from local
            return getOrderHeartForAction()
        }
    }
}
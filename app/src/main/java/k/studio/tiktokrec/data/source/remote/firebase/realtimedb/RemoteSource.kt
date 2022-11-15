package k.studio.tiktokrec.data.source.remote.firebase.realtimedb

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import k.studio.realtime_ktx.vo.DataResponse
import k.studio.tiktokrec.data.error.ErrorFirebaseUpdateChildren
import k.studio.tiktokrec.data.vo.db.OrderHeart
import kotlin.jvm.Throws

interface RemoteSource {

    fun subscribeOrdersHearts(
        orderAt: Long,
        onAdded: (orderHeart: OrderHeart) -> Unit,
        onRemoved: (orderHeart: OrderHeart) -> Unit,
        onError: ((errorCode: DatabaseError) -> Unit)
    ): RDBService.CustomChildEventListener

    //TODO: impl call
    fun orderHeartDone(
        orderHeart: OrderHeart, onSuccess: () -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    )

    fun addOrderHearts(
        orderHeart: OrderHeart,
        stars: Long,
        onSuccess: () -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    )

    fun getOrderHearts(
        firebaseKey: String,
        onSuccess: (orderHeart: OrderHeart) -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    )

   /* fun subscribeOnceOrderHeart(
        orderAt: Long,
        onAdded: (orders: List<OrderHeart>) -> Unit,
        onError: ((errorCode: DatabaseError) -> Unit),
        onEmpty: () -> Unit
    )*/

    @Throws(RDBService.SubscribeOnceOrderHeart::class)
    suspend fun subscribeOnceOrderHeart(
        orderAt: Long
    ): DataResponse<List<OrderHeart>>

    fun unsubscribeOrdersHearts(childEventListener: RDBService.CustomChildEventListener)
    fun subscribeOrderHeartsByUsername(
        username: String,
        onAdded: (orderHeart: OrderHeart) -> Unit,
        onRemoved: (orderHeart: OrderHeart) -> Unit,
        onError: (errorCode: DatabaseError) -> Unit
    )
}
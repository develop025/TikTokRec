package k.studio.tiktokrec.data.source.remote.firebase.realtimedb

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import k.studio.realtime_ktx.extensions.singleValueEvent
import k.studio.realtime_ktx.vo.DataResponse
import k.studio.tiktokrec.data.error.ErrorFirebaseEventListener
import k.studio.tiktokrec.data.error.ErrorFirebaseUpdateChildren
import k.studio.tiktokrec.data.vo.db.OrderHeart
import k.studio.tiktokrec.utils.logD
import k.studio.tiktokrec.utils.logW
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RDBService @Inject constructor() : RemoteSource {

    private val database = Firebase.database
    private val reference = database.reference

    private val orderHeartsRef = database.getReference("$ORDER/$HEARTS/")

    private fun provideOrderHeartsValueEventListener(): CustomValueEventListener {
        return object : CustomValueEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                "RDBService.CustomValueEventListener onDataChange: $snapshot".logD()
                val orders = snapshot.toOrderHeartList()
                if (orders.isNotEmpty())
                    onAdded?.invoke(orders)
                else
                    onEmpty?.invoke()
            }

            override fun onCancelled(error: DatabaseError) {
                "RDBService.CustomValueEventListener onCancelled: ${error.code}, ${error.message}".logD()
                onError?.invoke(error)
            }
        }
    }

    private fun provideOrderHeartsChildEventListener(): CustomChildEventListener {
        return object : CustomChildEventListener() {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.toOrderHeart()?.let {
                    "RDBService.CustomChildEventListener onChildAdded:$it".logD()
                    onAdded?.invoke(it)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.toOrderHeart()?.let {
                    "RDBService.CustomChildEventListener onChildChanged:$it".logD()
                    onAdded?.invoke(it)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.toOrderHeart()?.let {
                    "RDBService.CustomChildEventListener onChildRemoved:$it".logD()
                    onRemoved?.invoke(it)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                "RDBService.CustomChildEventListener onChildMoved".logD()
                snapshot.toOrderHeart()?.let {
                    onAdded?.invoke(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                "RDBService.CustomChildEventListener onCancelled:${error.message}, ${error.code}, ${error.details}".logD()
                onError?.invoke(error)
            }
        }
    }

    override fun subscribeOrdersHearts(
        orderAt: Long,
        onAdded: (orderHeart: OrderHeart) -> Unit,
        onRemoved: (orderHeart: OrderHeart) -> Unit,
        onError: (errorCode: DatabaseError) -> Unit
    ): CustomChildEventListener {
        "RDBService.subscribeOrdersHearts orderAt:$orderAt".logD()
        val childEventListener = provideOrderHeartsChildEventListener().apply {
            this.onAdded = onAdded
            this.onRemoved = onRemoved
            this.onError = onError
        }
        orderHeartsRef.orderByChild(ORDER_AT)
            .startAt(orderAt.toDouble(), ORDER_AT)
            .addChildEventListener(childEventListener)

        return childEventListener
    }

    /**
     * Add order for follow to the current account
     * @param orderHeart
     * @param stars - how much stars will spent for this video
     */
    override fun addOrderHearts(
        orderHeart: OrderHeart,
        stars: Long,
        onSuccess: () -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    ) {
        "RDBService.addOrderHearts orderHeart:${orderHeart.id}, stars:$stars".logD()
        val keyOrdersHearts = reference.child("$ORDER/$HEARTS").push().key
        if (keyOrdersHearts == null) {
            "RDBService Couldn't get push key:$keyOrdersHearts for posts".logD()
            return
        }

        val postValues = orderHeart.toMap(keyOrdersHearts)

        val childUpdates = hashMapOf(
            "$ORDER/$HEARTS/$keyOrdersHearts" to postValues,
            "$USER/${orderHeart.username}/$COINS" to ServerValue.increment(-1 * stars)
        )

        reference.updateChildren(childUpdates).addOnSuccessListener {
            "RDBService.addOrderHearts addOnSuccessListener".logD()
        }.addOnFailureListener {
            "RDBService.addOrderHearts addOnFailureListener:${it.message}, ${it::class.java.name}".logW()
            onFailure(ErrorFirebaseUpdateChildren.ON_FAILURE)
        }.addOnCanceledListener {
            "RDBService.addOrderHearts addOnCanceledListener".logD()
            onFailure(ErrorFirebaseUpdateChildren.ON_CANCEL)
        }
    }

    /**
     * Subscribe to listen first order onAdded and unsubscribe
     */
   /* override fun subscribeOnceOrderHeart(
        orderAt: Long,
        onAdded: (orders: List<OrderHeart>) -> Unit,
        onError: (errorCode: DatabaseError) -> Unit,
        onEmpty: () -> Unit,
    ) {
        "RDBService.subscribeOnceOrderHeart orderAt:$orderAt".logD()
        val orderHeartsValueEventListener = provideOrderHeartsValueEventListener().apply {
            this.onAdded = onAdded
            this.onError = onError
        }

        orderHeartsRef.orderByChild(ORDER_AT)
            .startAt(orderAt.toDouble(), ORDER_AT)
            .limitToFirst(10)
            .addListenerForSingleValueEvent(orderHeartsValueEventListener)
    }*/

    /**
     * Subscribe to listen first order onAdded and unsubscribe
     */
    override suspend fun subscribeOnceOrderHeart(orderAt: Long): DataResponse<List<OrderHeart>> {
        "RDBService.subscribeOnceOrderHeart suspend orderAt:$orderAt".logD()
        return orderHeartsRef.orderByChild(ORDER_AT)
            .startAt(orderAt.toDouble(), ORDER_AT)
            .limitToFirst(10).singleValueEvent().toOrderHeartList()
    }

    /* @Throws(SubscribeOnceOrderHeart::class)
     override suspend fun subscribeOnceOrderHeart(orderAt: Long): List<OrderHeart> {
         "RDBService.subscribeOnceOrderHeart suspend orderAt:$orderAt".logD()
         return suspendCoroutine { continuation ->
             val orderHeartsValueEventListener = provideOrderHeartsValueEventListener().apply {
                 this.onAdded = { orders: List<OrderHeart> -> continuation.resume(orders) }
                 this.onError = { errorCode: DatabaseError ->
                     continuation.resume(orders)
                     val error = ErrorFirebaseEventListener.getByCode(errorCode.code)
                     throw SubscribeOnceOrderHeart(error)
                 }
                 this.onEmpty = {
                     val error = ErrorFirebaseEventListener.EMPTY_LIST
                     throw SubscribeOnceOrderHeart(error)
                 }
             }

             orderHeartsRef.orderByChild(ORDER_AT)
                 .startAt(orderAt.toDouble(), ORDER_AT)
                 .limitToFirst(10)
                 .addListenerForSingleValueEvent(orderHeartsValueEventListener)
         }
     }*/

    class SubscribeOnceOrderHeart(val error: ErrorFirebaseEventListener?) : Exception()

    override fun subscribeOrderHeartsByUsername(
        username: String,
        onAdded: (orderHeart: OrderHeart) -> Unit,
        onRemoved: (orderHeart: OrderHeart) -> Unit,
        onError: (errorCode: DatabaseError) -> Unit
    ) {
        "RDBService.getOrderHeartsByUsername $username".logD()
        val orderHeartsChildEventListener = provideOrderHeartsChildEventListener().apply {
            this.onAdded = onAdded
            this.onRemoved = onRemoved
            this.onError = onError
        }

        orderHeartsRef
            .orderByChild(USERNAME)
            .equalTo(username)
            .addChildEventListener(orderHeartsChildEventListener)
    }

    override fun getOrderHearts(
        firebaseKey: String,
        onSuccess: (orderHeart: OrderHeart) -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    ) {
        "RDBService.getOrderHearts:$firebaseKey".logD()
        orderHeartsRef
            .child(firebaseKey)
            .get()
            .addOnSuccessListener { snapshot ->
                "RDBService.getOrderHearts addOnSuccessListener:$snapshot, \nsnapshot.toOrderHeartList():${snapshot.toOrderHeart()}".logD()
                snapshot.toOrderHeart()?.let { orderHeart ->
                    onSuccess(orderHeart)
                }
            }
            .addOnCanceledListener {
                "RDBService.getOrderHearts addOnCanceledListener".logD()
                onFailure(ErrorFirebaseUpdateChildren.ON_CANCEL)
            }
            .addOnFailureListener {
                "RDBService.getOrderHearts addOnFailureListener:${it.message}, ${it::class.java.name}".logW()
                onFailure(ErrorFirebaseUpdateChildren.ON_FAILURE)
            }
    }

    override fun unsubscribeOrdersHearts(childEventListener: CustomChildEventListener) {
        "RDBService.unsubscribeOrdersHearts".logD()
        childEventListener.onAdded = null
        childEventListener.onRemoved = null
        childEventListener.onError = null
        orderHeartsRef.removeEventListener(childEventListener)
    }

    override fun orderHeartDone(
        orderHeart: OrderHeart, onSuccess: () -> Unit,
        onFailure: (errorCode: ErrorFirebaseUpdateChildren) -> Unit
    ) {
        "RDBService.orderHeartDone".logD()
        val childUpdates = hashMapOf(
            "$ORDER/$HEARTS/${orderHeart.username}/${orderHeart.videoLink}"
                    to ServerValue.increment(-1)
        )
        reference.updateChildren(childUpdates).addOnSuccessListener {
            "RDBService.orderHeartDone addOnSuccessListener".logD()
        }.addOnFailureListener {
            "RDBService.orderHeartDone addOnFailureListener:${it.message}, ${it::class.java.name}".logW()
            onFailure(ErrorFirebaseUpdateChildren.ON_FAILURE)
        }.addOnCanceledListener {
            "RDBService.orderHeartDone addOnCanceledListener".logD()
            onFailure(ErrorFirebaseUpdateChildren.ON_CANCEL)
        }
    }

    companion object {
        const val ORDER = "order"
        const val HEARTS = "hearts"
        const val USER = "user"
        const val COINS = "coins"
        const val ORDER_AT = "orderAt"
        const val USERNAME = "username"
    }

    abstract class CustomChildEventListener : ChildEventListener {
        var onAdded: ((orderHeart: OrderHeart) -> Unit)? = null
        var onRemoved: ((orderHeart: OrderHeart) -> Unit)? = null
        var onError: ((errorCode: DatabaseError) -> Unit)? = null
    }

    abstract class CustomValueEventListener : ValueEventListener {
        var onAdded: ((orders: List<OrderHeart>) -> Unit)? = null
        var onError: ((errorCode: DatabaseError) -> Unit)? = null
        var onEmpty: (() -> Unit)? = null
    }
}

private fun DataSnapshot.toOrderHeart(): OrderHeart? {
    return this.getValue(OrderHeart::class.java)
}

private fun DataSnapshot.toOrderHeartList(): List<OrderHeart> {
    val orders = mutableListOf<OrderHeart>()
    children.forEach { orderHeartsSnapshot ->
        orderHeartsSnapshot.toOrderHeart()?.let {
            orders.add(it)
        }
    }
    return orders
}

private fun DataResponse<DataSnapshot>.toOrderHeartList(): DataResponse<List<OrderHeart>> {
    return when (this) {
        is DataResponse.Complete -> DataResponse.complete(this.data.toOrderHeartList())
        is DataResponse.Error -> DataResponse.error(this.error)
    }
}

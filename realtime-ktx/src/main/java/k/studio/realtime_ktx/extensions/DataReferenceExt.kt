package k.studio.realtime_ktx.extensions

import com.google.firebase.database.*
import com.google.firebase.database.snapshot.Node
import k.studio.realtime_ktx.vo.ChildEventResponse
import k.studio.realtime_ktx.vo.DataResponse
import k.studio.realtime_ktx.vo.ValueEventResponse
import k.studio.screendriverktx.logW
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.CoroutineContext

/**
 * Created by Kaustubh Patange at 31st Dec 2020
 */

typealias CancellationCallback = ((cause: Throwable) -> Unit)

/**
 * Performs a [DatabaseReference.setValue] event call on databaseReference as suspending.
 *
 * @param onCancellation action to perform if there is a cancellation
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun DatabaseReference.setValueAsync(
    value: Any?,
    priority: Node? = null,
    onCancellation: CancellationCallback = {}
): DataResponse<DatabaseReference> = suspendCancellableCoroutine { continuation ->
    val completeListener = DatabaseReference.CompletionListener { error, ref ->
        if (error == null)
            continuation.resume(DataResponse.complete(ref), onCancellation)
        else
            continuation.resume(DataResponse.error(error.toException()), onCancellation)
    }
    setValue(value, priority, completeListener)
}

/**
 * Performs an [DatabaseReference.updateChildren] event call on databaseReference as suspending.
 *
 * @param onCancellation action to perform if there is a cancellation
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun DatabaseReference.updateChildrenAsync(
    value: Map<String, Any>,
    onCancellation: CancellationCallback = {}
): DataResponse<DatabaseReference> = suspendCancellableCoroutine { continuation ->
    val completeListener = DatabaseReference.CompletionListener { error, ref ->
        if (error == null)
            continuation.resume(DataResponse.complete(ref), onCancellation)
        else
            continuation.resume(DataResponse.error(error.toException()), onCancellation)
    }
    updateChildren(value, completeListener)
}

/**
 * Performs an [DatabaseReference.setPriority] event call on databaseReference as suspending.
 *
 * @param onCancellation action to perform if there is a cancellation
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun DatabaseReference.setPriorityAsync(
    value: Any?,
    onCancellation: CancellationCallback = {}
): DataResponse<DatabaseReference> = suspendCancellableCoroutine { continuation ->
    val completeListener = DatabaseReference.CompletionListener { error, ref ->
        if (error == null)
            continuation.resume(DataResponse.complete(ref), onCancellation)
        else
            continuation.resume(DataResponse.error(error.toException()), onCancellation)
    }
    setPriority(value, completeListener)
}

/**
 * Performs a [DatabaseReference.removeValue] event call on databaseReference as suspending.
 *
 * @param onCancellation action to perform if there is a cancellation
 */
suspend fun DatabaseReference.removeValueAsync(onCancellation: CancellationCallback = {}): DataResponse<DatabaseReference> {
    return setValueAsync(null, onCancellation = onCancellation)
}

/**
 * Perform a [Query.addListenerForSingleValueEvent] call on a databaseReference as suspending.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Query.singleValueEvent(onCancellation: CancellationCallback = {}): DataResponse<DataSnapshot> =
    suspendCancellableCoroutine { continuation ->
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(DataResponse.error(error.toException()), onCancellation)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(DataResponse.complete(snapshot), onCancellation)
            }
        }
        addListenerForSingleValueEvent(valueEventListener)
        continuation.invokeOnCancellation { removeEventListener(valueEventListener) }
    }

/**
 * Returns a flow for [Query.addChildEventListener].
 *
 * Example code:
 * ```
 * val job = SupervisorJob()
 * CoroutineScope(Dispatchers.Main + job).launch {
 *    dataReference.childEventFlow().collect { result ->
 *       when(result) {
 *          ...
 *       }
 *    }
 * }
 * ```
 *
 * To stop collecting from the flow cancel the [CoroutineContext] `job.cancel()`.
 */
suspend fun DatabaseReference.childEventFlow(): Flow<ChildEventResponse> = callbackFlow {
    val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            trySendBlocking(ChildEventResponse.Added(snapshot, previousChildName))
                .onFailure { throwable ->
                    throwable?.let {
                        "DatabaseReference.childEventFlow onChildAdded".logW(it)
                    } ?: run {
                        "DatabaseReference.childEventFlow onChildAdded".logW()
                    }
                }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            trySendBlocking(ChildEventResponse.Changed(snapshot, previousChildName))
                .onFailure { throwable ->
                    throwable?.let {
                        "DatabaseReference.childEventFlow onChildChanged".logW(it)
                    } ?: run {
                        "DatabaseReference.childEventFlow onChildChanged".logW()
                    }
                }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            trySendBlocking(ChildEventResponse.Removed(snapshot))
                .onFailure { throwable ->
                    throwable?.let {
                        "DatabaseReference.childEventFlow onChildRemoved".logW(it)
                    } ?: run {
                        "DatabaseReference.childEventFlow onChildRemoved".logW()
                    }
                }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            trySendBlocking(ChildEventResponse.Moved(snapshot, previousChildName))
                .onFailure { throwable ->
                    throwable?.let {
                        "DatabaseReference.childEventFlow onChildMoved".logW(it)
                    } ?: run {
                        "DatabaseReference.childEventFlow onChildMoved".logW()
                    }
                }
        }

        override fun onCancelled(error: DatabaseError) {
            trySendBlocking(ChildEventResponse.Cancelled(error))
                .onFailure { throwable ->
                    throwable?.let {
                        "DatabaseReference.childEventFlow onCancelled".logW(it)
                    } ?: run {
                        "DatabaseReference.childEventFlow onCancelled".logW()
                    }
                }
        }
    }
    addChildEventListener(childEventListener)
    awaitClose {
        removeEventListener(childEventListener)
    }
}

/**
 * Returns a flow for [Query.addValueEventListener].
 *
 * Example code:
 * ```
 * val job = SupervisorJob()
 * CoroutineScope(Dispatchers.Main + job).launch {
 *    dataReference.valueEventFlow().collect { result ->
 *       when(result) {
 *          ...
 *       }
 *    }
 * }
 * ```
 *
 * To stop collecting from the flow cancel the [CoroutineContext] `job.cancel()`.
 */
suspend fun DatabaseReference.valueEventFlow(onFailure: (exception: Throwable?) -> Unit): Flow<ValueEventResponse> =
    callbackFlow {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySendBlocking(ValueEventResponse.Changed(snapshot))
                    .onFailure { throwable ->
                        throwable?.let {
                            "DatabaseReference.valueEventFlow onDataChange".logW(it)
                        } ?: run {
                            "DatabaseReference.valueEventFlow onDataChange".logW()
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(ValueEventResponse.Cancelled(error))
                    .onFailure { throwable ->
                        throwable?.let {
                            "DatabaseReference.valueEventFlow onCancelled".logW(it)
                        } ?: run {
                            "DatabaseReference.valueEventFlow onCancelled".logW()
                        }
                    }
            }
        }
        addValueEventListener(valueEventListener)
        awaitClose {
            removeEventListener(valueEventListener)
        }
    }
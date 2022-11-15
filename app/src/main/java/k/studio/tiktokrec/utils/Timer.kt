package k.studio.tiktokrec.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn

class Timer {
    private var orderHeartTimer: Job? = null

    fun start(scope: CoroutineScope, periodSec: Int, initialDelaySec: Int, onFinish: () -> Unit) {
        orderHeartTimer = flow {
            "Timer.start".logD()
            //delay((initialDelaySec * 1000).toLong())
            var remain = initialDelaySec
            while (remain > 0) {
                emit(remain)
                delay((periodSec * 1000).toLong())
                remain -= periodSec
            }
            onFinish.invoke()
            stop()
        }.launchIn(scope)
    }

    suspend fun stop() {
        "Timer.stop".logD()
        orderHeartTimer?.cancelAndJoin()
    }
}
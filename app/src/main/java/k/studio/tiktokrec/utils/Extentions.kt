package k.studio.tiktokrec.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import timber.log.Timber
import kotlin.concurrent.thread

private const val TAG = "TikTokRec"

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun String.logD(tag: String = TAG) {
    Timber.tag(tag).log(Log.DEBUG, this)
}

fun String.logD(tag: String, throwable: Throwable) {
    Timber.tag(tag).log(Log.DEBUG, throwable, this)
}

fun String.logD(throwable: Throwable) {
    this.logD(TAG, throwable)
}

fun String.logW(tag: String = TAG) {
    Timber.tag(tag).log(Log.WARN, this)
}

fun String.logW(tag: String, throwable: Throwable) {
    Timber.tag(tag).log(Log.WARN, throwable, this)
}

fun String.logW(throwable: Throwable) {
    this.logW(TAG, throwable)
}

fun runOnMainWithDelay(delay: Long, afterDelay: () -> Unit) {
    thread {
        Thread.sleep(delay)
        Handler(Looper.getMainLooper()).post {
            afterDelay()
        }
    }
}

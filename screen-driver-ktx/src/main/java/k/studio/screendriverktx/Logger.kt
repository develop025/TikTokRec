package k.studio.screendriverktx

import android.util.Log
import timber.log.Timber

private const val TAG = "TikTokRec"

fun String.logD(tag: String = TAG) {
    Timber.tag(tag).log(Log.DEBUG, this)
}

fun String.logD(tag: String = TAG, t: Throwable) {
    Timber.tag(tag).log(Log.DEBUG, t, this)
}

fun String.logD(t: Throwable) {
    this.logD(TAG, t)
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
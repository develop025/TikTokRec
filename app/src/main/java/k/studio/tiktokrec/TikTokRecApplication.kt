package k.studio.tiktokrec

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import k.studio.tiktokrec.utils.logD
import timber.log.Timber

@HiltAndroidApp
class TikTokRecApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        "TikTokRecApplication onCreate".logD()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
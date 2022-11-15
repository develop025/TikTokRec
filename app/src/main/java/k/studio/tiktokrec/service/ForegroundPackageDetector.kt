package k.studio.tiktokrec.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import k.studio.tiktokrec.utils.logD
import java.util.*
import kotlin.concurrent.schedule

class ForegroundPackageDetector {

    fun detect(context: Context) {
        //TODO: impl call permissions https://www.droidcon.com/2022/02/08/accessing-app-usage-history-in-android/
        //TODO: add permission to manifest: <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />
        val usm = (context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager)
        Timer().schedule(1000, 2000) {

            val usageEvents = usm.queryEvents(
                System.currentTimeMillis() - 100000,
                System.currentTimeMillis()
            )

            val mySortedMap: SortedMap<Long, UsageEvents.Event> = TreeMap()

            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                val result = usageEvents.getNextEvent(event)

                val eventType = when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_PAUSED -> "ACTIVITY_PAUSED"
                    UsageEvents.Event.ACTIVITY_RESUMED -> "ACTIVITY_RESUMED"
                    UsageEvents.Event.CONFIGURATION_CHANGE -> "CONFIGURATION_CHANGE"
                    UsageEvents.Event.USER_INTERACTION -> "USER_INTERACTION"
                    UsageEvents.Event.STANDBY_BUCKET_CHANGED -> "STANDBY_BUCKET_CHANGED"
                    UsageEvents.Event.FOREGROUND_SERVICE_START -> "FOREGROUND_SERVICE_START"
                    UsageEvents.Event.FOREGROUND_SERVICE_STOP -> "FOREGROUND_SERVICE_STOP"
                    UsageEvents.Event.ACTIVITY_STOPPED -> "ACTIVITY_STOPPED"
                    else -> "UNKNOWN:${event.eventType}"
                }

                "event.className:${event.className} eventType:${eventType}".logD()
                if (result && event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    mySortedMap[event.timeStamp] = event
                }
            }

            if (!mySortedMap.isEmpty()) {
                val foregroundPackageName = mySortedMap.get(mySortedMap.lastKey())?.packageName
                "currentApp:$foregroundPackageName".logD()
            }
        }
    }
}
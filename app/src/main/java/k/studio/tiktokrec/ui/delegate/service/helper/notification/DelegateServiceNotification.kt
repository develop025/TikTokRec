package k.studio.tiktokrec.ui.delegate.service.helper.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import k.studio.tiktokrec.R
import k.studio.tiktokrec.ui.main.MainActivity

/**
 * Delegate manage Notifications
 */
class DelegateServiceNotification : ServiceNotification {

    override fun createServiceNotification(service: Service) {
        service.apply {
            val channelId = getString(R.string.channel_id)
            val channelVisibleName = getString(R.string.channel_visible_name)
            val channelDescription = getString(R.string.channel_description)
            val serviceTitle = getString(R.string.service_notification_title)
            val serviceDescription = getString(R.string.service_description)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    service,
                    channelId,
                    channelVisibleName,
                    channelDescription
                )

                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val notification = Notification.Builder(this, channelId)
                    .setContentTitle(serviceTitle)
                    .setContentText(serviceDescription)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setTicker(serviceTitle)
                    .build()
                startForeground(NOTIFICATION_ID, notification)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        context: Context,
        id: String,
        visibleName: String,
        description: String
    ) {
        val channel = NotificationChannel(id, visibleName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = description
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 101
    }
}
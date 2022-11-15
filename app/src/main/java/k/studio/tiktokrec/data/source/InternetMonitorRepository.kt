package k.studio.tiktokrec.data.source

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import k.studio.tiktokrec.data.source.remote.internet.ConnectionSettings
import k.studio.tiktokrec.data.source.remote.internet.LollipopNetworkMonitor
import k.studio.tiktokrec.data.source.remote.internet.MarshmallowNetworkMonitor
import k.studio.tiktokrec.data.source.remote.internet.NetworkMonitor
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create NetworkCallback
 * Interact with NetworkCallback
 */
@Singleton
class InternetMonitorRepository @Inject constructor(@ApplicationContext val context: Context) {

    private var networkCallback: NetworkMonitor

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            networkCallback = MarshmallowNetworkMonitor(context)
        else {
            val connectionSettings = ConnectionSettings(
                HTTP_PROTOCOL,
                HTTPS_PROTOCOL,
                INITIAL_INTERVAL,
                INTERVAL,
                HOST,
                PORT,
                TIMEOUT,
                HTTP_RESPONSE
            )

            networkCallback = LollipopNetworkMonitor(
                context,
                connectionSettings
            )
        }
    }

    suspend fun collectLatest(onResult: (Boolean) -> Unit) {
        networkCallback.collectLatest(onResult)
    }

    companion object {
        private const val HTTP_PROTOCOL = "http://"
        private const val HTTPS_PROTOCOL = "https://"
        private const val INITIAL_INTERVAL = 0
        private const val INTERVAL = 2000
        private const val HOST = "http://clients3.google.com/generate_204"
        private const val PORT = 80
        private const val TIMEOUT = 2000
        private const val HTTP_RESPONSE = HttpURLConnection.HTTP_NO_CONTENT
    }
}
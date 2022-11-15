package k.studio.tiktokrec.data.source.remote.internet

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.schedule

/**
 * Network callback for devices with Android Lollipop (API 21) or higher.
 * Determine current connection status.
 */
@TargetApi(21)
class LollipopNetworkMonitor(
    context: Context,
    private val connectionSettings: ConnectionSettings
) : NetworkMonitor {

    private val isConnected = MutableStateFlow(true)
    private val request: NetworkRequest = NetworkRequest.Builder().build()
    private val service = Context.CONNECTIVITY_SERVICE
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var manager = context.getSystemService(service) as ConnectivityManager

    @Volatile
    private var timer: Timer? = null

    override suspend fun collectLatest(onResult: (Boolean) -> Unit) {
        isConnected.onStart {
            if (isConnected.subscriptionCount.value == 0)
                registerNetworkCallback()
        }.onCompletion {
            if (isConnected.subscriptionCount.value == 0)
                unregisterNetworkCallback()
        }.collectLatest {
            onResult.invoke(it)
        }
    }

    private fun registerNetworkCallback() {
        startRequestTimer()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                startRequestTimer()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                timer?.cancel()
            }
        }

        manager.registerNetworkCallback(request, networkCallback!!)
    }

    private fun unregisterNetworkCallback() {
        timer?.cancel()
        networkCallback?.let {
            manager.unregisterNetworkCallback(it)
        }
    }

    private fun startRequestTimer() {
        timer?.cancel()
        Timer().apply {
            timer = this
            schedule(
                connectionSettings.initialInterval.toLong(),
                connectionSettings.interval.toLong()
            ) {
                val result = isConnected(
                    adjustHost(connectionSettings.host),
                    connectionSettings.port,
                    connectionSettings.timeout,
                    connectionSettings.httpResponse
                )
                isConnected.tryEmit(result)
            }
        }
    }

    private fun adjustHost(host: String): String {
        return if (!host.startsWith(connectionSettings.httpProtocol) && !host.startsWith(
                connectionSettings.httpsProtocol
            )
        ) {
            connectionSettings.httpsProtocol + host
        } else host
    }

    private fun isConnected(
        host: String,
        port: Int,
        timeoutInMs: Int,
        httpResponse: Int
    ): Boolean {
        var urlConnection: HttpURLConnection? = null
        return try {
            urlConnection = if (host.startsWith(connectionSettings.httpsProtocol)) {
                createHttpsUrlConnection(host, port, timeoutInMs)
            } else {
                createHttpUrlConnection(host, port, timeoutInMs)
            }
            urlConnection.responseCode == httpResponse
        } catch (e: IOException) {
            false
        } finally {
            urlConnection?.disconnect()
        }
    }

    @Throws(IOException::class)
    private fun createHttpUrlConnection(
        host: String?,
        port: Int,
        timeoutInMs: Int
    ): HttpURLConnection {
        val initialUrl = URL(host)
        val url = URL(initialUrl.protocol, initialUrl.host, port, initialUrl.file)
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = timeoutInMs
        urlConnection.readTimeout = timeoutInMs
        urlConnection.instanceFollowRedirects = false
        urlConnection.useCaches = false
        return urlConnection
    }

    @Throws(IOException::class)
    private fun createHttpsUrlConnection(
        host: String?,
        port: Int,
        timeoutInMs: Int
    ): HttpsURLConnection {
        val initialUrl = URL(host)
        val url = URL(initialUrl.protocol, initialUrl.host, port, initialUrl.file)
        val urlConnection = url.openConnection() as HttpsURLConnection
        urlConnection.connectTimeout = timeoutInMs
        urlConnection.readTimeout = timeoutInMs
        urlConnection.instanceFollowRedirects = false
        urlConnection.useCaches = false
        return urlConnection
    }
}

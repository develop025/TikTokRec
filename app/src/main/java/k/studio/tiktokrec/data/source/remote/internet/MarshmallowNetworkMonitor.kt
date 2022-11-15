package k.studio.tiktokrec.data.source.remote.internet

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * Network callback strategy for devices with Android Marshmallow (API 23) or higher.
 * Determine current connection status.
 * Init registerNetworkCallback - on app start
 */
@TargetApi(23)
class MarshmallowNetworkMonitor(context: Context) : NetworkMonitor {

    companion object {
        const val VALIDATED = NetworkCapabilities.NET_CAPABILITY_VALIDATED
    }

    private val isConnected = MutableStateFlow(true)
    private val request = NetworkRequest.Builder().build()
    private val service = Context.CONNECTIVITY_SERVICE
    private val manager = context.getSystemService(service) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

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
        manager.getNetworkCapabilities(manager.activeNetwork)?.let { networkCapabilities ->
            val netCapabilityValidated = networkCapabilities.hasCapability(VALIDATED)
            isConnected.tryEmit(netCapabilityValidated)
        } ?: run {
            isConnected.tryEmit(false)
        }

        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected.tryEmit(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val netCapabilityValidated = networkCapabilities.hasCapability(VALIDATED)
                isConnected.tryEmit(netCapabilityValidated)
            }
        }

        manager.registerNetworkCallback(request, networkCallback!!)
    }

    private fun unregisterNetworkCallback() {
        networkCallback?.let {
            manager.unregisterNetworkCallback(it)
        }
    }
}




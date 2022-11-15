package k.studio.tiktokrec.data.source.remote.internet

interface NetworkMonitor {
    suspend fun collectLatest(onResult: (Boolean) -> Unit)
}

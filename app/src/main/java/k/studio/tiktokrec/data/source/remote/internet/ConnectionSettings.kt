package k.studio.tiktokrec.data.source.remote.internet

data class ConnectionSettings(
    val httpProtocol: String,
    val httpsProtocol: String,
    val initialInterval: Int,
    val interval: Int,
    val host: String,
    val port: Int,
    val timeout: Int,
    val httpResponse: Int
)
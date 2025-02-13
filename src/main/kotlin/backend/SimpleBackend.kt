package online.aruka.backend

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Headers
import okhttp3.OkHttpClient
import online.aruka.util.request.Request

@Serializable
data class SimpleBackend(
    val balance: Balance = Balance(),
    val from: String = "unknown",
    val mode: Mode = Mode.Unknown,
    val name: String = "unknown"
): HAProxyBackend {

    companion object {
        fun get(
            address: String,
            apiVersion: String,
            credential: Pair<String, String>
        ): Triple<Int, List<SimpleBackend>, Headers> {
            return Request.getList<SimpleBackend>(
                address = "$address/$apiVersion/services/haproxy/configuration/backends",
                client = OkHttpClient(),
                credential = credential
            )
        }
    }

    @Serializable
    @SerialName("balance")
    data class Balance(
        val algorithm: Algorithm = Algorithm.Unknown
    ) {
        @Serializable
        @SerialName("algorithm")
        enum class Algorithm(type: String) {
            @SerialName("unknown") Unknown("unknown"),
            @SerialName("first") First("first"),
            @SerialName("hash") Hash("hash"),
            @SerialName("hdr") Hdr("hdr"),
            @SerialName("leastconn") Leastconn("leastconn"),
            @SerialName("random") Random("random"),
            @SerialName("rdp-cookie") RdpCookie("rdp-cookie"),
            @SerialName("roundrobin") Roundrobin("roundrobin"),
            @SerialName("source") Source("source"),
            @SerialName("static-rr") StaticRr("static-rr"),
            @SerialName("uri") Uri("uri"),
            @SerialName("url_param") UrlParam("url_param")
        }
    }

    @Serializable
    @SerialName("mode")
    enum class Mode(type: String) {
        @SerialName("unknown") Unknown("unknown"),
        @SerialName("http") HTTP("http"),
        @SerialName("tcp") TCP("tcp"),
        @SerialName("log") LOG("log")
    }
}

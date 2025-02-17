package online.aruka.route_sign_core.backend

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.RequestBody.Companion.toRequestBody
import online.aruka.route_sign_core.frontend.SimpleBind
import online.aruka.route_sign_core.util.request.ConnectionIdentifier
import online.aruka.route_sign_core.util.request.EssentialData
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class SimpleBackend(
    val balance: Balance = Balance(),
    val from: String = "unknown",
    val mode: Mode = Mode.Unknown,
    val name: String = "unknown"
): HAProxyBackend {

    companion object {
        fun get(
            essential: EssentialData
        ): Triple<Int, List<SimpleBackend>, Headers> {
            return Request.getList<SimpleBackend>(
                address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/backends",
                credential = essential.credential
            )
        }
    }

    fun add(
        essential: EssentialData,
        connectionIdentifier: ConnectionIdentifier
    ): Triple<Int, SimpleBind?, Headers> {
        return Request.postSingle(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/backends${connectionIdentifier.toQueryString()}",
            requestBody = Json.encodeToString(this).toRequestBody(Request.APPLICATION_JSON),
        )
    }

    fun delete(
        essential: EssentialData,
        connectionIdentifier: ConnectionIdentifier
    ): Pair<Int, Headers> {
        return Request.deleteSingle(
            address =  "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/backends/${this.name}${connectionIdentifier.toQueryString()}",
        )
    }

    @Serializable
    @SerialName("balance")
    data class Balance(
        val algorithm: Algorithm = Algorithm.Unknown
    ) {
        @Serializable
        @SerialName("algorithm")
        enum class Algorithm(val type: String) {
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
    enum class Mode(val type: String) {
        @SerialName("unknown") Unknown("unknown"),
        @SerialName("http") HTTP("http"),
        @SerialName("tcp") TCP("tcp"),
        @SerialName("log") LOG("log")
    }
}

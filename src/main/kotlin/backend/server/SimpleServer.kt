package online.aruka.backend.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Headers
import okhttp3.OkHttpClient
import online.aruka.util.request.Request
import online.aruka.util.value.BoolExtend

@Serializable
data class SimpleServer(
    @SerialName("send-proxy-v2") val sendProxyV2: BoolExtend = BoolExtend.Disabled,
    val address: String = "",
    val name: String = "unknown",
    val port: Int = -1
): HAProxyBackendServer {
    companion object {
        fun get(
            address: String,
            parent: String,
            apiVersion: String,
            credential: Pair<String, String>
        ): Triple<Int, List<SimpleServer>, Headers> {
            return Request.getList<SimpleServer>(
                address = "$address/$apiVersion/services/haproxy/configuration/backends/$parent/servers",
                client = OkHttpClient(),
                credential = credential
            )
        }

        fun add(
            address: String,
            parent: String,
            apiVersion: String,
            credential: Pair<String, String>
        ): Pair<SimpleServer, Headers> {
            val (_, addedServer, headers) = Request.postSingle<SimpleServer>(
                address = "$address/$apiVersion/service/haproxy/configuration/backends/$parent/servers",
                client = OkHttpClient(),
                credential = credential
            )
            return addedServer!! to headers
        }

        fun delete(
            address: String,
            parent: String,
            serverName: String,
            apiVersion: String,
            credentials: Pair<String, String>,
            forceReload: Boolean = false
        ): Pair<Int, Headers> {
            val preAddress = "$address/$apiVersion/services/haproxy/configuration/backends/$parent/servers/$serverName"
            val parameterApplied = "$preAddress?force_reload=$forceReload"
            return Request.deleteSingle(
                address = parameterApplied,
                client = OkHttpClient(),
                credential = credentials
            )
        }
    }
}
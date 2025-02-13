package online.aruka.backend.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import online.aruka.util.request.Request
import online.aruka.util.value.BoolExtend

@Serializable
data class SimpleServer(
    @SerialName("send-proxy-v2") val sendProxyV2: BoolExtend = BoolExtend.Disabled,
    val address: String = "",
    val name: String = "unknown",
    val port: Int = -1
): HAProxyBackendServer {

    data class ConnectionIdentifier(
        val type: Type,
        val id: String // "version" or "transaction_id". see more -> https://www.haproxy.com/documentation/dataplaneapi/community/?v=v3#post-/services/haproxy/configuration/backends/-parent_name-/servers
    ) {
        enum class Type(val type: String) {
            VERSION("version"),
            TRANSACTION_ID("transaction_id")
        }

        fun toQueryString(isFirst: Boolean = true): String {
            return "${if (isFirst) "?" else "&"}${this.type.type}=${this.id}"
        }
    }

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
            new: SimpleServer,
            connectionIdentifier: ConnectionIdentifier,
            allowCode: Set<Int> = setOf(201, 202),
            ignoreCode: Set<Int> = emptySet(),
            apiVersion: String,
            credential: Pair<String, String>
        ): Pair<SimpleServer?, Headers> {
            val (_, addedServer, headers) = Request.postSingle<SimpleServer>(
                address = "$address/$apiVersion/services/haproxy/configuration/backends/$parent/servers${connectionIdentifier.toQueryString()}",
                client = OkHttpClient(),
                allowCode = allowCode,
                ignoreCode = ignoreCode,
                requestBody = Json.encodeToString(new).toRequestBody(Request.APPLICATION_JSON),
                credential = credential
            )
            return addedServer to headers
        }

        fun delete(
            address: String,
            parent: String,
            serverName: String,
            connectionIdentifier: ConnectionIdentifier,
            allowCode: Set<Int> = setOf(202, 204),
            ignoreCode: Set<Int> = emptySet(),
            apiVersion: String,
            credentials: Pair<String, String>,
            forceReload: Boolean = false
        ): Pair<Int, Headers> {
            val queryApplied = "$address/$apiVersion/services/haproxy/configuration/backends/$parent/servers/$serverName${connectionIdentifier.toQueryString()}&force_reload=$forceReload"
            return Request.deleteSingle(
                address = queryApplied,
                client = OkHttpClient(),
                allowCode = allowCode,
                ignoreCode = ignoreCode,
                credential = credentials
            )
        }
    }
}
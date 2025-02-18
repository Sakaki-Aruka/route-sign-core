package online.aruka.route_sign_core.backend.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.RequestBody.Companion.toRequestBody
import online.aruka.route_sign_core.util.request.ConnectionIdentifier
import online.aruka.route_sign_core.util.request.EssentialData
import online.aruka.route_sign_core.util.request.Request
import online.aruka.route_sign_core.util.value.BoolExtend

@Serializable
data class SimpleServer(
    @SerialName("send-proxy-v2") val sendProxyV2: BoolExtend = BoolExtend.Disabled,
    val address: String = "",
    val name: String = "unknown",
    val port: Int = -1
): HAProxyBackendServer {

    companion object {
        fun get(
            essential: EssentialData,
            parent: String
        ): Triple<Int, List<SimpleServer>, Headers> {
            return Request.getList<SimpleServer>(
                address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/backends/$parent/servers",
                credential = essential.credential
            )
        }
    }

    fun add(
        essential: EssentialData,
        parent: String,
        connectionIdentifier: ConnectionIdentifier,
    ): Pair<SimpleServer?, Headers> {
        val (_, addedServer, headers) = Request.postSingle<SimpleServer>(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/backends/$parent/servers${connectionIdentifier.toQueryString()}",
            requestBody = Json.encodeToString(this).toRequestBody(Request.APPLICATION_JSON),
            credential = essential.credential
        )
        return addedServer to headers
    }

    fun delete(
        essential: EssentialData,
        parent: String,
        connectionIdentifier: ConnectionIdentifier,
    ): Pair<Int, Headers> {
        val queryApplied = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/backends/$parent/servers/${this.name}${connectionIdentifier.toQueryString()}"
        return Request.deleteSingle(address = queryApplied, credential = essential.credential)
    }
}
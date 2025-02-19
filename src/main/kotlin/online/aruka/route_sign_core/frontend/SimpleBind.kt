package online.aruka.route_sign_core.frontend

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.RequestBody.Companion.toRequestBody
import online.aruka.route_sign_core.util.request.ConnectionIdentifier
import online.aruka.route_sign_core.util.request.EssentialData
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class SimpleBind(
    val address: String,
    val port: Int,
    val name: String = "$address:$port"
):HAProxyBind {
    companion object {
        fun get(
            essential: EssentialData,
            frontendName: String,
        ): Triple<Int, List<SimpleBind>, Headers> {
            return Request.getList<SimpleBind>(
                address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/binds",
                credential = essential.credential
            )
        }
    }

    fun add(
        essential: EssentialData,
        frontendName: String,
        connectionIdentifier: ConnectionIdentifier
    ): Triple<Int, SimpleBind?, Headers> {
        return Request.postSingle<SimpleBind>(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/binds${connectionIdentifier.toQueryString()}",
            requestBody = Json.encodeToString(this).toRequestBody(Request.APPLICATION_JSON),
            credential = essential.credential
        )
    }

    fun delete(
        essential: EssentialData,
        frontendName: String,
        connectionIdentifier: ConnectionIdentifier
    ): Pair<Int, Headers> {
        return Request.deleteSingle(
            credential = essential.credential,
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/binds/${this.name}${connectionIdentifier.toQueryString()}",
        )
    }
}
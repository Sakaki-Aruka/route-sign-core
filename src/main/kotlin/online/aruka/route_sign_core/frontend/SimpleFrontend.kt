package online.aruka.route_sign_core.frontend

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.RequestBody.Companion.toRequestBody
import online.aruka.route_sign_core.util.request.ConnectionIdentifier
import online.aruka.route_sign_core.util.request.EssentialData
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class SimpleFrontend(
    @SerialName("default_backend") val defaultBackend: String = "unknown",
    val from: String = "unknown",
    val mode: Mode = Mode.Unknown,
    val name: String = "unknown"
): HAProxyFrontend {

    companion object {
        fun get(
            essential: EssentialData
        ): Triple<Int, List<SimpleFrontend>, Headers> {
            return Request.getList<SimpleFrontend>(
                address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends",
                credential = essential.credential
            )
        }
    }

    fun add(
        essential: EssentialData,
        configurationIdentifier: ConnectionIdentifier
    ): Triple<Int, SimpleFrontend?, Headers> {
        return Request.postSingle(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends${configurationIdentifier.toQueryString()}",
            requestBody = Json.encodeToString(this).toRequestBody(Request.APPLICATION_JSON),
            credential = essential.credential
        )
    }

    fun delete(
        essential: EssentialData,
        configurationIdentifier: ConnectionIdentifier
    ): Pair<Int, Headers> {
        return Request.deleteSingle(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/${this.name}${configurationIdentifier.toQueryString()}",
        )
    }

    fun getBinds(
        essential: EssentialData
    ): Triple<Int, List<SimpleBind>, Headers> {
        return SimpleBind.get(
            essential = essential,
            frontendName = this.name,
        )
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

package online.aruka.route_sign_core.frontend

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Headers
import online.aruka.route_sign_core.util.request.ConnectionIdentifier
import online.aruka.route_sign_core.util.request.EssentialData
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class BackendSwitchingRule(
    @SerialName("cond") val condition: Condition,
    @SerialName("cond_test") val expression: String,
    val index: Int,
    @SerialName("name") val backendName: String
) {

    companion object {
        fun get(
            essential: EssentialData,
            frontendName: String
        ): Triple<Int, List<BackendSwitchingRule>, Headers> {
            return Request.getList<BackendSwitchingRule>(
                address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/backend_switching_rules",
                credential = essential.credential
            )
        }
    }

    fun add(
        essential: EssentialData,
        frontendName: String,
        connectionIdentifier: ConnectionIdentifier,
        index: Int = this.index,
    ): Triple<Int, BackendSwitchingRule?, Headers> {
        return Request.postSingle<BackendSwitchingRule>(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/backend_switching_rules/$index${connectionIdentifier.toQueryString()}",
            credential = essential.credential
        )
    }

    fun delete(
        essential: EssentialData,
        frontendName: String,
        connectionIdentifier: ConnectionIdentifier,
        index: Int = this.index
    ): Pair<Int, Headers> {
        return Request.deleteSingle(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/backend_switching_rules/$index${connectionIdentifier.toQueryString()}",
            credential = essential.credential
        )
    }

    enum class Condition(val type: String) {
        IF("if"),
        UNLESS("unless")
    }
}

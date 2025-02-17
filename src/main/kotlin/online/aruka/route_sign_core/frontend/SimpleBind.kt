package online.aruka.route_sign_core.frontend

import kotlinx.serialization.Serializable
import okhttp3.Headers
import okhttp3.OkHttpClient
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class SimpleBind(
    val address: String,
    val port: Int,
    val name: String = "$address:$port"
):HAProxyBind {
    companion object {
        fun get(
            address: String,
            apiVersion: String,
            frontendName: String,
            credential: Pair<String, String>
        ): Triple<Int, List<SimpleBind>, Headers> {
            return Request.getList<SimpleBind>(
                address = "$address/$apiVersion/services/haproxy/configuration/frontends/$frontendName/binds",
                client = OkHttpClient(),
                credential = credential
            )
        }
    }

    fun add(
        address: String,
        apiVersion: String,
        frontendName: String,
        credential: Pair<String, String>
    ): Triple<Int, SimpleBind?, Headers> {
        return Request.postSingle<SimpleBind>(
            address = "$address/$apiVersion/services/haproxy/configuration/frontends/$frontendName/binds",
            client = OkHttpClient(),
            credential = credential
        )
    }
}
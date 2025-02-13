package online.aruka.frontend

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Headers
import okhttp3.OkHttpClient
import online.aruka.util.request.Request

@Serializable
data class SimpleFrontend(
    @SerialName("default_backend") val defaultBackend: String = "unknown",
    val from: String = "unknown",
    val mode: Mode = Mode.Unknown,
    val name: String = "unknown"
): HAProxyFrontend {

    companion object {
        fun get(
            address: String,
            apiVersion: String,
            credential: Pair<String, String>
        ): Triple<Int, List<SimpleFrontend>, Headers> {
            return Request.getList<SimpleFrontend>(
                address = "$address/$apiVersion/services/haproxy/configuration/frontends",
                client = OkHttpClient(),
                credential = credential
            )
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

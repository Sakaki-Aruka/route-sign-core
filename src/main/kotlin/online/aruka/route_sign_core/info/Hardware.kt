package online.aruka.route_sign_core.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Headers
import okhttp3.OkHttpClient
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class Hardware(
    val api: APIInfo = APIInfo(),
    val system: HAProxySystem = HAProxySystem()
) {

    companion object {
        fun get(
            address: String, //address is pure address e.g.) http://localhost:5555
            apiVersion: String,
            credential: Pair<String, String>
        ): Triple<Int, Hardware, Headers> {
            return Request.getSingle<Hardware>(
                address = "$address/$apiVersion/info",
                client = OkHttpClient(),
                credential = credential
            )
        }
    }

    @Serializable
    @SerialName("api")
    data class APIInfo(
        @SerialName("build_date") val buildDate: String = "unknown",
        val version: String = "unknown",
    )

    @Serializable
    @SerialName("system")
    data class HAProxySystem(
        @SerialName("cpu_info") val cpuInfo: CPU = CPU(),
        val hostname: String = "unknown",
        @SerialName("mem_info") val memory: Memory = Memory(),
        @SerialName("os_string") val os: String = "unknown",
        val time: Long = -1, // = System.currentTimeMillis(),
        val uptime: Long? = -1 // System uptime
    )

    @Serializable
    @SerialName("cpu_info")
    data class CPU(
        val model: String = "unknown",
        @SerialName("num_cpus") val cpus: Int = -1
    )

    @Serializable
    @SerialName("mem_info")
    data class Memory(
        @SerialName("dataplaneapi_memory") val dataPlaneAPIMemory: Long = -1,
        @SerialName("free_memory") val freeMemory: Long = -1,
        @SerialName("total_memory") val totalMemory: Long = -1
    )
}

package online.aruka.route_sign_core.util.request

import okhttp3.OkHttpClient

data class ConnectionIdentifier(
    val type: Type,
    val id: String // "version" or "transaction_id". see more -> https://www.haproxy.com/documentation/dataplaneapi/community/?v=v3#post-/services/haproxy/configuration/backends/-parent_name-/servers
) {

    companion object {
        fun getLatestVersion(
            essential: EssentialData
        ): ConnectionIdentifier {
            val version: String = Request.getCore(
                address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/version",
                client = OkHttpClient(),
                credential = essential.credential
            ).body?.string()
                ?: return ConnectionIdentifier(Type.VERSION, "-1")
            return ConnectionIdentifier(Type.VERSION, version)
        }
    }

    enum class Type(val type: String) {
        VERSION("version"),
        TRANSACTION_ID("transaction_id")
    }

    fun toQueryString(isFirst: Boolean = true): String {
        return "${if (isFirst) "?" else "&"}${this.type.type}=${this.id}"
    }
}

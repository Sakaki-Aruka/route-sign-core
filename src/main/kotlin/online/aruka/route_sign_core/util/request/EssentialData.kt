package online.aruka.route_sign_core.util.request

data class EssentialData(
    val address: String,
    val apiVersion: String,
    val credential: Pair<String, String>
)

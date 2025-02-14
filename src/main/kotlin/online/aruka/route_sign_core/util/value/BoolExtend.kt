package online.aruka.route_sign_core.util.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BoolExtend(val v: Boolean) {
    @SerialName("enabled") Enabled(true),
    @SerialName("disabled") Disabled(false)
}
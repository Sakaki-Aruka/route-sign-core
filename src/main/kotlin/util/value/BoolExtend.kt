package online.aruka.util.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BoolExtend(val v: Boolean) {
    @SerialName("enabled") Enabled(true),
    @SerialName("disabled") Disabled(false)
}
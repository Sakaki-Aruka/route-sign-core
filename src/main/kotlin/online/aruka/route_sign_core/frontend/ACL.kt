package online.aruka.route_sign_core.frontend

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import okhttp3.Headers
import online.aruka.route_sign_core.util.request.ConnectionIdentifier
import online.aruka.route_sign_core.util.request.EssentialData
import online.aruka.route_sign_core.util.request.Request

@Serializable
data class ACL(
    @SerialName("acl_name") val name: String,
    @Transient val criterion: Criterion? = null,
    val value: String,
    @SerialName("criterion") val criterionStr: String = criterion?.flat() ?: "",
) {

    fun add(
        essential: EssentialData,
        frontendName: String,
        connectionIdentifier: ConnectionIdentifier,
        index: Int = 0
    ): Triple<Int, ACL?,Headers> {
        return Request.postSingle<ACL>(
            address = "${essential.address}/${essential.apiVersion}/services/haproxy/configuration/frontends/$frontendName/acls/$index${connectionIdentifier.toQueryString()}",
            credential = essential.credential
        )
    }

    data class Criterion(
        val type: CriterionType,
        val flags: List<HAPRoxyACLFlag>
    ) {
        fun flat(): String {
            return "${type.type} ${flags.joinToString(" ") { it.flat() }}"
        }
    }

    enum class CriterionType(val type: String) {
        SOURCE_ADDRESS("src"),
        DESTINATION_ADDRESS("dst"),
        SOURCE_PORT("src_port"),
        DESTINATION_PORT("dst_port"),
        PROTOCOL("proto"),
        TCP_FLAGS("tcp_flags"),
        CONNECTION_STATE("state"),
        TCP_OPTION("tcp_option"),
        TCP_SEQUENCE("tcp_seq"),
        TCP_ACKNOWLEDGEMENT("tcp_ack")
    }

    interface HAPRoxyACLFlag {
        fun flat(): String
    }

    data class NoArgumentACLFlag(
        val type: ACLFlagType
    ): HAPRoxyACLFlag {
        override fun flat(): String = type.type
    }

    data class ArgumentBasedACLFlag(
        val type: ACLFlagType,
        val argument: String
    ): HAPRoxyACLFlag {
        override fun flat(): String = "${type.type} $argument"
    }

    enum class ACLFlagType(
        val type: String,
        val hasArgument: Boolean
    ) {
        CASE_INSENSITIVE("-i", false),
        MATCH_MODE("-m", true),
        NUMERIC("-n", false),
        FILE_BASED("-f", true),
        DOMAIN("-d", false),
        USER("-u", false),
        PORT("-p", false),
        FORCE_TRUE("-M", false),
        INTEGER_MATCH("-m int", false),
        IP_MATCH("-m ip", false),
        BINARY_MATCH("-m bin", false)
    }
}

import kotlinx.serialization.json.Json
import online.aruka.backend.SimpleBackend
import online.aruka.backend.server.SimpleServer
import online.aruka.frontend.SimpleFrontend
import online.aruka.info.Hardware
import online.aruka.util.value.BoolExtend
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

object Test {

    @Test
    fun test() {
        //debug
        /*
         * required values
         * - server-ip: debug server's ip
         * - username, password: debug server's user and its password
         * - backend-ip-1: test backend server's ip
         */

        val debugCredential: File = Paths.get("./src/test/kotlin/.connect-credential.json").toFile()
        val data: Map<String, String> = Json.decodeFromString<Map<String, String>>(debugCredential.readText())
        val baseAddress = "http://${data["server-ip"]}:5555"
        val apiVersion = "v3"
        val credential = data["username"]!! to data["password"]!!

        println(Hardware.get(baseAddress, apiVersion, credential))
        println(SimpleBackend.get(baseAddress, apiVersion, credential))
        println(SimpleFrontend.get(baseAddress, apiVersion, credential))
        println(SimpleServer.get(baseAddress, "minecraft_server", apiVersion, credential))

        val (_, backends, headers) = SimpleBackend.get(baseAddress, apiVersion, credential)
        val backend = backends.first()
        val existServers: List<SimpleServer> = SimpleServer.get(baseAddress, backend.name, apiVersion, credential).second
        val connectionId = SimpleServer.ConnectionIdentifier(
            SimpleServer.ConnectionIdentifier.Type.VERSION,
            headers["Configuration-Version"]!!
        )

        val newServer = SimpleServer(BoolExtend.Enabled, data["backend-ip-1"]!!, "second", 30000)
        println(Json.encodeToString(newServer))
        SimpleServer.add(
            address = baseAddress,
            parent = backend.name,
            new = newServer,
            connectionIdentifier =  connectionId,
            ignoreCode = setOf(409),
            apiVersion = apiVersion,
            credential = credential)
        val addedServers = SimpleServer.get(baseAddress, backend.name, apiVersion, credential).second

        println(existServers.size != addedServers.size)

        SimpleServer.delete(
            address = baseAddress,
            parent = backend.name,
            serverName = newServer.name,
            connectionIdentifier = connectionId,
            ignoreCode = setOf(200, 202, 204),
            apiVersion = apiVersion,
            credentials = credential)
        val deletedServers = SimpleServer.get(baseAddress, backend.name, apiVersion, credential).second

        println(existServers == deletedServers)
    }
}
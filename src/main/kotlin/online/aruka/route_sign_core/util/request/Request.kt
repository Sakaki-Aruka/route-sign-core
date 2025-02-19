package online.aruka.route_sign_core.util.request

import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

object Request {

    val APPLICATION_JSON: MediaType = "application/json".toMediaType()

    fun getIllegalCodeWarn(
        code: Int,
        allowedCode: Set<Int>,
        body: String,
        headers: Map<String, String> = emptyMap()
    ): String {
        val n = System.lineSeparator()
        var warn = "$n${"-".repeat(10)}$n"
        warn += "code '$code' is not allowed.$n"
        warn += "allowed=$allowedCode$n"
        warn += "body=$body$n"
        warn += "headers=${headers}$n"
        warn += "${"-".repeat(10)}$n"
        return warn
    }

    inline fun <reified T> getSingle(
        address: String,
        client: OkHttpClient = OkHttpClient(),
        headers: Headers? = null,
        credential: Pair<String, String>? = null, // only Basic auth,
        ignoreUnknown: Boolean = true,
        defaultBody: String = "",
        allowCode: Set<Int> = setOf(200)
    ): Triple<Int, T, Headers> {
        val response: Response = getCore(address, client, headers, credential)
        val code: Int = response.code
        val body: String = response.body?.string() ?: defaultBody

        if (code !in allowCode) {
            throw IllegalStateException(getIllegalCodeWarn(code, allowCode, body, response.headers.toMap()))
        }

        val deserializer = Json { ignoreUnknownKeys = ignoreUnknown }
        return Triple(code, deserializer.decodeFromString<T>(body), response.headers)
    }

    inline fun <reified T> getList(
        address: String,
        client: OkHttpClient = OkHttpClient(),
        headers: Headers? = null,
        credential: Pair<String, String>? = null, // only Basic auth,
        ignoreUnknown: Boolean = true,
        defaultBody: String = "",
        allowCode: Set<Int> = setOf(200)
    ): Triple<Int, List<T>, Headers> {
        val response: Response = getCore(address, client, headers, credential)
        val code: Int = response.code
        val body: String = response.body?.string() ?: defaultBody

        if (code !in allowCode) {
            throw IllegalStateException(getIllegalCodeWarn(code, allowCode, body, response.headers.toMap()))
        }

        val deserializer = Json { ignoreUnknownKeys = ignoreUnknown }
        return Triple(code, deserializer.decodeFromString<List<T>>(body), response.headers)
    }

    fun getCore(
        address: String,
        client: OkHttpClient,
        headers: Headers? = null,
        credential: Pair<String, String>? = null
    ): Response {
        val request = Request.Builder()
            .url(address)
            .let { builder ->
                headers?.let { h -> builder.headers(h) }
                credential?.let { (name, password) ->
                    builder.header(
                        "Authorization",
                        Credentials.basic(name, password)
                    )
                }
                builder
            }
            .build()

        return client.newCall(request).execute()
    }

    fun postCore(
        address: String,
        client: OkHttpClient,
        requestBody: RequestBody? = null,
        headers: Headers? = null,
        credential: Pair<String, String>? = null
    ): Response {
        val request = Request.Builder()
            .url(address)
            .let { builder ->
                headers?.let { h -> builder.headers(h) }
                credential?.let { (name, password) ->
                    builder.header(
                        "Authorization",
                        Credentials.basic(name, password)
                    )
                }
                requestBody?.let { r -> builder.post(r) }
                builder
            }
            .build()

        return client.newCall(request).execute()
    }

    inline fun <reified T> postSingle(
        address: String,
        client: OkHttpClient = OkHttpClient(),
        headers: Headers? = null,
        requestBody: RequestBody? = null,
        credential: Pair<String, String>? = null,
        ignoreUnknown: Boolean = true,
        defaultResponseBody: String = "",
        allowCode: Set<Int> = setOf(201, 202),
        ignoreCode: Set<Int> = emptySet(),
        buildFromResponse: Boolean = true // true = decode "T" from a response. when false doesn't and result will be Triple<Int, null, Headers?>
    ): Triple<Int, T?, Headers> {
        val response: Response = postCore(address, client, requestBody, headers, credential)
        val code: Int = response.code
        val body: String = response.body?.string() ?: defaultResponseBody
        val responseHeaders: Headers = response.headers

        if (code in ignoreCode) {
            return Triple(code, null, responseHeaders)
        } else if (code !in allowCode) {
            throw IllegalStateException(getIllegalCodeWarn(code, allowCode, body, responseHeaders.toMap()))
        }

        val deserializer = Json { ignoreUnknownKeys = ignoreUnknown }
        val v: T? =
            if (buildFromResponse) deserializer.decodeFromString<T>(body)
            else null
        return Triple(code, v, responseHeaders)
    }

    inline fun <reified T> postList(
        address: String,
        client: OkHttpClient = OkHttpClient(),
        headers: Headers? = null,
        requestBody: RequestBody? = null,
        credential: Pair<String, String>? = null,
        ignoreUnknown: Boolean = true,
        defaultResponseBody: String = "",
        allowCode: Set<Int> = setOf(201, 202),
        ignoreCode: Set<Int> = emptySet(),
        buildFromResponse: Boolean = true
    ): Triple<Int, List<T>?, Headers> {
        val response: Response = postCore(address, client, requestBody, headers, credential)
        val code: Int = response.code
        val body: String = response.body?.string() ?: defaultResponseBody
        val responseHeaders: Headers = response.headers

        if (code in ignoreCode) {
            return Triple(code, null, responseHeaders)
        } else if (code !in allowCode) {
            throw IllegalStateException(getIllegalCodeWarn(code, allowCode, body, response.headers.toMap()))
        }

        val deserializer = Json { ignoreUnknownKeys = ignoreUnknown }
        val v: List<T>? =
            if (buildFromResponse) deserializer.decodeFromString<List<T>>(body)
            else null
        return Triple(code, v, responseHeaders)
    }

    fun deleteCore(
        address: String,
        client: OkHttpClient,
        requestBody: RequestBody? = null,
        headers: Headers? = null,
        credential: Pair<String, String>? = null
    ): Response {
        val request = Request.Builder()
            .url(address)
            .let { builder ->
                headers?.let { h -> builder.headers(h) }
                credential?.let { (name, password) ->
                    builder.header(
                        "Authorization",
                        Credentials.basic(name, password)
                    )
                }
                builder
            }
            .delete(requestBody)
            .build()

        return client.newCall(request).execute()
    }

    fun deleteSingle(
        address: String,
        client: OkHttpClient = OkHttpClient(),
        headers: Headers? = null,
        requestBody: RequestBody? = null,
        credential: Pair<String, String>? = null,
        allowCode: Set<Int> = setOf(200, 202, 204),
        ignoreCode: Set<Int> = emptySet()
    ): Pair<Int, Headers> {
        val response: Response = deleteCore(address, client, requestBody, headers, credential)
        val code: Int = response.code
        val body: String = response.body?.string() ?: ""
        val responseHeaders: Headers = response.headers

        if (code in ignoreCode) {
            return code to responseHeaders
        } else if (code !in allowCode) {
            throw IllegalStateException(getIllegalCodeWarn(code, allowCode, body, responseHeaders.toMap()))
        }
        return code to responseHeaders
    }
}
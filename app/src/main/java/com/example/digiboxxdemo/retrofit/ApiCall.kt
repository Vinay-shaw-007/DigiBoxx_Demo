package com.example.digiboxxdemo.retrofit

import android.util.Log
import com.example.digiboxxdemo.BuildConfig
import com.example.digiboxxdemo.Constant
import com.example.digiboxxdemo.db.UserAuthManager
import com.example.digiboxxdemo.ui.home.HomeFragment
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.JsonObject
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiCall @Inject constructor() {
    private val interceptor = HttpLoggingInterceptor()
    var httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    @Inject
    lateinit var userAuthManager: UserAuthManager

    init {

        httpClient.addInterceptor(JWTInterceptor())

        httpClient.addInterceptor(JwtDecryptionInterceptor())



        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.connectTimeout(1, TimeUnit.MINUTES)
        httpClient.callTimeout(1, TimeUnit.MINUTES)
        httpClient.readTimeout(1, TimeUnit.MINUTES)
        httpClient.writeTimeout(2, TimeUnit.MINUTES)

        httpClient.addNetworkInterceptor(StethoInterceptor())

        httpClient.addInterceptor(interceptor)

        httpClient.addInterceptor(Interceptor { chain ->
            var request = chain.request()
            val requestBuilder: Request.Builder = request.newBuilder()
            var requestBody = request.body
            if (!userAuthManager.getToken().isNullOrBlank()) {
                if (requestBody?.contentType()?.subtype.equals(
                        "json",
                        ignoreCase = true
                    )
                ) requestBody = processApplicationJsonRequestBody(requestBody)
                requestBuilder.method(request.method, requestBody).header(
                    "authorization",
                    "Bearer ${userAuthManager.getToken()}"
                )
            }

            // add header in request
            Timber.tag("USER_TOKEN").d(userAuthManager.getToken().toString())


//                requestBuilder.addHeader("referer", "https://apptest.digiboxx.com")
            requestBuilder.addHeader("referer", "com.liqvd.digibox.test")
            request = requestBuilder.build()
            chain.proceed(request)
        })
    }

    private fun processApplicationJsonRequestBody(
        requestBody: RequestBody?
    ): RequestBody? {
        val customReq = requestBody.bodyToString()
        try {
            val obj = JSONObject(customReq)
//            obj.put(PreKey.email, emailId) //Email Activation we are passing email which create conflict i.e we commented
//            obj.put(PreKey.organization_id, organizationId)
            return obj.toString().toRequestBody(requestBody!!.contentType())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }


    private fun RequestBody?.bodyToString(): String {
        try {
            val buffer = Buffer()
            if (this != null) {
                this.writeTo(buffer)
            } else {
                return ""
            }
            buffer.close()
            return buffer.readUtf8()
        } catch (e: Exception) {
            return ""
        }
    }

    /* JWT encryption of request body in Interceptor */
    inner class JWTInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var requestBody = request.body

//            val token: String = CommonUtils().fetchPreference(DigiboxxApplication.appContext, PreKey.Token) ?: ""
            val token: String = userAuthManager.getToken() ?: ""
            val apiEndpoint = request.url.toString().split("/").run {
                getOrNull(size - 2)
            } ?: ""
            // SecureKey with apiEndpoint_token
            val secretKey = apiEndpoint + "_" + token

            var requestBodyString = request.body?.bodyToString() ?: ""
//            val payload = JSONObject(jsonPayload).toMap()
            Timber.tag("api_endpoint_log").d("actual payload: $requestBodyString")

            if (arrayOf("POST","PUT").contains(request.method)) {
                val contentType = request.body?.contentType()?.type

                // if contentType is multipart convert FormData to JsonString
                if (contentType == "multipart") {
                    val json = JSONObject()
                    val requestMultipartBody = request.body as MultipartBody

                    for (part in requestMultipartBody.parts) {

                        val disposition = part.headers?.get("Content-Disposition")
                        val key = disposition?.substringAfterLast("name=")?.removeSurrounding("\"") ?: ""
                        val value = part.body.bodyToString()

                        json.put(key, value)
                    }
                    val jsonString = json.toString()
//                    Timber.tag("api_endpoint_log").d("new jsonString: $jsonString")

                    requestBodyString = jsonString

                }

//                Timber.tag("api_endpoint_log").d("payload: $payload")
                Timber.tag("api_endpoint_log").d("payload jsonPayload: $requestBodyString")

                if (requestBodyString.isNotEmpty()) {
                    val encryptedPayload = createJwt(secretKey, requestBodyString)
                    val jsonData = """
                    {
                        "data": "$encryptedPayload"
                    }
                """
                    Timber.tag("api_endpoint_log").d("encrypted payload: $jsonData")
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("data", encryptedPayload)

                    val data = jsonObject.toString()
                        .toRequestBody("application/json; charset-urf-8".toMediaTypeOrNull())
                    val newRequest = request.newBuilder().run{
                        if(request.method=="POST") post(data) else put(data)
                        build()
                    }
                    return chain.proceed(newRequest)
                } else {
                    val newRequest = request.newBuilder()
                        .build()
                    return chain.proceed(newRequest)
                }

            } else if (arrayOf("GET","DELETE").contains(request.method)) {

                return if (request.url.querySize > 0) {
                    val newRequest = urlEncrypt(request, secretKey, request.method)
                    chain.proceed(newRequest)
                } else {
                    if (request.method == "DELETE") {
                        val newRequest = request.newBuilder()
                            .delete()
                            .build()
                        chain.proceed(newRequest)
                    } else {
                        val newRequest = request.newBuilder()
                            .build()
                        chain.proceed(newRequest)
                    }
                }

            } else {
                val newRequest = request.newBuilder()
                    .build()
                return chain.proceed(newRequest)
            }


        }

        private fun urlEncrypt(request: Request, secretKey: String, method: String): Request {
            val json = JSONObject()

            request.url.queryParameterNames.forEach { name ->
                val queryValue = request.url.queryParameter(name)
                Timber.tag("api_endpoint_log").d("queryParameterNames: $name queryValue: $queryValue")
                json.put(name, queryValue)
            }

            val encryptedPayload = createJwt(secretKey, json.toString())
//                Timber.tag("api_endpoint_log").d("encryptedPayload: $encryptedPayload")

            val url =
                Constant.BASE_URL + request.url.pathSegments[0] + "/" + request.url.pathSegments[1] + "/" + "?data=$encryptedPayload"

            Timber.tag("api_endpoint_log").d("queryParameterNames: ${request.url.querySize}")

            return if (method == "DELETE") {
                request.newBuilder()
                    .url(url)
                    .delete()
                    .build()
            } else {
                request.newBuilder()
                    .url(url)
                    .build()
            }
        }

        private fun createJwt(secretKey: String, payload: String): String {
            return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.toByteArray())
                .setPayload(payload)
                .compact()
        }

        private fun createJwtWithClaims(secretKey: String, claims: Claims): String {

            val cal = Calendar.getInstance()
            cal.time = Date()
            val myCurDate = cal.time

//            Timber.tag("api_endpoint_log").d("current time: ${myCurDate.time}")

//            100s addition
            cal[Calendar.MILLISECOND] = cal[Calendar.MILLISECOND] + (1000 * 100)
//            convert to date
            val myDate = cal.time
//            Timber.tag("api_endpoint_log").d("time: ${myDate.time}")

            return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.toByteArray())
                .setExpiration(myDate)
                .setNotBefore(myCurDate)
                .setIssuedAt(Date())
                .setClaims(claims)
                .compact()
        }

    }

    /* JWT decryption of response in Interceptor */
    inner class JwtDecryptionInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            // Call the next interceptor or the endpoint
            val response = chain.proceed(request)

//            val token: String = CommonUtils().fetchPreference(DigiboxxApplication.appContext, PreKey.Token) ?: ""
            val token: String = userAuthManager.getToken() ?: ""
            val apiEndpoint = request.url.toString().split("/").run {
                getOrNull(size - 2)
            } ?: ""
            // SecureKey with apiEndpoint_token
            val secretKey = apiEndpoint + "_" + token

            // Check if the response body is encrypted
            val contentType = response.body?.contentType()?.toString()

            if (contentType != null && contentType.contains("application/json")) {

                // Decrypt the response body
                val responseBodyString = response.body?.string() ?: ""
                val decryptedBody = decryptJwt(responseBodyString, secretKey)

                // Create a new response object with the decrypted body
                val newBody = decryptedBody.toResponseBody(response.body?.contentType())
                return response.newBuilder().body(newBody).build()
            }

            return response
        }

    }

    // Decrypt Jwt algorithm
    private fun decryptJwt(jwt: String, secret: String): String {
        // perform the decryption here
        try {
            val obj = JSONObject(jwt)
            val data = obj.getString("data")

            val encryptedStr = data.split(".")
            val encryptedStrSplit = encryptedStr[1]

            // decrypt using Base64.decode
            val decodeResponse = android.util.Base64.decode(encryptedStrSplit, android.util.Base64.URL_SAFE).toString(Charsets.UTF_8)
            Timber.tag("OK").d("decodeResponse: $decodeResponse")

            return decodeResponse

/*            // Decode using Jwts.parser()
            val claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(apiKey.getSecret()))
                .parseClaimsJws(data).body
            return claims.subject*/

        } catch (e: Exception) {
            Timber.tag("JwtDecryption").d("Json Exception: ${e.printStackTrace()}")
            return ""
        }
    }
}
package com.example.digiboxxdemo.retrofit

import android.util.Log
import com.example.digiboxxdemo.BuildConfig
import com.example.digiboxxdemo.db.UserAuthManager
import com.example.digiboxxdemo.ui.home.HomeFragment
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiCall @Inject constructor() {
    private val interceptor = HttpLoggingInterceptor()
    var httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    @Inject
    lateinit var userAuthManager: UserAuthManager

    init {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.connectTimeout(1, TimeUnit.MINUTES)
        httpClient.callTimeout(1, TimeUnit.MINUTES)
        httpClient.readTimeout(1, TimeUnit.MINUTES)
        httpClient.writeTimeout(2, TimeUnit.MINUTES)

        httpClient.addNetworkInterceptor(StethoInterceptor())

        httpClient.addInterceptor(Interceptor { chain ->
            var request = chain.request()
            var requestBody = request.body
            if (userAuthManager.getToken() != null) {
                if (requestBody?.contentType()?.subtype.equals(
                        "json",
                        ignoreCase = true
                    )
                ) requestBody = processApplicationJsonRequestBody(requestBody)
            }

            // add header in request
            val requestBuilder: Request.Builder = request.newBuilder()
            requestBuilder.method(request.method, requestBody).header(
                "authorization",
                "Bearer ${userAuthManager.getToken()}"
            )
//                requestBuilder.addHeader("referer", "https://apptest.digiboxx.com")
            requestBuilder.addHeader("referer", "com.liqvd.digibox.test")
            request = requestBuilder.build()
            chain.proceed(request)
        })

//        httpClient.addInterceptor(Interceptor { chain ->
//            var request = chain.request()
//            var requestBody = request.body
//            if (requestBody?.contentType()?.subtype.equals(
//                    "json",
//                    ignoreCase = true
//                )
//            ) requestBody = processApplicationJsonRequestBody(requestBody)
//            // add header in request
//            val requestBuilder: Request.Builder = request.newBuilder()
//
//            if (userAuthManager.getStatusCode() == 200) {
//                Log.d(HomeFragment.TAG, "Bearer ${userAuthManager.getToken()}")
//                requestBuilder.addHeader("authorization", "Bearer ${userAuthManager.getToken()}")
//            }
//            requestBuilder.addHeader("referer", "com.liqvd.digibox.dev")
//
//            request = requestBuilder.build()
//            chain.proceed(request)
////            requestBuilder.addHeader("referer", "https://apptest.digiboxx.com")
//        })
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
}
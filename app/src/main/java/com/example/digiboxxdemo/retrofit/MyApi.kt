package com.example.digiboxxdemo.retrofit

import com.example.digiboxxdemo.LoginResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MyApi {

    @POST("dgb_user_func/dgb_user_details_fn/")
    suspend fun getLoginResponse(@Body jsonObject: JsonObject): Response<LoginResponse>
}
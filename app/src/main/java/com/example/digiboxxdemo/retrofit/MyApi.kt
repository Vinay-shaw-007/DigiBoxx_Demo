package com.example.digiboxxdemo.retrofit

import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.model.Files
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface MyApi {
    @POST("dgb_login_func/dgb_user_login_post_fn/")
    suspend fun getLoginResponse(@Body jsonObject: JsonObject): LoginResponse
    @POST("dgb_asset_access_func/dgb_user_landing_page_post_fn/")
    suspend fun getLandingResponse(@Body jsonObject: JsonObject): Files

    @POST("dgb_user_func/dgb_user_logout_fn/")
    suspend fun getUserLogged(@Body jsonObject: JsonObject): LoginResponse
}
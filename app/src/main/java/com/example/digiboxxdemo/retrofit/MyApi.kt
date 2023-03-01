package com.example.digiboxxdemo.retrofit

import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.model.Files
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MyApi {
    @POST("dgb_login_func/dgb_user_login_post_fn/")
    suspend fun getLoginResponse(@Body jsonObject: JsonObject): Response<LoginResponse>
    @POST("dgb_asset_access_func/dgb_user_landing_page_post_fn/")
    suspend fun getUserDetailsResponse(): Response<Files>
}
package com.example.digiboxxdemo.retrofit

import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.model.Files
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface MyApi {
    @POST("dgb_login_func/dgb_user_login_post_fn/")
    suspend fun getLoginResponse(@Body jsonObject: JsonObject): LoginResponse

    @POST("dgb_asset_access_func/dgb_user_landing_page_post_fn/")
    suspend fun getLandingResponse(@Body jsonObject: JsonObject): Files

    @POST("dgb_user_func/dgb_user_logout_fn/")
    suspend fun getUserLogged(@Body jsonObject: JsonObject): LoginResponse

    @POST("dgb_user_func/dgb_check_user_limit_fn/")
    suspend fun checkUserLimit(@Body jsonObject: JsonObject): JsonObject

    @POST("dgb_asset_file_mgmt_func/dgb_get_minio_url_fn/")
    suspend fun getMinioUrlAndFileID(@Body requestBody: RequestBody): JsonObject

    @POST("dgb_asset_file_mgmt_func/dgb_user_file_upload_fn/")
    suspend fun fileUploaded(@Body requestBody: RequestBody): JsonObject
}

interface Client {
    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Body requestBody: RequestBody
    ): retrofit2.Response<Unit>
}
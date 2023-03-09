package com.example.digiboxxdemo.repository

import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.retrofit.Client
import com.example.digiboxxdemo.retrofit.MyApi
import com.example.digiboxxdemo.retrofit.Resource
import com.example.digiboxxdemo.retrofit.SimpleResource
import com.google.android.exoplayer2.util.Log
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DigiRepo @Inject constructor(private val myApi: MyApi, private val client: Client) {

    companion object {
        const val TAG = "DigiRepo"
    }

    suspend fun getLoginResponse(
        email: String,
        password: String,
        session: Int
    ): Resource<LoginResponse> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("logUsername", email)
        jsonObject.addProperty("logUserpass", password)
        jsonObject.addProperty("force_delete_sessions", session)
        jsonObject.addProperty("mobile_application", 1)

        return try {
            val response = myApi.getLoginResponse(jsonObject)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }

    }

    suspend fun getUserDetails(type: String): Resource<Files> {
        val jsonObject = JsonObject()

        jsonObject.addProperty("page_no", 1)
        jsonObject.addProperty("type", type)
        jsonObject.addProperty("view_type", "created_by")
        jsonObject.addProperty("filter_by_size", "")
        jsonObject.addProperty("filter_by_name", "")
        jsonObject.addProperty("filter_by_format", "")
        jsonObject.addProperty("filter_format_type", "[]")
        jsonObject.addProperty("filter_by_tags", "[]")
        jsonObject.addProperty("filter_by_date", "")

        return try {
            val response = myApi.getLandingResponse(jsonObject)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    suspend fun getUserLogged(userToken: String): Resource<LoginResponse> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_token", userToken)
        return try {
            val response = myApi.getUserLogged(jsonObject)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    suspend fun checkUserLimit(): Resource<JsonObject> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("is_managed", "N")
        return try {
            val response = myApi.checkUserLimit(jsonObject)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    suspend fun getMinioUrlAndFileID(
        fileTitle: String,
        fileTypes: String,
        fileSize: Long,
        fileName: String
    ): Resource<JsonObject> {
        val requestBody = MultipartBody.Builder()



        requestBody.addFormDataPart("file_title", fileTitle)
        requestBody.addFormDataPart("file_type", fileTypes)
        requestBody.addFormDataPart("file_size", fileSize.toString())
        requestBody.addFormDataPart("folder_session", "0")
        requestBody.addFormDataPart("folder_is_resource", "0")
        requestBody.addFormDataPart("file_name", fileName)
        requestBody.addFormDataPart("digiPath", "")
        requestBody.addFormDataPart("parent_folder", "0")
        requestBody.addFormDataPart("replace_file", "0")


        val multiPartBody = requestBody.setType(MultipartBody.FORM).build()

        return try {
            val response = myApi.getMinioUrlAndFileID(multiPartBody)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    suspend fun uploadFile(url: String, requestBody: RequestBody): SimpleResource {
        return try {
            client.uploadFile(url, requestBody)
            Log.d(TAG, "success uploading")
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Log.d(TAG, "Error uploading")
            Resource.Error(message = e.message, code = 500)
        } catch (e: IOException) {
            Log.d(TAG, "Error uploading")
            Resource.Error(message = e.message, code = 400)
        }
    }

    suspend fun fileUploaded(
        fileTitle: String,
        tagDetails: String,
        lastModified: String,
        fileTitle1: String,
        fileID: String,
        fileDesc: String,
        fileSize: String
    ): Resource<JsonObject> {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("chooseFile1", fileTitle)
            .addFormDataPart("tag_details1", tagDetails)
            .addFormDataPart("user_details", JSONObject().toString())
            .addFormDataPart("file_title1", fileTitle1)
            .addFormDataPart("file_id", fileID)
            .addFormDataPart("file_description1", fileDesc)
            .addFormDataPart("file_size1", fileSize)
            .addFormDataPart("file_digiPath", "")
            .addFormDataPart("folder_session", "0")
            .addFormDataPart("lastModified", lastModified)
            .addFormDataPart("folder_is_resource", "0")
            .addFormDataPart("folder_is_downloadable", "1")
            .addFormDataPart("resource_array", JSONArray().toString())
            .addFormDataPart("downloadable_array", JSONArray().toString())
            .addFormDataPart("new_folder_name", "")
            .addFormDataPart("folder_color", "")
            .addFormDataPart("replace_file", "2")
            .build()


//        val multiPartBody = requestBody.setType(MultipartBody.FORM).build()

        return try {
            val response = myApi.fileUploaded(requestBody)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

}
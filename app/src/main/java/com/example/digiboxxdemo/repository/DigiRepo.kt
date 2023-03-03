package com.example.digiboxxdemo.repository

import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.retrofit.MyApi
import com.example.digiboxxdemo.retrofit.Resource
import com.google.gson.JsonObject
import javax.inject.Inject

class DigiRepo @Inject constructor(private val myApi: MyApi) {

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
        } catch(e: Exception) {
            Resource.Error(e.message)
        }

    }

    /*
      "page_no": 1,
      "type": "file",
      "view_type": "created_by",
      "filter_by_size": "",
      "filter_by_name": "",
      "filter_by_format": "",
      "filter_format_type": "[]",
      "filter_by_tags": "[]",
      "filter_by_date": "",
     */

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
        } catch(e: Exception) {
            Resource.Error(e.message)
        }
    }

    suspend fun getUserLogged(userToken: String): Resource<LoginResponse> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_token", userToken)
        return try {
            val response = myApi.getUserLogged(jsonObject)
            Resource.Success(response)
        } catch(e: Exception) {
            Resource.Error(e.message)
        }
    }
}
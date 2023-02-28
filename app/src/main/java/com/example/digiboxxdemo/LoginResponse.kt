package com.example.digiboxxdemo

data class LoginResponse(
    val creator: Int,
    val digispace: String,
    val email: String,
    val gst_no: String,
    val industry: String,
    val is_first_time: Int,
    val message: String,
    val organization_id: String,
    val package_type: Int,
    val role_id: Int,
    val status: String,
    val status_code: Int,
    val storage_consumed: Int,
    val token: String,
    val total_storage_allowed: Long,
    val user_id: Int
)
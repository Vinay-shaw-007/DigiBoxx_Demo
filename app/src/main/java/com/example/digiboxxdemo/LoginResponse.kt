package com.example.digiboxxdemo

data class LoginResponse(
    val creator: Int? = 0,
    val digispace: String? = "",
    val email: String? = "",
    val gst_no: String? = "",
    val industry: String? = "",
    val is_first_time: Int? = 0,
    val message: String? = "",
    val organization_id: String? = "",
    val package_type: Int? = 0,
    val role_id: Int? = 0,
    val status: String? = "",
    val status_code: Int? = 0,
    val storage_consumed: Int? = 0,
    val token: String? = "",
    val total_storage_allowed: Long? = 0,
    val user_id: Int? = 0
)
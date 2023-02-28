package com.example.digiboxxdemo

data class LoginResponse(
    val address: String,
    val company_logo: String,
    val company_logo_shape: String,
    val data: String,
    val package_type: String,
    val package_type_id: Int,
    val plan_type: String,
    val profile_image: String,
    val status: String,
    val status_code: Int,
    val subscription_expiry: String
)
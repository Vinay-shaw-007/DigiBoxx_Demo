package com.example.digiboxxdemo.db

import android.content.Context
import com.example.digiboxxdemo.Constant.PREF_USER_AUTH
import com.example.digiboxxdemo.Constant.USER_CREATOR
import com.example.digiboxxdemo.Constant.USER_DIGISPACE
import com.example.digiboxxdemo.Constant.USER_EMAIL
import com.example.digiboxxdemo.Constant.USER_GST_NO
import com.example.digiboxxdemo.Constant.USER_INDUSTRY
import com.example.digiboxxdemo.Constant.USER_IS_FIRST_TIME
import com.example.digiboxxdemo.Constant.USER_MESSAGE
import com.example.digiboxxdemo.Constant.USER_ORGANIZATION_ID
import com.example.digiboxxdemo.Constant.USER_PACKAGE_TYPE
import com.example.digiboxxdemo.Constant.USER_ROLE_ID
import com.example.digiboxxdemo.Constant.USER_STATUS
import com.example.digiboxxdemo.Constant.USER_STATUS_CODE
import com.example.digiboxxdemo.Constant.USER_STORAGE_CONSUMED
import com.example.digiboxxdemo.Constant.USER_TOKEN
import com.example.digiboxxdemo.Constant.USER_TOTAL_STORAGE_ALLOWED
import com.example.digiboxxdemo.Constant.USER_USER_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserAuthManager @Inject constructor(@ApplicationContext context: Context) {

    private var pref = context.getSharedPreferences(PREF_USER_AUTH, Context.MODE_PRIVATE)

    private val editor = pref.edit()

    fun saveStatus(data: String?) {
        editor.putString(USER_STATUS, data).apply()
    }
    fun getStatus(): String? {
        return pref.getString(USER_STATUS, null)
    }

    fun saveMessage(data: String?) {
        editor.putString(USER_MESSAGE, data).apply()
    }
    fun getMessage(): String? {
        return pref.getString(USER_MESSAGE, null)
    }

    fun saveToken(data: String?) {
        editor.putString(USER_TOKEN, data).apply()
    }
    fun getToken(): String? {
        return pref.getString(USER_TOKEN, null)
    }

    fun saveEmail(data: String?) {
        editor.putString(USER_EMAIL, data).apply()
    }
    fun getEmail(): String? {
        return pref.getString(USER_EMAIL, null)
    }

    fun saveOrganizationID(data: String?) {
        editor.putString(USER_ORGANIZATION_ID, data).apply()
    }
    fun getOrganizationID(): String? {
        return pref.getString(USER_ORGANIZATION_ID, null)
    }

    fun saveRoleID(data: Int?) {
        if (data != null) {
            editor.putInt(USER_ROLE_ID, data).apply()
        }
    }
    fun getRoleID(): Int {
        return pref.getInt(USER_ROLE_ID, -1)
    }

    fun saveTotalStorageAllowed(data: Long?) {
        if (data != null) {
            editor.putLong(USER_TOTAL_STORAGE_ALLOWED, data).apply()
        }
    }
    fun getTotalStorageAllowed(): Long {
        return pref.getLong(USER_TOTAL_STORAGE_ALLOWED, -1)
    }

    fun saveIsFirstTime(data: Int?) {
        if (data != null) {
            editor.putInt(USER_IS_FIRST_TIME, data).apply()
        }
    }
    fun getIsFirstTime(): Int {
        return pref.getInt(USER_IS_FIRST_TIME, -1)
    }

    fun savePackageType(data: Int?) {
        if (data != null) {
            editor.putInt(USER_PACKAGE_TYPE, data).apply()
        }
    }
    fun getPackageType(): Int {
        return pref.getInt(USER_PACKAGE_TYPE, -1)
    }

    fun saveDigiSpace(data: String?) {
        editor.putString(USER_DIGISPACE, data).apply()
    }
    fun getDigiSpace(): String? {
        return pref.getString(USER_DIGISPACE, null)
    }

    fun saveIndustry(data: String?) {
        editor.putString(USER_INDUSTRY, data).apply()
    }
    fun getIndustry(): String? {
        return pref.getString(USER_INDUSTRY, null)
    }

    fun saveGstNo(data: String?) {
        editor.putString(USER_GST_NO, data).apply()
    }
    fun getGstNo(): String? {
        return pref.getString(USER_GST_NO, null)
    }

    fun saveUserID(data: Int?) {
        if (data != null) {
            editor.putInt(USER_USER_ID, data).apply()
        }
    }
    fun getUserID(): Int {
        return pref.getInt(USER_USER_ID, -1)
    }

    fun saveStorageConsumed(data: Int?) {
        if (data != null) {
            editor.putInt(USER_STORAGE_CONSUMED, data).apply()
        }
    }
    fun getStorageConsumed(): Int {
        return pref.getInt(USER_STORAGE_CONSUMED, -1)
    }

    fun saveCreator(data: Int?) {
        if (data != null) {
            editor.putInt(USER_CREATOR, data).apply()
        }
    }
    fun getCreator(): Int {
        return pref.getInt(USER_CREATOR, -1)
    }

    fun saveStatusCode(data: Int?) {
        if (data != null) {
            editor.putInt(USER_STATUS_CODE, data).apply()
        }
    }
    fun getStatusCode(): Int {
        return pref.getInt(USER_STATUS_CODE, -1)
    }


}
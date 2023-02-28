package com.example.digiboxxdemo

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.digiboxxdemo.databinding.ActivityMainBinding
import com.example.digiboxxdemo.retrofit.MyApi
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var myApi: MyApi

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtEmailAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmailValid = validateEmail(binding.edtEmailAddress)
                val isPasswordValid = validatePassword(binding.edtPassword)
                binding.btnLogin.isEnabled = isEmailValid && isPasswordValid
            }
        })

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmailValid = validateEmail(binding.edtEmailAddress)
                val isPasswordValid = validatePassword(binding.edtPassword)
                binding.btnLogin.isEnabled = isEmailValid && isPasswordValid
            }
        })

        binding.btnLogin.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("logUsername", binding.edtEmailAddress.toString())
                jsonObject.addProperty("logUserpass", binding.edtPassword.toString())
                jsonObject.addProperty("force_delete_sessions", 0)

                val response = myApi.getLoginResponse(
                    jsonObject
                )
                if (response.isSuccessful) {
                    val jwt = response.body()
                    Log.d("APIResponse", jwt.toString())
                }
            }
        }
    }

    private fun validateEmail(edtEmailAddress: EditText): Boolean {
        if (TextUtils.isEmpty(edtEmailAddress.text)) {
            edtEmailAddress.error = "Email address is required"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.text.toString()).matches()) {
            edtEmailAddress.error = "Invalid email address"
            return false
        }
        return true
    }

    private fun validatePassword(edtPassword: EditText): Boolean {
        if (TextUtils.isEmpty(edtPassword.text)) {
            edtPassword.error = "Password is required"
            return false
        }
        val password: String = edtPassword.text.toString()
        val passwordPattern =
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$"
        val pattern: Pattern = Pattern.compile(passwordPattern)
        if (!pattern.matcher(password).matches()) {
            edtPassword.error =
                "Password should be at least 6 characters long and contain at least one uppercase letter, one number, and one special character"
            return false
        }
        return true

    }
}
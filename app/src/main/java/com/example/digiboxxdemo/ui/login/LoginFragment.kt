package com.example.digiboxxdemo.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.R
import com.example.digiboxxdemo.databinding.FragmentLoginBinding
import com.example.digiboxxdemo.databinding.FragmentNotificationsBinding
import com.example.digiboxxdemo.db.UserAuthManager
import com.example.digiboxxdemo.retrofit.MyApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var myApi: MyApi

    @Inject
    lateinit var userAuthManager: UserAuthManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.GONE

        binding.edtEmailAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnLogin.isEnabled = validateEmail(binding.edtEmailAddress) && validatePassword(binding.edtPassword)
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
                binding.btnLogin.isEnabled = validateEmail(binding.edtEmailAddress) && validatePassword(binding.edtPassword)
            }
        })

        binding.btnLogin.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("logUsername", binding.edtEmailAddress.text.toString())
                    jsonObject.addProperty("logUserpass", binding.edtPassword.text.toString())
                    jsonObject.addProperty("force_delete_sessions", 0)

                    val response = myApi.getLoginResponse(
                        jsonObject
                    )
                    Log.d("APIResponse", response.toString())
                    if (response.isSuccessful) {
                        val loginResponse = response.body()!!
                        when (loginResponse.status_code) {
                            400 -> {
                                withContext(Dispatchers.Main) {
                                    binding.edtEmailAddress.error = loginResponse.message
                                }
                            }
                            403 -> {
                                withContext(Dispatchers.Main) {
                                    binding.edtPassword.error = loginResponse.message
                                }
                            }
                            200 -> {
                                saveDataIntoSharedPreference(loginResponse)
                                val action = LoginFragmentDirections.actionLoginFragmentToNavigationHome()
                                findNavController().navigate(action)
                                Log.d("APIResponse", loginResponse.toString())
                            }
                        }
                    } else {
                        Log.d("APIResponse", response.errorBody().toString())
                    }
                }
            }
        }
    }

    private fun saveDataIntoSharedPreference(loginResponse: LoginResponse) {
        userAuthManager.saveCreator(loginResponse.creator)
        userAuthManager.saveDigiSpace(loginResponse.digispace)
        userAuthManager.saveEmail(loginResponse.email)
        userAuthManager.saveGstNo(loginResponse.gst_no)
        userAuthManager.saveIndustry(loginResponse.industry)
        userAuthManager.saveIsFirstTime(loginResponse.is_first_time)
        userAuthManager.saveMessage(loginResponse.message)
        userAuthManager.saveOrganizationID(loginResponse.organization_id)
        userAuthManager.savePackageType(loginResponse.package_type)
        userAuthManager.saveRoleID(loginResponse.role_id)
        userAuthManager.saveStatus(loginResponse.status)
        userAuthManager.saveStorageConsumed(loginResponse.storage_consumed)
        userAuthManager.saveToken(loginResponse.token)
        userAuthManager.saveTotalStorageAllowed(loginResponse.total_storage_allowed)
        userAuthManager.saveUserID(loginResponse.user_id)
        userAuthManager.saveStatusCode(loginResponse.status_code)

        Log.d("Status_code", userAuthManager.getStatusCode().toString())
        Log.d("Status_code", userAuthManager.getStatus().toString())
        Log.d("Status_code", userAuthManager.getDigiSpace().toString())
        Log.d("Status_code", userAuthManager.getEmail().toString())
        Log.d("Status_code", userAuthManager.getToken().toString())
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
    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.VISIBLE
        _binding = null
    }
}
package com.example.digiboxxdemo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.repository.DigiRepo
import com.example.digiboxxdemo.retrofit.MyApi
import com.example.digiboxxdemo.retrofit.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: DigiRepo
) : ViewModel() {

    private val _loginResponse = MutableSharedFlow<LoginResponse?>()
    val loginResponse = _loginResponse.asSharedFlow()



    fun getLoginData(
        email: String,
        password: String,
        session: Int
    ) {
        viewModelScope.launch {
            when (val response = repo.getLoginResponse(email,password, session)) {
                is Resource.Success -> _loginResponse.emit(response.data)
                else -> {
                    _loginResponse.emit(null)
                }
            }
        }
    }
}
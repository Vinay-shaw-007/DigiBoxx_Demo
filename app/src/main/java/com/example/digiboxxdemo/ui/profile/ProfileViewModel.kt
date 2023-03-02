package com.example.digiboxxdemo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digiboxxdemo.LoginResponse
import com.example.digiboxxdemo.repository.DigiRepo
import com.example.digiboxxdemo.retrofit.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: DigiRepo
) : ViewModel() {
    private val _loggedOutResponse = MutableSharedFlow<LoginResponse?>()
    val loggedOutResponse = _loggedOutResponse.asSharedFlow()


    fun getUserLoggedOut(
        userToken: String
    ) {
        viewModelScope.launch {
            when (val response = repo.getUserLogged(userToken)) {
                is Resource.Success -> _loggedOutResponse.emit(response.data)
                else -> {
                    _loggedOutResponse.emit(null)
                }
            }
        }
    }
}
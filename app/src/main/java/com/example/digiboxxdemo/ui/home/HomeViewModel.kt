package com.example.digiboxxdemo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.repository.DigiRepo
import com.example.digiboxxdemo.retrofit.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: DigiRepo
) :  ViewModel() {

    private val _userDetails = MutableSharedFlow<Files?>()
    val userDetails = _userDetails.asSharedFlow()

    fun getUserDetails(type: String) {
        viewModelScope.launch {
            when (val response = repo.getUserDetails(type)) {
                is Resource.Success -> _userDetails.emit(response.data)
                else -> {
                    _userDetails.emit(null)
                }
            }
        }
    }
}
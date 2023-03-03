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

    private val _userDetailsForFile = MutableSharedFlow<Files?>()
    val userDetailsForFile = _userDetailsForFile.asSharedFlow()

    private val _userDetailsForFolder = MutableSharedFlow<Files?>()
    val userDetailsForFolder = _userDetailsForFolder.asSharedFlow()

    fun getUserDetailsForFile(type: String) {
        viewModelScope.launch {
            when (val response = repo.getUserDetails(type)) {
                is Resource.Success -> _userDetailsForFile.emit(response.data)
                else -> {
                    _userDetailsForFile.emit(null)
                }
            }
        }
    }
    fun getUserDetailsForFolder(type: String) {
        viewModelScope.launch {
            when (val response = repo.getUserDetails(type)) {
                is Resource.Success -> _userDetailsForFolder.emit(response.data)
                else -> {
                    _userDetailsForFolder.emit(null)
                }
            }
        }
    }
}
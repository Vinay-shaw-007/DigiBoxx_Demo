package com.example.digiboxxdemo.ui.add

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digiboxxdemo.UploadRequestBody
import com.example.digiboxxdemo.repository.DigiRepo
import com.example.digiboxxdemo.retrofit.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val repo: DigiRepo
) : ViewModel() {

    companion object {
        const val TAG = "AddViewModel"
    }

    private val _userLimit = MutableSharedFlow<Resource.Success<JsonObject>?>()
    val userLimit = _userLimit.asSharedFlow()

    private val _fileUploaded = MutableSharedFlow<Resource.Success<JsonObject>?>()
    val fileUploaded = _fileUploaded.asSharedFlow()

    private val _minioUrlAndFileID = MutableSharedFlow<Resource.Success<JsonObject>?>()
    val minioUrlAndFileID = _minioUrlAndFileID.asSharedFlow()

    private val _uploadFile = MutableSharedFlow<String?>()
    val uploadFile = _uploadFile.asSharedFlow()


    fun checkUserLimit() {
        viewModelScope.launch {
            when (val response = repo.checkUserLimit()) {
                is Resource.Success -> _userLimit.emit(response)
                else -> {
                    _userLimit.emit(null)
                }
            }
        }
    }

    fun getMinioUrlAndFileID(
        fileTitle: String,
        fileTypes: String,
        fileSize: Long,
        fileName: String
    ) {
        viewModelScope.launch {
            when (val response =
                repo.getMinioUrlAndFileID(fileTitle, fileTypes, fileSize, fileName)) {
                is Resource.Success -> _minioUrlAndFileID.emit(response)
                else -> {
                    _minioUrlAndFileID.emit(null)
                }
            }
        }
    }

    fun uploadFile(url: String, fileUri:Uri, context: Context, fileName:String) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val file = File(context.cacheDir,fileName)
            val outPutStream = FileOutputStream(file)
            inputStream?.copyTo(outPutStream)
            val contentType = "application/octet-stream"
            val requestBody = UploadRequestBody(file, contentType)
//            val requestFile =
//                file.path.toRequestBody("application/octet-stream/*".toMediaTypeOrNull())
            if (url.isNotBlank()) {
                when (repo.uploadFile(url, requestBody)) {
                    is Resource.Success -> {
                        _uploadFile.emit("Success")
                    }
                    is Resource.Error -> {
                        _uploadFile.emit("Error")
                    }
                    else -> {
                        _uploadFile.emit(null)
                    }
                }
            }
        }
    }

    fun fileUploaded(
        fileTitle: String,
        tagDetails: String,
        lastModified: String,
        fileID: String,
        fileName: String,
        fileSize: String
    ) {
        viewModelScope.launch {
            when (val response = repo.fileUploaded(
                fileTitle,
                tagDetails,
                lastModified,
                fileName.substringBeforeLast("."),
                fileID,
                fileName,
                fileSize
            )) {
                is Resource.Success -> _fileUploaded.emit(response)
                else -> {
                    _fileUploaded.emit(null)
                }
            }
        }

    }
}
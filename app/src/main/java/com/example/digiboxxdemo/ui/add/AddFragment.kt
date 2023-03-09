package com.example.digiboxxdemo.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Operation.State.SUCCESS
import com.example.digiboxxdemo.databinding.FragmentAddBinding
import com.google.gson.JsonArray
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONArray

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val addViewModel: AddViewModel by viewModels()

    private var maxFileSize: Long = 0
    private var byteSize: Long = 0
    private var fileTitle: String = ""
    private var tagDetails: String = ""
    private var fileID: String = ""
    private var fileName: String = ""
    private var fileSize: String = ""
    private var uploadFile: Uri? = null



    companion object {
        const val TAG = "AddFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addViewModel.userLimit.collectLatest { it ->
                it?.let { _ ->
                    it.data?.let {
                        it.apply {
                            maxFileSize = get("max_file_size").asLong
                            byteSize = get("bytes_left").asLong
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addViewModel.minioUrlAndFileID.collectLatest { it ->
                it?.let { _ ->
                    it.data?.let {
                        it.apply {
                            val status = this.get("status").asString
                            val message = this.get("message").asString
                            val url = this.get("url").asString
                            val fileId = this.get("file_id").asLong
                            fileID = fileId.toString()
                            uploadFile?.let { it1 ->
                                addViewModel.uploadFile(url,
                                    it1,requireContext(), fileName)
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addViewModel.fileUploaded.collectLatest {
                if (it != null) {
                    Log.d(TAG, "File Uploaded ${it.data}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addViewModel.uploadFile.collectLatest {
                it?.let {
                    when (it) {
                        "Success" -> {
                            addViewModel.fileUploaded(fileTitle, tagDetails, System.currentTimeMillis().toString(), fileID, fileName, fileSize)
                        }
                        "Error" -> {
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        binding.btnUploadFile.setOnClickListener {
            addViewModel.checkUserLimit()
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                selectFiles()
            } else {
                dexterRequestPermission()
            }
        }
    }

    private fun dexterRequestPermission() {
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_DOCUMENTS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    selectFiles()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun selectFiles() {
        Intent().apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*", "application/pdf"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            requestFile.launch(this)
        }
    }

    private val requestFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data = result.data
            data?.let {
                if (data.clipData != null) {
                    //If multiple images chosen
                    val count = data.clipData!!.itemCount
//                    val imageList: ArrayList<FilesEntity> = ArrayList()
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri.toString()
                        requireContext().contentResolver.takePersistableUriPermission(
                            data.clipData!!.getItemAt(
                                i
                            ).uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
//                        imageList.add(getFileNameAndSize(imageUri))
                    }

                } else {
                    //If single image chosen
                    data.data?.let { it1 ->
                        requireContext().contentResolver.takePersistableUriPermission(
                            it1, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        getFileNameTypeAndSize(it1)
                    }
                }
            }
        }
    }

    private fun getFileNameTypeAndSize(imageUri: Uri) {
        uploadFile = imageUri
        val cursor =
            context?.contentResolver?.query(imageUri, null, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                //Get the file name
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(nameIndex)

                //Get the file title
                val random = (100000..999999).random()
                fileTitle = "${random}_$fileName"

                //Get the file type
                val fileType = fileName.substringAfterLast(".")
                tagDetails = JSONArray().put(fileType).toString()

                //Get the size of the file
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val size = cursor.getLong(sizeIndex)
                fileSize = size.toString()
                //Check User Limit
                if (maxFileSize >= size && byteSize >= size) {
                    //get Minio url
                    getMinioUrlAndFileID(fileTitle, fileType, size, fileName)
                }
            }
        }
        cursor?.close()
    }

    private fun getMinioUrlAndFileID(fileTitle: String, fileTypes: String, fileSize: Long, fileName: String) {
        addViewModel.getMinioUrlAndFileID(fileTitle, fileTypes, fileSize, fileName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
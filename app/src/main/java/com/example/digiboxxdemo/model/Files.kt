package com.example.digiboxxdemo.model

data class Files(
    val file_data: List<FileData>? = emptyList(),
    val file_folder_list: List<FileFolder>? = emptyList(),
    val folder_data: List<Any>? = emptyList(),
    val folder_parent_id: Int? = 0,
    val message: String? = "",
    val offset: Int? = 0,
    val page_no: String? = "",
    val status: String? = "",
    val type: String? = ""
)
package com.example.digiboxxdemo.model

data class Files(
    val file_data: List<FileData>,
    val file_folder_list: List<FileFolder>,
    val folder_data: List<Any>,
    val folder_parent_id: Int,
    val message: String,
    val offset: Int,
    val page_no: String,
    val status: String,
    val type: String
)
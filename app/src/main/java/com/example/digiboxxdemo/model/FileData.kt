package com.example.digiboxxdemo.model

data class FileData(
    val action: String,
    val assigned_user_details: List<Any>,
    val copy_link: Int,
    val created_by: Int,
    val created_by_fname: String,
    val created_by_image_path: Any,
    val created_by_lname: String,
    val created_date: String,
    val created_time: String,
    val download_original: Any,
    val duration: Int,
    val expiry_days: String,
    val extension: String,
    val file_created_time: String,
    val file_creation_date: String,
    val file_id: Int,
    val file_name: String,
    val file_size: Int,
    val is_downloadable: Int,
    val is_managed: String,
    val is_recycle_bin: Int,
    val is_resource: Int,
    val is_secured: Int,
    val organization_id: String,
    val parent_folder_id: Int,
    val path_location: String,
    val star_file: String,
    val title: String
)
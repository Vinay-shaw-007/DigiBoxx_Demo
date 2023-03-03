package com.example.digiboxxdemo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.digiboxxdemo.Constant
import com.example.digiboxxdemo.Constant.DIGIFOTOS
import com.example.digiboxxdemo.Constant.FILE_TYPE
import com.example.digiboxxdemo.Constant.FOLDER_TYPE
import com.example.digiboxxdemo.R
import com.example.digiboxxdemo.databinding.FileItemBinding
import com.example.digiboxxdemo.model.FileData
import com.example.digiboxxdemo.model.FileOrFolder
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.model.FolderData
import com.example.digiboxxdemo.ui.diffcallbacks.FilesDiffCallBack

class FilesAdapter : ListAdapter<FileOrFolder, FilesAdapter.FilesViewHolder>(FilesDiffCallBack()) {

    private lateinit var context: Context

    inner class FilesViewHolder(val binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        context = parent.context
        val view = FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val fileDetails = getItem(position)
        if (fileDetails.type == FILE_TYPE) {
            if (fileDetails.data is FileData) {
                val file = fileDetails.data
                Glide.with(context).load(file.path_location).into(holder.binding.shapeableImageView)
                holder.binding.tvfileName.text = file.file_name
                holder.binding.tvCreatedBy.text = file.created_by_fname
            }
        } else if (fileDetails.type == FOLDER_TYPE) {
            if (fileDetails.data is FolderData) {
                val file = fileDetails.data
                if (file.folder_name == DIGIFOTOS)
                    Glide.with(context).load(R.drawable.digiphoto_bg_active)
                        .into(holder.binding.shapeableImageView)
                else
                    Glide.with(context).load(R.drawable.bg_folder)
                        .into(holder.binding.shapeableImageView)

                holder.binding.tvfileName.text = file.folder_name
                holder.binding.tvCreatedBy.text = file.created_by_fname
            }
        }
    }
}
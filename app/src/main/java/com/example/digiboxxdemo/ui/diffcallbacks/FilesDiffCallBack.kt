package com.example.digiboxxdemo.ui.diffcallbacks
import androidx.recyclerview.widget.DiffUtil
import com.example.digiboxxdemo.model.FileData

class FilesDiffCallBack : DiffUtil.ItemCallback<FileData>() {

    override fun areItemsTheSame(oldItem: FileData, newItem: FileData): Boolean {
        return oldItem.file_id == newItem.file_id
    }

    override fun areContentsTheSame(oldItem: FileData, newItem: FileData): Boolean {
        return oldItem == newItem
    }

}
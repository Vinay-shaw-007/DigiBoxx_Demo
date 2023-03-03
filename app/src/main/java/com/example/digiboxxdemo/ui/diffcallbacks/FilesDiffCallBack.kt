package com.example.digiboxxdemo.ui.diffcallbacks
import androidx.recyclerview.widget.DiffUtil
import com.example.digiboxxdemo.model.FileData
import com.example.digiboxxdemo.model.FileOrFolder

class FilesDiffCallBack : DiffUtil.ItemCallback<FileOrFolder>() {

    override fun areItemsTheSame(oldItem: FileOrFolder, newItem: FileOrFolder): Boolean {
        return oldItem.data == newItem.data
    }

    override fun areContentsTheSame(oldItem: FileOrFolder, newItem: FileOrFolder): Boolean {
        return oldItem == newItem
    }

}
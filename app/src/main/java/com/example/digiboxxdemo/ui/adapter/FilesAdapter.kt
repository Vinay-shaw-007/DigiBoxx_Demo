package com.example.digiboxxdemo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.digiboxxdemo.databinding.FileItemBinding
import com.example.digiboxxdemo.model.FileData
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.ui.diffcallbacks.FilesDiffCallBack

class FilesAdapter: ListAdapter<FileData, FilesAdapter.FilesViewHolder>(FilesDiffCallBack()) {

    private lateinit var context: Context

    inner class FilesViewHolder(binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        context = parent.context
        val view = FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {

    }
}
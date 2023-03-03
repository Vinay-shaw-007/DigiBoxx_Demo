package com.example.digiboxxdemo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.digiboxxdemo.Constant.FILE_TYPE
import com.example.digiboxxdemo.Constant.FOLDER_TYPE
import com.example.digiboxxdemo.Constant.TOKEN_EXPIRED
import com.example.digiboxxdemo.R
import com.example.digiboxxdemo.databinding.FragmentHomeBinding
import com.example.digiboxxdemo.model.FileOrFolder
import com.example.digiboxxdemo.retrofit.MyApi
import com.example.digiboxxdemo.ui.EqualSpacingItemDecoration
import com.example.digiboxxdemo.ui.adapter.FilesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var myApi: MyApi

    companion object {
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val myfiles = ArrayList<FileOrFolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.getUserDetailsForFile(FILE_TYPE)
        homeViewModel.getUserDetailsForFolder(FOLDER_TYPE)

        val adapter = FilesAdapter()
        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.spacing) // Define the spacing dimension in resources
        binding.rvFiles.addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        binding.rvFiles.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFiles.adapter = adapter

        val myFolders = mutableListOf<FileOrFolder>()
        val myFiles = mutableListOf<FileOrFolder>()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.userDetailsForFolder.collectLatest { it ->

                it?.let {
                    if (it.message == TOKEN_EXPIRED) {

                        val action =
                            HomeFragmentDirections.actionNavigationHomeToTokenExpiredFragment()
                        findNavController().navigate(action)

                    } else {

                        if (it.type == FOLDER_TYPE) {

                            it.folder_data?.let { folderData ->
                                myFolders.addAll(folderData.map { folder -> FileOrFolder(it.type, folder) })
                            }
                            adapter.submitList(myFolders + myFiles)
                        }

                    }
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.userDetailsForFile.collectLatest {
                it?.let {

                    if (it.type == FILE_TYPE) {

                        it.file_data?.let { fileData ->
                            myFiles.addAll(fileData.map { file -> FileOrFolder(it.type, file) })
                        }
                        adapter.submitList(myFolders + myFiles)
                    }

                }
            }
        }











































//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            homeViewModel.userDetailsForFolder.collectLatest { it ->
//
//                it?.let {
//                    if (it.message == TOKEN_EXPIRED) {
//
//                        val action =
//                            HomeFragmentDirections.actionNavigationHomeToTokenExpiredFragment()
//                        findNavController().navigate(action)
//
//                    } else {
//
//                        if (it.type == FOLDER_TYPE) {
//
//                            it.folder_data?.let { it1 ->
//                                it1.map { folderData ->
//                                    myfiles.add(FileOrFolder(it.type, folderData))
//                                }
//                            }
//
//
//                        }
//
//                    }
//                }
//
//            }
//        }
//
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            homeViewModel.userDetailsForFile.collectLatest {
//                it?.let {
//
//                    if (it.type == FILE_TYPE) {
//
//                        it.file_data?.let { it1 ->
//                            it1.map { fileData ->
//                                myfiles.add(FileOrFolder(it.type, fileData))
//                            }
//                        }
//                        adapter.submitList(myfiles)
//                    }
//
//                }
//            }
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
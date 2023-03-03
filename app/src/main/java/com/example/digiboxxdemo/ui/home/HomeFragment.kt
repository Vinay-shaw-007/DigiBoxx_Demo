package com.example.digiboxxdemo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.digiboxxdemo.Constant
import com.example.digiboxxdemo.Constant.TOKEN_EXPIRED
import com.example.digiboxxdemo.R
import com.example.digiboxxdemo.databinding.FragmentHomeBinding
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.retrofit.MyApi
import com.example.digiboxxdemo.ui.EqualSpacingItemDecoration
import com.example.digiboxxdemo.ui.adapter.FilesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

        homeViewModel.getUserDetails("file")

        val adapter = FilesAdapter()
        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.spacing) // Define the spacing dimension in resources
        binding.rvFiles.addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        binding.rvFiles.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFiles.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.userDetails.collectLatest {
                Log.d(TAG, it.toString())
                it?.let {
                    if (it.message == TOKEN_EXPIRED) {
                        val action = HomeFragmentDirections.actionNavigationHomeToTokenExpiredFragment()
                        findNavController().navigate(action)
                        return@collectLatest
                    }
                    adapter.submitList(it.file_data)
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
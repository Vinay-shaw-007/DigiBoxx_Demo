package com.example.digiboxxdemo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.digiboxxdemo.R
import com.example.digiboxxdemo.databinding.FragmentHomeBinding
import com.example.digiboxxdemo.model.Files
import com.example.digiboxxdemo.retrofit.MyApi
import com.example.digiboxxdemo.ui.EqualSpacingItemDecoration
import com.example.digiboxxdemo.ui.adapter.FilesAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var myApi: MyApi

    companion object {
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val response = myApi.getUserDetailsResponse()
                if (response.isSuccessful) {
                    Log.d(TAG, response.body().toString())
                }

            }
        }
        val adapter = FilesAdapter()
        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.spacing) // Define the spacing dimension in resources
        binding.rvFiles.addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        binding.rvFiles.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFiles.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
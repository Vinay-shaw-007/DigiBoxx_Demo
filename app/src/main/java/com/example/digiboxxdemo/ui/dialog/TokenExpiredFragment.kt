package com.example.digiboxxdemo.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digiboxxdemo.R
import com.example.digiboxxdemo.databinding.FragmentTokenExpiredBinding
import com.example.digiboxxdemo.db.UserAuthManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class TokenExpiredFragment : DialogFragment() {

    @Inject
    lateinit var userAuthManager: UserAuthManager

    private val viewModel: TokenExpiredViewModel by viewModels()

    private var _binding: FragmentTokenExpiredBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTokenExpiredBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLoginAgain.setOnClickListener {
            userAuthManager.saveToken("")
            val action = TokenExpiredFragmentDirections.actionTokenExpiredFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
}
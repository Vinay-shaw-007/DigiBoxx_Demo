package com.example.digiboxxdemo.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.digiboxxdemo.databinding.FragmentProfileBinding
import com.example.digiboxxdemo.db.UserAuthManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var userAuthManager: UserAuthManager

    companion object {
        const val TAG = "NotificationsFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            userAuthManager.getToken()?.let { it1 -> profileViewModel.getUserLoggedOut(it1) }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                profileViewModel.loggedOutResponse.collectLatest {
                    it?.let {
                        when(it.status_code) {
                            200 -> {
                                userAuthManager.saveToken("")
                                findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToLoginFragment())
                            }
                            else -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Log.d(TAG, it.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
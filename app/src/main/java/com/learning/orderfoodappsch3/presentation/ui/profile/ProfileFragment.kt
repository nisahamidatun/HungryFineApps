package com.learning.orderfoodappsch3.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.learning.orderfoodappsch3.R
import com.learning.orderfoodappsch3.databinding.FragmentProfileBinding
import com.learning.orderfoodappsch3.presentation.ui.login.LoginActivity
import com.learning.orderfoodappsch3.utils.AssetWrapper
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val assetWrapper: AssetWrapper by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getProfile()
        setupForm()
        setClickListeners()
        showDataUser()
    }

    override fun onResume() {
        super.onResume()
        getProfile()
    }

    private fun getProfile() {
        viewModel.getCurrentUser()
    }

    private fun setupForm() {
        binding.layoutProfile.tvName.isVisible = true
        binding.layoutProfile.tvEmail.isVisible = true
    }

    private fun showDataUser() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            val name = it?.fullName.orEmpty()
            val email = it?.email.orEmpty()

            binding.layoutProfile.tvName.text = name
            binding.layoutProfile.tvEmail.text = email
        }
    }

    private fun setClickListeners() {
        binding.icPencil.setOnClickListener {
            navigateToProfileChange()
        }
        binding.layoutProfile.tvLogout.setOnClickListener {
            doLogout()
        }
    }

    private fun doLogout() {
        AlertDialog.Builder(requireContext())
            .setMessage(assetWrapper.getString(R.string.do_you_want_to_logout))
            .setPositiveButton(assetWrapper.getString(R.string.okay)) { _, _ ->
                viewModel.doLogout()
                navigateToLogin()
            }.setNegativeButton(assetWrapper.getString(R.string.no)) { _, _ ->
                // do nothing
            }.create().show()
    }

    private fun navigateToLogin() {
        val intentToLogin = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intentToLogin)
    }

    private fun navigateToProfileChange() {
        startActivity(Intent(requireContext(), ProfileChangeActivity::class.java))
    }
}

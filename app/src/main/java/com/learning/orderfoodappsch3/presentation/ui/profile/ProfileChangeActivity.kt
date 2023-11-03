package com.learning.orderfoodappsch3.presentation.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.learning.orderfoodappsch3.R
import com.learning.orderfoodappsch3.databinding.ActivityChangeProfileBinding
import com.learning.orderfoodappsch3.utils.AssetWrapper
import com.learning.orderfoodappsch3.utils.hideKeyboard
import com.learning.orderfoodappsch3.utils.proceedWhen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileChangeActivity : AppCompatActivity() {
    private val binding: ActivityChangeProfileBinding by lazy {
        ActivityChangeProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: ProfileChangeViewModel by viewModel()
    private val assetWrapper: AssetWrapper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setClickListeners()
        showDataUser()
        setupForm()
        observeData()
    }

    private fun showDataUser() {
        val name = viewModel.getCurrentUser()?.fullName.orEmpty()
        val email = viewModel.getCurrentUser()?.email.orEmpty()

        binding.editTextName.setText(name)
        binding.editTextEmail.setText(email)
    }

    private fun setClickListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.btnChangeProfile.setOnClickListener {
            requestChangeProfile()
        }
        binding.tvChangePwd.setOnClickListener {
            requestChangePassword()
        }
    }

    private fun setupForm() {
        binding.textInputName.isVisible = true
        binding.textInputEmail.isVisible = true
        binding.textInputEmail.isEnabled = false
    }

    private fun observeData() {
        viewModel.changeProfileResult.observe(this) {
            it.proceedWhen(doOnLoading = {
                binding.pbLoading.isVisible = true
                binding.btnChangeProfile.isVisible = false
            }, doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    binding.btnChangeProfile.isEnabled = true
                    Toast.makeText(
                        this,
                        assetWrapper.getString(R.string.change_profile_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    showDataUser()
                }, doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    binding.btnChangeProfile.isEnabled = true
                    Toast.makeText(
                        this,
                        assetWrapper.getString(R.string.change_profile_failed) + it.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
    }

    private fun checkNameValid(): Boolean {
        return if (binding.editTextName.text?.isEmpty() == true) {
            binding.textInputName.isErrorEnabled = true
            binding.textInputName.error = assetWrapper.getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.textInputName.isErrorEnabled = false
            true
        }
    }

    private fun requestChangeProfile() {
        if (checkNameValid()) {
            val fullName = binding.editTextName.text.toString().trim()
            viewModel.changeProfile(fullName)
            hideKeyboard()
        }
    }

    private fun requestChangePassword() {
        viewModel.createChangePwdRequest()
        AlertDialog.Builder(this)
            .setMessage(
                assetWrapper.getString(R.string.change_password_request_send_to_your_email) +
                    "${viewModel.getCurrentUser()?.email}"
            )
            .setPositiveButton(assetWrapper.getString(R.string.okay)) { _, _ ->
                // do nothing
            }.create().show()
    }
}

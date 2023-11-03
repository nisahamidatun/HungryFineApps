package com.learning.orderfoodappsch3.presentation.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.learning.orderfoodappsch3.R
import com.learning.orderfoodappsch3.databinding.ActivityRegisterBinding
import com.learning.orderfoodappsch3.presentation.ui.login.LoginActivity
import com.learning.orderfoodappsch3.utils.AssetWrapper
import com.learning.orderfoodappsch3.utils.highLightWord
import com.learning.orderfoodappsch3.utils.proceedWhen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val viewModel: RegisterViewModel by viewModel()
    private val assetWrapper: AssetWrapper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupForm()
        setClickListeners()
        observeResult()
    }

    private fun setupForm() {
        binding.layoutRegister.tilName.isVisible = true
        binding.layoutRegister.tilEmail.isVisible = true
        binding.layoutRegister.tilPassword.isVisible = true
        binding.layoutRegister.tilConfirmPassword.isVisible = true
    }

    private fun setClickListeners() {
        binding.layoutRegister.btnRegister.setOnClickListener {
            doRegister()
        }
        binding.layoutRegister.tvNavToLogin.highLightWord(assetWrapper.getString(R.string.text_higlight_login)) {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intentToLogin = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intentToLogin)
    }

    private fun doRegister() {
        if (isFormValid()) {
            val name = binding.layoutRegister.editTextName.text.toString().trim()
            val email = binding.layoutRegister.editTextEmail.text.toString().trim()
            val password = binding.layoutRegister.editTextPassword.text.toString().trim()
            viewModel.doRegister(name, email, password)
        }
    }

    private fun isFormValid(): Boolean {
        val name = binding.layoutRegister.editTextName.text.toString().trim()
        val email = binding.layoutRegister.editTextEmail.text.toString().trim()
        val password = binding.layoutRegister.editTextPassword.text.toString().trim()
        val confirmPassword = binding.layoutRegister.editTextConfirmPassword.text.toString().trim()
        return checkNameValidation(name) && checkEmailValidation(email) &&
            checkPasswordValidation(password, binding.layoutRegister.tilPassword) &&
            checkPasswordValidation(confirmPassword, binding.layoutRegister.tilConfirmPassword) &&
            checkPwdAndConfirmPwd(password, confirmPassword)
    }

    private fun checkNameValidation(fullName: String): Boolean {
        return if (fullName.isEmpty()) {
            binding.layoutRegister.tilName.isErrorEnabled = true
            binding.layoutRegister.tilName.error = assetWrapper.getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.layoutRegister.tilName.isErrorEnabled = false
            true
        }
    }

    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isEmpty()) {
            binding.layoutRegister.tilEmail.isErrorEnabled = true
            binding.layoutRegister.tilEmail.error = assetWrapper.getString(R.string.text_error_email_empty)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutRegister.tilEmail.isErrorEnabled = true
            binding.layoutRegister.tilEmail.error = assetWrapper.getString(R.string.text_error_email_invalid)
            false
        } else {
            binding.layoutRegister.tilEmail.isErrorEnabled = false
            true
        }
    }

    private fun observeResult() {
        viewModel.registerResult.observe(this) {
            it.proceedWhen(
                doOnLoading = {
                    binding.layoutRegister.pbLoading.isVisible = true
                    binding.layoutRegister.btnRegister.isVisible = false
                },
                doOnSuccess = {
                    binding.layoutRegister.pbLoading.isVisible = false
                    binding.layoutRegister.btnRegister.isVisible = true
                    binding.layoutRegister.btnRegister.isEnabled = false
                    Toast.makeText(this, assetWrapper.getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                },
                doOnError = {
                    binding.layoutRegister.pbLoading.isVisible = false
                    binding.layoutRegister.btnRegister.isVisible = true
                    binding.layoutRegister.btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        assetWrapper.getString(R.string.register_failed) + it.exception?.message.orEmpty(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun checkPasswordValidation(
        password: String,
        textInputLayout: TextInputLayout
    ): Boolean {
        return if (password.isEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = assetWrapper.getString(R.string.text_error_password_empty)
            false
        } else if (password.length < 8) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = assetWrapper.getString(R.string.text_error_password_less_than_8_char)
            false
        } else {
            textInputLayout.isErrorEnabled = false
            true
        }
    }

    private fun checkPwdAndConfirmPwd(password: String, confirmPassword: String): Boolean {
        return if (password != confirmPassword) {
            binding.layoutRegister.tilPassword.isErrorEnabled = true
            binding.layoutRegister.tilConfirmPassword.isErrorEnabled = true
            binding.layoutRegister.tilPassword.error = assetWrapper.getString(R.string.text_error_password_cannot_same)
            binding.layoutRegister.tilConfirmPassword.error = assetWrapper.getString(R.string.text_error_password_cannot_same)
            false
        } else {
            binding.layoutRegister.tilPassword.isErrorEnabled = false
            binding.layoutRegister.tilConfirmPassword.isErrorEnabled = false
            true
        }
    }
}

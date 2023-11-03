package com.learning.orderfoodappsch3.presentation.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.learning.orderfoodappsch3.databinding.ActivitySplashBinding
import com.learning.orderfoodappsch3.presentation.ui.login.LoginActivity
import com.learning.orderfoodappsch3.presentation.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModel()

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkIfUserLogin()
    }

    private fun checkIfUserLogin() {
        lifecycleScope.launch {
            delay(2000)
            if (viewModel.isUserLoggedIn()) {
                navigateToMain()
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        val intentToLogin = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intentToLogin)
    }

    private fun navigateToMain() {
        val intentToMain = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intentToMain)
    }
}

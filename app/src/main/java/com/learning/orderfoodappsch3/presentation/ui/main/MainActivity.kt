package com.learning.orderfoodappsch3.presentation.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.learning.orderfoodappsch3.R
import com.learning.orderfoodappsch3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setBottomNav()
    }

    private fun setBottomNav() {
        val bottomNavController = findNavController(R.id.nav_host_fragment)
        binding.btmNav.setupWithNavController(bottomNavController)
    }
}

package com.learning.orderfoodappsch3.presentation.ui.splashscreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.learning.orderfoodappsch3.data.repository.UserRepository
import com.learning.orderfoodappsch3.tools.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SplashViewModelTest {

    @MockK
    lateinit var userRepo: UserRepository

    private lateinit var splashViewModel: SplashViewModel

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(
        UnconfinedTestDispatcher()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        splashViewModel = spyk(SplashViewModel(userRepo))
        coEvery { userRepo.isLoggedIn() } returns true
    }

    @Test
    fun `test is logged in`() {
        splashViewModel.isUserLoggedIn()
        coVerify { userRepo.isLoggedIn() }
    }
}

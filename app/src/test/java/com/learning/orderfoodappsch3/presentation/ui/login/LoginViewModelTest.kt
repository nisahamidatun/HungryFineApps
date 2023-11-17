package com.learning.orderfoodappsch3.presentation.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.learning.orderfoodappsch3.data.repository.UserRepository
import com.learning.orderfoodappsch3.tools.MainCoroutineRule
import com.learning.orderfoodappsch3.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LoginViewModelTest {

    @MockK
    lateinit var userRepo: UserRepository

    private lateinit var viewModel: LoginViewModel

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
        viewModel = spyk(LoginViewModel(userRepo))
        val updateResult = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { userRepo.doLogin(any(), any()) } returns updateResult
    }

    @Test
    fun `test login`() {
        viewModel.doLogin("email", "password")
        coVerify { userRepo.doLogin(any(), any()) }
    }
}

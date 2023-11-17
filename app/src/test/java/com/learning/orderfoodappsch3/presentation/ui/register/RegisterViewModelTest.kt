package com.learning.orderfoodappsch3.presentation.ui.register

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

class RegisterViewModelTest {

    @MockK
    lateinit var userRepo: UserRepository

    private lateinit var registerViewModel: RegisterViewModel

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
        registerViewModel = spyk(RegisterViewModel(userRepo))
        val updateResult = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { userRepo.doRegister(any(), any(), any()) } returns updateResult
    }

    @Test
    fun `test register`() {
        registerViewModel.doRegister("full name", "email", "password")
        coVerify { userRepo.doRegister(any(), any(), any()) }
    }
}

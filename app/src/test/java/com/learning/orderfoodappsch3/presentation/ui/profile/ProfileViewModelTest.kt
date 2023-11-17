package com.learning.orderfoodappsch3.presentation.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.learning.orderfoodappsch3.data.repository.UserRepository
import com.learning.orderfoodappsch3.model.User
import com.learning.orderfoodappsch3.tools.MainCoroutineRule
import com.learning.orderfoodappsch3.tools.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ProfileViewModelTest {

    @MockK
    private lateinit var repository: UserRepository

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var profileViewModel: ProfileViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        profileViewModel = spyk(ProfileViewModel(repository))
        every { repository.getCurrentUser() } returns mockk(relaxed = true)
    }

    @Test
    fun `test current user`() {
        profileViewModel.getCurrentUser()
        val result = profileViewModel.profileLiveData.getOrAwaitValue()
        assertTrue(result is User)
        coVerify { repository.getCurrentUser() }
    }

    @Test
    fun `test logout`() {
        every { repository.doLogout() } returns true
        val result = profileViewModel.doLogout()
        assertEquals(result, Unit)
        verify { repository.doLogout() }
    }
}

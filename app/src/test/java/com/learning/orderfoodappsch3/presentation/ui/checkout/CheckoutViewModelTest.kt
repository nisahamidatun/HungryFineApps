package com.learning.orderfoodappsch3.presentation.ui.checkout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.learning.orderfoodappsch3.data.repository.CartRepo
import com.learning.orderfoodappsch3.tools.MainCoroutineRule
import com.learning.orderfoodappsch3.tools.getOrAwaitValue
import com.learning.orderfoodappsch3.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CheckoutViewModelTest {

    @MockK
    lateinit var cartRepo: CartRepo

    private lateinit var viewModel: CheckoutViewModel

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
        coEvery { cartRepo.getDataCartFromUser() } returns flow {
            emit(
                ResultWrapper.Success(
                    Pair(
                        listOf(
                            mockk(relaxed = true),
                            mockk(relaxed = true),
                            mockk(relaxed = true),
                            mockk(relaxed = true)
                        ),
                        50000
                    )
                )
            )
        }
        viewModel = spyk(CheckoutViewModel(cartRepo))
        val updateResult = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { cartRepo.deleteAll() } returns Unit
        coEvery { cartRepo.order(any()) } returns updateResult
    }

    @Test
    fun`test get list cart`() {
        val result = viewModel.cartList.getOrAwaitValue()
        assertEquals(result.payload?.first?.size, 4)
        assertEquals(result.payload?.second, 50000)
    }

    @Test
    fun `test order`() {
        runTest {
            viewModel.order()
            coVerify { cartRepo.getDataCartFromUser() }
        }
    }

    @Test
    fun `test clear all cart`() {
        viewModel.clearCart()
        coVerify { cartRepo.deleteAll() }
    }
}

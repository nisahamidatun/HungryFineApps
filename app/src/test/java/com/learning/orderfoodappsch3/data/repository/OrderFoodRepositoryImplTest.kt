package com.learning.orderfoodappsch3.data.repository

import app.cash.turbine.test
import com.learning.orderfoodappsch3.data.network.api.datasource.RestaurantDataSource
import com.learning.orderfoodappsch3.data.network.api.model.category.CategoriesResponse
import com.learning.orderfoodappsch3.data.network.api.model.category.CategoryResponse
import com.learning.orderfoodappsch3.data.network.api.model.orderfood.MenuItemResponse
import com.learning.orderfoodappsch3.data.network.api.model.orderfood.MenusResponse
import com.learning.orderfoodappsch3.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IllegalStateException

class OrderFoodRepositoryImplTest {

    @MockK
    lateinit var remoteDataSource: RestaurantDataSource

    private lateinit var repository: OrderFoodRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = OrderFoodRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `get categories, with result loading`() {
        val mockCategoryResponse = mockk<CategoriesResponse>()
        runTest {
            coEvery { remoteDataSource.getCategories() } returns mockCategoryResponse
            repository.getCategories().map {
                delay(100)
                it
            }.test {
                delay(110)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Loading)
                coVerify { remoteDataSource.getCategories() }
            }
        }
    }

    @Test
    fun `get categories, with result success`() {
        val fakeCategoryResponse = CategoryResponse(
            imgUrl = "url",
            name = "name"
        )
        val fakeCategoriesResponse = CategoriesResponse(
            code = 200,
            status = true,
            message = "Success",
            data = listOf(fakeCategoryResponse)
        )
        runTest {
            coEvery { remoteDataSource.getCategories() } returns fakeCategoriesResponse
            repository.getCategories().map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Success)
                assertEquals(data.payload?.size, 1)
                coVerify { remoteDataSource.getCategories() }
            }
        }
    }

    @Test
    fun `get categories, with result empty`() {
        val fakeCategoriesResponse = CategoriesResponse(
            code = 200,
            status = true,
            message = "Success but empty",
            data = emptyList()
        )
        runTest {
            coEvery { remoteDataSource.getCategories() } returns fakeCategoriesResponse
            repository.getCategories().map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Empty)
                coVerify { remoteDataSource.getCategories() }
            }
        }
    }

    @Test
    fun `get categories, with result error`() {
        runTest {
            coEvery { remoteDataSource.getCategories() } throws IllegalStateException("Mock error")
            repository.getCategories().map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Error)
                coVerify { remoteDataSource.getCategories() }
            }
        }
    }

    @Test
    fun `get products, with result loading`() {
        val mockProductResponse = mockk<MenusResponse>()
        runTest {
            coEvery { remoteDataSource.getOrderFood(any()) } returns mockProductResponse
            repository.getOrderFoods("burger").map {
                delay(100)
                it
            }.test {
                delay(110)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Loading)
                coVerify { remoteDataSource.getOrderFood(any()) }
            }
        }
    }

    @Test
    fun `get products, with result success`() {
        val fakeProductItemResponse = MenuItemResponse(
            description = "desc",
            name = "name",
            price = 12000,
            imageUrl = "url",
            formattedPrice = "rp",
            restaurantAddress = "BSD Tangerang"
        )
        val fakeProductsResponse = MenusResponse(
            code = 200,
            status = true,
            message = "Success",
            data = listOf(fakeProductItemResponse)
        )
        runTest {
            coEvery { remoteDataSource.getOrderFood(any()) } returns fakeProductsResponse
            repository.getOrderFoods("burger").map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Success)
                assertEquals(data.payload?.size, 1)
                coVerify { remoteDataSource.getOrderFood(any()) }
            }
        }
    }

    @Test
    fun `get products, with result empty`() {
        val fakeProductsResponse = MenusResponse(
            code = 200,
            status = true,
            message = "Success",
            data = emptyList()
        )
        runTest {
            coEvery { remoteDataSource.getOrderFood(any()) } returns fakeProductsResponse
            repository.getOrderFoods("burger").map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Empty)
                coVerify { remoteDataSource.getOrderFood(any()) }
            }
        }
    }

    @Test
    fun `get products, with result error`() {
        runTest {
            coEvery { remoteDataSource.getOrderFood(any()) } throws IllegalStateException("Mock error")
            repository.getOrderFoods("burger").map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Error)
                coVerify { remoteDataSource.getOrderFood(any()) }
            }
        }
    }
}

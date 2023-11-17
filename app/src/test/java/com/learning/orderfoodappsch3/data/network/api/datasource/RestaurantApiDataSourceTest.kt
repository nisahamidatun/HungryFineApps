package com.learning.orderfoodappsch3.data.network.api.datasource

import com.learning.orderfoodappsch3.data.network.api.model.category.CategoriesResponse
import com.learning.orderfoodappsch3.data.network.api.model.order.OrderRequest
import com.learning.orderfoodappsch3.data.network.api.model.order.OrderResponse
import com.learning.orderfoodappsch3.data.network.api.model.orderfood.MenusResponse
import com.learning.orderfoodappsch3.data.network.api.service.RestaurantService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RestaurantApiDataSourceTest {

    @MockK
    lateinit var service: RestaurantService

    private lateinit var dataSource: RestaurantDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = RestaurantApiDataSource(service)
    }

    @Test
    fun getProducts() {
        runTest {
            val mockResponse = mockk<MenusResponse>()
            coEvery { service.getMenus(any()) } returns mockResponse
            val response = dataSource.getOrderFood("makanan")
            coVerify { service.getMenus(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun getCategories() {
        runTest {
            val mockResponse = mockk<CategoriesResponse>(relaxed = true)
            coEvery { service.getCategories() } returns mockResponse
            val response = dataSource.getCategories()
            coVerify { service.getCategories() }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun createOrder() {
        runTest {
            val mockResponse = mockk<OrderResponse>(relaxed = true)
            val mockRequest = mockk<OrderRequest>(relaxed = true)
            coEvery { service.createOrder(any()) } returns mockResponse
            val response = dataSource.createOrder(mockRequest)
            coVerify { service.createOrder(any()) }
            assertEquals(response, mockResponse)
        }
    }
}

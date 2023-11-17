package com.learning.orderfoodappsch3.data.repository

import app.cash.turbine.test
import com.learning.orderfoodappsch3.data.database.datasource.CartDataSource
import com.learning.orderfoodappsch3.data.database.entity.CartEntity
import com.learning.orderfoodappsch3.data.network.api.datasource.RestaurantDataSource
import com.learning.orderfoodappsch3.data.network.api.model.order.OrderResponse
import com.learning.orderfoodappsch3.model.Cart
import com.learning.orderfoodappsch3.model.OrderFood
import com.learning.orderfoodappsch3.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CartRepoImplTest {

    @MockK
    lateinit var localDataSource: CartDataSource

    @MockK
    lateinit var remoteDataSource: RestaurantDataSource

    private lateinit var repository: CartRepo

    private val fakeCartList = listOf(
        CartEntity(
            id = 1,
            orderfoodId = 1,
            orderfoodName = "Sate Cirebon",
            orderfoodPrice = 12000,
            orderfoodImgUrl = "url",
            quantityCartItem = 2,
            notes = "notes"
        ),
        CartEntity(
            id = 2,
            orderfoodId = 1,
            orderfoodName = "Sate Padang",
            orderfoodPrice = 14000,
            orderfoodImgUrl = "url",
            quantityCartItem = 2,
            notes = "notes"
        )
    )
    val mockCart = Cart(
        id = 1,
        orderfoodId = 1,
        orderfoodName = "Sate",
        orderfoodPrice = 12000,
        orderfoodImgUrl = "url",
        quantityCartItem = 2,
        notes = "notes"
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CartRepoImpl(localDataSource, remoteDataSource)
    }

    @Test
    fun deleteAll() {
        coEvery { localDataSource.deleteAll() } returns 1
        runTest {
            val result = repository.deleteAll()
            coVerify { localDataSource.deleteAll() }
            assertEquals(result, Unit)
        }
    }

    @Test
    fun `get user card data, result success`() {
        every { localDataSource.getAllCart() } returns flow {
            emit(fakeCartList)
        }
        runTest {
            repository.getDataCartFromUser().map {
                delay(100)
                it
            }.test {
                delay(2201)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Success)
                assertEquals(data.payload?.first?.size, 2)
                assertEquals(data.payload?.second, 52000)
                verify { localDataSource.getAllCart() }
            }
        }
    }

    @Test
    fun `get user card data, result loading`() {
        every { localDataSource.getAllCart() } returns flow {
            emit(fakeCartList)
        }
        runTest {
            repository.getDataCartFromUser().map {
                delay(100)
                it
            }.test {
                delay(2101)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Loading)
                verify { localDataSource.getAllCart() }
            }
        }
    }

    @Test
    fun `get user card data, result error`() {
        every { localDataSource.getAllCart() } returns flow {
            throw IllegalStateException("Mock Error")
        }
        runTest {
            repository.getDataCartFromUser().map {
                delay(100)
                it
            }.test {
                delay(2201)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Error)
                verify { localDataSource.getAllCart() }
            }
        }
    }

    @Test
    fun `get user card data, result empty`() {
        every { localDataSource.getAllCart() } returns flow {
            emit(listOf())
        }
        runTest {
            repository.getDataCartFromUser().map {
                delay(100)
                it
            }.test {
                delay(2201)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Empty)
                verify { localDataSource.getAllCart() }
            }
        }
    }

    @Test
    fun `create cart loading,order food id not null`() {
        runTest {
            val mockOrderFood = mockk<OrderFood>(relaxed = true)
            coEvery { localDataSource.insertCart(any()) } returns 1
            repository.createCart(mockOrderFood, 1)
                .map {
                    delay(100)
                    it
                }.test {
                    delay(110)
                    val result = expectMostRecentItem()
                    assertTrue(result is ResultWrapper.Loading)
                    coVerify { localDataSource.insertCart(any()) }
                }
        }
    }

    @Test
    fun `create cart success,order food id not null`() {
        runTest {
            val mockOrderFood = mockk<OrderFood>(relaxed = true)
            coEvery { localDataSource.insertCart(any()) } returns 1
            repository.createCart(mockOrderFood, 1)
                .map {
                    delay(100)
                    it
                }.test {
                    delay(210)
                    val result = expectMostRecentItem()
                    assertTrue(result is ResultWrapper.Success)
                    assertEquals(result.payload, true)
                    coVerify { localDataSource.insertCart(any()) }
                }
        }
    }

    @Test
    fun `create cart error, order food id not null`() {
        runTest {
            val mockOrderFood = mockk<OrderFood>(relaxed = true)
            coEvery { localDataSource.insertCart(any()) } throws IllegalStateException("Mock Error")
            repository.createCart(mockOrderFood, 1)
                .map {
                    delay(100)
                    it
                }.test {
                    delay(210)
                    val result = expectMostRecentItem()
                    assertTrue(result is ResultWrapper.Error)
                    coVerify { localDataSource.insertCart(any()) }
                }
        }
    }

    @Test
    fun `decrease cart when quantity less than or equal 0`() {
        val mockCart = Cart(
            id = 1,
            orderfoodId = 1,
            orderfoodName = "Sate",
            orderfoodPrice = 12000,
            orderfoodImgUrl = "url",
            quantityCartItem = 0,
            notes = "notes"
        )
        coEvery { localDataSource.deleteCart(any()) } returns 1
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.decreaseCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 1) { localDataSource.deleteCart(any()) }
                coVerify(atLeast = 0) { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `decrease cart when quantity more than 0`() {
        val mockCart = Cart(
            id = 1,
            orderfoodId = 1,
            orderfoodName = "Sate",
            orderfoodPrice = 12000,
            orderfoodImgUrl = "url",
            quantityCartItem = 2,
            notes = "notes"
        )
        coEvery { localDataSource.deleteCart(any()) } returns 1
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.decreaseCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 0) { localDataSource.deleteCart(any()) }
                coVerify(atLeast = 1) { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `increase cart`() {
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.increaseCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 1) { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `set cart notes`() {
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.setNote(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 1) { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `delete cart`() {
        coEvery { localDataSource.deleteCart(any()) } returns 1
        runTest {
            repository.deleteCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 1) { localDataSource.deleteCart(any()) }
            }
        }
    }

    @Test
    fun `test order`() {
        runTest {
            val mockCarts = listOf(
                Cart(
                    id = 1,
                    orderfoodId = 1,
                    orderfoodName = "Sate",
                    orderfoodPrice = 12000,
                    orderfoodImgUrl = "url",
                    quantityCartItem = 2,
                    notes = "notes"
                ),
                Cart(
                    id = 2,
                    orderfoodId = 2,
                    orderfoodName = "Sate Kambing",
                    orderfoodPrice = 12000,
                    orderfoodImgUrl = "url",
                    quantityCartItem = 2,
                    notes = "notes"
                )
            )
            coEvery { remoteDataSource.createOrder(any()) } returns OrderResponse(
                code = 200,
                message = "Success",
                status = true
            )
            repository.order(mockCarts).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertTrue(result is ResultWrapper.Success)
            }
        }
    }
}

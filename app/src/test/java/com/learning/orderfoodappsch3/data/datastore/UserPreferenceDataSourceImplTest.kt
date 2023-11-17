package com.learning.orderfoodappsch3.data.datastore

import com.learning.orderfoodappsch3.utils.PreferenceDataStoreHelper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserPreferenceDataSourceImplTest {

    @MockK
    lateinit var preferenceDataStore: PreferenceDataStoreHelper

    private lateinit var userPreferenceDataSource: UserPreferenceDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        userPreferenceDataSource = UserPreferenceDataSourceImpl(preferenceDataStore)
    }

    @Test
    fun setUserLayoutPref() {
        runTest {
            coEvery { preferenceDataStore.putPreference(any(), true) } returns Unit
            val result = userPreferenceDataSource.setListLayoutMenuPref(true)
            coVerify { preferenceDataStore.putPreference(any(), true) }
            assertEquals(result, Unit)
        }
    }
}

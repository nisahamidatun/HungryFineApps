package com.learning.orderfoodappsch3.data.network.firebase.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class FirebaseAuthDataSourceImplTest {

    @MockK(relaxed = true)
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var dataSourceFirebase: FirebaseAuthDataSource

    val firebaseMock = mockk<FirebaseUser>()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSourceFirebase = FirebaseAuthDataSourceImpl(firebaseAuth)
    }

    private fun mockTaskAuthResult(exception: Exception? = null): Task<AuthResult> {
        val task: Task<AuthResult> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.exception } returns exception
        every { task.isCanceled } returns false

        val relaxedResult: AuthResult = mockk(relaxed = true)
        every { task.result } returns relaxedResult
        every { task.result.user } returns mockk(relaxed = true)
        return task
    }

    private fun mockTaskVoid(exception: Exception? = null): Task<Void> {
        val task: Task<Void> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.exception } returns exception
        every { task.isCanceled } returns false

        val relaxedVoid: Void = mockk(relaxed = true)
        every { task.result } returns relaxedVoid
        return task
    }

    @Test
    fun `test login`() {
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuthResult()
        runTest {
            val result = dataSourceFirebase.doLogin("email", "password")
            Assert.assertEquals(result, true)
        }
    }

    @Test
    fun `test get current user`() {
        every { firebaseAuth.currentUser } returns firebaseMock
        runTest {
            val result = dataSourceFirebase.getCurrentUser()
            Assert.assertEquals(result, firebaseMock)
        }
    }

    @Test
    fun `test is login`() {
        every { firebaseAuth.currentUser } returns firebaseMock
        runTest {
            val result = dataSourceFirebase.isLoggedIn()
            Assert.assertEquals(result, true)
        }
    }

    @Test
    fun `test update profile`() {
        coEvery { firebaseAuth.currentUser?.updateProfile(any()) } returns mockTaskVoid()
        runTest {
            val result = dataSourceFirebase.updateProfile("name", null)
            Assert.assertEquals(result, true)
        }
    }

    @Test
    fun `test update password`() {
        coEvery { firebaseAuth.currentUser?.updatePassword(any()) } returns mockTaskVoid()
        runTest {
            val result = dataSourceFirebase.updatePassword("new pass")
            Assert.assertEquals(result, true)
        }
    }

    @Test
    fun `test update email`() {
        coEvery { firebaseAuth.currentUser?.updateEmail(any()) } returns mockTaskVoid()
        runTest {
            val result = dataSourceFirebase.updateEmail("new email")
            Assert.assertEquals(result, true)
        }
    }

    @Test
    fun `test send change password by email`() {
        coEvery { firebaseAuth.currentUser?.email } returns ""
        runTest {
            val result = dataSourceFirebase.sendChangePasswordRequestByEmail()
            Assert.assertEquals(result, true)
        }
    }
}

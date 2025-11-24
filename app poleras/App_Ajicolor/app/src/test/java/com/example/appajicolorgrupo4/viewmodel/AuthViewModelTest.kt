package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk()
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private lateinit var viewModel: AuthViewModel

    @Test
    fun `submitLogin updates state to success when login is successful`() = runTest {
        // Given
        viewModel = AuthViewModel(userRepository, sessionManager)
        val email = "test@test.com"
        val password = "password"
        val user = UserEntity(1, "Test User", email, "123456789", password, "Address")
        
        coEvery { userRepository.login(email, password) } returns Result.success(user)

        // When
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(password)
        viewModel.submitLogin()
        
        // Advance time to let coroutines complete (including delays)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.login.value.success)
        assertFalse(viewModel.login.value.isSubmitting)
        assertNull(viewModel.login.value.errorMsg)
        coVerify { sessionManager.saveSession(user) }
    }

    @Test
    fun `submitLogin updates state to error when login fails`() = runTest {
        // Given
        viewModel = AuthViewModel(userRepository, sessionManager)
        val email = "test@test.com"
        val password = "wrongpassword"
        
        coEvery { userRepository.login(email, password) } returns Result.failure(Exception("Error"))

        // When
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(password)
        viewModel.submitLogin()
        
        // Advance time to let coroutines complete
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.login.value.success)
        assertFalse(viewModel.login.value.isSubmitting)
        assertEquals("Error", viewModel.login.value.errorMsg)
    }

    @Test
    fun `submitLogin detects admin user correctly`() = runTest {
        // Given
        viewModel = AuthViewModel(userRepository, sessionManager)
        val email = "admin@ajicolor.cl"
        val password = "ajicolor"

        // When
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(password)
        viewModel.submitLogin()
        
        // Advance time to let coroutines complete
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.login.value.success)
        assertTrue(viewModel.login.value.isAdmin)
    }
}

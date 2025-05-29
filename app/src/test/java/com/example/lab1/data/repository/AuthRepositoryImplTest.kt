package com.example.lab1.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.lab1.data.local.dao.UserDao
import com.example.lab1.data.model.User
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthRepositoryImplTest {

    private lateinit var userDao: UserDao
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0 // For Log.w(tag, msg)
        every { Log.w(any(), any(), any()) } returns 0 // For Log.w(tag, msg, tr)
        every { Log.e(any(), any()) } returns 0 // For Log.e(tag, msg)
        every { Log.e(any(), any(), any()) } returns 0 // For Log.e(tag, msg, tr)

        userDao = mockk()
        settingsRepository = mockk(relaxUnitFun = true) // relaxUnitFun for void functions like setLoggedInUserUsername
        authRepository = AuthRepositoryImpl(userDao, settingsRepository)
    }

    @After
    fun tearDown() {
        unmockkAll() // This also unmocks static mocks for the class if any were made by mockkStatic(Log::class)
    }

    @Test
    fun `login success when credentials are correct`() = runTest {
        // Arrange
        val username = "testuser"
        val passwordHash = "password123"
        val user = User(username, passwordHash, "2000-01-01")
        coEvery { userDao.getUserByUsername(username) } returns user

        // Act
        val result = authRepository.login(username, passwordHash)

        // Assert
        assertTrue(result is AuthResult.Success)
        coVerify { settingsRepository.setLoggedInUserUsername(username) }
    }

    @Test
    fun `login failure when user not found`() = runTest {
        // Arrange
        val username = "nonexistentuser"
        val passwordHash = "password123"
        coEvery { userDao.getUserByUsername(username) } returns null

        // Act
        val result = authRepository.login(username, passwordHash)

        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals("invalid_username_or_password_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { settingsRepository.setLoggedInUserUsername(any()) }
    }

    @Test
    fun `login failure when password incorrect`() = runTest {
        // Arrange
        val username = "testuser"
        val correctPasswordHash = "password123"
        val incorrectPasswordHash = "wrongpassword"
        val user = User(username, correctPasswordHash, "2000-01-01")
        coEvery { userDao.getUserByUsername(username) } returns user

        // Act
        val result = authRepository.login(username, incorrectPasswordHash)

        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals("invalid_username_or_password_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { settingsRepository.setLoggedInUserUsername(any()) }
    }

    @Test
    fun `register success when valid data is provided`() = runTest {
        // Arrange
        val username = "newuser"
        val passwordHash = "newpassword123"
        val dateOfBirth = "2000-01-01"
        coEvery { userDao.insertUser(any()) } just runs

        // Act
        val result = authRepository.register(username, passwordHash, dateOfBirth)

        // Assert
        assertTrue(result is AuthResult.Success)
        coVerify { userDao.insertUser(User(username, passwordHash, dateOfBirth)) }
    }

    @Test
    fun `register failure when username is too short`() = runTest {
        // Arrange
        val username = "usr" // Less than 4 chars
        val passwordHash = "newpassword123"
        val dateOfBirth = "2000-01-01"

        // Act
        val result = authRepository.register(username, passwordHash, dateOfBirth)

        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals("username_too_short_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `register failure when password is too short`() = runTest {
        // Arrange
        val username = "validuser"
        val passwordHash = "pass" // Less than 6 chars
        val dateOfBirth = "2000-01-01"

        // Act
        val result = authRepository.register(username, passwordHash, dateOfBirth)

        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals("password_too_short_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }
    
    @Test
    fun `register failure when username already exists`() = runTest {
        // Arrange
        val username = "existinguser"
        val passwordHash = "newpassword123"
        val dateOfBirth = "2000-01-01"
        // Simulate the specific exception that Log.w handles
        val sqlException = SQLiteConstraintException("Username already exists")
        coEvery { userDao.insertUser(any()) } throws sqlException

        // Act
        val result = authRepository.register(username, passwordHash, dateOfBirth)

        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals("username_already_exists_error", (result as AuthResult.Error).message)
        // Verify Log.w was called as expected
        verify { Log.w("AuthRepositoryImpl", "Registration failed for user 'existinguser'. Username likely already exists.", sqlException) }
    }

    @Test
    fun `register failure with other exception`() = runTest {
        // Arrange
        val username = "anotheruser"
        val passwordHash = "securepassword123"
        val dateOfBirth = "1995-05-05"
        val genericException = RuntimeException("Some other DB error")
        coEvery { userDao.insertUser(any()) } throws genericException

        // Act
        val result = authRepository.register(username, passwordHash, dateOfBirth)

        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals("registration_failed_unknown_error", (result as AuthResult.Error).message)
        verify { Log.e("AuthRepositoryImpl", "Registration failed for user 'anotheruser'. Exception: Some other DB error", genericException) }
    }

    @Test
    fun `logout clears logged in user`() = runTest {
        // Act
        authRepository.logout()

        // Assert
        coVerify { settingsRepository.setLoggedInUserUsername(null) }
    }
} 
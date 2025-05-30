package com.example.lab1.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.lab1.data.local.dao.UserDao
import com.example.lab1.data.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        userDao = mockk()
        settingsRepository = mockk(relaxUnitFun = true)
        authRepository = AuthRepositoryImpl(userDao, settingsRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `login success when credentials are correct`() = runTest {

        val username = "testuser"
        val passwordHash = "password123"
        val user = User(username, passwordHash, "2000-01-01")
        coEvery { userDao.getUserByUsername(username) } returns user


        val result = authRepository.login(username, passwordHash)


        assertTrue(result is AuthResult.Success)
        coVerify { settingsRepository.setLoggedInUserUsername(username) }
    }

    @Test
    fun `login failure when user not found`() = runTest {

        val username = "nonexistentuser"
        val passwordHash = "password123"
        coEvery { userDao.getUserByUsername(username) } returns null


        val result = authRepository.login(username, passwordHash)


        assertTrue(result is AuthResult.Error)
        assertEquals("invalid_username_or_password_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { settingsRepository.setLoggedInUserUsername(any()) }
    }

    @Test
    fun `login failure when password incorrect`() = runTest {

        val username = "testuser"
        val correctPasswordHash = "password123"
        val incorrectPasswordHash = "wrongpassword"
        val user = User(username, correctPasswordHash, "2000-01-01")
        coEvery { userDao.getUserByUsername(username) } returns user


        val result = authRepository.login(username, incorrectPasswordHash)


        assertTrue(result is AuthResult.Error)
        assertEquals("invalid_username_or_password_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { settingsRepository.setLoggedInUserUsername(any()) }
    }

    @Test
    fun `register success when valid data is provided`() = runTest {

        val username = "newuser"
        val passwordHash = "newpassword123"
        val dateOfBirth = "2000-01-01"
        coEvery { userDao.insertUser(any()) } just runs


        val result = authRepository.register(username, passwordHash, dateOfBirth)


        assertTrue(result is AuthResult.Success)
        coVerify { userDao.insertUser(User(username, passwordHash, dateOfBirth)) }
    }

    @Test
    fun `register failure when username is too short`() = runTest {

        val username = "usr"
        val passwordHash = "newpassword123"
        val dateOfBirth = "2000-01-01"


        val result = authRepository.register(username, passwordHash, dateOfBirth)


        assertTrue(result is AuthResult.Error)
        assertEquals("username_too_short_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `register failure when password is too short`() = runTest {

        val username = "validuser"
        val passwordHash = "pass"
        val dateOfBirth = "2000-01-01"


        val result = authRepository.register(username, passwordHash, dateOfBirth)


        assertTrue(result is AuthResult.Error)
        assertEquals("password_too_short_error", (result as AuthResult.Error).message)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `register failure when username already exists`() = runTest {

        val username = "existinguser"
        val passwordHash = "newpassword123"
        val dateOfBirth = "2000-01-01"

        val sqlException = SQLiteConstraintException("Username already exists")
        coEvery { userDao.insertUser(any()) } throws sqlException


        val result = authRepository.register(username, passwordHash, dateOfBirth)


        assertTrue(result is AuthResult.Error)
        assertEquals("username_already_exists_error", (result as AuthResult.Error).message)

        verify {
            Log.w(
                "AuthRepositoryImpl",
                "Registration failed for user 'existinguser'. Username likely already exists.",
                sqlException
            )
        }
    }

    @Test
    fun `register failure with other exception`() = runTest {

        val username = "anotheruser"
        val passwordHash = "securepassword123"
        val dateOfBirth = "1995-05-05"
        val genericException = RuntimeException("Some other DB error")
        coEvery { userDao.insertUser(any()) } throws genericException


        val result = authRepository.register(username, passwordHash, dateOfBirth)


        assertTrue(result is AuthResult.Error)
        assertEquals("registration_failed_unknown_error", (result as AuthResult.Error).message)
        verify {
            Log.e(
                "AuthRepositoryImpl",
                "Registration failed for user 'anotheruser'. Exception: Some other DB error",
                genericException
            )
        }
    }

    @Test
    fun `logout clears logged in user`() = runTest {

        authRepository.logout()


        coVerify { settingsRepository.setLoggedInUserUsername(null) }
    }
} 
package com.example.lab1.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.lab1.data.local.dao.UserDao
import com.example.lab1.data.model.User
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    suspend fun login(username: String, passwordHash: String): AuthResult
    suspend fun register(username: String, passwordHash: String, dateOfBirth: String?): AuthResult
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {
    private val tag = "AuthRepositoryImpl"

    override suspend fun login(username: String, passwordHash: String): AuthResult {
        delay(500) // Simulate network delay
        val user = userDao.getUserByUsername(username)
        return if (user != null && user.passwordHash == passwordHash) {
            Log.d(tag, "Login successful for user '$username'")
            AuthResult.Success
        } else {
            Log.d(tag, "Login failed for user '$username'. User found: ${user != null}")
            AuthResult.Error("invalid_username_or_password_error")
        }
    }

    override suspend fun register(
        username: String,
        passwordHash: String,
        dateOfBirth: String?
    ): AuthResult {
        delay(1000) // Simulate network delay
        if (username.length < 4) {
            Log.d(tag, "Registration failed for user '$username'. Username too short.")
            return AuthResult.Error("username_too_short_error")
        }
        if (passwordHash.length < 6) {
            Log.d(tag, "Registration failed for user '$username'. Password too short.")
            return AuthResult.Error("password_too_short_error")
        }

        try {
            val newUser = User(username = username, passwordHash = passwordHash, dateOfBirth = dateOfBirth)
            userDao.insertUser(newUser)
            Log.d(tag, "Registration successful for user '$username'. DOB: $dateOfBirth")
            return AuthResult.Success
        } catch (e: SQLiteConstraintException) {
            Log.w(tag, "Registration failed for user '$username'. Username likely already exists.", e)
            return AuthResult.Error("username_already_exists_error")
        } catch (e: Exception) {
            Log.e(tag, "Registration failed for user '$username'. Exception: ${e.message}", e)
            return AuthResult.Error("registration_failed_unknown_error") // Generic error
        }
    }
}
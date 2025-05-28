package com.example.lab1.data.repository

import com.example.lab1.data.model.User
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap 

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    suspend fun login(username: String, passwordHash: String): AuthResult
    suspend fun register(username: String, passwordHash: String, dateOfBirth: String?): AuthResult
}

class MockAuthRepository : AuthRepository {
    private val registeredUsers = ConcurrentHashMap<String, User>()

    init {
        registeredUsers["testuser"] = User(
            username = "testuser",
            passwordHash = "password123" 
        )
        registeredUsers["user"] = User(
            username = "user",
            passwordHash = "pass"
        )
    }

    override suspend fun login(username: String, passwordHash: String): AuthResult {
        delay(1000)
        val user = registeredUsers[username]
        return if (user != null && user.passwordHash == passwordHash) {
            println("MockAuthRepository: Login successful for user '$username'")
            AuthResult.Success
        } else {
            println("MockAuthRepository: Login failed for user '$username'. User found: ${user != null}")
            AuthResult.Error("invalid_username_or_password_error")
        }
    }

    override suspend fun register(
        username: String,
        passwordHash: String,
        dateOfBirth: String?
    ): AuthResult {
        delay(1500)
        if (username.length < 4) {
            println("MockAuthRepository: Registration failed for user '$username'. Username too short.")
            return AuthResult.Error("username_too_short_error")
        }
        if (passwordHash.length < 6) {
            println("MockAuthRepository: Registration failed for user '$username'. Password too short.")
            return AuthResult.Error("password_too_short_error")
        }
        if (registeredUsers.containsKey(username)) {
            println("MockAuthRepository: Registration failed for user '$username'. Username already exists.")
            return AuthResult.Error("username_already_exists_error")
        }
        
        val newUser = User(username = username, passwordHash = passwordHash, dateOfBirth = dateOfBirth)
        registeredUsers[username] = newUser
        println("MockAuthRepository: Registration successful for user '$username'. DOB: $dateOfBirth")
        println("MockAuthRepository: Current registered users: ${registeredUsers.keys}")
        return AuthResult.Success
    }
}
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
            AuthResult.Error("Invalid username or password.")
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
            return AuthResult.Error("Username must be at least 4 characters long.")
        }
        if (passwordHash.length < 6) {
            println("MockAuthRepository: Registration failed for user '$username'. Password too short.")
            return AuthResult.Error("Password must be at least 6 characters long.")
        }
        if (registeredUsers.containsKey(username)) {
            println("MockAuthRepository: Registration failed for user '$username'. Username already exists.")
            return AuthResult.Error("Username already exists. Please choose another.")
        }
        
        val newUser = User(username = username, passwordHash = passwordHash, dateOfBirth = dateOfBirth)
        registeredUsers[username] = newUser
        println("MockAuthRepository: Registration successful for user '$username'. DOB: $dateOfBirth")
        println("MockAuthRepository: Current registered users: ${registeredUsers.keys}")
        return AuthResult.Success
    }
}
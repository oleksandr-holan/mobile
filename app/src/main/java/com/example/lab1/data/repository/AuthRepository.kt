package com.example.lab1.data.repository

import com.example.lab1.data.model.User
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap // For a simple in-memory user store

// A simple data class to represent the result of an operation
sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    suspend fun login(username: String, passwordHash: String): AuthResult
    suspend fun register(username: String, passwordHash: String, dateOfBirth: String?): AuthResult
    // You could add other methods like:
    // suspend fun logout()
    // suspend fun getCurrentUser(): User?
}

class MockAuthRepository : AuthRepository {

    // Simple in-memory storage for registered users (username to User object)
    // Use ConcurrentHashMap for basic thread safety if you were to access it from multiple coroutines simultaneously,
    // though for this mock, a simple HashMap would also likely be fine.
    private val registeredUsers = ConcurrentHashMap<String, User>()

    init {
        // Pre-register a dummy user for testing login
        registeredUsers["testuser"] = User(
            username = "testuser",
            passwordHash = "password123" // In a real app, this would be a securely hashed password
        )
        registeredUsers["user"] = User(
            username = "user",
            passwordHash = "pass"
        )
    }

    override suspend fun login(username: String, passwordHash: String): AuthResult {
        // Simulate network delay
        delay(1000) // 1 second delay

        val user = registeredUsers[username]

        return if (user != null && user.passwordHash == passwordHash) {
            // In a real app, you might fetch user details, store a session token, etc.
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
        // Simulate network delay
        delay(1500) // 1.5 seconds delay

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

        // Simulate successful registration
        val newUser = User(username = username, passwordHash = passwordHash, dateOfBirth = dateOfBirth)
        registeredUsers[username] = newUser
        println("MockAuthRepository: Registration successful for user '$username'. DOB: $dateOfBirth")
        println("MockAuthRepository: Current registered users: ${registeredUsers.keys}")
        return AuthResult.Success
    }
}
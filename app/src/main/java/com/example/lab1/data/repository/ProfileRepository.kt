package com.example.lab1.data.repository

import com.example.lab1.util.DataResult
import kotlinx.coroutines.delay

data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val dateOfBirth: String?,
    val memberSince: String,
    val profileImageUrl: String? = null 
)

interface ProfileRepository {
    suspend fun getUserProfile(userId: String): DataResult<UserProfile>
}

class MockProfileRepository : ProfileRepository {
    private val currentLoggedInUserId = "testuser"
    private val userProfiles = mapOf(
        "testuser" to UserProfile(
            userId = "testuser",
            username = "Test User",
            email = "test.user@example.com",
            dateOfBirth = "1990-01-01", 
            memberSince = "2023-01-15",
            profileImageUrl = "https://example.com/avatar/testuser.png" 
        ),
        "user" to UserProfile(
            userId = "user",
            username = "Another User",
            email = "user@example.com",
            dateOfBirth = "1985-05-20",
            memberSince = "2022-11-01"
        )
    )

    override suspend fun getUserProfile(userId: String): DataResult<UserProfile> {
        delay(500) 
        return userProfiles[userId]?.let {
            DataResult.Success(it)
        } ?: DataResult.Error("User profile not found for ID: $userId")
    }
    
    suspend fun getCurrentUserProfile(): DataResult<UserProfile> {
        return getUserProfile(currentLoggedInUserId)
    }
}
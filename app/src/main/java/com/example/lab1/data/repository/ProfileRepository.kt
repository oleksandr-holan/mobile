package com.example.lab1.data.repository

import com.example.lab1.util.DataResult
import kotlinx.coroutines.delay

// Represents user profile information
data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val dateOfBirth: String?,
    val memberSince: String,
    val profileImageUrl: String? = null // Optional
)

interface ProfileRepository {
    suspend fun getUserProfile(userId: String): DataResult<UserProfile>
    // suspend fun updateUserProfile(profile: UserProfile): DataResult<Boolean>
}

class MockProfileRepository : ProfileRepository {

    // Simulate a logged-in user's ID (in a real app, this would come from auth state)
    private val currentLoggedInUserId = "testuser" // Matches one from MockAuthRepository

    private val userProfiles = mapOf(
        "testuser" to UserProfile(
            userId = "testuser",
            username = "Test User",
            email = "test.user@example.com",
            dateOfBirth = "1990-01-01", // Fetch from User model if integrated
            memberSince = "2023-01-15",
            profileImageUrl = "https://example.com/avatar/testuser.png" // Placeholder
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
        delay(500) // Simulate network delay
        return userProfiles[userId]?.let {
            DataResult.Success(it)
        } ?: DataResult.Error("User profile not found for ID: $userId")
    }

    // Example for getting the current user's profile
    suspend fun getCurrentUserProfile(): DataResult<UserProfile> {
        // In a real app, you'd get the current user's ID from an auth manager
        return getUserProfile(currentLoggedInUserId)
    }
}
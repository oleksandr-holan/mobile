package com.example.lab1.util.fakes

import com.example.lab1.data.repository.ProfileRepository
import com.example.lab1.data.repository.UserProfile
import com.example.lab1.util.DataResult
import javax.inject.Inject

class FakeProfileRepository @Inject constructor() : ProfileRepository {
    override suspend fun getUserProfile(userId: String): DataResult<UserProfile> {
        return DataResult.Success(
            UserProfile(
                "fakeUser",
                "Fake User",
                "fake@example.com",
                "2000-01-01",
                "2024-01-01"
            )
        )
    }
}
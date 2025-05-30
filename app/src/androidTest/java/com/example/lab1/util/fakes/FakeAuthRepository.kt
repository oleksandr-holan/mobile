package com.example.lab1.util.fakes

import com.example.lab1.data.repository.AuthRepository
import com.example.lab1.data.repository.AuthResult
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeAuthRepository @Inject constructor() : AuthRepository {
    var loginDelay: Long = 0L
    var nextAuthResultProvider: suspend (username: String, passwordHash: String) -> AuthResult =
        { _, _ -> AuthResult.Success }
    private var registerShouldSucceed: Boolean = true

    override suspend fun login(username: String, passwordHash: String): AuthResult {
        delay(loginDelay)
        return nextAuthResultProvider(username, passwordHash)
    }

    override suspend fun register(
        username: String, passwordHash: String, dateOfBirth: String?
    ): AuthResult {
        delay(loginDelay)
        return if (registerShouldSucceed) AuthResult.Success else AuthResult.Error("fake_registration_error")
    }

    override suspend fun logout() {

    }
}
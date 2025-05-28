package com.example.lab1.di

import com.example.lab1.data.repository.AuthRepository
import com.example.lab1.data.repository.AuthRepositoryImpl
import com.example.lab1.data.repository.OrderRepository
import com.example.lab1.data.repository.OrderRepositoryImpl
import com.example.lab1.data.repository.SettingsRepository
import com.example.lab1.data.repository.SettingsRepositoryImpl
import com.example.lab1.data.repository.ProfileRepository
import com.example.lab1.data.repository.MockProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        mockProfileRepository: MockProfileRepository
    ): ProfileRepository
}
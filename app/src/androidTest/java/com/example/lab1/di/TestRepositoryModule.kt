package com.example.lab1.di

import com.example.lab1.data.repository.*
import com.example.lab1.util.fakes.FakeAuthRepository
import com.example.lab1.util.fakes.FakeOrderRepository
import com.example.lab1.util.fakes.FakeProfileRepository
import com.example.lab1.util.fakes.FakeSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class] // Assuming RepositoryModule provides all these
)
abstract class TestRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        fakeSettingsRepository: FakeSettingsRepository
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        fakeAuthRepository: FakeAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        fakeOrderRepository: FakeOrderRepository
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        fakeProfileRepository: FakeProfileRepository
    ): ProfileRepository
}
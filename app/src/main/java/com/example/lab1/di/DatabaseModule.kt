package com.example.lab1.di

import android.content.Context
import com.example.lab1.data.local.AppDatabase
// import com.example.lab1.data.local.dao.MenuItemDao // Removed
import com.example.lab1.data.local.dao.OrderDao
import com.example.lab1.data.local.dao.OrderItemDao
import com.example.lab1.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, applicationScope)
    }

    // Removed provideMenuItemDao
    // @Provides
    // @Singleton
    // fun provideMenuItemDao(appDatabase: AppDatabase): MenuItemDao {
    //     return appDatabase.menuItemDao()
    // }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideOrderItemDao(appDatabase: AppDatabase): OrderItemDao {
        return appDatabase.orderItemDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}
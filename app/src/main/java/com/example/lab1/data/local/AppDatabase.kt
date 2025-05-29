package com.example.lab1.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lab1.data.local.dao.OrderDao
import com.example.lab1.data.local.dao.OrderItemDao
import com.example.lab1.data.local.dao.UserDao
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.model.User

@Database(
    entities = [OrderEntity::class, OrderItemEntity::class, User::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val TAG = "AppDatabase"

        private class AppDatabaseCallback: Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Database onCreate called.")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    Log.d(TAG, "Creating new database instance. Version: 3")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "waiter_app_database"
                    )
                        .addCallback(AppDatabaseCallback())
                        .fallbackToDestructiveMigration(true)
                        .build()
                    INSTANCE = instance
                    Log.d(TAG, "Database instance created and INSTANCE set.")
                    instance
                }
            }
        }
    }
}
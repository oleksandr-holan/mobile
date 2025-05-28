package com.example.lab1.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lab1.data.local.dao.MenuItemDao
import com.example.lab1.data.local.dao.OrderDao
import com.example.lab1.data.local.dao.OrderItemDao
import com.example.lab1.data.local.dao.UserDao
import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import com.example.lab1.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MenuItem::class, OrderEntity::class, OrderItemEntity::class, User::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun menuItemDao(): MenuItemDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val TAG = "AppDatabase"

        private class AppDatabaseCallback(
            private val applicationContext: Context,
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Database onCreate called.")
                scope.launch(Dispatchers.IO) {
                    val databaseInstance = getDatabase(applicationContext, scope)
                    Log.d(TAG, "Coroutine in onCreate: Populating initial menu.")
                    populateInitialMenu(applicationContext, databaseInstance.menuItemDao())
                }
            }

            suspend fun populateInitialMenu(context: Context, menuItemDao: MenuItemDao) {
                val currentCount = menuItemDao.getMenuItemsCount()
                Log.d(TAG, "populateInitialMenu called. Current menu items count: $currentCount")
                if (currentCount == 0) {
                    Log.d(TAG, "Populating initial menu items as count is 0.")
                    val initialMenuItems = MockMenuItemDataProvider.getMockMenuItems(context)
                    menuItemDao.insertAll(initialMenuItems)
                    Log.d(TAG, "Finished populating ${initialMenuItems.size} initial menu items. New count: ${menuItemDao.getMenuItemsCount()}")
                } else {
                    Log.d(TAG, "Menu items already exist (count: $currentCount). Skipping population.")
                }
            }
        }

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    Log.d(TAG, "Creating new database instance. Version: 3")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "waiter_app_database"
                    )
                        .addCallback(AppDatabaseCallback(context.applicationContext, coroutineScope))
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
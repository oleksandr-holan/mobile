package com.example.lab1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lab1.data.local.dao.MenuItemDao
import com.example.lab1.data.local.dao.OrderDao
import com.example.lab1.data.local.dao.OrderItemDao
import com.example.lab1.data.model.MenuItem
import com.example.lab1.data.model.OrderEntity
import com.example.lab1.data.model.OrderItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MenuItem::class, OrderEntity::class, OrderItemEntity::class],
    version = 1, 
    exportSchema = false 
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun menuItemDao(): MenuItemDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialMenu(database.menuItemDao())
                    }
                }
            }

            suspend fun populateInitialMenu(menuItemDao: MenuItemDao) {
                
                if (menuItemDao.getMenuItemsCount() == 0) {
                    val initialMenuItems = MockMenuItemDataProvider.getMockMenuItems()
                    menuItemDao.insertAll(initialMenuItems)
                }
            }
        }
        
        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waiter_app_database"
                )
                    .addCallback(AppDatabaseCallback(coroutineScope)) 
                    .fallbackToDestructiveMigration(false) 
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
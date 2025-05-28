package com.example.lab1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lab1.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(menuItems: List<MenuItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItem)

    @Query("SELECT * FROM menu_items ORDER BY category, nameKey")
    fun getAllMenuItems(): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE id = :itemId")
    fun getMenuItemById(itemId: String): Flow<MenuItem?>

    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun getMenuItemsCount(): Int

    @Query("DELETE FROM menu_items WHERE id = :itemId")
    suspend fun deleteMenuItemById(itemId: String)

    @Query("DELETE FROM menu_items")
    suspend fun deleteAllMenuItems()
}
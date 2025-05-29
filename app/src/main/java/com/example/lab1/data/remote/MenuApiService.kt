package com.example.lab1.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface MenuApiService {
    @GET("menu_items")
    suspend fun getMenuItems(): Response<List<MenuItemApiDTO>>
} 
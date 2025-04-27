package com.example.lab1.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1.ui.navigation.AppDestinations
import com.example.lab1.ui.navigation.BottomNavItem
import com.example.lab1.ui.components.AppBottomNavigationBar
import com.example.lab1.ui.feature.item.AddItemDetailsScreen
import com.example.lab1.ui.feature.order.view.OrderScreen
import com.example.lab1.ui.theme.Lab1Theme

@OptIn(ExperimentalMaterial3Api::class) // For Scaffold
@Composable
fun MainAppScreen() {
    // NavController for the *inner* NavHost (for bottom nav screens)
    val innerNavController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController = innerNavController) }
    ) { innerPadding ->
        // NavHost for the screens accessible via Bottom Navigation
        NavHost(
            navController = innerNavController,
            // Start destination for the bottom nav section (e.g., Orders)
            startDestination = BottomNavItem.Orders.route,
            modifier = Modifier.padding(innerPadding) // Apply padding from Scaffold
        ) {
            composable(BottomNavItem.Orders.route) {
                OrderScreen(
                    onNavigateToAddItem = { /* TODO: Handle navigation if needed */ }
                )
            }
            composable(BottomNavItem.History.route) {
                // Replace with your actual HistoryScreen composable
                HistoryScreenPlaceholder()
            }
            // Add composable destinations for other bottom nav items here
            // Example: Add Item Details might still be navigated to from OrderScreen
            // but it won't be a bottom nav item itself.
            composable(AppDestinations.ADD_ITEM_DETAILS_ROUTE) {
                AddItemDetailsScreen(
                    itemName = "Item from Orders", // Pass actual item later
                    onNavigateBack = { innerNavController.popBackStack() }
                )
            }
        }
    }
}

// Placeholder for the History screen content
@Composable
fun HistoryScreenPlaceholder() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Text("History Screen Content")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    Lab1Theme {
        MainAppScreen()
    }
}
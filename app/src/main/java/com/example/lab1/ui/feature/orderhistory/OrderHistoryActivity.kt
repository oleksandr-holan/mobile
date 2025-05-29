package com.example.lab1.ui.feature.orderhistory

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.R

class OrderHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_order_history)

        val rootView = findViewById<View>(R.id.order_history_root_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewOrderHistory)

        val originalRootPaddingLeft = rootView.paddingLeft
        val originalRootPaddingTop = rootView.paddingTop
        val originalRootPaddingRight = rootView.paddingRight
        val originalRootPaddingBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalRootPaddingLeft + systemBars.left,
                originalRootPaddingTop + systemBars.top,
                originalRootPaddingRight + systemBars.right,
                originalRootPaddingBottom + systemBars.bottom
            )
            WindowInsetsCompat.Builder(insets).setInsets(
                WindowInsetsCompat.Type.systemBars(),
                androidx.core.graphics.Insets.of(0, 0, 0, 0)
            ).build()
        }

        recyclerView.adapter = OrderHistoryAdapter(getMockOrderHistoryData())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun getMockOrderHistoryData(): List<OrderHistoryItem> {
        return listOf(
            OrderHistoryItem("78901", "2023-10-25", "$32.50", "Paid"),
            OrderHistoryItem("78902", "2023-10-24", "$120.00", "Paid"),
            OrderHistoryItem("78903", "2023-10-23", "$15.75", "Cancelled"),
            OrderHistoryItem("78904", "2023-10-22", "$88.10", "Paid"),
            OrderHistoryItem("78905", "2023-10-21", "$61.30", "Paid"),
            OrderHistoryItem("78906", "2023-10-20", "$42.00", "Paid"),
            OrderHistoryItem("78907", "2023-10-19", "$9.50", "Cancelled"),
            OrderHistoryItem("78908", "2023-10-18", "$75.25", "Paid"),
            OrderHistoryItem("78909", "2023-10-17", "$23.00", "Paid"),
            OrderHistoryItem("78910", "2023-10-16", "$50.50", "Paid")
        )
    }
} 
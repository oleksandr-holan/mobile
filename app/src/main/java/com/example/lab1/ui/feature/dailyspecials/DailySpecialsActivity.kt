package com.example.lab1.ui.feature.dailyspecials

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab1.R

class DailySpecialsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_daily_specials)

        val rootView = findViewById<View>(R.id.daily_specials_root_layout)
        val buttonClose = findViewById<Button>(R.id.buttonClose)

        buttonClose.setOnClickListener {
            finish()
        }

        val originalPaddingLeft = rootView.paddingLeft
        val originalPaddingTop = rootView.paddingTop
        val originalPaddingRight = rootView.paddingRight
        val originalPaddingBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPaddingLeft + systemBars.left,
                originalPaddingTop + systemBars.top,
                originalPaddingRight + systemBars.right,
                originalPaddingBottom + systemBars.bottom
            )
            insets
        }
    }
} 
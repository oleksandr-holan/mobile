package com.example.lab1.ui.feature.order.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab1.ui.components.MenuItemCard


@Composable
fun OrderScreen(
    onNavigateToAddItem: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Нове замовлення: Стіл №5",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        Box(modifier = Modifier.clickable { onNavigateToAddItem("Піца Маргарита") }) {
            MenuItemCard(
                itemName = "Піца Маргарита",
                itemDescription = "Класична піца з томатним соусом, моцарелою та базиліком.",
                price = "150 грн",
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.clickable { onNavigateToAddItem("Салат Цезар") }) {
            MenuItemCard(
                itemName = "Салат Цезар",
                itemDescription = "Салат з куркою, грінками, пармезаном та соусом Цезар.",
                price = "120 грн",
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Підказка: Не забудьте запропонувати напої!",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .background(Color(0xFFFFF59D), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    Lab1Theme {
//        OrderScreen()
//    }
//}
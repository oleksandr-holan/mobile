package com.example.lab1 

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* 
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab1.ui.theme.Lab1Theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // *** Enable Edge-to-Edge ***
        // This allows drawing behind the system bars.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Lab1Theme { 
                Surface(
                    modifier = Modifier.fillMaxSize(), 
                    color = MaterialTheme.colorScheme.background 
                ) {
                    OrderScreen() 
                }
            }
        }
    }
}

@Composable
fun OrderScreen() {
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
        MenuItemCard(
            itemName = "Піца Маргарита",
            itemDescription = "Класична піца з томатним соусом, моцарелою та базиліком.",
            price = "150 грн",
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        MenuItemCard(
            itemName = "Салат Цезар",
            itemDescription = "Салат з куркою, грінками, пармезаном та соусом Цезар.",
            price = "120 грн",
        )
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

@Composable
fun MenuItemCard(
    itemName: String,
    itemDescription: String,
    price: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        
        Icon( 
            imageVector = Icons.Filled.RestaurantMenu, 
            contentDescription = itemName,
            tint = MaterialTheme.colorScheme.primary, 
            modifier = Modifier
                .size(60.dp) 
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) 
                .padding(8.dp) 
        )

        Spacer(modifier = Modifier.width(16.dp))

        
        Column(modifier = Modifier.weight(1f)) {
            Text(itemName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(itemDescription, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
            Text(price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Lab1Theme {
        OrderScreen()
    }
}
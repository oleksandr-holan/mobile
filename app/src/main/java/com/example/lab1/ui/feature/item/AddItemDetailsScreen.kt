package com.example.lab1.ui.feature.item
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Use Material 3 components
import androidx.compose.runtime.* // Import remember, mutableStateOf, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab1.ui.theme.Lab1Theme
import kotlin.math.roundToInt // For converting slider value

@Composable
fun AddItemDetailsScreen(
    itemName: String,
    onNavigateBack: () -> Unit
) {
    // --- State Variables ---
    var quantity by remember { mutableFloatStateOf(1f) }
    var specialRequests by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the item being added
        Text(
            text = "Add Details for:",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = itemName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- 1. Quantity Slider ---
        Text("Quantity: ${quantity.roundToInt()}", style = MaterialTheme.typography.bodyLarge) // Display rounded Int
        Slider(
            value = quantity,
            onValueChange = { newValue -> quantity = newValue }, // Update state on change
            valueRange = 1f..10f, // Example range: 1 to 10 items
            steps = 8, // Allows selecting integers: 1, 2, ..., 10 (steps = count - 2)
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. Special Requests Input ---
        OutlinedTextField(
            value = specialRequests,
            onValueChange = { newValue -> specialRequests = newValue }, // Update state
            label = { Text("Special Requests (optional)") }, // Placeholder/label
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3 // Allow multiple lines but limit height
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. Urgent Switch ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Pushes items to ends
        ) {
            Text("Mark as Urgent:", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isUrgent,
                onCheckedChange = { newValue -> isUrgent = newValue } // Update state
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom

        // --- Action Button ---
        Button(
            onClick = {
                Log.d("AddItem", "Item: $itemName, Qty: ${quantity.roundToInt()}, Urgent: $isUrgent, Notes: $specialRequests")
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Add to Order", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemDetailsScreenPreview() {
    Lab1Theme {
        AddItemDetailsScreen(itemName = "Cheeseburger", onNavigateBack = {})
    }
}
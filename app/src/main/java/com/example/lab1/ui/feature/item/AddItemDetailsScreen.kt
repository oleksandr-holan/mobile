package com.example.lab1.ui.feature.item

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab1.data.repository.MockOrderRepository 

@Composable
fun AddItemDetailsScreen(
    onNavigateBack: () -> Unit,
    addItemDetailsViewModel: AddItemDetailsViewModel = viewModel(
        factory = AddItemDetailsViewModelFactory(MockOrderRepository())
    )
) {
    val uiState by addItemDetailsViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(addItemDetailsViewModel.sideEffect, lifecycleOwner) {
        addItemDetailsViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect { effect ->
            when (effect) {
                AddItemDetailsSideEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding() 
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoadingDetails && uiState.itemId != null) { 
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading item details...")
            Spacer(modifier = Modifier.weight(1f)) 
        } else if (uiState.errorMessage != null) {
            Text(
                text = "Error: ${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        Text(
            text = "Add Details for:",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = uiState.itemName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text("Quantity: ${uiState.roundedQuantity}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = uiState.quantity,
            onValueChange = {
                addItemDetailsViewModel.onAction(
                    AddItemDetailsAction.QuantityChanged(
                        it
                    )
                )
            },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = !uiState.isLoadingDetails
        )

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = uiState.specialRequests,
            onValueChange = {
                addItemDetailsViewModel.onAction(
                    AddItemDetailsAction.SpecialRequestsChanged(
                        it
                    )
                )
            },
            label = { Text("Special Requests (optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            enabled = !uiState.isLoadingDetails
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Mark as Urgent:", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = uiState.isUrgent,
                onCheckedChange = {
                    addItemDetailsViewModel.onAction(
                        AddItemDetailsAction.UrgencyChanged(
                            it
                        )
                    )
                },
                enabled = !uiState.isLoadingDetails
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { addItemDetailsViewModel.onAction(AddItemDetailsAction.AddToOrderClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoadingDetails && uiState.errorMessage == null
        ) {
            Text("Add to Order", fontSize = 16.sp)
        }
    }
}
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.collectLatest


@Composable
fun AddItemDetailsScreen(
    onNavigateBack: () -> Unit,
    addItemDetailsViewModel: AddItemDetailsViewModel = hiltViewModel()
) {
    val uiState by addItemDetailsViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(addItemDetailsViewModel.sideEffect, lifecycleOwner) {
        addItemDetailsViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collectLatest { effect -> // Use collectLatest for side effects
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
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading item details...")
        } else if (uiState.errorMessage != null) {
            Text(
                text = "Error: ${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (!uiState.isLoading) {
            Text(
                text = if(uiState.isEditMode) "Edit Details for:" else "Add Details for:",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = uiState.itemName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Price: ${uiState.itemPrice}", // Show price
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text("Quantity: ${uiState.roundedQuantity}", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = uiState.quantity,
                onValueChange = {
                    addItemDetailsViewModel.onAction(
                        AddItemDetailsAction.QuantityChanged(it)
                    )
                },
                valueRange = 1f..10f, // Or make dynamic based on item type
                steps = 8, // valueRange (10-1) / steps (8) -> 9/8 = 1.125 per step.
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !uiState.isSaving && uiState.errorMessage == null
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.specialRequests,
                onValueChange = {
                    addItemDetailsViewModel.onAction(
                        AddItemDetailsAction.SpecialRequestsChanged(it)
                    )
                },
                label = { Text("Special Requests (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                enabled = !uiState.isSaving && uiState.errorMessage == null
            )
            Spacer(modifier = Modifier.height(16.dp))

            // isUrgent switch (kept UI, but not saved in VM logic yet)
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
                            AddItemDetailsAction.UrgencyChanged(it)
                        )
                    },
                    enabled = !uiState.isSaving && uiState.errorMessage == null
                )
            }
        }


        Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

        Button(
            onClick = { addItemDetailsViewModel.onAction(AddItemDetailsAction.SaveOrUpdateItemClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isSaving && uiState.errorMessage == null && uiState.menuItemOriginalId != null
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(uiState.buttonText, fontSize = 16.sp)
            }
        }
    }
}
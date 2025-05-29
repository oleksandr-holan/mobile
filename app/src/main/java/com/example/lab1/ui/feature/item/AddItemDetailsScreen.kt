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
import androidx.compose.ui.res.stringResource
import com.example.lab1.R


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
        ).collectLatest { effect ->
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
            Text(stringResource(R.string.add_item_details_loading))
        } else if (uiState.errorMessage != null) {
            Text(
                text = when (uiState.errorMessage) {
                    "invalid_item_or_order_error" -> stringResource(R.string.invalid_item_or_order_error)
                    "menu_item_not_found_error" -> stringResource(R.string.menu_item_not_found_error)
                    "order_item_details_not_found_error" -> stringResource(R.string.order_item_details_not_found_error)
                    "missing_item_base_info_error" -> stringResource(R.string.missing_item_base_info_error)
                    "failed_to_find_item_to_update_error" -> stringResource(R.string.failed_to_find_item_to_update_error)
                    "order_info_missing_error" -> stringResource(R.string.order_info_missing_error)
                    "error_saving_item_error" -> stringResource(R.string.error_saving_item_error)
                    else -> stringResource(R.string.generic_error_text, uiState.errorMessage!!)
                },
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (!uiState.isLoading) {
            Text(
                text = if (uiState.isEditMode) stringResource(R.string.add_item_details_edit_title) else stringResource(
                    R.string.add_item_details_add_title
                ),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = uiState.itemName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.price_label, uiState.itemPrice),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                stringResource(R.string.quantity_label, uiState.roundedQuantity),
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = uiState.quantity,
                onValueChange = {
                    addItemDetailsViewModel.onAction(
                        AddItemDetailsAction.QuantityChanged(it)
                    )
                },
                valueRange = 1f..10f,
                steps = 8,
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
                label = { Text(stringResource(R.string.special_requests_label)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                enabled = !uiState.isSaving && uiState.errorMessage == null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.mark_as_urgent_label),
                    style = MaterialTheme.typography.bodyLarge
                )
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
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { addItemDetailsViewModel.onAction(AddItemDetailsAction.SaveOrUpdateItemClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isSaving && uiState.errorMessage == null && uiState.menuItemOriginalId != null
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = when (uiState.buttonText) {
                        "add_to_order_button" -> stringResource(R.string.add_to_order_button)
                        "update_item_button" -> stringResource(R.string.update_item_button)
                        else -> uiState.buttonText
                    },
                    fontSize = 16.sp
                )
            }
        }
    }
}
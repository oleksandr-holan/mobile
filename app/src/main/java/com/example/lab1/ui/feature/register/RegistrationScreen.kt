package com.example.lab1.ui.feature.register

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab1.data.repository.MockAuthRepository // Import the concrete repository implementation

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateBackToLogin: () -> Unit,
    registrationViewModel: RegistrationViewModel = viewModel(
        factory = RegistrationViewModelFactory(MockAuthRepository()) // Provide the factory
    )
) {
    // Collect UI state from the ViewModel
    val uiState by registrationViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Collect side effects from the ViewModel
    LaunchedEffect(registrationViewModel.sideEffect, lifecycleOwner) {
        registrationViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect { effect ->
            when (effect) {
                is RegistrationSideEffect.NavigateToMainApp -> {
                    Log.d("RegistrationScreen", "SideEffect: NavigateToMainApp received")
                    onRegistrationSuccess()
                }
                // Handle other side effects if any
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                // .safeDrawingPadding() // Uncomment if you enabled edge-to-edge
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Register", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Display error message if present
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))


            // Username Field
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { registrationViewModel.onAction(RegistrationUiAction.UsernameChanged(it)) },
                label = { Text("Username/Login") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage?.contains("Username", ignoreCase = true) == true, // Example error check
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { registrationViewModel.onAction(RegistrationUiAction.PasswordChanged(it)) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (uiState.isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (uiState.isPasswordVisible) "Hide password" else "Show password"
                    IconButton(
                        onClick = { registrationViewModel.onAction(RegistrationUiAction.TogglePasswordVisibility) },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage?.contains("Password", ignoreCase = true) == true, // Example error check
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth Field
            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = { registrationViewModel.onAction(RegistrationUiAction.DateOfBirthChanged(it)) },
                label = { Text("Date of Birth (e.g., YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage?.contains("Date of Birth", ignoreCase = true) == true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Policy Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.isPrivacyPolicyAccepted,
                    onCheckedChange = { registrationViewModel.onAction(RegistrationUiAction.PrivacyPolicyAcceptedChanged(it)) },
                    enabled = !uiState.isLoading
                )
                Text(
                    text = "I accept the Privacy Policy",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            Button(
                onClick = {
                    Log.d("RegistrationScreen", "Register button clicked, dispatching RegisterClicked action")
                    registrationViewModel.onAction(RegistrationUiAction.RegisterClicked)
                },
                enabled = uiState.isRegisterEnabled && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Register")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to navigate back to Login
            TextButton(
                onClick = onNavigateBackToLogin,
                enabled = !uiState.isLoading
            ) {
                Text("Already have an account? Log In")
            }
        }
    }
}
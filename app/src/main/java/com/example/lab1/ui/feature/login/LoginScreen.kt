package com.example.lab1.ui.feature.login

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
import androidx.lifecycle.viewmodel.compose.viewModel // Import for viewModel()
import com.example.lab1.data.repository.MockAuthRepository

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(MockAuthRepository())
    ) // Obtain ViewModel instance
) {
    // Collect UI state from the ViewModel
    // Use collectAsStateWithLifecycle for better lifecycle awareness (add dependency if needed)
    // or collectAsState for simplicity in this example.
    val uiState by loginViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Collect side effects from the ViewModel
    LaunchedEffect(loginViewModel.sideEffect, lifecycleOwner) {
        loginViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect { effect ->
            when (effect) {
                is LoginSideEffect.NavigateToMainApp -> {
                    Log.d("LoginScreen", "SideEffect: NavigateToMainApp received")
                    onLoginSuccess()
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
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
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
                onValueChange = { loginViewModel.onAction(LoginUiAction.UsernameChanged(it)) },
                label = { Text("Username/Login") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage != null, // Optionally highlight field on error
                enabled = !uiState.isLoading // Disable when loading
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { loginViewModel.onAction(LoginUiAction.PasswordChanged(it)) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (uiState.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description = if (uiState.isPasswordVisible) "Hide password" else "Show password"

                    IconButton(
                        onClick = { loginViewModel.onAction(LoginUiAction.TogglePasswordVisibility) },
                        enabled = !uiState.isLoading // Disable when loading
                    ) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage != null, // Optionally highlight field on error
                enabled = !uiState.isLoading // Disable when loading
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = {
                    Log.d("LoginScreen", "Login button clicked, dispatching LoginClicked action")
                    loginViewModel.onAction(LoginUiAction.LoginClicked)
                },
                enabled = uiState.isLoginEnabled && !uiState.isLoading, // Use derived state from ViewModel
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Log In")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToRegister,
                enabled = !uiState.isLoading // Disable when loading
            ) {
                Text("Don't have an account? Register")
            }
        }
    }
}
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.example.lab1.R
import java.util.Locale

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val currentLanguage = Locale.getDefault().language
    val loginTitleString = stringResource(R.string.login_title)
    Log.d("LoginScreen", "Composing. Current Locale.getDefault(): $currentLanguage. Login title: $loginTitleString")

    val uiState by loginViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

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
            Text(loginTitleString, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = when (uiState.errorMessage) {
                        "username_password_required_error" -> stringResource(R.string.username_password_required_error)
                        "invalid_username_or_password_error" -> stringResource(R.string.invalid_username_or_password_error)
                        else -> uiState.errorMessage!!
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { loginViewModel.onAction(LoginUiAction.UsernameChanged(it)) },
                label = { Text(stringResource(R.string.username_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage != null,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { loginViewModel.onAction(LoginUiAction.PasswordChanged(it)) },
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (uiState.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description =
                        if (uiState.isPasswordVisible) stringResource(R.string.hide_password_desc) else stringResource(R.string.show_password_desc)

                    IconButton(
                        onClick = { loginViewModel.onAction(LoginUiAction.TogglePasswordVisibility) },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage != null,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    Log.d("LoginScreen", "Login button clicked, dispatching LoginClicked action")
                    loginViewModel.onAction(LoginUiAction.LoginClicked)
                },
                enabled = uiState.isLoginEnabled && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.login_button_text))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToRegister,
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(R.string.register_prompt))
            }
        }
    }
}
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lab1.ui.components.AppTopAppBar
import androidx.compose.ui.res.stringResource
import com.example.lab1.R

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateBackToLogin: () -> Unit,
    registrationViewModel: RegistrationViewModel = hiltViewModel()
) {
    val uiState by registrationViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

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
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.register_title),
                canNavigateBack = true,
                onNavigateBack = onNavigateBackToLogin
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 16.dp, horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.errorMessage != null) {
                Text(
                    text = when (uiState.errorMessage) {
                        "fill_all_fields_and_accept_policy_error" -> stringResource(R.string.fill_all_fields_and_accept_policy_error)
                        "username_too_short_error" -> stringResource(R.string.username_too_short_error)
                        "password_too_short_error" -> stringResource(R.string.password_too_short_error)
                        "username_already_exists_error" -> stringResource(R.string.username_already_exists_error)
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
                onValueChange = {
                    registrationViewModel.onAction(
                        RegistrationUiAction.UsernameChanged(
                            it
                        )
                    )
                },
                label = { Text(stringResource(R.string.username_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage?.let { it == "username_too_short_error" || it == "username_already_exists_error" } == true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = {
                    registrationViewModel.onAction(
                        RegistrationUiAction.PasswordChanged(
                            it
                        )
                    )
                },
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image =
                        if (uiState.isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description =
                        if (uiState.isPasswordVisible) stringResource(R.string.hide_password_desc) else stringResource(R.string.show_password_desc)
                    IconButton(
                        onClick = { registrationViewModel.onAction(RegistrationUiAction.TogglePasswordVisibility) },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage == "password_too_short_error",
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = {
                    registrationViewModel.onAction(
                        RegistrationUiAction.DateOfBirthChanged(
                            it
                        )
                    )
                },
                label = { Text(stringResource(R.string.dob_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage?.contains(
                    stringResource(R.string.dob_hint)
                ) == true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.isPrivacyPolicyAccepted,
                    onCheckedChange = {
                        registrationViewModel.onAction(
                            RegistrationUiAction.PrivacyPolicyAcceptedChanged(
                                it
                            )
                        )
                    },
                    enabled = !uiState.isLoading
                )
                Text(
                    text = stringResource(R.string.privacy_policy_accept),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    Log.d(
                        "RegistrationScreen",
                        "Register button clicked, dispatching RegisterClicked action"
                    )
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
                    Text(stringResource(R.string.register_button_text))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateBackToLogin,
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(R.string.login_prompt))
            }
        }
    }
}
package com.example.waiterapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waiterapp.ui.theme.Lab1Theme
import androidx.compose.foundation.text.KeyboardOptions


@Composable
fun RegistrationScreen() {
    // State for input fields
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var dateOfBirth by rememberSaveable { mutableStateOf("") } // Simple text for now
    var privacyPolicyAccepted by rememberSaveable { mutableStateOf(false) }

    // Determine if the register button should be enabled
    val isRegisterEnabled = username.isNotBlank() &&
            password.isNotBlank() &&
            dateOfBirth.isNotBlank() && // Basic check if not empty
            privacyPolicyAccepted

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
        Spacer(modifier = Modifier.height(24.dp))

        // Username Field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username/Login") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, description) }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Date of Birth Field (Simple Text - Consider DatePicker in real app)
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth (e.g., YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
            // You might add visual transformation or validation later
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Policy Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            // horizontalArrangement = Arrangement.SpaceBetween // Alternative alignment
        ) {
            Checkbox( // Using Checkbox as it's common for terms acceptance
                checked = privacyPolicyAccepted,
                onCheckedChange = { privacyPolicyAccepted = it }
            )
            // You could make this text clickable to show the policy
            Text(
                text = "I accept the Privacy Policy",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
                // Add clickable modifier if needed
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = {
                // Register action - just log for now
                Log.d("RegisterScreen", "Attempting registration: User: $username, Pass: $password, DoB: $dateOfBirth, Accepted: $privacyPolicyAccepted")
                // In real app: call ViewModel -> Repository -> Network/DB
            },
            enabled = isRegisterEnabled, // Enable button based on state
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    Lab1Theme {
        RegistrationScreen()
    }
}
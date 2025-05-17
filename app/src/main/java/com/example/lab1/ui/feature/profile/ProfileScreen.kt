package com.example.lab1.ui.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lab1.R // Assuming you have a placeholder drawable
import com.example.lab1.data.repository.MockProfileRepository
import com.example.lab1.ui.theme.Lab1Theme

@Composable
fun ProfileScreen(
    // This lambda would be provided by MainAppScreen's NavController to handle global navigation
    onNavigateToLogin: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(MockProfileRepository())
    )
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(profileViewModel.sideEffect, lifecycleOwner) {
        profileViewModel.sideEffect.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect { effect ->
            when (effect) {
                ProfileScreenSideEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (uiState.userProfile != null) {
            val userProfile = uiState.userProfile!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.profileImageUrl)
                        .crossfade(true)
                        .error(R.drawable.ic_launcher_foreground) // Replace with a placeholder avatar
                        .placeholder(R.drawable.ic_launcher_foreground) // Replace
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = userProfile.username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                ProfileInfoRow(label = "Email:", value = userProfile.email)
                userProfile.dateOfBirth?.let { ProfileInfoRow(label = "Date of Birth:", value = it) }
                ProfileInfoRow(label = "Member Since:", value = userProfile.memberSince)

                Spacer(modifier = Modifier.weight(1f)) // Pushes logout button to bottom

                Button(
                    onClick = { profileViewModel.onAction(ProfileScreenAction.LogoutClicked) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out")
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("No profile data available. Please try again.")
                // You could add a retry button here that dispatches LoadProfile action
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp)
    }
}
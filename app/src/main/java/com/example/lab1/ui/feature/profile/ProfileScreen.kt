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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lab1.R
import androidx.compose.ui.res.stringResource

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
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
                    text = when (uiState.errorMessage) {
                        "user_profile_not_found_error" -> stringResource(R.string.user_profile_not_found_error)
                        else -> stringResource(R.string.generic_error_text, uiState.errorMessage!!)
                    },
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.profileImageUrl)
                        .crossfade(true)
                        .error(R.drawable.ic_launcher_foreground)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .build(),
                    contentDescription = stringResource(R.string.profile_picture_desc),
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
                ProfileInfoRow(
                    label = stringResource(R.string.email_label),
                    value = userProfile.email
                )
                userProfile.dateOfBirth?.let {
                    ProfileInfoRow(
                        label = stringResource(R.string.dob_label),
                        value = it
                    )
                }
                ProfileInfoRow(
                    label = stringResource(R.string.member_since_label),
                    value = userProfile.memberSince
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { profileViewModel.onAction(ProfileScreenAction.LogoutClicked) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.logout_button))
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(stringResource(R.string.no_profile_data_text))
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
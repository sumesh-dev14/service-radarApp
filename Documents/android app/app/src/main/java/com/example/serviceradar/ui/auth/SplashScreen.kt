package com.example.serviceradar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.AuthViewModel
import com.example.serviceradar.viewmodel.OnboardingViewModel

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel = viewModel(),
    onboardingViewModel: OnboardingViewModel = viewModel(),
    onResult: (String?) -> Unit = {},
    onShowOnboarding: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        authViewModel.fetchRoleForCurrentUser { role ->
            if (role != null) {
                onResult(role)
            } else {
                if (onboardingViewModel.hasSeenOnboarding()) {
                    onResult(null)
                } else {
                    onShowOnboarding()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📡",
                fontSize = 72.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Service Radar",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = "Find trusted services near you",
                fontSize = 15.sp,
                color = White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
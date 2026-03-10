package com.example.serviceradar.ui.landing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviceradar.ui.theme.*

// Helper extension to replace the undefined slideInUp()
private fun slideInUp() = slideInVertically(initialOffsetY = { it }) + fadeIn()

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onFindServicesClick: () -> Unit = {},
    onBeAProviderClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header Section
            HeaderSection(
                isVisible = isVisible,
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick
            )

            // Hero Section
            HeroSection(
                isVisible = isVisible,
                onFindServicesClick = onFindServicesClick,
                onBeAProviderClick = onBeAProviderClick
            )

            // Features Section
            FeaturesSection(isVisible = isVisible)

            // Stats Section
            StatsSection(isVisible = isVisible)

            // How It Works Section
            HowItWorksSection(isVisible = isVisible)

            // Call to Action Section
            CallToActionSection(
                isVisible = isVisible,
                onSignUpClick = onSignUpClick
            )

            // Footer
            FooterSection()
        }
    }
}

@Composable
fun HeaderSection(
    isVisible: Boolean,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                Text(
                    text = "📡 Service Radar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                // Auth Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = White
                        ),
                        border = BorderStroke(2.dp, White)
                    ) {
                        Text(
                            "Login",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onSignUpClick,
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White
                        )
                    ) {
                        Text(
                            "Sign Up",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GradientStart
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeroSection(isVisible: Boolean, onFindServicesClick: () -> Unit, onBeAProviderClick: () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInUp()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F7FF),
                            Color(0xFFEEF2FF)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main Emoji
            Text(
                text = "🎯",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Main Headline
            Text(
                text = "Find Trusted Services Near You",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )

            // Subheadline
            Text(
                text = "Connect with verified service providers in your area. Book instantly, get quality work done.",
                fontSize = 16.sp,
                color = TextLight,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // CTA Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Button(
                    onClick = onFindServicesClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GradientStart
                    )
                ) {
                    Text(
                        "Find Services",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }

                OutlinedButton(
                    onClick = onBeAProviderClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    border = BorderStroke(2.dp, GradientStart),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = GradientStart
                    )
                ) {
                    Text(
                        "Be a Provider",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GradientStart
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturesSection(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInUp()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(vertical = 60.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Section Title
            Text(
                text = "Why Choose Service Radar?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            // Features Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FeatureCard(
                    emoji = "⚡",
                    title = "Lightning Fast",
                    description = "Book services in seconds. No lengthy forms or waiting."
                )

                FeatureCard(
                    emoji = "✅",
                    title = "Verified Providers",
                    description = "All providers are checked and verified for quality work."
                )

                FeatureCard(
                    emoji = "⭐",
                    title = "Real Reviews",
                    description = "Read honest reviews from real customers who used the service."
                )

                FeatureCard(
                    emoji = "🔒",
                    title = "Safe & Secure",
                    description = "Your personal information is protected with industry-standard security."
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8F9FF),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = emoji,
            fontSize = 40.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = TextLight,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun StatsSection(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInUp()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .padding(vertical = 60.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Text(
                text = "Trusted by Thousands",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    number = "10K+",
                    label = "Active Users"
                )

                StatItem(
                    number = "5K+",
                    label = "Providers"
                )

                StatItem(
                    number = "50K+",
                    label = "Services Booked"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    number: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = number,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Text(
            text = label,
            fontSize = 13.sp,
            color = White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun HowItWorksSection(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInUp()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(vertical = 60.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Text(
                text = "How It Works",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StepItem(
                    step = "1",
                    emoji = "🔍",
                    title = "Search",
                    description = "Find services in your area with filters for price and rating"
                )

                StepItem(
                    step = "2",
                    emoji = "📅",
                    title = "Book",
                    description = "Select a provider and book instantly with a few taps"
                )

                StepItem(
                    step = "3",
                    emoji = "✅",
                    title = "Complete",
                    description = "Service provider arrives and completes the work"
                )

                StepItem(
                    step = "4",
                    emoji = "⭐",
                    title = "Review",
                    description = "Rate your experience and help others find great services"
                )
            }
        }
    }
}

@Composable
fun StepItem(
    step: String,
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Step Circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFFE3E8FF),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GradientStart
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 20.sp
                )

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Text(
                text = description,
                fontSize = 14.sp,
                color = TextLight,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun CallToActionSection(
    isVisible: Boolean,
    onSignUpClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInUp()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5F7FF), Color(0xFFEEF2FF))
                    )
                )
                .padding(vertical = 60.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Ready to Get Started?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Join thousands of users and service providers already using Service Radar",
                fontSize = 16.sp,
                color = TextLight,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart
                )
            ) {
                Text(
                    "Sign Up Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
            }

            Text(
                text = "Already have an account? Log in",
                fontSize = 14.sp,
                color = GradientStart,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TextDark)
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "📡 Service Radar",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Terms",
                fontSize = 12.sp,
                color = White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = "Privacy",
                fontSize = 12.sp,
                color = White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = "Contact",
                fontSize = 12.sp,
                color = White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Text(
            text = "© 2026 Service Radar. All rights reserved.",
            fontSize = 12.sp,
            color = White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
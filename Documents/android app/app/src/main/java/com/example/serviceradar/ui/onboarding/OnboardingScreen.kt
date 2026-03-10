package com.example.serviceradar.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.ui.components.GradientButton
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: OnboardingViewModel = viewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            emoji = "🔍",
            title = "Find Services",
            subtitle = "Browse 100+ trusted local services in your area instantly",
            gradientStart = GradientStart,
            gradientEnd = GradientEnd
        ),
        OnboardingPage(
            emoji = "⚡",
            title = "Book Instantly",
            subtitle = "Connect with verified providers in seconds, anytime anywhere",
            gradientStart = Color(0xFF1565C0),
            gradientEnd = Color(0xFF1A237E)
        ),
        OnboardingPage(
            emoji = "⭐",
            title = "Rate & Review",
            subtitle = "Build community trust with honest ratings and detailed reviews",
            gradientStart = Color(0xFF283593),
            gradientEnd = Color(0xFF1A237E)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Skip Button (Top Right)
        TextButton(
            onClick = {
                viewModel.markOnboardingComplete()
                onNavigateToLogin()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Skip",
                color = NavyPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Horizontal Pager with Pages
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) { pageIndex ->
            OnboardingPageContent(
                page = pages[pageIndex],
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Section (White Card with Controls)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Page Dots Indicator
                PageDotsIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Next or Get Started Button
                if (pagerState.currentPage < pages.size - 1) {
                    GradientButton(
                        text = "Next",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    GradientButton(
                        text = "Get Started 🚀",
                        onClick = {
                            viewModel.markOnboardingComplete()
                            onNavigateToLogin()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(page.gradientStart, page.gradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 100.dp)
        ) {
            // Emoji
            Text(
                text = page.emoji,
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Title
            Text(
                text = page.title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Subtitle
            Text(
                text = page.subtitle,
                fontSize = 16.sp,
                color = White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun PageDotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            AnimatedDot(
                isActive = index == currentPage
            )
        }
    }
}

@Composable
fun AnimatedDot(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val dotWidth = animateDpAsState(
        targetValue = if (isActive) 24.dp else 10.dp,
        label = "DotWidth"
    )
    val backgroundColor = if (isActive) NavyPrimary else NavyUltraLight

    Box(
        modifier = modifier
            .height(10.dp)
            .width(dotWidth.value)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
    )
}


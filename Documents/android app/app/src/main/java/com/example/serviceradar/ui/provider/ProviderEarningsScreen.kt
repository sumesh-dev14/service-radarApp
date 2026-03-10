package com.example.serviceradar.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.ui.components.ServiceCard
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.ProviderEarningsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderEarningsScreen(
    earningsViewModel: ProviderEarningsViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val earningCards by earningsViewModel.earningCards.collectAsState()
    val totalEarnings by earningsViewModel.totalEarnings.collectAsState()
    val monthlyEarnings by earningsViewModel.monthlyEarnings.collectAsState()
    val completedJobs by earningsViewModel.completedJobs.collectAsState()
    val isLoading by earningsViewModel.isLoading.collectAsState()
    val uiMessage by earningsViewModel.uiMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            earningsViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData,
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        containerColor = Color.Black,
                        contentColor = White,
                        actionColor = White
                    )
                }
            )
        },
        containerColor = LightGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header with back button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF3949AB)
                                )
                            )
                        )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 48.dp, bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "My Earnings 💰",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.size(40.dp)) // Balance for alignment
                        }
                    }
                }
            }

            // Summary Cards Row
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Earned
                    SummaryCard(
                        title = "Total Earned",
                        value = "₹${totalEarnings.toLong()}",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // This Month
                    SummaryCard(
                        title = "This Month",
                        value = "₹${monthlyEarnings.toLong()}",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Completed Jobs
                    SummaryCard(
                        title = "Completed Jobs",
                        value = completedJobs.toString(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Earnings Title
            item {
                if (earningCards.isNotEmpty()) {
                    Text(
                        text = "Recent Earnings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Loading state
            if (isLoading && earningCards.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NavyPrimary)
                    }
                }
            }

            // Empty state
            if (!isLoading && earningCards.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ServiceCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No Earnings Yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "Complete service bookings to start earning money!",
                                    fontSize = 14.sp,
                                    color = TextLight,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Earning Cards
            items(earningCards) { earning ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EarningCard(
                        serviceCategory = earning.serviceCategory,
                        date = earning.date,
                        amount = earning.amount
                    )
                }
            }

            // Total card at bottom
            if (earningCards.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TotalEarningsCard(
                            totalAmount = totalEarnings
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .heightIn(min = 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = TextLight,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }
    }
}

@Composable
fun EarningCard(
    serviceCategory: String,
    date: Long,
    amount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = serviceCategory,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Text(
                    text = formatDate(date),
                    fontSize = 12.sp,
                    color = TextLight
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "₹${amount.toLong()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF43A047)
                )
            }
        }
    }
}

@Composable
fun TotalEarningsCard(
    totalAmount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF3949AB)
                        )
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Total Earnings",
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${totalAmount.toLong()}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(calendar.time)
    } catch (_: Exception) {
        "Date unavailable"
    }
}






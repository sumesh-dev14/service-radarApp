package com.example.serviceradar.ui.customer

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.ui.components.*
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.CustomerViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareProvidersScreen(
    providers: List<Provider> = emptyList(),
    customerViewModel: CustomerViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onBookClick: (String, String) -> Unit = { _, _ -> }
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Compare Providers", color = White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LightGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 0.dp)
        ) {
            if (providers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No providers to compare",
                            color = TextLight,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Comparison Header
                item {
                    Text(
                        "Side-by-Side Comparison",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                // Price Comparison
                item {
                    ComparisonCard(
                        title = "💰 Price per Hour",
                        providers = providers,
                        valueExtractor = { "₹${it.price}" },
                        bestValueExtractor = { it.price }
                    )
                }

                // Rating Comparison
                item {
                    ComparisonCard(
                        title = "⭐ Average Rating",
                        providers = providers,
                        valueExtractor = { String.format(Locale.getDefault(), "%.1f", it.averageRating) },
                        bestValueExtractor = { it.averageRating }
                    )
                }

                // Category Comparison
                item {
                    ComparisonCard(
                        title = "🔧 Service Category",
                        providers = providers,
                        valueExtractor = { it.category },
                        isBestAlways = true
                    )
                }

                // Status Comparison
                item {
                    ComparisonCard(
                        title = "🟢 Online Status",
                        providers = providers,
                        valueExtractor = { if (it.isOnline) "Online" else "Offline" },
                        isBestAlways = true
                    )
                }

                // Recommendation Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Provider Recommendations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                items(providers) { provider ->
                    ComparisonProviderCard(
                        provider = provider,
                        onBookClick = { onBookClick(provider.id, provider.category) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ComparisonCard(
    title: String,
    providers: List<Provider>,
    valueExtractor: (Provider) -> String,
    bestValueExtractor: ((Provider) -> Double)? = null,
    isBestAlways: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                providers.forEach { provider ->
                    val value = valueExtractor(provider)
                    val isBest = if (isBestAlways) {
                        true
                    } else if (bestValueExtractor != null) {
                        val bestValue = providers.maxOfOrNull { bestValueExtractor(it) } ?: 0.0
                        bestValueExtractor(provider) == bestValue
                    } else {
                        false
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isBest) NavyPrimary.copy(alpha = 0.1f)
                                else LightGray
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isBest && !isBestAlways) {
                                Text(
                                    "🏆 Best",
                                    fontSize = 10.sp,
                                    color = NavyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = value,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isBest) NavyPrimary else TextDark
                            )
                            Text(
                                text = provider.category,
                                fontSize = 11.sp,
                                color = TextLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonProviderCard(
    provider: Provider,
    onBookClick: () -> Unit
) {
    ServiceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = provider.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                Text("₹${provider.price}/hr", color = NavyAccent, fontWeight = FontWeight.SemiBold)
                Text("⭐ ${String.format(Locale.getDefault(), "%.1f", provider.averageRating)}", color = TextLight, fontSize = 13.sp)
            }
            Button(
                onClick = onBookClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Book Now", fontSize = 12.sp, color = White)
            }
        }
    }
}






package com.example.serviceradar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviceradar.ui.theme.*
import java.util.Locale

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled) Brush.horizontalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                ) else Brush.horizontalGradient(
                    colors = listOf(MediumGray, MediumGray)
                )
            )
            .then(
                if (enabled) Modifier else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) White else MediumGray
            )
        }
    }
}

@Composable
fun ServiceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TextDark,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun StatusBadge(status: String) {
    val (color, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        "accepted" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "completed" -> Color(0xFFE3F2FD) to NavyPrimary
        "rejected" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "cancelled" -> ErrorRed to White
        else -> LightGray to TextLight
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun BookingStatusTimeline(
    status: String,
    acceptedAt: Long? = null,
    completedAt: Long? = null,
    modifier: Modifier = Modifier
) {
    val isPending = status == "pending"
    val isAccepted = status == "accepted" || !isPending
    val isCompleted = status == "completed"

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pending
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isPending || isAccepted || isCompleted) GradientStart else LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = White, fontWeight = FontWeight.Bold)
            }
            Text("Pending", fontSize = 10.sp, color = TextLight, modifier = Modifier.padding(top = 4.dp))
        }

        // Line
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .padding(horizontal = 4.dp),
            color = if (isAccepted || isCompleted) GradientStart else LightGray
        )

        // Accepted
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isAccepted || isCompleted) GradientStart else LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = White, fontWeight = FontWeight.Bold)
            }
            Text("Accepted", fontSize = 10.sp, color = TextLight, modifier = Modifier.padding(top = 4.dp))
        }

        // Line
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .padding(horizontal = 4.dp),
            color = if (isCompleted) GradientStart else LightGray
        )

        // Completed
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isCompleted) GradientStart else LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = White, fontWeight = FontWeight.Bold)
            }
            Text("Completed", fontSize = 10.sp, color = TextLight, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun ProviderComparisonCard(
    provider1: com.example.serviceradar.data.model.Provider,
    provider2: com.example.serviceradar.data.model.Provider,
    modifier: Modifier = Modifier
) {
    ServiceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Provider 1
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(provider1.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("₹${provider1.price}", color = GradientStart, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★ ${String.format(Locale.getDefault(), "%.1f", provider1.averageRating)}", fontSize = 12.sp)
                }
                Text("${provider1.completedBookings} completed", fontSize = 11.sp, color = TextLight)
            }

            // Provider 2
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(provider2.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("₹${provider2.price}", color = GradientStart, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★ ${String.format(Locale.getDefault(), "%.1f", provider2.averageRating)}", fontSize = 12.sp)
                }
                Text("${provider2.completedBookings} completed", fontSize = 11.sp, color = TextLight)
            }
        }
    }
}

@Composable
fun EmptyStateIllustration(
    title: String = "No Results",
    description: String = "Try adjusting your filters",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Simple illustration (emoji)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            Text("📭", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            description,
            style = MaterialTheme.typography.bodySmall,
            color = TextLight,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun SearchFilterBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(CardWhite, RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search providers...", color = TextLight) },
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Filter",
                tint = GradientStart
            )
        }
    }
}

@Composable
fun RatingFilterSlider(
    minRating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Minimum Rating: ${String.format(Locale.getDefault(), "%.1f", minRating)} ★", fontWeight = FontWeight.Bold)
        Slider(
            value = minRating,
            onValueChange = onRatingChange,
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = GradientStart,
                activeTrackColor = GradientStart,
                inactiveTrackColor = LightGray
            )
        )
    }
}

@Composable
fun PriceRangeSlider(
    minPrice: Float,
    maxPrice: Float,
    onMinPriceChange: (Float) -> Unit,
    onMaxPriceChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Price Range: ₹${minPrice.toInt()} - ₹${maxPrice.toInt()}", fontWeight = FontWeight.Bold)
        Slider(
            value = minPrice,
            onValueChange = onMinPriceChange,
            valueRange = 0f..10000f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = GradientStart,
                activeTrackColor = GradientStart
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = maxPrice,
            onValueChange = onMaxPriceChange,
            valueRange = 0f..10000f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = GradientStart,
                activeTrackColor = GradientStart
            )
        )
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    ServiceCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = TextLight)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextLight)
            }
        }
    }
}

package com.example.serviceradar.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviceradar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderCategoriesScreen(
    onBackClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    val categories = listOf(
        "Plumber",
        "Electrician",
        "Tutor",
        "Cleaner",
        "Carpenter",
        "Mechanic",
        "Painter",
        "Gardener",
        "Chef",
        "Photographer"
    )

    val categoryEmojis = mapOf(
        "Plumber" to "🔧",
        "Electrician" to "⚡",
        "Tutor" to "📚",
        "Cleaner" to "🧹",
        "Carpenter" to "🪵",
        "Mechanic" to "🔩",
        "Painter" to "🎨",
        "Gardener" to "🌱",
        "Chef" to "👨‍🍳",
        "Photographer" to "📸"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Browse Categories", color = TextDark, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories.size) { index ->
                CategoryCard(
                    category = categories[index],
                    emoji = categoryEmojis[categories[index]] ?: "🌟",
                    onClick = { onCategoryClick(categories[index]) }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                emoji,
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                category,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}


package com.example.serviceradar.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.serviceradar.ui.theme.*

import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun CustomerProfileScreen(
    viewModel: CustomerProfileViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val email by viewModel.email.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val totalBookings by viewModel.totalBookings.collectAsState()
    val completedBookings by viewModel.completedBookings.collectAsState()
    val pendingBookings by viewModel.pendingBookings.collectAsState()
    val cancelledBookings by viewModel.cancelledBookings.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    var editName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3949AB))))
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Text(
                text = "My Profile",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = email.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (!editName) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayName.ifBlank { "No Name" },
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = {
                            editName = true
                            newName = displayName
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = Color.White)
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            singleLine = true,
                            modifier = Modifier.width(180.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White,
                                cursorColor = Color.White,
                                focusedLabelColor = Color.White
                            )
                        )
                        Button(
                            onClick = {
                                viewModel.updateDisplayName(newName)
                                editName = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Save", color = Color(0xFF1A237E))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(text = email, color = Color.White, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        // Stat cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard("Total", totalBookings)
            StatCard("Completed", completedBookings)
            StatCard("Pending", pendingBookings)
            StatCard("Cancelled", cancelledBookings)
        }
        Spacer(Modifier.height(24.dp))
        // ...other profile content...
        if (uiMessage != null) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF1A237E),
                contentColor = Color.White,
                action = {
                    TextButton(onClick = { viewModel.clearMessage() }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) { Text(uiMessage ?: "") }
        }
    }
}

@Composable
fun StatCard(label: String, value: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1A237E))
            Text(text = label, fontSize = 13.sp, color = Color(0xFF3949AB))
        }
    }
}


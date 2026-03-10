package com.example.serviceradar.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.ui.components.GradientButton
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProviderScreen(
    provider: Provider,
    customerViewModel: CustomerViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val uiMessage by customerViewModel.uiMessage.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()

    var selectedReason by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val reportReasons = listOf(
        "Poor Quality Service",
        "Unprofessional Behavior",
        "Overcharging",
        "No Show",
        "Rude/Disrespectful",
        "Safety Concerns",
        "Other"
    )

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            customerViewModel.clearMessage()
            if (it.contains("successfully")) {
                onSuccess()
            }
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
        topBar = {
            TopAppBar(
                title = {
                    Text("Report Provider", color = TextDark, fontWeight = FontWeight.Bold)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Provider Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Reporting:",
                        fontSize = 12.sp,
                        color = TextLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        provider.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        "ID: ${provider.id.take(16)}...",
                        fontSize = 12.sp,
                        color = TextLight
                    )
                }
            }

            // Warning
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("⚠️", fontSize = 20.sp)
                    Column {
                        Text(
                            "Please be honest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextDark
                        )
                        Text(
                            "False reports may result in action on your account",
                            fontSize = 12.sp,
                            color = TextLight
                        )
                    }
                }
            }

            // Reason Selection
            Text(
                "What's the issue?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reportReasons.forEach { reason ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedReason == reason) NavyPrimary.copy(alpha = 0.1f) else White
                        ),
                        border = if (selectedReason == reason) {
                            CardDefaults.outlinedCardBorder()
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = NavyPrimary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(reason, fontSize = 14.sp, color = TextDark)
                        }
                    }
                }
            }

            // Description
            Text(
                "Additional Details",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Describe the issue in detail...", color = TextLight) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            GradientButton(
                text = "Submit Report",
                onClick = {
                    if (selectedReason.isNotEmpty() && description.isNotEmpty()) {
                        customerViewModel.reportProvider(
                            providerId = provider.id,
                            reason = selectedReason,
                            description = description
                        )
                    }
                },
                enabled = selectedReason.isNotEmpty() && description.isNotEmpty() && !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LightGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = TextDark)
            }
        }
    }
}

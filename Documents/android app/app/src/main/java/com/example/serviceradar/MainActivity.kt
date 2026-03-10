package com.example.serviceradar

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.navigation.NavGraph
import com.example.serviceradar.ui.theme.ServiceRadarTheme
import com.example.serviceradar.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            // ── Location Permission Request ────────────────────────────────────
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                // Location features will work if either permission is granted.
                // If both denied, the map will still open but GPS won't auto-detect.
            }

            LaunchedEffect(Unit) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            // ─────────────────────────────────────────────────────────────────

            ServiceRadarTheme(darkTheme = isDarkMode) {
                NavGraph(themeViewModel = themeViewModel)
            }
        }
    }
}
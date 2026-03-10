package com.example.serviceradar.ui.provider

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.utils.LocationManager
import com.example.serviceradar.utils.LocationUtils
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun ProviderLocationSetupScreen(
    initialLat: Double = 0.0,
    initialLon: Double = 0.0,
    onLocationSaved: (lat: Double, lon: Double) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationManager = remember { LocationManager(context) }

    var pinnedLat by remember { mutableStateOf(if (LocationUtils.hasValidLocation(initialLat, initialLon)) initialLat else 0.0) }
    var pinnedLon by remember { mutableStateOf(if (LocationUtils.hasValidLocation(initialLat, initialLon)) initialLon else 0.0) }
    var isLocating by remember { mutableStateOf(false) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var pinMarker by remember { mutableStateOf<Marker?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    fun dropPin(map: MapView, lat: Double, lon: Double) {
        pinnedLat = lat
        pinnedLon = lon
        pinMarker?.let { map.overlays.remove(it) }
        val marker = Marker(map).apply {
            position = GeoPoint(lat, lon)
            title = "Your Service Location"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        map.overlays.add(marker)
        pinMarker = marker
        map.invalidate()
    }

    Scaffold(containerColor = LightGray) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Map ────────────────────────────────────────────────────────────
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(13.0)
                        controller.setCenter(
                            if (LocationUtils.hasValidLocation(initialLat, initialLon))
                                GeoPoint(initialLat, initialLon)
                            else
                                GeoPoint(20.5937, 78.9629)
                        )
                        if (LocationUtils.hasValidLocation(initialLat, initialLon)) {
                            val marker = Marker(this).apply {
                                position = GeoPoint(initialLat, initialLon)
                                title = "Your Service Location"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(marker)
                            pinMarker = marker
                        }
                        val tapOverlay = MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let { dropPin(this@apply, it.latitude, it.longitude) }
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint?) = false
                        })
                        overlays.add(0, tapOverlay)
                        mapViewRef = this
                    }
                }
            )

            // ── Top Bar ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd)))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Set Your Location",
                            fontWeight = FontWeight.Bold,
                            color = White,
                            fontSize = 17.sp
                        )
                        Text(
                            "Tap the map to pin your location",
                            color = White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                isLocating = true
                                val loc = locationManager.getCurrentLocation()
                                isLocating = false
                                if (loc != null) {
                                    mapViewRef?.let { map ->
                                        dropPin(map, loc.first, loc.second)
                                        map.controller.animateTo(
                                            GeoPoint(loc.first, loc.second), 15.0, 500L
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(White.copy(alpha = 0.15f))
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(
                                color = White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.MyLocation,
                                contentDescription = "Use GPS",
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // ── Bottom Card ────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MediumGray.copy(alpha = 0.4f))
                    )

                    if (!LocationUtils.hasValidLocation(pinnedLat, pinnedLon)) {
                        Text(
                            "📍 Tap anywhere on the map\nto set your service location",
                            textAlign = TextAlign.Center,
                            color = TextLight,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SuccessGreen.copy(alpha = 0.08f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    "Location pinned ✓",
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "${"%.4f".format(pinnedLat)}, ${"%.4f".format(pinnedLon)}",
                                    color = TextLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // FIXED: added BorderStroke so Cancel button is visible on white background
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, NavyPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NavyPrimary
                            )
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { onLocationSaved(pinnedLat, pinnedLon) },
                            enabled = LocationUtils.hasValidLocation(pinnedLat, pinnedLon),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyPrimary,
                                disabledContainerColor = NavyPrimary.copy(alpha = 0.4f),
                                disabledContentColor = White.copy(alpha = 0.6f)
                            )
                        ) {
                            Text("Save Location", color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
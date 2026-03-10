package com.example.serviceradar.ui.customer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.utils.LocationUtils
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyProvidersMapScreen(
    providers: List<Provider>,
    customerLat: Double,
    customerLon: Double,
    onBack: () -> Unit,
    onBookProvider: (Provider) -> Unit
) {
    val context = LocalContext.current

    // ── FIXED: proper OSMDroid config with cache directory ────────────────────
    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            // Set tile cache so map tiles load and persist properly
            osmdroidBasePath = File(context.cacheDir, "osmdroid")
            osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
        }
    }

    val sortedProviders = remember(providers, customerLat, customerLon) {
        providers
            .filter { LocationUtils.hasValidLocation(it.latitude, it.longitude) }
            .map { p ->
                p.copy(
                    distanceKm = LocationUtils.calculateDistance(
                        customerLat, customerLon, p.latitude, p.longitude
                    )
                )
            }
            .sortedBy { it.distanceKm }
    }

    var selectedProvider by remember { mutableStateOf<Provider?>(null) }
    var showListView by remember { mutableStateOf(false) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Default center — India if no customer location
    val centerLat = if (LocationUtils.hasValidLocation(customerLat, customerLon)) customerLat else 20.5937
    val centerLon = if (LocationUtils.hasValidLocation(customerLat, customerLon)) customerLon else 78.9629

    Scaffold(containerColor = LightGray) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!showListView) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            // ── FIXED: set tile source AFTER config is ready ──
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(13.0)
                            controller.setCenter(GeoPoint(centerLat, centerLon))

                            // Show user's blue dot
                            val myLocationOverlay = MyLocationNewOverlay(
                                GpsMyLocationProvider(ctx), this
                            )
                            myLocationOverlay.enableMyLocation()
                            overlays.add(myLocationOverlay)

                            // Add provider pins
                            sortedProviders.forEach { provider ->
                                val marker = Marker(this).apply {
                                    position = GeoPoint(provider.latitude, provider.longitude)
                                    title = provider.name.ifEmpty { provider.category }
                                    snippet = "₹${provider.price}/hr · ⭐${provider.averageRating}"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    setOnMarkerClickListener { _, _ ->
                                        selectedProvider = provider
                                        true
                                    }
                                }
                                overlays.add(marker)
                            }
                            mapViewRef = this
                        }
                    },
                    update = { mapView ->
                        // Re-center if customerLat/Lon changes
                        if (LocationUtils.hasValidLocation(customerLat, customerLon)) {
                            mapView.controller.setCenter(GeoPoint(customerLat, customerLon))
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 80.dp, bottom = 24.dp,
                        start = 20.dp, end = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (sortedProviders.isEmpty()) {
                        item { EmptyStatePlaceholder() }
                    } else {
                        items(sortedProviders) { provider ->
                            NearbyProviderListCard(
                                provider = provider,
                                onCardClick = {
                                    selectedProvider = provider
                                    showListView = false
                                },
                                onBookClick = { onBookProvider(provider) }
                            )
                        }
                    }
                }
            }

            // Top Bar
            MapTopBar(
                count = sortedProviders.size,
                isList = showListView,
                onBack = onBack,
                onToggle = { showListView = !showListView }
            )

            // Re-center FAB
            if (!showListView) {
                FloatingActionButton(
                    onClick = {
                        mapViewRef?.controller?.animateTo(
                            GeoPoint(centerLat, centerLon), 15.0, 500L
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = 20.dp,
                            bottom = if (selectedProvider != null) 290.dp else 24.dp
                        ),
                    containerColor = NavyPrimary,
                    contentColor = White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.MyLocation, "Recenter")
                }
            }

            // Provider Bottom Sheet
            AnimatedVisibility(
                visible = selectedProvider != null,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                selectedProvider?.let { provider ->
                    ProviderLocationBottomSheet(
                        provider = provider,
                        customerLat = customerLat,
                        customerLon = customerLon,
                        onDismiss = { selectedProvider = null },
                        onBook = {
                            onBookProvider(provider)
                            selectedProvider = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MapTopBar(
    count: Int,
    isList: Boolean,
    onBack: () -> Unit,
    onToggle: () -> Unit
) {
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
                    "Back",
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Nearby Providers",
                    fontWeight = FontWeight.Bold,
                    color = White,
                    fontSize = 17.sp
                )
                Text(
                    "$count found",
                    color = White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(White.copy(alpha = 0.15f))
            ) {
                Icon(
                    if (isList) Icons.Default.Map else Icons.Default.LocationOn,
                    "Toggle",
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStatePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📍", fontSize = 48.sp)
        Text(
            "No nearby providers found",
            fontWeight = FontWeight.Bold,
            color = TextDark,
            fontSize = 16.sp
        )
        Text(
            "Providers need to set their location first",
            color = TextLight,
            fontSize = 13.sp
        )
    }
}

@Composable
fun NearbyProviderListCard(
    provider: Provider,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    provider.name.ifEmpty { provider.category },
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(provider.category, fontSize = 12.sp, color = TextLight)
                val dist = provider.distanceKm ?: 0.0
                if (dist > 0) {
                    Text(
                        LocationUtils.formatDistance(dist),
                        fontSize = 12.sp,
                        color = NavyAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Button(
                onClick = onBookClick,
                enabled = provider.isOnline,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Book", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ProviderLocationBottomSheet(
    provider: Provider,
    customerLat: Double,
    customerLon: Double,
    onDismiss: () -> Unit,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NavyUltraLight)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                provider.name.ifEmpty { provider.category },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
            )
            Text(
                provider.category,
                fontSize = 13.sp,
                color = TextLight
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val dist = provider.distanceKm ?: 0.0
                if (dist > 0) {
                    AssistChip(
                        onClick = {},
                        label = { Text(LocationUtils.formatDistance(dist), fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp)) }
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text("₹${provider.price}/hr", fontSize = 12.sp) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("⭐ ${provider.averageRating}", fontSize = 12.sp) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
                ) {
                    Text("Close")
                }
                Button(
                    onClick = onBook,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Book Now", color = White)
                }
            }
        }
    }
}
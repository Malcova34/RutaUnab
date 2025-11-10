package com.rutaunab.app.presentation.screens.main.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.rutaunab.app.presentation.components.BottomNavBar

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToQR: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentScreen = "map",
                onNavigateToHome = onNavigateToHome,
                onNavigateToRoutes = onNavigateToRoutes,
                onNavigateToQR = onNavigateToQR,
                onNavigateToMap = onNavigateToMap,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                HeaderSection(
                    onBackClick = onNavigateToHome,
                    onLayersClick = { /* TODO: Implementar cambio de tipo de mapa */ }
                )

                // Google Maps
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    GoogleMapView(
                        uiState = uiState,
                        onCameraMoved = viewModel::onCameraMoved
                    )
                    
                    // Botón "Mi ubicación"
                    FloatingActionButton(
                        onClick = viewModel::onCenterToUserLocation,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint = Color(0xFFFEA604)
                        )
                    }
                }

                // Route Filters
                RouteFiltersSection(
                    routes = uiState.routes,
                    selectedRoute = uiState.selectedRoute,
                    onRouteClick = { routeName -> viewModel.onRouteFilterClick(routeName) }
                )

                // Active Buses
                ActiveBusesSection(
                    buses = uiState.activeBuses,
                    selectedRoute = uiState.selectedRoute
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onBackClick: () -> Unit,
    onLayersClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFEA604),
                        Color(0xFFFEB92C)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Mapa en Tiempo Real",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Seguimiento de buses",
                        fontSize = 12.sp,
                        color = Color(0xFFFFF3E0)
                    )
                }
            }

            IconButton(
                onClick = onLayersClick,
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Capas",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun GoogleMapView(
    uiState: MapUiState,
    onCameraMoved: () -> Unit
) {
    val userLocation = uiState.userLocation ?: LatLng(7.119444, -73.120833) // Default Bucaramanga
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 14f)
    }
    
    // Centrar en ubicación del usuario cuando shouldCenterOnUser sea true
    androidx.compose.runtime.LaunchedEffect(uiState.shouldCenterOnUser) {
        if (uiState.shouldCenterOnUser && uiState.userLocation != null) {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    uiState.userLocation,
                    16f
                ),
                durationMs = 1000
            )
            onCameraMoved()
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = true
        )
    ) {
        // User Location Marker (Azul)
        uiState.userLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Tu ubicación",
                snippet = "Ubicación actual",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }

        // Bus Markers
        uiState.activeBuses.forEach { bus ->
            // Filtrar por ruta seleccionada
            if (uiState.selectedRoute == null || uiState.selectedRoute == "Todas" || bus.route == uiState.selectedRoute) {
                val markerColor = when (bus.route) {
                    "Ruta 1" -> BitmapDescriptorFactory.HUE_BLUE
                    "Ruta 2" -> BitmapDescriptorFactory.HUE_GREEN
                    else -> BitmapDescriptorFactory.HUE_RED
                }
                
                Marker(
                    state = MarkerState(position = bus.latLng),
                    title = bus.route,
                    snippet = bus.location,
                    icon = BitmapDescriptorFactory.defaultMarker(markerColor)
                )
            }
        }
    }
}

@Composable
private fun RouteFiltersSection(
    routes: List<RouteFilter>,
    selectedRoute: String?,
    onRouteClick: (String?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Filtrar",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Filtrar por ruta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "Todas" button
                item {
                    FilterChip(
                        selected = selectedRoute == null,
                        onClick = { onRouteClick(null) },
                        label = { Text("Todas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEA604),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF3F4F6),
                            labelColor = Color(0xFF6B7280)
                        )
                    )
                }

                // Route buttons
                items(routes) { route ->
                    FilterChip(
                        selected = selectedRoute == route.name,
                        onClick = { onRouteClick(route.name) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = when (route.color) {
                                                RouteMapColor.BLUE -> Color(0xFF3B82F6)
                                                RouteMapColor.GREEN -> Color(0xFF10B981)
                                                RouteMapColor.RED -> Color(0xFFEF4444)
                                                RouteMapColor.YELLOW -> Color(0xFFFBBF24)
                                            },
                                            shape = CircleShape
                                        )
                                )
                                Text(route.name)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEA604),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF3F4F6),
                            labelColor = Color(0xFF6B7280)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveBusesSection(
    buses: List<BusLocation>,
    selectedRoute: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Buses en Servicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filtrar buses por ruta seleccionada
            val filteredBuses = if (selectedRoute == null || selectedRoute == "Todas") {
                buses
            } else {
                buses.filter { it.route == selectedRoute }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredBuses.forEach { bus ->
                    BusInfoCard(bus)
                }
            }
        }
    }
}

@Composable
private fun BusInfoCard(bus: BusLocation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFEA604), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Bus",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bus.route,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = bus.location,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}


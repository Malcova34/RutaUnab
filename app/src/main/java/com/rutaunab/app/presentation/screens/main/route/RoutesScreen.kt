package com.rutaunab.app.presentation.screens.main.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rutaunab.app.presentation.components.BottomNavBar

@Composable
fun RoutesScreen(
    viewModel: RoutesViewModel = viewModel(),
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
                currentScreen = "routes",
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF8F0),
                            Color.White,
                            Color(0xFFFFF8F0)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                item {
                    HeaderSection(onBackClick = onNavigateToHome)
                }

                // Routes List
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(uiState.routes) { route ->
                    RouteCard(
                        route = route,
                        onClick = { onNavigateToMap() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Info Card
                item {
                    InfoCard()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(onBackClick: () -> Unit) {
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
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    text = "Rutas Disponibles",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Selecciona una ruta para ver detalles",
                    fontSize = 14.sp,
                    color = Color(0xFFFFF3E0)
                )
            }
        }
    }
}

@Composable
private fun RouteCard(
    route: RouteInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color Indicator
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(80.dp)
                    .background(
                        color = when (route.color) {
                            RouteColor.BLUE -> Color(0xFF3B82F6)
                            RouteColor.GREEN -> Color(0xFF10B981)
                            RouteColor.RED -> Color(0xFFEF4444)
                            RouteColor.YELLOW -> Color(0xFFFBBF24)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Route Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = route.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = route.destination,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (route.status == "En servicio") {
                            Color(0xFF10B981)
                        } else {
                            Color(0xFF9CA3AF)
                        }
                    ) {
                        Text(
                            text = route.status,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Route Details
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Paradas",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${route.stops} paradas",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Frecuencia",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cada ${route.frequency}",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalles",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Información",
                tint = Color(0xFFFEA604),
                modifier = Modifier.size(20.dp)
            )

            Column {
                Text(
                    text = "Información",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Toca cualquier ruta para ver su recorrido en el mapa y las paradas disponibles.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )
            }
        }
    }
}


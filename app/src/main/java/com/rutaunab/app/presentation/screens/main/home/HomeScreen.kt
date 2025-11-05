package com.rutaunab.app.presentation.screens.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToQR: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = "home",
                onNavigateToHome = { /* Ya estamos en Home */ },
                onNavigateToRoutes = onNavigateToRoutes,
                onNavigateToQR = onNavigateToQR,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF7ED),
                            Color.White,
                            Color(0xFFFFF7ED)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Header
            item {
                HeaderSection(
                    userName = uiState.user?.name ?: "Usuario",
                    activeRoutesCount = uiState.activeRoutesCount,
                    totalStops = uiState.totalStops,
                    onProfileClick = onNavigateToProfile
                )
            }

            // Google Maps Container
            item {
                MapSection(
                    showPlaceholder = uiState.showMapPlaceholder,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            // Active Routes Section
            item {
                RoutesSection(
                    routes = uiState.activeRoutes,
                    onSeeAllClick = onNavigateToRoutes,
                    onRouteClick = onNavigateToRoutes
                )
            }

            // Notification Banner
            item {
                NotificationBanner(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            // Spacer para el bottom nav
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(
    userName: String,
    activeRoutesCount: Int,
    totalStops: Int,
    onProfileClick: () -> Unit
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
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header con nombre y botón perfil
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bienvenido/a",
                        fontSize = 14.sp,
                        color = Color(0xFFFED7A0)
                    )
                    Text(
                        text = userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.DirectionsBus,
                    label = "Rutas activas",
                    value = activeRoutesCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.LocationOn,
                    label = "Paradas",
                    value = totalStops.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color(0xFFFED7A0)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MapSection(
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Mapa de Rutas",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (showPlaceholder) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Mapa",
                            tint = Color(0xFFFEA604),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Google Maps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "Implementar con API de Google",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            } else {
                // TODO: Integrar Google Maps aquí
                Text("Google Maps API")
            }
        }
    }
}

@Composable
private fun RoutesSection(
    routes: List<RouteInfo>,
    onSeeAllClick: () -> Unit,
    onRouteClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rutas en Servicio",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )
            TextButton(onClick = onSeeAllClick) {
                Text(
                    text = "Ver todas",
                    color = Color(0xFFFEA604),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            routes.forEach { route ->
                RouteCard(
                    route = route,
                    onClick = onRouteClick
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = route.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = route.status,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun NotificationBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEA604)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificación",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Mantente informado sobre cambios en los horarios y rutas disponibles.",
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentScreen: String,
    onNavigateToHome: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToQR: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = onNavigateToHome,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "routes",
            onClick = onNavigateToRoutes,
            icon = {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Rutas"
                )
            },
            label = { Text("Rutas", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "qr",
            onClick = onNavigateToQR,
            icon = {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "QR"
                )
            },
            label = { Text("QR", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )

        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = onNavigateToProfile,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )
    }
}


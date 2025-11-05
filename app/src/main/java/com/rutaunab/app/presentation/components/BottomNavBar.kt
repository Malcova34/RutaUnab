package com.rutaunab.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigateToHome: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToQR: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Inicio
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = onNavigateToHome,
            icon = {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )

        // Rutas
        NavigationBarItem(
            selected = currentScreen == "routes",
            onClick = onNavigateToRoutes,
            icon = {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Rutas"
                )
            },
            label = { Text("Rutas", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )

        // QR - Tab central con círculo naranja grande
        NavigationBarItem(
            selected = currentScreen == "qr",
            onClick = onNavigateToQR,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFFEA604), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            label = { Text("QR", fontSize = 10.sp, color = Color(0xFF9CA3AF)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFF9CA3AF),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )

        // Mapa
        NavigationBarItem(
            selected = currentScreen == "map",
            onClick = onNavigateToMap,
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Mapa"
                )
            },
            label = { Text("Mapa", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFFFF7ED)
            )
        )

        // Perfil
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = onNavigateToProfile,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil", fontSize = 10.sp) },
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


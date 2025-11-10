 package com.rutaunab.app.presentation.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 fun DriverProfileScreen(
     viewModel: DriverProfileViewModel = viewModel(),
     onNavigateToScanner: () -> Unit = {},
     onNavigateToProfile: () -> Unit = {},
     onLogout: () -> Unit = {}
 ) {
     val uiState by viewModel.uiState.collectAsState()

     Scaffold(
         containerColor = Color(0xFFFFF8F0) // Fondo general uniforme
     ) { paddingValues ->
         LazyColumn(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(paddingValues), // Respeta el espacio del navbar automáticamente
             horizontalAlignment = Alignment.CenterHorizontally
         ) {
             // ✅ Header: ahora va hasta arriba sin bordes extra
             item {
                 HeaderSection(
                     driverName = uiState.driver?.name ?: "Conductor",
                     driverId = uiState.driver?.id ?: "DRV-2024-001",
                     busNumber = uiState.driver?.driverInfo?.assignedBusId ?: "Bus 101"
                 )
             }

             item { Spacer(modifier = Modifier.height(16.dp)) }

             // ✅ Tarjeta de estadísticas
             item {
                 StatisticsCard(
                     tripsToday = uiState.tripsToday,
                     hoursActive = uiState.hoursActive,
                     passengersToday = uiState.passengersToday
                 )
             }

             item { Spacer(modifier = Modifier.height(16.dp)) }

             // ✅ Info personal
             item { DriverInfoCard(driver = uiState.driver) }

             item { Spacer(modifier = Modifier.height(16.dp)) }

             // ✅ Horario
             item {
                 Text(
                     text = "Horario de Hoy",
                     fontSize = 16.sp,
                     fontWeight = FontWeight.Bold,
                     color = Color(0xFF1F2937),
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(horizontal = 20.dp)
                 )
             }

             item { Spacer(modifier = Modifier.height(12.dp)) }

             items(uiState.schedule) { shift ->
                 ScheduleShiftCard(shift)
                 Spacer(modifier = Modifier.height(8.dp))
             }

             /*item {
                 Text(
                     text = "Viajes Recientes",
                     fontSize = 16.sp,
                     fontWeight = FontWeight.Bold,
                     color = Color(0xFF1F2937),
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(horizontal = 20.dp)
                 )
             }

             item { Spacer(modifier = Modifier.height(12.dp)) }

             items(uiState.recentTrips.take(3)) { trip ->
                 TripCard(trip)
                 Spacer(modifier = Modifier.height(8.dp))
             }
               */
             // ✅ Quick Access to Scanner
             item {
                 QuickAccessButton(onNavigateToScanner = onNavigateToScanner)
             }

             item { Spacer(modifier = Modifier.height(16.dp)) }

             // ✅ Logout Button
             item {
                 OutlinedButton(
                     onClick = {
                         viewModel.logout(onLogoutComplete = onLogout)
                     },
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(horizontal = 20.dp),
                     colors = ButtonDefaults.outlinedButtonColors(
                         contentColor = Color(0xFFEF4444)
                     ),
                     border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                 ) {
                     Icon(
                         imageVector = Icons.Default.Logout,
                         contentDescription = "Cerrar Sesión",
                         modifier = Modifier.size(20.dp)
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     Text("Cerrar Sesión")
                 }
             }

             item { Spacer(modifier = Modifier.height(40.dp)) } // margen extra para no chocar con el navbar
         }

     }
 }


@Composable
private fun HeaderSection(
    driverName: String,
    driverId: String,
    busNumber: String
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
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Perfil Conductor",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981)
                ) {
                    Text(
                        text = "Activo",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "Información del conductor",
                fontSize = 14.sp,
                color = Color(0xFFFFF3E0)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEA604)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = driverName.split(" ")
                                .mapNotNull { it.firstOrNull() }
                                .take(2)
                                .joinToString("")
                                .uppercase()
                                .ifEmpty { "DR" },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = driverName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = Color(0xFFFEA604),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = busNumber,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ID: $driverId",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    tripsToday: Int,
    hoursActive: String,
    passengersToday: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFFFEA604),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Estadísticas de Hoy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(Icons.Default.DirectionsBus, tripsToday.toString(), "Viajes hoy")
                StatItem(Icons.Default.AccessTime, hoursActive, "Horas activo")
                StatItem(Icons.Default.People, passengersToday.toString(), "Pasajeros")
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFEA604),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun DriverInfoCard(driver: com.rutaunab.app.domain.model.User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información Personal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow(
                icon = Icons.Default.Badge,
                label = "Licencia de Conducir",
                value = driver?.driverInfo?.licenseNumber ?: "A1-12345678"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.Phone,
                label = "Teléfono",
                value = "+56 9 8765 4321"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.Email,
                label = "Correo Electrónico",
                value = driver?.email ?: "conductor@unab.cl"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.DirectionsBus,
                label = "Bus Asignado",
                value = driver?.driverInfo?.assignedBusId ?: "Bus 101"
            )
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFEA604),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ScheduleShiftCard(shift: ScheduleShift) {
    val backgroundColor = when (shift.status) {
        ShiftStatus.COMPLETED -> Color(0xFFF0FDF4)
        ShiftStatus.IN_PROGRESS -> Color(0xFFFFF3E0)
        ShiftStatus.PENDING -> Color(0xFFF9FAFB)
    }

    val borderColor = when (shift.status) {
        ShiftStatus.COMPLETED -> Color(0xFFBBF7D0)
        ShiftStatus.IN_PROGRESS -> Color(0xFFFED7AA)
        ShiftStatus.PENDING -> Color(0xFFE5E7EB)
    }

    val badgeColor = when (shift.status) {
        ShiftStatus.COMPLETED -> Color(0xFF10B981)
        ShiftStatus.IN_PROGRESS -> Color(0xFFFEA604)
        ShiftStatus.PENDING -> Color(0xFF6B7280)
    }

    val statusText = when (shift.status) {
        ShiftStatus.COMPLETED -> "Completado"
        ShiftStatus.IN_PROGRESS -> "En curso"
        ShiftStatus.PENDING -> "Pendiente"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = shift.time,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shift.route,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEA604)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = trip.route,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = trip.time,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color(0xFFFEA604),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = trip.passengers.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937)
                        )
                    }
                    Text(
                        text = "pasajeros",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFFEA604),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trip.from,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "→",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = trip.to,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(onNavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { /* TODO: Edit profile */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEA604)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Editar Perfil",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF6B7280)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Volver al Inicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickAccessButton(onNavigateToScanner: () -> Unit) {
    Surface(
        onClick = onNavigateToScanner,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFEA604), Color(0xFFFEB92C))
                    )
                )
                .padding(vertical = 16.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ir al Escáner QR",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Modificador personalizado para hacer click sin ripple effect
@Composable
private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}

@Composable
private fun DriverBottomNavBar(
    currentScreen: String,
    onNavigateToScanner: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = currentScreen == "scanner",
            onClick = onNavigateToScanner,
            icon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escáner QR"
                )
            },
            label = { Text("Escáner") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9E9E9E),
                unselectedTextColor = Color(0xFF9E9E9E)
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
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFEA604),
                selectedTextColor = Color(0xFFFEA604),
                unselectedIconColor = Color(0xFF9E9E9E),
                unselectedTextColor = Color(0xFF9E9E9E)
            )
        )
    }
}

//a
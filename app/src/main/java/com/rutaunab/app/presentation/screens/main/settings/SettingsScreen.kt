package com.rutaunab.app.presentation.screens.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.rutaunab.app.presentation.components.BottomNavBar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToQR: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentScreen = "settings",
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
                    SettingsHeader(onBackClick = onNavigateToHome)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // User Card
                item {
                    UserCard(
                        userName = uiState.user?.name ?: "Usuario",
                        userEmail = uiState.user?.email ?: "correo@unab.cl",
                        onNavigateToProfile = onNavigateToProfile
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Notificaciones Section
                item {
                    SectionTitle(title = "Notificaciones")
                }

                item {
                    SettingsCard {
                        SettingToggleItem(
                            icon = Icons.Default.Notifications,
                            label = "Notificaciones push",
                            checked = uiState.pushNotificationsEnabled,
                            onCheckedChange = viewModel::togglePushNotifications
                        )
                        Divider(color = Color(0xFFE5E7EB))
                        SettingToggleItem(
                            icon = Icons.Default.Notifications,
                            label = "Alertas de rutas",
                            checked = uiState.routeAlertsEnabled,
                            onCheckedChange = viewModel::toggleRouteAlerts
                        )
                        Divider(color = Color(0xFFE5E7EB))
                        SettingToggleItem(
                            icon = Icons.Default.Notifications,
                            label = "Recordatorios de horarios",
                            checked = uiState.scheduleRemindersEnabled,
                            onCheckedChange = viewModel::toggleScheduleReminders
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Apariencia Section
                item {
                    SectionTitle(title = "Apariencia")
                }

                item {
                    SettingsCard {
                        SettingToggleItem(
                            icon = Icons.Default.DarkMode,
                            label = "Modo Oscuro",
                            description = "Tema oscuro para la aplicación",
                            checked = uiState.darkModeEnabled,
                            onCheckedChange = viewModel::toggleDarkMode
                        )
                        Divider(color = Color(0xFFE5E7EB))
                        SettingLanguageItem(
                            icon = Icons.Default.Language,
                            label = "Idioma",
                            currentLanguage = if (uiState.language == "es") "Español" else "English",
                            onClick = { viewModel.showLanguageDialog(true) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Cuenta Section
                item {
                    SectionTitle(title = "Cuenta")
                }

                item {
                    SettingsCard {
                        SettingNavigationItem(
                            icon = Icons.Default.Security,
                            label = "Privacidad",
                            onClick = { /* TODO */ }
                        )
                        Divider(color = Color(0xFFE5E7EB))
                        SettingNavigationItem(
                            icon = Icons.Default.HelpOutline,
                            label = "Ayuda y soporte",
                            onClick = { /* TODO */ }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // App Info
                item {
                    AppInfoCard()
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Logout Button
                item {
                    LogoutButton(
                        isLoggingOut = uiState.isLoggingOut,
                        onClick = { showLogoutDialog = true }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Legal Links
                item {
                    LegalLinks()
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color(0xFFFEA604)
                )
            },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres cerrar sesión?",
                    color = Color(0xFF6B7280)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Cerrar Sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Color(0xFF6B7280))
                }
            }
        )
    }
    
    // Language Selection Dialog
    if (uiState.showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showLanguageDialog(false) },
            containerColor = Color.White,
            icon = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color(0xFFFEA604)
                )
            },
            title = {
                Text(
                    text = "Seleccionar Idioma",
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LanguageOption(
                        language = "Español",
                        isSelected = uiState.language == "es",
                        onClick = { viewModel.changeLanguage("es") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LanguageOption(
                        language = "English",
                        isSelected = uiState.language == "en",
                        onClick = { viewModel.changeLanguage("en") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.showLanguageDialog(false) }) {
                    Text("Cancelar", color = Color(0xFF6B7280))
                }
            }
        )
    }
}

@Composable
private fun SettingsHeader(onBackClick: () -> Unit) {
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
            .padding(24.dp)
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
                    text = "Configuración",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Personaliza tu experiencia",
                    fontSize = 14.sp,
                    color = Color(0xFFFFF3E0)
                )
            }
        }
    }
}

@Composable
private fun UserCard(
    userName: String,
    userEmail: String,
    onNavigateToProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onNavigateToProfile),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEA604)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingToggleItem(
    icon: ImageVector,
    label: String,
    description: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFEA604),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD)
            )
        )
    }
}

@Composable
private fun SettingNavigationItem(
    icon: ImageVector,
    label: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
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
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = Color(0xFFFEA604),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ruta UNAB",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Text(
                text = "Versión 1.0.0",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "© 2024 Universidad Andrés Bello",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun LogoutButton(
    isLoggingOut: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp),
        enabled = !isLoggingOut,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFEF4444)
        ),
        border = ButtonDefaults.outlinedButtonBorder,
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoggingOut) {
            CircularProgressIndicator(
                color = Color(0xFFEF4444),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LegalLinks() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { /* TODO */ }) {
            Text(
                text = "Términos de Servicio",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }

        Text(
            text = "•",
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        TextButton(onClick = { /* TODO */ }) {
            Text(
                text = "Política de Privacidad",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun SettingLanguageItem(
    icon: ImageVector,
    label: String,
    currentLanguage: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
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

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = currentLanguage,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFF9FAFB)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFEA604))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = language,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color(0xFFFEA604) else Color(0xFF1F2937)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFFFEA604),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

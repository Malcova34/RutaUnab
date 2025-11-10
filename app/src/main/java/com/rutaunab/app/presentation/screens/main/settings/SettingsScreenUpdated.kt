package com.rutaunab.app.presentation.screens.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
fun SettingsScreenNew(
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
                currentScreen = "profile",
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                item {
                    HeaderSection(userName = uiState.user?.name ?: "Usuario")
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Notificaciones Section
                item {
                    SectionTitle("Notificaciones")
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    NotificationsCard(
                        pushNotificationsEnabled = uiState.pushNotificationsEnabled,
                        routeAlertsEnabled = uiState.routeAlertsEnabled,
                        scheduleRemindersEnabled = uiState.scheduleRemindersEnabled,
                        onTogglePushNotifications = viewModel::togglePushNotifications,
                        onToggleRouteAlerts = viewModel::toggleRouteAlerts,
                        onToggleScheduleReminders = viewModel::toggleScheduleReminders
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Apariencia Section
                item {
                    SectionTitle("Apariencia")
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    AppearanceCard(
                        darkModeEnabled = uiState.darkModeEnabled,
                        language = uiState.language,
                        onToggleDarkMode = viewModel::toggleDarkMode,
                        onLanguageClick = { viewModel.showLanguageDialog(true) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Cuenta Section
                item {
                    SectionTitle("Cuenta")
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    AccountCard(
                        userName = uiState.user?.name ?: "Usuario",
                        userEmail = uiState.user?.email ?: "",
                        onLogoutClick = { showLogoutDialog = true }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // App Info
                item {
                    AppInfoCard()
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            LogoutDialog(
                onConfirm = {
                    showLogoutDialog = false
                    viewModel.logout(onLogout)
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        // Language Selection Dialog
        if (uiState.showLanguageDialog) {
            LanguageDialog(
                currentLanguage = uiState.language,
                onLanguageSelected = { viewModel.changeLanguage(it) },
                onDismiss = { viewModel.showLanguageDialog(false) }
            )
        }
    }
}

@Composable
private fun HeaderSection(userName: String) {
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
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFEA604)
                )
            }

            Column {
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Configuración",
                    fontSize = 14.sp,
                    color = Color(0xFFFFF3E0)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun NotificationsCard(
    pushNotificationsEnabled: Boolean,
    routeAlertsEnabled: Boolean,
    scheduleRemindersEnabled: Boolean,
    onTogglePushNotifications: (Boolean) -> Unit,
    onToggleRouteAlerts: (Boolean) -> Unit,
    onToggleScheduleReminders: (Boolean) -> Unit
) {
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
                .padding(16.dp)
        ) {
            // Push Notifications
            SettingItem(
                icon = Icons.Default.Notifications,
                title = "Notificaciones Push",
                description = "Recibe alertas importantes",
                isSwitch = true,
                switchState = pushNotificationsEnabled,
                onSwitchChange = onTogglePushNotifications
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))

            // Route Alerts
            SettingItem(
                icon = Icons.Default.LocationOn,
                title = "Alertas de Ruta",
                description = "Cuando la ruta esté cerca",
                isSwitch = true,
                switchState = routeAlertsEnabled,
                onSwitchChange = onToggleRouteAlerts
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))

            // Schedule Reminders
            SettingItem(
                icon = Icons.Default.Schedule,
                title = "Recordatorios de Horario",
                description = "Notificaciones de horarios",
                isSwitch = true,
                switchState = scheduleRemindersEnabled,
                onSwitchChange = onToggleScheduleReminders
            )
        }
    }
}

@Composable
private fun AppearanceCard(
    darkModeEnabled: Boolean,
    language: String,
    onToggleDarkMode: (Boolean) -> Unit,
    onLanguageClick: () -> Unit
) {
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
                .padding(16.dp)
        ) {
            // Dark Mode
            SettingItem(
                icon = Icons.Default.DarkMode,
                title = "Modo Oscuro",
                description = "Tema oscuro para la aplicación",
                isSwitch = true,
                switchState = darkModeEnabled,
                onSwitchChange = onToggleDarkMode
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))

            // Language
            val languageText = if (language == "es") "Español" else "English"
            SettingItem(
                icon = Icons.Default.Language,
                title = "Idioma",
                description = languageText,
                isSwitch = false,
                onClick = onLanguageClick
            )
        }
    }
}

@Composable
private fun AccountCard(
    userName: String,
    userEmail: String,
    onLogoutClick: () -> Unit
) {
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
                .padding(16.dp)
        ) {
            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFFFEA604),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithoutRipple(onClick = onLogoutClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cerrar Sesión",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFDC2626)
                    )
                    Text(
                        text = "Salir de tu cuenta",
                        fontSize = 13.sp,
                        color = Color(0xFFEF4444)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    isSwitch: Boolean,
    switchState: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!isSwitch && onClick != null) {
                    Modifier.clickableWithoutRipple(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(vertical = 4.dp),
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
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        }

        if (isSwitch) {
            Switch(
                checked = switchState,
                onCheckedChange = onSwitchChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFFEA604),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE5E7EB)
                )
            )
        } else {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ruta UNAB",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Versión 1.0.0",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "© 2024 Universidad Andrés Bello",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "¿Cerrar sesión?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas cerrar tu sesión?",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "Cerrar sesión",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = Color(0xFF6B7280)
                )
            }
        }
    )
}

@Composable
private fun LanguageDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Seleccionar Idioma",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        },
        text = {
            Column {
                // Español
                LanguageOption(
                    language = "Español",
                    isSelected = currentLanguage == "es",
                    onClick = { onLanguageSelected("es") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // English
                LanguageOption(
                    language = "English",
                    isSelected = currentLanguage == "en",
                    onClick = { onLanguageSelected("en") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cerrar",
                    color = Color(0xFFFEA604)
                )
            }
        }
    )
}

@Composable
private fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithoutRipple(onClick = onClick)
            .background(
                color = if (isSelected) Color(0xFFFFF3E0) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFFFEA604),
                unselectedColor = Color(0xFF9CA3AF)
            )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = language,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF1F2937) else Color(0xFF6B7280)
        )
    }
}

@Composable
private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}


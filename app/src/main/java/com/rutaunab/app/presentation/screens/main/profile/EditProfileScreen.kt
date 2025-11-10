package com.rutaunab.app.presentation.screens.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar de vuelta cuando se guarde exitosamente
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Editar Perfil",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Actualiza tu información",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFE0B2)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFEA604),
                    titleContentColor = Color.White
                )
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
                            Color(0xFFFFF3E0),
                            Color.White,
                            Color(0xFFFFF3E0)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
            // Avatar Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AvatarSection(userName = uiState.name)
            }

            // Edit Form
            item {
                Spacer(modifier = Modifier.height(24.dp))
                EditFormCard(
                    name = uiState.name,
                    email = uiState.email,
                    studentId = uiState.studentId,
                    career = uiState.career,
                    onNameChange = viewModel::onNameChanged,
                    onEmailChange = viewModel::onEmailChanged,
                    onCareerChange = viewModel::onCareerChanged
                )
            }

            // Action Buttons
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ActionButtons(
                    isSaving = uiState.isSaving,
                    onSave = viewModel::saveProfile,
                    onCancel = onNavigateBack
                )
            }

            // Info Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                InfoCard()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Error Message
            if (uiState.errorMessage != null) {
                item {
                    ErrorMessage(message = uiState.errorMessage)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    }
}

@Composable
private fun AvatarSection(userName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(96.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEA604)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.split(" ")
                            .mapNotNull { it.firstOrNull() }
                            .take(2)
                            .joinToString("")
                            .uppercase()
                            .ifEmpty { "UN" },
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Camera button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEA604)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Toca para cambiar foto",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun EditFormCard(
    name: String,
    email: String,
    studentId: String,
    career: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onCareerChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nombre
            InputField(
                value = name,
                onValueChange = onNameChange,
                label = "Nombre Completo",
                icon = Icons.Default.Person,
                placeholder = "Ingresa tu nombre completo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            InputField(
                value = email,
                onValueChange = onEmailChange,
                label = "Correo Electrónico",
                icon = Icons.Default.Email,
                placeholder = "correo@unab.cl"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Student ID (disabled)
            InputField(
                value = studentId,
                onValueChange = {},
                label = "ID Estudiante",
                icon = Icons.Default.CreditCard,
                placeholder = "202012345",
                enabled = false
            )

            Text(
                text = "El ID de estudiante no se puede modificar",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Career
            InputField(
                value = career,
                onValueChange = onCareerChange,
                label = "Carrera",
                icon = Icons.Default.School,
                placeholder = "Ingeniería Civil"
            )
        }
    }
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFEA604),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = placeholder,
                    color = Color(0xFF9CA3AF)
                )
            },
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937),
                disabledTextColor = Color(0xFF6B7280),
                focusedBorderColor = Color(0xFFFEA604),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                disabledBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFFFEA604),
                cursorColor = Color(0xFFFEA604)
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun ActionButtons(
    isSaving: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFEA604),
                disabledContainerColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar Cambios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel Button
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF424242)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Cancelar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFFEA604),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Información",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Asegúrate de que tu información esté actualizada para recibir notificaciones importantes sobre el servicio de transporte.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String?) {
    if (message != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEBEE)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}


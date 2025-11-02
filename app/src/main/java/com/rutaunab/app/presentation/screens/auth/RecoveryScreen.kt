package com.rutaunab.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun RecoveryScreen(onClickBack: () -> Unit = {}) {
    var inputEmail by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF8F0),
                        Color.White,
                        Color(0xFFFFF8F0)
                    )
                )
            )
    ) {
        if (emailSent) {
            // Pantalla de confirmación
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Icono de éxito
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Éxito",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        // Título
                        Text(
                            text = "¡Correo Enviado!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )

                        // Mensaje
                        Text(
                            text = buildString {
                                append("Hemos enviado un enlace para restablecer tu contraseña a ")
                            },
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = inputEmail,
                            fontSize = 15.sp,
                            color = Color(0xFFFEA604),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        // Caja de información
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF8F0),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFFED7AA)
                            )
                        ) {
                            Text(
                                text = "Por favor revisa tu bandeja de entrada y sigue las instrucciones para restablecer tu contraseña.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Botón volver al login
                        Button(
                            onClick = onClickBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEA604)
                            )
                        ) {
                            Text(
                                text = "Volver al Inicio de Sesión",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        } else {
            // Formulario de recuperación
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header naranja compacto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFEA604),
                                        Color(0xFFFEB92C)
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            onClick = onClickBack
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Card con formulario
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Icono de email
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                color = Color(0xFFFFF3E0)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = Color(0xFFFEA604),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            // Título y descripción
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Recuperar Contraseña",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Campo de email
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Correo Electrónico",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151)
                                )
                                OutlinedTextField(
                                    value = inputEmail,
                                    onValueChange = { inputEmail = it },
                                    placeholder = { Text("ejemplo@unab.cl") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = "Email",
                                            tint = Color(0xFF9CA3AF)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFEA604),
                                        unfocusedBorderColor = Color(0xFFE5E7EB)
                                    )
                                )
                            }

                            // Botón enviar
                            Button(
                                onClick = {
                                    if (inputEmail.isNotEmpty()) {
                                        emailSent = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFEA604)
                                )
                            ) {
                                Text(
                                    text = "Enviar Enlace de Recuperación",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Enlace volver
                            Text(
                                text = "Volver al inicio de sesión",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier
                                    .clickable { onClickBack() }
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

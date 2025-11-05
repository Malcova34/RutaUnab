package com.rutaunab.app.presentation.screens.auth.login

import com.rutaunab.app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue



@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onClickRegister: () -> Unit = {},
    onClickRecovery: () -> Unit = {},
    onSuccesfullLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Mostrar mensaje de éxito y navegar
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            snackbarHostState.showSnackbar(
                message = "¡Bienvenido! Ingresando...",
                duration = SnackbarDuration.Short
            )
            kotlinx.coroutines.delay(1000) // Esperar 1 segundo
            viewModel.resetLoginSuccess()
            onSuccesfullLogin()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con gradiente naranja
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFEA604),
                                Color(0xFFFEB92C)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp)
                    )
                    .padding(top = 80.dp, bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Logo circular como en splash
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.img_icon_unab),
                            contentDescription = "Logo UNAB",
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }
            }

            // Card blanco con formulario (superpuesto al header)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-64).dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Campo de Email
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Correo Electrónico",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            isError = uiState.isEmailError,
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
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFEA604),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedTextColor = Color(0xFF1F2937),
                                unfocusedTextColor = Color(0xFF1F2937),
                                cursorColor = Color(0xFFFEA604)
                            )
                        )
                    }

                    // Campo de Contraseña
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Contraseña",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            isError = uiState.isPasswordError,
                            placeholder = { Text("••••••••") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Contraseña",
                                    tint = Color(0xFF9CA3AF)
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFEA604),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedTextColor = Color(0xFF1F2937),
                                unfocusedTextColor = Color(0xFF1F2937),
                                cursorColor = Color(0xFFFEA604)
                            )
                        )
                    }

                    // Enlace "¿Olvidaste tu contraseña?"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            fontSize = 13.sp,
                            color = Color(0xFFFEA604),
                            modifier = Modifier
                                .clickable { onClickRecovery() }
                                .padding(vertical = 4.dp)
                        )
                    }

                    // Mostrar errores de validación
                    uiState.emailErrorMessage?.let { errorMsg ->
                        Text(
                            text = errorMsg,
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    uiState.passwordErrorMessage?.let { errorMsg ->
                        Text(
                            text = errorMsg,
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    // Mostrar error general
                    uiState.errorMessage?.let { errorMsg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                        ) {
                            Text(
                                text = errorMsg,
                                fontSize = 13.sp,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Botón de Iniciar Sesión
                    Button(
                        onClick = { viewModel.onLoginClick() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEA604)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Divisor con "o"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE5E7EB)
                        )
                        Text(
                            text = "o",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE5E7EB)
                        )
                    }

                    // Enlace para Registrarse
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClickRegister() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF6B7280))) {
                                    append("¿No tienes cuenta? ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFFEA604),
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append("Regístrate aquí")
                                }
                            },
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        }
    }
}


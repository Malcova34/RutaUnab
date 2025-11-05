package com.rutaunab.app.presentation.screens.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onClickBack: () -> Unit = {},
    onSuccesfulRegister: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Mostrar mensaje de éxito y navegar
    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            snackbarHostState.showSnackbar(
                message = "¡Registro exitoso! Redirigiendo...",
                duration = SnackbarDuration.Short
            )
            kotlinx.coroutines.delay(1500) // Esperar 1.5 segundos para que vea el mensaje
            viewModel.resetRegisterSuccess()
            onSuccesfulRegister()
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
            // Header naranja con botón de volver (compacto)
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
                // Botón de volver
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

            // Card blanco con formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 32.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Todos los campos de texto permanecen igual...
                    // Campo Nombre
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nombre Completo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            placeholder = { Text("Juan Pérez") },
                            leadingIcon = { Icon(Icons.Default.Person, "Nombre", tint = Color(0xFF9CA3AF)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
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
                    
                    // Email
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Correo Institucional", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            placeholder = { Text("ejemplo@unab.cl") },
                            leadingIcon = { Icon(Icons.Default.Email, "Email", tint = Color(0xFF9CA3AF)) },
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

                    // ID UNAB
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ID UNAB", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                        OutlinedTextField(
                            value = uiState.studentId,
                            onValueChange = { viewModel.onStudentIdChange(it) },
                            placeholder = { Text("202012345") },
                            leadingIcon = { Icon(Icons.Default.Badge, "ID", tint = Color(0xFF9CA3AF)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
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

                    // Carrera
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Carrera", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                        OutlinedTextField(
                            value = uiState.career,
                            onValueChange = { viewModel.onCareerChange(it) },
                            placeholder = { Text("Ingeniería Civil Informática") },
                            leadingIcon = { Icon(Icons.Default.School, "Carrera", tint = Color(0xFF9CA3AF)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
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

                    // Contraseña
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            placeholder = { Text("••••••••") },
                            leadingIcon = { Icon(Icons.Default.Lock, "Contraseña", tint = Color(0xFF9CA3AF)) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
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

                    // Confirmar Contraseña
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Confirmar Contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                        OutlinedTextField(
                            value = uiState.passwordConfirmation,
                            onValueChange = { viewModel.onPasswordConfirmationChange(it) },
                            placeholder = { Text("••••••••") },
                            leadingIcon = { Icon(Icons.Default.Lock, "Confirmar", tint = Color(0xFF9CA3AF)) },
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

                    // Términos
                    Text(
                        text = "Al registrarte, aceptas nuestros Términos de Servicio y Política de Privacidad",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    // Mostrar error
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

                    // Botón Registrar
                    Button(
                        onClick = { viewModel.onRegisterClick() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEA604))
                    ) {
                        if (uiState.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Creando cuenta...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text("Crear Cuenta", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Link a login
                    Box(
                        modifier = Modifier.fillMaxWidth().clickable { onClickBack() }.padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFF6B7280))) { append("¿Ya tienes cuenta? ") }
                                withStyle(SpanStyle(color = Color(0xFFFEA604), fontWeight = FontWeight.Medium)) { append("Inicia sesión") }
                            },
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        }
    }
}


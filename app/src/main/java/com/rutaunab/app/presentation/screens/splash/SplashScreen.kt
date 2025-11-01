package com.rutaunab.app.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import com.rutaunab.app.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    // Estados para controlar animaciones secuenciales
    var showLogo by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    // Animación infinita de bounce para el logo
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Animación de pulsación para el halo
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Lógica de animación con delays
    LaunchedEffect(Unit) {
        delay(50)
        showLogo = true
        delay(200)
        showText = true
        delay(1800) // tiempo total del splash
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFEA604),
                        Color(0xFFFEA604),
                        Color(0xFFFEB92C)
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Animation con bounce
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(animationSpec = tween(600)) +
                        scaleIn(
                            initialScale = 0.5f,
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.offset(y = bounceOffset.dp)
                ) {
                    // Halo blanco difuminado con pulsación
                    Box(
                        modifier = Modifier
                            .size(140.dp * pulseScale)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .blur(30.dp)
                    )
                    // Círculo principal
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
                                colorFilter = ColorFilter.tint(Color(0xFFFEA604)),
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nombre de la app con animación de deslizamiento
            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Ruta UNAB",
                        fontSize = 48.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Sistema de Transporte Universitario",
                        color = Color(0xFFFFF3E0),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
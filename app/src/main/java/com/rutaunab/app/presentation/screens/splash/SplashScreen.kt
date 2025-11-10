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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(
    onFinished: (hasSession: Boolean, userType: String?) -> Unit = { _, _ -> }
) {
    // --- Estados para controlar animaciones secuenciales
    var showLogo by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    
    // Context para SessionManager
    val context = androidx.compose.ui.platform.LocalContext.current

    // --- Animación infinita de rebote (bounce) - reduje el desplazamiento
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // --- Animación de pulsación (halo)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // --- Secuencia de aparición y transición
    LaunchedEffect(Unit) {
        delay(50)
        showLogo = true
        delay(180)
        showText = true
        delay(1800)
        // Verificar sesión
        val sessionManager = com.rutaunab.app.data.local.SessionManager.getInstance(context)
        val hasSession = sessionManager.isSessionValid()
        val userType = sessionManager.getUserType()
        onFinished(hasSession, userType)
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
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .padding(top = 24.dp)
            .graphicsLayer(clip = false),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO CON ANIMACIÓN ---
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(animationSpec = tween(600)) +
                        scaleIn(
                            initialScale = 0.6f,
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        )
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(y = bounceOffset.dp)
                        .graphicsLayer(clip = false)
                ) {
                    // Halo blanco difuminado
                    Box(
                        modifier = Modifier
                            .size((130.dp * pulseScale).coerceAtLeast(120.dp))
                            .background(Color.White.copy(alpha = 0.22f), CircleShape)
                            .blur(24.dp)
                    )

                    // Círculo principal con el logo
                    Surface(
                        modifier = Modifier.size(112.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 10.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.img_icon_unab),
                                contentDescription = "Logo UNAB",
                                colorFilter = ColorFilter.tint(Color(0xFFFEA604)),
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- TEXTO CON ANIMACIÓN ---
            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(animationSpec = tween(550)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(550, easing = FastOutSlowInEasing)
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Ruta UNAB",
                        fontSize = 42.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Sistema de Transporte Universitario",
                        color = Color(0xFFFFF3E0),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

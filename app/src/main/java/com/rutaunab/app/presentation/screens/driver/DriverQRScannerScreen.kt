package com.rutaunab.app.presentation.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DriverQRScannerScreen(
    viewModel: DriverQRScannerViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Scaffold(
        bottomBar = {
            DriverBottomNavBar(
                currentScreen = "scanner",
                onNavigateToScanner = onNavigateToScanner,
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
                    HeaderSection()
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Scanner Card
                item {
                    ScannerCard(
                        viewModel = viewModel,
                        scanResult = uiState.scanResult,
                        cameraPermissionGranted = cameraPermissionState.status.isGranted,
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Stats Cards
                item {
                    StatsCards(
                        successScans = uiState.successScansToday,
                        rejectedScans = uiState.rejectedScansToday
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Recent Scans Title
                item {
                    Text(
                        text = "Últimos escaneos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(uiState.recentScans) { scan ->
                    RecentScanCard(scan)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Help Card
                item {
                    HelpCard()
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
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
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Escáner QR",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Escanea el código del estudiante",
                    fontSize = 14.sp,
                    color = Color(0xFFFFF3E0)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF10B981)
            ) {
                Text(
                    text = "En línea",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ScannerCard(
    viewModel: DriverQRScannerViewModel,
    scanResult: ScanResult?,
    cameraPermissionGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            // Camera Scanner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                if (cameraPermissionGranted) {
                    // Escáner QR real
                    val lifecycleOwner = LocalLifecycleOwner.current
                    
                    AndroidView(
                        factory = { context ->
                            DecoratedBarcodeView(context).apply {
                                val callback = object : BarcodeCallback {
                                    override fun barcodeResult(result: BarcodeResult?) {
                                        result?.text?.let { qrContent ->
                                            viewModel.onQRScanned(qrContent)
                                        }
                                    }
                                }
                                
                                barcodeView.decodeContinuous(callback)
                                resume()
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { view ->
                            view.resume()
                        }
                    )
                    
                    DisposableEffect(Unit) {
                        onDispose {
                            // Cleanup cuando se destruya la vista
                        }
                    }
                    
                    // Overlay con marco de escaneo
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .border(4.dp, Color(0xFFFEA604), RoundedCornerShape(16.dp))
                    )
                } else {
                    // Solicitar permiso de cámara
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Permiso de cámara requerido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Necesitamos acceso a tu cámara para escanear códigos QR",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEA604)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Permitir Cámara")
                        }
                    }
                }
                
                // Resultado del escaneo (overlay)
                scanResult?.let { result ->
                    ScanResultOverlay(
                        result = result,
                        onDismiss = { viewModel.clearScanResult() }
                    )
                }
            }

            // Instructions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Coloca el código QR dentro del marco",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "El escaneo es automático",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun ScanResultOverlay(
    result: ScanResult,
    onDismiss: () -> Unit
) {
    LaunchedEffect(result) {
        kotlinx.coroutines.delay(2500) // Mostrar por 2.5 segundos
        onDismiss()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        when (result) {
            is ScanResult.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Checkmark verde animado
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "✓ ACCESO VÁLIDO",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = result.studentName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
            }
            is ScanResult.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Error",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "✗ ACCESO DENEGADO",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = result.message,
                        fontSize = 16.sp,
                        color = Color(0xFFEF4444),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCards(successScans: Int, rejectedScans: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Success Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Accesos",
                        fontSize = 14.sp,
                        color = Color(0xFF065F46)
                    )
                }
                Text(
                    text = successScans.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46)
                )
                Text(
                    text = "Hoy",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981)
                )
            }
        }

        // Rejected Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rechazados",
                        fontSize = 14.sp,
                        color = Color(0xFF991B1B)
                    )
                }
                Text(
                    text = rejectedScans.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                )
                Text(
                    text = "Hoy",
                    fontSize = 12.sp,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
private fun RecentScanCard(scan: ScannedStudent) {
    val backgroundColor = if (scan.status == com.rutaunab.app.domain.model.ScanStatus.SUCCESS) 
        Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
    val iconColor = if (scan.status == com.rutaunab.app.domain.model.ScanStatus.SUCCESS) 
        Color(0xFF10B981) else Color(0xFFEF4444)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (scan.status == com.rutaunab.app.domain.model.ScanStatus.SUCCESS) 
                        Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "ID: ${scan.studentId}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Text(
                text = scan.time,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun HelpCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "💡", fontSize = 20.sp)

            Column {
                Text(
                    text = "Tip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mantén el dispositivo estable para un escaneo más rápido. El sistema valida automáticamente cada código QR.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )
            }
        }
    }
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

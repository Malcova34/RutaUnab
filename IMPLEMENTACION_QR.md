# 📱 Guía de Implementación del Sistema de QR

## ✅ **Lo que YA está hecho:**

1. ✅ **Modelos de datos** (`QRData`, `QRScan`, `ScanStatus`)
2. ✅ **Generador de QR** (`QRCodeGenerator`)
3. ✅ **Repositorio de validación** (`QRScanRepository`)
4. ✅ **QRViewModel actualizado** (genera QR con datos del usuario)

---

## 🔧 **Pasos que DEBES completar:**

### **Paso 1: Añadir Dependencias**

Abre `app/build.gradle.kts` y añade:

```kotlin
dependencies {
    // ... tus dependencias existentes ...
    
    // QR Code Generation & Scanning
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    
    // Gson para parsear JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // CameraX para el escáner (si no está ya)
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
}
```

Luego haz click en **"Sync Now"**.

---

### **Paso 2: Añadir Permisos de Cámara**

En `AndroidManifest.xml`, añade:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.camera" android:required="false" />
```

---

### **Paso 3: Actualizar QRScreen para mostrar el QR real**

El QR ya se está generando en el ViewModel. Solo necesitas mostrarlo en la UI.

En `QRScreen.kt`, actualiza la función `QRCodeCard`:

```kotlin
@Composable
private fun QRCodeCard(
    userName: String,
    qrBitmap: Bitmap?  // ← Cambia de qrCode: String a qrBitmap: Bitmap?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Tu Código de Acceso",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Muestra este código al conductor",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // QR Code Real
            if (qrBitmap != null) {
                Box(
                    modifier = Modifier
                        .size(256.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Código QR",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Placeholder mientras carga
                CircularProgressIndicator(
                    color = Color(0xFFFEA604)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Name
            Text(
                text = "Código válido para: $userName",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
```

Y actualiza donde se llama `QRCodeCard`:

```kotlin
item {
    QRCodeCard(
        userName = uiState.user?.name ?: "Usuario",
        qrBitmap = uiState.qrBitmap  // ← Pasar el bitmap en lugar del string
    )
}
```

Añade el import:

```kotlin
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import androidx.compose.foundation.Image
```

---

### **Paso 4: Implementar el Escáner en DriverQRScannerScreen**

Actualiza `DriverQRScannerViewModel.kt`:

```kotlin
package com.rutaunab.app.presentation.screens.driver

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.data.repository.QRScanRepository
import com.rutaunab.app.domain.model.QRScan
import com.rutaunab.app.domain.model.ScanStatus
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DriverQRScannerViewModel(
    private val context: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverQRScannerUiState())
    val uiState: StateFlow<DriverQRScannerUiState> = _uiState.asStateFlow()

    private val getCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
    
    private val qrScanRepository = QRScanRepository()

    init {
        loadDriverData()
        loadRecentScans()
    }

    private fun loadDriverData() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    val driver = result.data
                    _uiState.update { it.copy(currentDriver = driver) }
                }
                else -> {}
            }
        }
    }

    private fun loadRecentScans() {
        viewModelScope.launch {
            val driverId = _uiState.value.currentDriver?.id ?: return@launch
            
            when (val result = qrScanRepository.getDriverScans(driverId, 10)) {
                is Result.Success -> {
                    val scans = result.data.map { scan ->
                        ScannedStudent(
                            name = scan.studentName,
                            studentId = scan.studentId,
                            time = formatTime(scan.timestamp.toDate()),
                            status = if (scan.status == ScanStatus.SUCCESS) 
                                ScanStatus.SUCCESS else ScanStatus.INVALID_QR
                        )
                    }
                    _uiState.update { it.copy(recentScans = scans) }
                }
                else -> {}
            }
            
            // Cargar contadores de hoy
            val successCount = qrScanRepository.getTodayScansCount(driverId)
            _uiState.update { 
                it.copy(
                    successScansToday = successCount,
                    rejectedScansToday = 0 // TODO: Implementar contador de rechazados
                )
            }
        }
    }

    /**
     * Procesa un código QR escaneado
     */
    fun onQRScanned(qrContent: String) {
        val driver = _uiState.value.currentDriver
        if (driver == null) {
            _uiState.update { 
                it.copy(
                    scanResult = ScanResult.Error("Error: conductor no identificado")
                )
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            
            val result = qrScanRepository.validateAndRegisterScan(
                qrContent = qrContent,
                driverId = driver.id,
                driverName = driver.name,
                busId = driver.driverInfo?.assignedBusId ?: "N/A"
            )
            
            when (result) {
                is Result.Success -> {
                    val scan = result.data
                    _uiState.update { 
                        it.copy(
                            isScanning = false,
                            scanResult = ScanResult.Success(scan.studentName),
                            successScansToday = it.successScansToday + 1
                        )
                    }
                    // Recargar la lista de escaneos
                    loadRecentScans()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isScanning = false,
                            scanResult = ScanResult.Error(result.message ?: "Error desconocido"),
                            rejectedScansToday = it.rejectedScansToday + 1
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isScanning = false) }
                }
            }
        }
    }

    /**
     * Limpia el resultado del escaneo
     */
    fun clearScanResult() {
        _uiState.update { it.copy(scanResult = null) }
    }

    private fun formatTime(date: java.util.Date): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(date)
    }
}

sealed class ScanResult {
    data class Success(val studentName: String) : ScanResult()
    data class Error(val message: String) : ScanResult()
}
```

Actualiza `DriverQRScannerUiState.kt`:

```kotlin
package com.rutaunab.app.presentation.screens.driver

import com.rutaunab.app.domain.model.ScanStatus
import com.rutaunab.app.domain.model.User

data class DriverQRScannerUiState(
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val currentDriver: User? = null,
    val recentScans: List<ScannedStudent> = emptyList(),
    val successScansToday: Int = 0,
    val rejectedScansToday: Int = 0,
    val scanResult: ScanResult? = null
)

data class ScannedStudent(
    val name: String,
    val studentId: String,
    val time: String,
    val status: ScanStatus
)
```

---

### **Paso 5: Integrar el Escáner de Cámara**

Actualiza `DriverQRScannerScreen.kt` para reemplazar el placeholder con un escáner real:

```kotlin
@Composable
private fun ScannerCard(
    viewModel: DriverQRScannerViewModel,
    scanResult: ScanResult?
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    
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
                if (cameraPermissionState.status.isGranted) {
                    // Escáner QR real
                    AndroidView(
                        factory = { ctx ->
                            val previewView = CompoundBarcodeView(ctx).apply {
                                val capture = CaptureManager(context as AppCompatActivity, this)
                                capture.initializeFromIntent((context as AppCompatActivity).intent, null)
                                capture.decode()
                                this.decodeContinuous { result ->
                                    result.text?.let { qrContent ->
                                        viewModel.onQRScanned(qrContent)
                                    }
                                }
                                this.resume()
                            }
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
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
                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEA604)
                            )
                        ) {
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
        kotlinx.coroutines.delay(2000) // Mostrar por 2 segundos
        onDismiss()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        when (result) {
            is ScanResult.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Checkmark verde animado
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "✓ Válido",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.studentName,
                        fontSize = 16.sp,
                        color = Color.White
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
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "✗ Error",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.message,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
```

Añade los imports necesarios:

```kotlin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import androidx.appcompat.app.AppCompatActivity
```

---

### **Paso 6: Añadir Accompanist Permissions**

En `build.gradle.kts`:

```kotlin
implementation("com.google.accompanist:accompanist-permissions:0.32.0")
```

---

## 🎯 **Cómo Funciona:**

1. **Estudiante abre QRScreen** → Ve su QR generado con sus datos
2. **Conductor abre DriverQRScannerScreen** → Cámara lista para escanear
3. **Conductor escanea QR del estudiante** → Sistema valida:
   - ¿QR válido?
   - ¿No expirado?
   - ¿No duplicado?
4. **Si es válido** → ✅ Checkmark verde + Registra en Firestore
5. **Si es inválido** → ❌ Mensaje de error

---

## 📊 **Estructura de Base de Datos (Firestore):**

```
qr_scans/
  └── {scanId}/
       ├── studentId: "abc123"
       ├── studentName: "Juan Pérez"
       ├── driverId: "driver456"
       ├── driverName: "Carlos López"
       ├── busId: "Bus 101"
       ├── timestamp: Timestamp
       ├── qrData: "{...JSON del QR...}"
       └── status: "SUCCESS"
```

---

## ✅ **Checklist Final:**

- [ ] Dependencias añadidas y sincronizadas
- [ ] Permisos de cámara en AndroidManifest
- [ ] QRScreen muestra QR real (no placeholder)
- [ ] DriverQRScannerScreen con cámara funcional
- [ ] Validación funcionando (checkmark verde/rojo)
- [ ] Datos guardándose en Firestore
- [ ] Probado en dispositivo real (no emulador)

---

¡Con esto tendrás un sistema completo de QR funcional! 🎉


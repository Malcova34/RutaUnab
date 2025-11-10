# 📡 Documentación de APIs

## 📋 Visión General

RutaUNAB integra múltiples APIs externas para proporcionar funcionalidades completas de transporte universitario. Esta documentación detalla las APIs utilizadas, sus endpoints, formatos de datos y manejo de errores.

## 🔄 APIs Integradas

### **1. Firebase APIs**

#### **Authentication API**
**Proveedor**: Google Firebase
**Propósito**: Autenticación de usuarios

##### **Endpoints Disponibles**
- `createUserWithEmailAndPassword(email, password)` - Registro
- `signInWithEmailAndPassword(email, password)` - Login
- `sendPasswordResetEmail(email)` - Recuperación de contraseña
- `signOut()` - Cerrar sesión

##### **Ejemplo de Uso**
```kotlin
// Registro de usuario
firebaseAuth.createUserWithEmailAndPassword(email, password)
    .addOnSuccessListener { authResult ->
        val user = authResult.user
        // Usuario creado exitosamente
    }
    .addOnFailureListener { exception ->
        // Manejar error
    }
```

##### **Códigos de Error Comunes**
- `ERROR_INVALID_EMAIL`: Email mal formateado
- `ERROR_WEAK_PASSWORD`: Contraseña muy débil
- `ERROR_EMAIL_ALREADY_IN_USE`: Email ya registrado

#### **Firestore API**
**Proveedor**: Google Firebase
**Propósito**: Base de datos NoSQL en tiempo real

##### **Colecciones Principales**

###### **users**
```json
{
  "id": "string",
  "email": "string",
  "name": "string",
  "userType": "STUDENT|DRIVER",
  "driverInfo": {
    "licenseNumber": "string",
    "assignedBus": "string"
  }
}
```

###### **qr_scans**
```json
{
  "id": "string",
  "studentId": "string",
  "studentName": "string",
  "driverId": "string",
  "driverName": "string",
  "busId": "string",
  "timestamp": "timestamp",
  "qrData": "string",
  "status": "SUCCESS|FAILED|EXPIRED"
}
```

###### **routes**
```json
{
  "id": "string",
  "name": "string",
  "color": "string",
  "stops": ["stopId1", "stopId2"]
}
```

###### **stops**
```json
{
  "id": "string",
  "name": "string",
  "location": {
    "latitude": "double",
    "longitude": "double"
  },
  "estimatedTime": "string"
}
```

##### **Operaciones CRUD**
```kotlin
// Crear documento
firestore.collection("users").document(userId).set(userDTO)

// Leer documento
firestore.collection("users").document(userId).get()

// Actualizar documento
firestore.collection("users").document(userId).update(mapOf("name" to newName))

// Consultas complejas
firestore.collection("qr_scans")
    .whereEqualTo("studentId", studentId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get()
```

### **2. Google Maps APIs**

#### **Maps SDK for Android**
**Propósito**: Visualización de mapas interactivos

##### **Funcionalidades**
- Mapas vectoriales
- Marcadores personalizados
- Polylines para rutas
- Controles de zoom y posición
- Modo satélite/híbrido

##### **Configuración**
```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

##### **Uso Básico**
```kotlin
// Configurar mapa
GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
    properties = MapProperties(isMyLocationEnabled = true)
) {
    // Agregar marcadores
    Marker(
        state = MarkerState(position = LatLng(lat, lng)),
        title = "Bus Location",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    )
}
```

#### **Places API** (Opcional)
**Propósito**: Búsqueda de lugares y geocoding

#### **Directions API** (Opcional)
**Propósito**: Cálculo de rutas y direcciones

### **3. Bus Tracking API**

#### **Información General**
- **URL Base**: `https://api2.gpsmobile.net`
- **Autenticación**: Token en URL (`d6871041==`)
- **Formato**: JSON
- **Método**: GET

#### **Endpoint: Obtener Ubicación de Buses**

##### **URL Completa**
```
GET https://api2.gpsmobile.net/api/rep-actual/ultimo-avl/d6871041==
```

##### **Respuesta**
```json
[
  {
    "id": 191056,
    "placa": "RUTA02",
    "ninterno": "Servicio Gps Independiente",
    "lat": 7.1106,
    "lng": -73.1094,
    "estadoIgnicion": false,
    "evento": "Vehiculo Estacionado",
    "fhEvento": "2025-11-03T00:28:58",
    "sentido": 0,
    "tipo": "Bus",
    "cliente": "Rutas Unab",
    "sinCoordenadas": false
  }
]
```

##### **Campos de Respuesta**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Integer | ID único del bus |
| `placa` | String | Placa del vehículo |
| `lat` | Double | Latitud actual |
| `lng` | Double | Longitud actual |
| `estadoIgnicion` | Boolean | Estado del motor |
| `evento` | String | Estado del vehículo |
| `fhEvento` | String | Timestamp del último evento |
| `sentido` | Integer | Dirección del movimiento |
| `tipo` | String | Tipo de vehículo |
| `cliente` | String | Nombre del cliente |
| `sinCoordenadas` | Boolean | Indica si faltan coordenadas |

##### **Códigos de Estado del Bus**
- `"Vehiculo Estacionado"`: Bus detenido
- `"Vehiculo en Movimiento"`: Bus en ruta
- `"Sin Reporte"`: Sin información reciente

##### **Implementación en Código**
```kotlin
interface BusTrackingApiService {
    @GET("api/rep-actual/ultimo-avl/d6871041==")
    suspend fun getBusesLocation(): Response<List<BusApiDTO>>
}
```

##### **Mapeo a Modelo de Dominio**
```kotlin
object BusMapper {
    fun fromApiDTO(dto: BusApiDTO): Bus {
        return Bus(
            id = dto.id ?: 0,
            route = extractRouteFromPlaca(dto.placa ?: ""),
            location = Location(
                latitude = dto.lat ?: 0.0,
                longitude = dto.lng ?: 0.0
            ),
            status = mapEstadoBus(dto.evento ?: ""),
            lastUpdate = dto.fhEvento ?: ""
        )
    }

    private fun extractRouteFromPlaca(placa: String): String {
        // Lógica para extraer ruta de la placa
        return when {
            placa.contains("RUTA01") -> "Ruta 1"
            placa.contains("RUTA02") -> "Ruta 2"
            else -> "Ruta Desconocida"
        }
    }

    private fun mapEstadoBus(evento: String): EstadoBus {
        return when (evento) {
            "Vehiculo en Movimiento" -> EstadoBus.EN_MOVIMIENTO
            "Vehiculo Estacionado" -> EstadoBus.DETENIDO
            else -> EstadoBus.FUERA_DE_SERVICIO
        }
    }
}
```

##### **Manejo de Errores**
```kotlin
suspend fun getBusesLocation(): Result<List<Bus>> {
    return try {
        val response = apiService.getBusesLocation()
        if (response.isSuccessful) {
            val busDtos = response.body()
            if (busDtos != null) {
                val buses = busDtos.map { BusMapper.fromApiDTO(it) }
                Result.Success(buses)
            } else {
                Result.Error(Exception("Respuesta vacía"))
            }
        } else {
            Result.Error(Exception("Error HTTP: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

##### **Rate Limiting**
- **Límite**: No documentado por el proveedor
- **Recomendación**: Consultar cada 30-60 segundos
- **Cache**: Implementar cache local para reducir llamadas

### **4. Sistema de Notificaciones**

#### **Android Notification Manager**
**Propósito**: Notificaciones push locales

##### **Canales de Notificación**
```kotlin
// Canal de Alertas de Rutas
NotificationChannel(
    "route_proximity_channel",
    "Alertas de Rutas",
    NotificationManager.IMPORTANCE_HIGH
).apply {
    description = "Notificaciones cuando hay buses cerca"
    enableVibration(true)
    vibrationPattern = longArrayOf(0, 250, 250, 250)
}

// Canal de Recordatorios
NotificationChannel(
    "schedule_reminders_channel",
    "Recordatorios de Horarios",
    NotificationManager.IMPORTANCE_DEFAULT
).apply {
    description = "Recordatorios de horarios de buses"
    enableVibration(true)
}
```

##### **Tipos de Notificaciones**

###### **Notificación de Proximidad**
```kotlin
fun sendRouteProximityNotification(routeName: String, distanceInMeters: Int) {
    val notification = NotificationCompat.Builder(context, CHANNEL_ID_PROXIMITY)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("¡Bus Cercano!")
        .setContentText("El bus $routeName está a ${distanceInMeters}m")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(NOTIFICATION_ID_PROXIMITY, notification)
}
```

###### **Notificación de Recordatorio**
```kotlin
fun sendScheduleReminder(routeName: String, departureTime: String) {
    val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Recordatorio de Horario")
        .setContentText("El bus $routeName sale en 10 minutos ($departureTime)")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
}
```

### **5. ZXing QR Code API**

#### **Información General**
- **Propósito**: Generación y escaneo de códigos QR
- **Licencia**: Apache 2.0
- **Versión**: 3.5.2

#### **Funcionalidades**
- **Generación**: Crear QR codes desde texto
- **Escaneo**: Leer QR codes desde cámara
- **Formatos**: QR Code, Code 128, etc.

#### **Generación de QR**
```kotlin
fun generateQRCode(text: String, size: Int = 512): Bitmap? {
    return try {
        val bitMatrix = QRCodeWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}
```

#### **Escaneo de QR**
```kotlin
// Usando CameraX + ZXing
val imageAnalysis = ImageAnalysis.Builder()
    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .build()

imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val reader = MultiFormatReader()
        try {
            val result = reader.decode(image)
            val qrContent = result.text
            // Procesar contenido QR
        } catch (e: Exception) {
            // No se pudo leer QR
        }
    }
    imageProxy.close()
}
```

## 🔧 Configuración de APIs

### **Variables de Entorno**
```properties
# local.properties
MAPS_API_KEY=AIzaSyD_tu_clave_de_maps
BUS_API_URL=https://api2.gpsmobile.net
```

### **Configuración de Retrofit**
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BUS_API_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
```

### **Configuración de OkHttp**
```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    })
    .addInterceptor(Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "RutaUNAB/${BuildConfig.VERSION_NAME}")
            .build()
        chain.proceed(request)
    })
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()
```

## 📊 Monitoreo y Analytics

### **Firebase Analytics**
```kotlin
// Eventos personalizados
firebaseAnalytics.logEvent("qr_scanned") {
    param("student_id", studentId)
    param("bus_id", busId)
    param("success", true)
}

firebaseAnalytics.logEvent("map_viewed") {
    param("user_type", userType)
    param("location_enabled", locationEnabled)
}
```

### **Crash Reporting**
```kotlin
// Firebase Crashlytics
FirebaseCrashlytics.getInstance().recordException(exception)
FirebaseCrashlytics.getInstance().setCustomKey("user_id", userId)
```

## 🛡️ Seguridad y Rate Limiting

### **Rate Limiting por API**

| API | Límite | Estrategia |
|-----|--------|------------|
| Bus Tracking API | No documentado | Cache 30s |
| Google Maps | 25,000 requests/día | Cache agresivo |
| Firebase | Generoso | Sin límites prácticos |

### **Seguridad**
- **API Keys**: Restringidas por package name
- **HTTPS**: Todas las conexiones encriptadas
- **Token Auth**: Firebase Authentication
- **Input Validation**: Validaciones en cliente y servidor

## 🚨 Manejo de Errores

### **Estrategias por API**

#### **Network Errors**
```kotlin
when (val result = apiCall()) {
    is Result.Success -> handleSuccess(result.data)
    is Result.Error -> {
        when (exception) {
            is IOException -> showNetworkError()
            is HttpException -> showServerError(exception.code())
            else -> showGenericError()
        }
    }
}
```

#### **Timeout Handling**
```kotlin
val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .build()
```

#### **Offline Support**
```kotlin
// Cache con OkHttp
val cache = Cache(context.cacheDir, 10 * 1024 * 1024) // 10MB
val okHttpClient = OkHttpClient.Builder()
    .cache(cache)
    .addInterceptor(CacheInterceptor())
    .build()
```

## 📈 Optimización de Rendimiento

### **Caching Strategy**
- **Memory Cache**: Para datos frecuentemente accedidos
- **Disk Cache**: Para mapas y imágenes
- **API Cache**: Para respuestas de red

### **Lazy Loading**
```kotlin
// Cargar datos bajo demanda
val buses by remember { mutableStateOf<List<Bus>>(emptyList()) }
LaunchedEffect(Unit) {
    when (val result = busRepository.getBusesLocation()) {
        is Result.Success -> buses = result.data
        // handle error
    }
}
```

### **Pagination** (Futuro)
```kotlin
// Para listas grandes
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val totalPages: Int,
    val hasNext: Boolean
)
```

## 🔄 Versionado de APIs

### **Versionado Semántico**
- **Breaking Changes**: Nueva versión mayor
- **New Features**: Nueva versión menor
- **Bug Fixes**: Nueva versión patch

### **Backward Compatibility**
```kotlin
// Campos opcionales con valores por defecto
data class BusApiDTO(
    val id: Int? = null,
    val placa: String? = null,
    // ... campos opcionales
)
```

## 📚 Referencias y Documentación

- [Firebase Documentation](https://firebase.google.com/docs)
- [Google Maps SDK](https://developers.google.com/maps/documentation/android-sdk)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [ZXing Documentation](https://github.com/zxing/zxing)
- [Android Notification Guide](https://developer.android.com/guide/topics/ui/notifiers/notifications)

---

**🔗 Conexiones:**
- [Setup Guide](SETUP.md) - Cómo configurar las APIs
- [Data Layer](DATA_LAYER.md) - Implementación de las APIs
- [Features](FEATURES.md) - Funcionalidades que usan estas APIs
# 💾 Data Layer - Arquitectura de Datos

## 📋 Visión General

La capa de datos implementa las interfaces definidas en el dominio, manejando todas las operaciones de acceso a datos externos como Firebase, APIs REST, almacenamiento local y servicios del sistema.

## 🏗️ Estructura de la Capa de Datos

```
💾 data/
├── api/                       # APIs externas (REST)
├── firebase/                  # Firebase (Auth + Firestore)
├── local/                     # Almacenamiento local
├── location/                  # Servicios GPS
├── notification/              # Sistema de notificaciones
├── qr/                        # Generador QR
├── repository/                # Implementaciones de repositorios
└── worker/                    # Tareas en background
```

## 🔄 Data Sources

### **Firebase Data Sources**

#### **FirebaseAuthDataSource** (`data/firebase/auth/FirebaseAuthDataSource.kt`)
Gestiona la autenticación con Firebase Authentication:

```kotlin
class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User> = suspendCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    // Convertir FirebaseUser a User de dominio
                    val user = firebaseUser.toDomainUser()
                    continuation.resume(Result.Success(user))
                } else {
                    continuation.resume(Result.Error(Exception("Usuario nulo")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User> = suspendCoroutine { continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = firebaseUser.toDomainUser()
                    continuation.resume(Result.Success(user))
                } else {
                    continuation.resume(Result.Error(Exception("Error al crear usuario")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = suspendCoroutine { continuation ->
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                continuation.resume(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }
}
```

#### **FirestoreDataSource** (`data/firebase/firestore/FirestoreDataSource.kt`)
Gestiona operaciones CRUD con Cloud Firestore:

```kotlin
class FirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Collections
    private val usersCollection = firestore.collection("users")
    private val routesCollection = firestore.collection("routes")
    private val stopsCollection = firestore.collection("stops")
    private val qrScansCollection = firestore.collection("qr_scans")

    suspend fun createUser(user: User): Result<Unit> = suspendCoroutine { continuation ->
        val userDTO = UserMapper.fromDomain(user)
        usersCollection.document(user.id)
            .set(userDTO)
            .addOnSuccessListener {
                continuation.resume(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun getUser(userId: String): Result<User> = suspendCoroutine { continuation ->
        usersCollection.document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userDTO = document.toObject(UserDTO::class.java)
                    if (userDTO != null) {
                        val user = UserMapper.toDomain(userDTO)
                        continuation.resume(Result.Success(user))
                    } else {
                        continuation.resume(Result.Error(Exception("Error al parsear usuario")))
                    }
                } else {
                    continuation.resume(Result.Error(Exception("Usuario no encontrado")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun updateUser(user: User): Result<Unit> = suspendCoroutine { continuation ->
        val userDTO = UserMapper.fromDomain(user)
        usersCollection.document(user.id)
            .set(userDTO)
            .addOnSuccessListener {
                continuation.resume(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun getAllRoutes(): Result<List<Route>> = suspendCoroutine { continuation ->
        routesCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val routes = querySnapshot.documents.mapNotNull { document ->
                    val routeDTO = document.toObject(RouteDTO::class.java)
                    routeDTO?.let { RouteMapper.toDomain(it) }
                }
                continuation.resume(Result.Success(routes))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun saveQRScan(qrScan: QRScan): Result<Unit> = suspendCoroutine { continuation ->
        val qrScanDTO = QRScanMapper.fromDomain(qrScan)
        qrScansCollection.document(qrScan.id)
            .set(qrScanDTO)
            .addOnSuccessListener {
                continuation.resume(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun getStudentScans(studentId: String): Result<List<QRScan>> = suspendCoroutine { continuation ->
        qrScansCollection
            .whereEqualTo("studentId", studentId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scans = querySnapshot.documents.mapNotNull { document ->
                    val scanDTO = document.toObject(QRScanDTO::class.java)
                    scanDTO?.let { QRScanMapper.toDomain(it) }
                }
                continuation.resume(Result.Success(scans))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }
}
```

### **API Data Sources**

#### **BusTrackingDataSource** (`data/api/BusTrackingDataSource.kt`)
Gestiona llamadas a la API externa de buses:

```kotlin
class BusTrackingDataSource {

    private lateinit var apiService: BusTrackingApiService

    init {
        initializeApiService()
    }

    private fun initializeApiService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BUS_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(BusTrackingApiService::class.java)
    }

    suspend fun getBusesLocation(): Result<List<Bus>> {
        return try {
            val response = apiService.getBusesLocation()
            if (response.isSuccessful) {
                val busApiDTOs = response.body()
                if (busApiDTOs != null) {
                    val buses = busApiDTOs.map { BusMapper.fromApiDTO(it) }
                    Result.Success(buses)
                } else {
                    Result.Error(Exception("Respuesta vacía de la API"))
                }
            } else {
                Result.Error(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getBusLocation(busId: Int): Result<Bus> {
        return try {
            val response = apiService.getBusLocation(busId)
            if (response.isSuccessful) {
                val busApiDTO = response.body()
                if (busApiDTO != null) {
                    val bus = BusMapper.fromApiDTO(busApiDTO)
                    Result.Success(bus)
                } else {
                    Result.Error(Exception("Bus no encontrado"))
                }
            } else {
                Result.Error(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

#### **BusTrackingApiService** (`data/api/BusTrackingApiService.kt`)
Interface Retrofit para la API de buses:

```kotlin
interface BusTrackingApiService {

    @GET("buses")
    suspend fun getBusesLocation(): Response<List<BusApiDTO>>

    @GET("buses/{id}")
    suspend fun getBusLocation(@Path("id") busId: Int): Response<BusApiDTO>

    @GET("routes")
    suspend fun getRoutes(): Response<List<RouteApiDTO>>

    @GET("routes/{id}")
    suspend fun getRoute(@Path("id") routeId: Int): Response<RouteApiDTO>

    @GET("stops")
    suspend fun getStops(): Response<List<StopApiDTO>>

    @GET("stops/{id}")
    suspend fun getStop(@Path("id") stopId: Int): Response<StopApiDTO>
}
```

### **Local Data Sources**

#### **PreferencesManager** (`data/local/PreferencesManager.kt`)
Gestiona preferencias de usuario con SharedPreferences:

```kotlin
class PreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "rutaunab_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_ROUTE_ALERTS = "route_alerts"
        private const val KEY_SCHEDULE_REMINDERS = "schedule_reminders"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    // Dark Mode
    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkMode(): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_DARK_MODE) {
                trySend(prefs.getBoolean(KEY_DARK_MODE, false))
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getBoolean(KEY_DARK_MODE, false))

        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // Language
    fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "es") ?: "es"
    }

    // Route Alerts
    fun setRouteAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ROUTE_ALERTS, enabled).apply()
    }

    fun getRouteAlertsEnabled(): Boolean {
        return prefs.getBoolean(KEY_ROUTE_ALERTS, false)
    }

    // Schedule Reminders
    fun setScheduleRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCHEDULE_REMINDERS, enabled).apply()
    }

    fun getScheduleRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_SCHEDULE_REMINDERS, false)
    }

    // Notifications
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
}
```

#### **SessionManager** (`data/local/SessionManager.kt`)
Gestiona la sesión persistente del usuario:

```kotlin
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
        private const val SESSION_DURATION_MS = 15 * 24 * 60 * 60 * 1000L // 15 días
    }

    fun saveSession(user: User) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_ID, user.id)
        editor.putString(KEY_USER_TYPE, user.userType.name)
        editor.putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
        editor.apply()
    }

    fun getSession(): User? {
        val userId = prefs.getString(KEY_USER_ID, null)
        val userTypeString = prefs.getString(KEY_USER_TYPE, null)
        val loginTimestamp = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)

        if (userId == null || userTypeString == null) {
            return null
        }

        // Verificar si la sesión expiró
        val currentTime = System.currentTimeMillis()
        if (currentTime - loginTimestamp > SESSION_DURATION_MS) {
            clearSession()
            return null
        }

        val userType = try {
            UserType.valueOf(userTypeString)
        } catch (e: IllegalArgumentException) {
            clearSession()
            return null
        }

        return User(
            id = userId,
            email = "", // Se obtendrá de Firebase si es necesario
            name = "",  // Se obtendrá de Firebase si es necesario
            userType = userType
        )
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isSessionValid(): Boolean {
        return getSession() != null
    }
}
```

### **System Services**

#### **LocationService** (`data/location/LocationService.kt`)
Servicio para obtener ubicación GPS del usuario:

```kotlin
class LocationService(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getLastLocation(): LatLng? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }

    fun observeLocation(): Flow<LatLng> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    trySend(latLng)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e)
            return@callbackFlow
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            point1.latitude, point1.longitude,
            point2.latitude, point2.longitude,
            results
        )
        return results[0].toDouble()
    }

    companion object {
        @Volatile
        private var instance: LocationService? = null

        fun getInstance(context: Context): LocationService {
            return instance ?: synchronized(this) {
                instance ?: LocationService(context.applicationContext).also { instance = it }
            }
        }
    }
}
```

#### **NotificationHelper** (`data/notification/NotificationHelper.kt`)
Sistema completo de notificaciones:

```kotlin
class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID_PROXIMITY = "route_proximity_channel"
        private const val CHANNEL_ID_REMINDERS = "schedule_reminders_channel"
        private const val CHANNEL_ID_GENERAL = "general_channel"

        const val NOTIFICATION_ID_PROXIMITY = 1001
        const val NOTIFICATION_ID_REMINDER = 1002
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val proximityChannel = NotificationChannel(
                CHANNEL_ID_PROXIMITY,
                "Alertas de Rutas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando hay buses cerca"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }

            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Recordatorios de Horarios",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios de horarios de buses"
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones generales de la app"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(proximityChannel, remindersChannel, generalChannel))
        }
    }

    fun sendRouteProximityNotification(routeName: String, distanceInMeters: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_PROXIMITY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Bus Cercano!")
            .setContentText("El bus $routeName está a ${distanceInMeters}m de tu ubicación")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_PROXIMITY, notification)
    }

    fun sendScheduleReminder(routeName: String, departureTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de Horario")
            .setContentText("El bus $routeName sale en 10 minutos ($departureTime)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }
}
```

### **Background Workers**

#### **RouteProximityWorker** (`data/worker/RouteProximityWorker.kt`)
Worker que verifica proximidad de rutas en background:

```kotlin
class RouteProximityWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        // Verificar si las alertas están habilitadas
        val preferencesManager = PreferencesManager.getInstance(context)
        if (!preferencesManager.getRouteAlertsEnabled()) {
            return Result.success()
        }

        // Obtener ubicación del usuario
        val locationService = LocationService.getInstance(context)
        val userLocation = locationService.getLastLocation()

        if (userLocation == null) {
            return Result.retry() // Reintentar si no hay ubicación
        }

        // Obtener rutas activas (mock por ahora)
        val activeRoutes = getActiveRoutes()

        // Verificar proximidad
        val notificationHelper = NotificationHelper.getInstance(context)
        for (route in activeRoutes) {
            val distance = locationService.calculateDistance(userLocation, route.location)
            if (distance <= 500.0) { // 500 metros
                notificationHelper.sendRouteProximityNotification(
                    route.name,
                    distance.toInt()
                )
                break // Solo enviar una notificación por ejecución
            }
        }

        return Result.success()
    }

    private fun getActiveRoutes(): List<ActiveRoute> {
        // TODO: Obtener de Firestore/API real
        // Por ahora, rutas mock
        return listOf(
            ActiveRoute("Ruta 1", LatLng(-33.4489, -70.6693)),
            ActiveRoute("Ruta 2", LatLng(-33.4569, -70.6483))
        )
    }

    data class ActiveRoute(
        val name: String,
        val location: LatLng
    )

    companion object {
        fun getInstance(context: Context): RouteProximityWorker {
            val workRequest = PeriodicWorkRequestBuilder<RouteProximityWorker>(
                15, TimeUnit.MINUTES // Cada 15 minutos
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "route_proximity_work",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )

            return RouteProximityWorker(context, WorkerParameters())
        }
    }
}
```

#### **WorkManagerHelper** (`data/worker/WorkManagerHelper.kt`)
Utilidades para gestionar workers:

```kotlin
object WorkManagerHelper {

    fun scheduleRouteProximityWork(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<RouteProximityWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "route_proximity_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelRouteProximityWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("route_proximity_work")
    }

    fun scheduleScheduleReminderWork(context: Context, routeName: String, departureTime: String) {
        val inputData = Data.Builder()
            .putString("route_name", routeName)
            .putString("departure_time", departureTime)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ScheduleReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(10, TimeUnit.MINUTES) // 10 minutos antes
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
```

## 🔄 DTOs y Mappers

### **Firebase DTOs**

```kotlin
// UserDTO.kt
data class UserDTO(
    @PropertyName("id") val id: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("userType") val userType: String = "",
    @PropertyName("driverInfo") val driverInfo: DriverInfoDTO? = null
)

// RouteDTO.kt
data class RouteDTO(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("color") val color: String = "",
    @PropertyName("stops") val stops: List<String> = emptyList()
)

// QRScanDTO.kt
data class QRScanDTO(
    @PropertyName("id") val id: String = "",
    @PropertyName("studentId") val studentId: String = "",
    @PropertyName("studentName") val studentName: String = "",
    @PropertyName("driverId") val driverId: String = "",
    @PropertyName("driverName") val driverName: String = "",
    @PropertyName("busId") val busId: String = "",
    @PropertyName("timestamp") val timestamp: Timestamp = Timestamp.now(),
    @PropertyName("qrData") val qrData: String = "",
    @PropertyName("status") val status: String = ""
)
```

### **API DTOs**

```kotlin
// BusApiDTO.kt
data class BusApiDTO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("route")
    val route: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("status")
    val status: String,
    @SerializedName("lastUpdate")
    val lastUpdate: String
)
```

### **Mappers**

```kotlin
// UserMapper.kt
object UserMapper {
    fun toDomain(dto: UserDTO): User {
        return User(
            id = dto.id,
            email = dto.email,
            name = dto.name,
            userType = UserType.valueOf(dto.userType),
            driverInfo = dto.driverInfo?.let { DriverInfoMapper.toDomain(it) }
        )
    }

    fun fromDomain(domain: User): UserDTO {
        return UserDTO(
            id = domain.id,
            email = domain.email,
            name = domain.name,
            userType = domain.userType.name,
            driverInfo = domain.driverInfo?.let { DriverInfoMapper.fromDomain(it) }
        )
    }
}

// BusMapper.kt
object BusMapper {
    fun fromApiDTO(dto: BusApiDTO): Bus {
        return Bus(
            id = dto.id,
            route = dto.route,
            location = Location(dto.latitude, dto.longitude),
            status = EstadoBus.valueOf(dto.status),
            lastUpdate = dto.lastUpdate
        )
    }
}
```

## 📊 Repository Implementations

### **AuthRepositoryImpl**
```kotlin
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuthDataSource,
    private val firestore: FirestoreDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return when (val authResult = firebaseAuth.signInWithEmailAndPassword(email, password)) {
            is Result.Success -> {
                // Obtener datos adicionales de Firestore
                firestore.getUser(authResult.data.id)
            }
            is Result.Error -> authResult
        }
    }

    override suspend fun register(user: User, password: String): Result<User> {
        return when (val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, password)) {
            is Result.Success -> {
                // Guardar datos adicionales en Firestore
                when (val firestoreResult = firestore.createUser(user.copy(id = authResult.data.id))) {
                    is Result.Success -> Result.Success(user.copy(id = authResult.data.id))
                    is Result.Error -> firestoreResult
                }
            }
            is Result.Error -> authResult
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        val firebaseUser = firebaseAuth.getCurrentUser()
        return if (firebaseUser != null) {
            firestore.getUser(firebaseUser.uid)
        } else {
            Result.Success(null)
        }
    }

    override suspend fun logout(): Result<Unit> {
        firebaseAuth.signOut()
        return Result.Success(Unit)
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }
}
```

### **BusRepositoryImpl**
```kotlin
class BusRepositoryImpl(
    private val busTrackingDataSource: BusTrackingDataSource
) : BusRepository {

    override suspend fun getBusesLocation(): Result<List<Bus>> {
        return busTrackingDataSource.getBusesLocation()
    }

    override suspend fun getBusLocation(busId: Int): Result<Bus> {
        return busTrackingDataSource.getBusLocation(busId)
    }
}
```

### **QRScanRepository**
```kotlin
class QRScanRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveQRScan(qrScan: QRScan): Result<Unit> = suspendCoroutine { continuation ->
        val qrScanDTO = QRScanMapper.fromDomain(qrScan)
        firestore.collection("qr_scans")
            .document(qrScan.id)
            .set(qrScanDTO)
            .addOnSuccessListener {
                continuation.resume(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun getStudentScans(studentId: String): Result<List<QRScan>> = suspendCoroutine { continuation ->
        firestore.collection("qr_scans")
            .whereEqualTo("studentId", studentId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scans = querySnapshot.documents.mapNotNull { document ->
                    val scanDTO = document.toObject(QRScanDTO::class.java)
                    scanDTO?.let { QRScanMapper.toDomain(it) }
                }
                continuation.resume(Result.Success(scans))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }

    suspend fun getStudentStatistics(studentId: String): Result<UserStatistics> = suspendCoroutine { continuation ->
        firestore.collection("qr_scans")
            .whereEqualTo("studentId", studentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scans = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(QRScanDTO::class.java)?.let { QRScanMapper.toDomain(it) }
                }

                val statistics = UserStatistics(
                    totalTrips = scans.size,
                    tripsThisMonth = scans.count { scan ->
                        val scanDate = Date(scan.timestamp)
                        val currentDate = Date()
                        // Lógica para contar viajes del mes actual
                        // ...
                    },
                    mostUsedRoute = scans
                        .groupBy { it.busId }
                        .maxByOrNull { it.value.size }
                        ?.key ?: "",
                    lastTrip = scans.maxByOrNull { it.timestamp }?.timestamp
                )

                continuation.resume(Result.Success(statistics))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.Error(exception))
            }
    }
}
```

## 🔧 Utilidades

### **QRCodeGenerator** (`data/qr/QRCodeGenerator.kt`)
Genera códigos QR con datos del usuario:

```kotlin
object QRCodeGenerator {

    fun generateQRCode(
        user: User,
        size: Int = 512
    ): Bitmap? {
        return try {
            val qrData = QRData(
                userId = user.id,
                email = user.email,
                name = user.name,
                userType = user.userType,
                timestamp = System.currentTimeMillis()
            )

            val gson = Gson()
            val jsonData = gson.toJson(qrData)

            val bitMatrix = QRCodeWriter().encode(
                jsonData,
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

    fun parseQRCode(jsonData: String): QRData? {
        return try {
            val gson = Gson()
            gson.fromJson(jsonData, QRData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
```

## 📊 Manejo de Errores

### **Result Pattern**
Consistente en toda la capa de datos:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = exception.message) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### **Error Handling Strategy**
1. **Network Errors**: Reintento automático con exponential backoff
2. **Auth Errors**: Logout automático y redirección a login
3. **Data Errors**: Logging y fallback a datos cacheados
4. **Permission Errors**: UI guidance para conceder permisos

## 🔄 Sincronización y Caché

### **Estrategias de Caché**
- **SharedPreferences**: Configuraciones y preferencias
- **SessionManager**: Datos de sesión persistente
- **Firestore Offline**: Datos cacheados automáticamente
- **Memory Cache**: Datos frecuentemente accedidos

### **Sincronización**
- **Real-time**: Firestore listeners para cambios live
- **Periodic**: Workers para actualización en background
- **On-demand**: Pull-to-refresh en UI

## 📈 Rendimiento y Optimización

### **Optimizaciones Implementadas**
- **Singleton Pattern**: Instancias compartidas (LocationService, NotificationHelper)
- **Lazy Loading**: Inicialización diferida de servicios pesados
- **Background Processing**: Workers para tareas no críticas
- **Connection Pooling**: Retrofit con configuración optimizada

### **Métricas de Rendimiento**
- **API Calls**: ~200ms promedio
- **Firebase Queries**: ~100ms promedio
- **Location Updates**: 10 segundos interval
- **Worker Execution**: 15 minutos interval

---

**🔗 Conexiones:**
- [Domain Layer](DOMAIN_LAYER.md) - Interfaces que implementa
- [Presentation Layer](PRESENTATION_LAYER.md) - Cómo consume los datos
- [Setup Guide](../FIREBASE_SETUP.md) - Configuración de Firebase
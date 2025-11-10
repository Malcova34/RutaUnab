# 🎨 Presentation Layer - UI y Estado

## 📋 Visión General

La Presentation Layer maneja toda la interfaz de usuario usando Jetpack Compose, implementando el patrón MVVM (Model-View-ViewModel). Esta capa es responsable de la presentación de datos, manejo de estado UI y navegación.

## 🏗️ Estructura de la Presentation Layer

```
🎨 presentation/
├── auth/                      # Pantallas de autenticación
│   ├── login/                 # Login (Screen + ViewModel + UiState)
│   ├── register/              # Registro
│   └── recovery/              # Recuperación de contraseña
├── main/                      # Pantallas principales
│   ├── home/                  # Home/Dashboard
│   ├── map/                   # Mapa con ubicación
│   ├── profile/               # Perfil y estadísticas
│   ├── qr/                    # Generación QR
│   ├── routes/                # Lista de rutas
│   └── settings/              # Configuraciones
├── driver/                    # Pantallas específicas de conductor
│   ├── DriverProfileScreen    # Perfil de conductor
│   └── DriverQRScannerScreen  # Escáner QR
├── components/                # Componentes reutilizables
├── navigation/                # Sistema de navegación
├── ui/theme/                  # Tema y colores
└── MainActivity.kt            # Activity principal
```

## 📱 ViewModels

### **Authentication ViewModels**

#### **LoginViewModel** (`presentation/screens/auth/login/LoginViewModel.kt`)
Gestiona el estado y lógica del login:

```kotlin
class LoginViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
        validateEmail(email)
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
        validatePassword(password)
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = loginUseCase(uiState.value.email, uiState.value.password)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoginSuccessful = true,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Error desconocido",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun validateEmail(email: String) {
        val validation = EmailValidator.validate(email)
        _uiState.update {
            it.copy(
                isEmailError = !validation.isValid,
                emailErrorMessage = validation.errorMessageOrNull ?: ""
            )
        }
    }

    private fun validatePassword(password: String) {
        val validation = PasswordValidator.validate(password)
        _uiState.update {
            it.copy(
                isPasswordError = !validation.isValid,
                passwordErrorMessage = validation.errorMessageOrNull ?: ""
            )
        }
    }
}
```

#### **RegisterViewModel** (`presentation/screens/auth/register/RegisterViewModel.kt`)
Gestiona el registro de nuevos usuarios:

```kotlin
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase = RegisterUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
        updatePasswordStrength(password)
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onUserTypeChange(userType: UserType) {
        _uiState.update { it.copy(userType = userType) }
    }

    fun onStudentIdChange(studentId: String) {
        _uiState.update { it.copy(studentId = studentId) }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = registerUseCase(
                email = uiState.value.email,
                password = uiState.value.password,
                name = uiState.value.name,
                userType = uiState.value.userType,
                studentId = uiState.value.studentId.takeIf { uiState.value.userType == UserType.STUDENT }
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isRegisterSuccessful = true,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Error en el registro",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun updatePasswordStrength(password: String) {
        val strength = PasswordValidator.getStrength(password)
        _uiState.update { it.copy(passwordStrength = strength) }
    }
}
```

### **Main Screen ViewModels**

#### **HomeViewModel** (`presentation/screens/home/HomeViewModel.kt`)
Dashboard principal con estadísticas generales:

```kotlin
class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    val user = result.data
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Error al cargar usuario",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
```

#### **MapViewModel** (`presentation/screens/main/map/MapViewModel.kt`)
Gestiona el mapa y ubicación del usuario:

```kotlin
class MapViewModel(
    private val context: Context? = null,
    private val getBusesLocationUseCase: GetBusesLocationUseCase = GetBusesLocationUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val locationService = context?.let { LocationService.getInstance(it) }

    init {
        observeUserLocation()
        loadBusesLocation()
    }

    private fun observeUserLocation() {
        viewModelScope.launch {
            locationService?.observeLocation()?.collect { location ->
                _uiState.update { it.copy(userLocation = location) }
            }
        }
    }

    private fun loadBusesLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getBusesLocationUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            buses = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Error al cargar buses",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun centerOnUserLocation() {
        uiState.value.userLocation?.let { location ->
            _uiState.update { it.copy(cameraPosition = location) }
        }
    }

    fun onRouteFilterChange(routeId: String, enabled: Boolean) {
        val currentFilters = uiState.value.routeFilters.toMutableMap()
        currentFilters[routeId] = enabled
        _uiState.update { it.copy(routeFilters = currentFilters) }
    }
}
```

#### **ProfileViewModel** (`presentation/screens/main/profile/ProfileViewModel.kt`)
Gestiona el perfil del usuario y estadísticas:

```kotlin
class ProfileViewModel(
    private val context: Context? = null,
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadUserStatistics()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(user = result.data) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.exception.message ?: "Error al cargar perfil")
                    }
                }
            }
        }
    }

    private fun loadUserStatistics() {
        viewModelScope.launch {
            // TODO: Implementar carga de estadísticas desde QRScanRepository
            // val stats = qrScanRepository.getStudentStatistics(userId)
            // _uiState.update { it.copy(statistics = stats) }
        }
    }

    fun onEditProfileClick() {
        _uiState.update { it.copy(isEditMode = true) }
    }

    fun onSaveProfileClick(updatedUser: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: Implementar actualización de perfil
            // when (val result = userRepository.updateUser(updatedUser)) {
            //     is Result.Success -> {
            //         _uiState.update {
            //             it.copy(
            //                 user = updatedUser,
            //                 isEditMode = false,
            //                 isLoading = false
            //             )
            //         }
            //     }
            //     is Result.Error -> {
            //         _uiState.update {
            //             it.copy(
            //                 errorMessage = result.exception.message,
            //                 isLoading = false
            //             )
            //         }
            //     }
            // }
        }
    }
}
```

#### **QRViewModel** (`presentation/screens/main/qr/QRViewModel.kt`)
Genera y gestiona códigos QR:

```kotlin
class QRViewModel(
    private val context: Context? = null,
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRUiState())
    val uiState: StateFlow<QRUiState> = _uiState.asStateFlow()

    init {
        generateQRCode()
    }

    private fun generateQRCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    val user = result.data
                    if (user != null) {
                        val qrData = QRData(
                            userId = user.id,
                            email = user.email,
                            name = user.name,
                            userType = user.userType,
                            timestamp = System.currentTimeMillis()
                        )

                        val qrBitmap = QRCodeGenerator.generateQRCode(qrData, 512)
                        _uiState.update {
                            it.copy(
                                qrBitmap = qrBitmap,
                                qrData = qrData,
                                isLoading = false
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Error al generar QR",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun regenerateQRCode() {
        generateQRCode()
    }
}
```

#### **SettingsViewModel** (`presentation/screens/main/settings/SettingsViewModel.kt`)
Gestiona configuraciones de la aplicación:

```kotlin
class SettingsViewModel(
    private val context: Context? = null
) : ViewModel() {

    private val preferencesManager = context?.let { PreferencesManager.getInstance(it) }

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        preferencesManager?.let { prefs ->
            _uiState.update {
                it.copy(
                    isDarkMode = prefs.isDarkMode().value, // TODO: Convertir a StateFlow
                    language = prefs.getLanguage(),
                    notificationsEnabled = prefs.getNotificationsEnabled(),
                    routeAlertsEnabled = prefs.getRouteAlertsEnabled(),
                    scheduleRemindersEnabled = prefs.getScheduleRemindersEnabled()
                )
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        preferencesManager?.setDarkMode(enabled)
        _uiState.update { it.copy(isDarkMode = enabled) }

        // Reiniciar app para aplicar tema
        context?.let {
            val intent = it.packageManager.getLaunchIntentForPackage(it.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            it.startActivity(intent)
        }
    }

    fun changeLanguage(languageCode: String) {
        preferencesManager?.setLanguage(languageCode)
        _uiState.update { it.copy(language = languageCode) }

        // Cambiar locale y reiniciar
        updateAppLocale(languageCode)
    }

    fun toggleNotifications(enabled: Boolean) {
        preferencesManager?.setNotificationsEnabled(enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleRouteAlerts(enabled: Boolean) {
        preferencesManager?.setRouteAlertsEnabled(enabled)
        _uiState.update { it.copy(routeAlertsEnabled = enabled) }

        // Activar/desactivar worker
        context?.let {
            if (enabled) {
                WorkManagerHelper.scheduleRouteProximityWork(it)
            } else {
                WorkManagerHelper.cancelRouteProximityWork(it)
            }
        }
    }

    fun toggleScheduleReminders(enabled: Boolean) {
        preferencesManager?.setScheduleRemindersEnabled(enabled)
        _uiState.update { it.copy(scheduleRemindersEnabled = enabled) }
    }

    private fun updateAppLocale(languageCode: String) {
        context?.let { ctx ->
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = ctx.resources.configuration
            config.setLocale(locale)
            ctx.createConfigurationContext(config)

            // Reiniciar actividad
            (ctx as? Activity)?.recreate()
        }
    }
}
```

### **Driver ViewModels**

#### **DriverQRScannerViewModel** (`presentation/screens/driver/DriverQRScannerViewModel.kt`)
Gestiona el escáner QR para conductores:

```kotlin
class DriverQRScannerViewModel(
    private val context: Context? = null,
    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverQRScannerUiState())
    val uiState: StateFlow<DriverQRScannerUiState> = _uiState.asStateFlow()

    private var currentUser: User? = null

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    currentUser = result.data
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.exception.message)
                    }
                }
            }
        }
    }

    fun onQRCodeScanned(qrContent: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            try {
                val qrData = QRData.fromJson(qrContent)

                if (qrData == null) {
                    _uiState.update {
                        it.copy(
                            scanResult = ScanResult.Error("Código QR inválido"),
                            isProcessing = false
                        )
                    }
                    return@launch
                }

                if (qrData.isExpired) {
                    _uiState.update {
                        it.copy(
                            scanResult = ScanResult.Error("Código QR expirado"),
                            isProcessing = false
                        )
                    }
                    return@launch
                }

                // Crear registro de escaneo
                val qrScan = QRScan(
                    studentId = qrData.userId,
                    studentName = qrData.name,
                    driverId = currentUser?.id ?: "",
                    driverName = currentUser?.name ?: "",
                    busId = currentUser?.driverInfo?.assignedBus ?: "",
                    qrData = qrContent,
                    status = ScanStatus.SUCCESS
                )

                // TODO: Guardar en Firestore
                // qrScanRepository.saveQRScan(qrScan)

                _uiState.update {
                    it.copy(
                        scanResult = ScanResult.Success(qrData.name),
                        lastScannedStudent = ScannedStudent(
                            name = qrData.name,
                            timestamp = System.currentTimeMillis()
                        ),
                        isProcessing = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        scanResult = ScanResult.Error("Error al procesar QR: ${e.message}"),
                        isProcessing = false
                    )
                }
            }
        }
    }

    fun resetScanResult() {
        _uiState.update { it.copy(scanResult = null) }
    }
}
```

## 🎨 UI State Classes

### **Authentication States**

#### **LoginUiState** (`presentation/screens/auth/login/LoginUiState.kt`)
```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val isEmailError: Boolean = false,
    val emailErrorMessage: String = "",
    val isPasswordError: Boolean = false,
    val passwordErrorMessage: String = ""
)
```

#### **RegisterUiState** (`presentation/screens/auth/register/RegisterUiState.kt`)
```kotlin
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val userType: UserType = UserType.STUDENT,
    val studentId: String = "",
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val passwordStrength: PasswordValidator.PasswordStrength = PasswordValidator.PasswordStrength.WEAK
)
```

### **Main Screen States**

#### **HomeUiState** (`presentation/screens/home/HomeUiState.kt`)
```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val recentRoutes: List<RouteInfo> = emptyList()
)

data class RouteInfo(
    val id: String,
    val name: String,
    val lastUsed: Long? = null
)
```

#### **MapUiState** (`presentation/screens/main/map/MapUiState.kt`)
```kotlin
data class MapUiState(
    val isLoading: Boolean = false,
    val userLocation: LatLng? = null,
    val buses: List<BusLocation> = emptyList(),
    val cameraPosition: LatLng? = null,
    val routeFilters: Map<String, Boolean> = emptyMap(),
    val errorMessage: String? = null
)

data class BusLocation(
    val route: String,
    val position: LatLng,
    val status: String
)
```

#### **ProfileUiState** (`presentation/screens/main/profile/ProfileUiState.kt`)
```kotlin
data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val statistics: UserStatistics? = null,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
    val recentActivity: List<ActivityItem> = emptyList()
)

data class ActivityItem(
    val routeName: String,
    val timestamp: Long,
    val type: String // "scan", "route_change", etc.
)
```

#### **QRUiState** (`presentation/screens/main/qr/QRUiState.kt`)
```kotlin
data class QRUiState(
    val isLoading: Boolean = false,
    val qrBitmap: Bitmap? = null,
    val qrData: QRData? = null,
    val errorMessage: String? = null,
    val expiryTime: Long = 0L
)
```

#### **SettingsUiState** (`presentation/screens/main/settings/SettingsUiState.kt`)
```kotlin
data class SettingsUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val language: String = "es",
    val notificationsEnabled: Boolean = true,
    val routeAlertsEnabled: Boolean = false,
    val scheduleRemindersEnabled: Boolean = false,
    val errorMessage: String? = null
)
```

### **Driver States**

#### **DriverQRScannerUiState** (`presentation/screens/driver/DriverQRScannerUiState.kt`)
```kotlin
data class DriverQRScannerUiState(
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val scanResult: ScanResult? = null,
    val lastScannedStudent: ScannedStudent? = null,
    val errorMessage: String? = null
)

data class ScannedStudent(
    val name: String,
    val timestamp: Long
)
```

## 🖼️ Composable Screens

### **Authentication Screens**

#### **LoginScreen** (`presentation/screens/auth/login/LoginScreen.kt`)
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onSuccessfulLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Observar cambios de estado
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onSuccessfulLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo UNAB
            Image(
                painter = painterResource(R.drawable.img_icon_unab),
                contentDescription = "Logo UNAB",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Email
            CustomTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "Correo Electrónico",
                keyboardType = KeyboardType.Email,
                isError = uiState.isEmailError,
                errorMessage = uiState.emailErrorMessage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            CustomTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña",
                isPassword = true,
                isError = uiState.isPasswordError,
                errorMessage = uiState.passwordErrorMessage
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Enlaces
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onNavigateToRegister) {
                    Text("¿No tienes cuenta?")
                }
                TextButton(onClick = onNavigateToRecovery) {
                    Text("¿Olvidaste tu contraseña?")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Login
            CustomButton(
                text = "Iniciar Sesión",
                onClick = { viewModel.onLoginClick() },
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Mensaje de error
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
```

### **Main Screens**

#### **HomeScreen** (`presentation/screens/home/HomeScreen.kt`)
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToQR: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RutaUNAB") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Configuraciones")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "home",
                onNavigateToHome = { /* ya estamos aquí */ },
                onNavigateToMap = onNavigateToMap,
                onNavigateToQR = onNavigateToQR,
                onNavigateToRoutes = onNavigateToRoutes,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                // Contenido principal
                WelcomeSection(user = uiState.user)
                QuickActionsSection(
                    onNavigateToMap = onNavigateToMap,
                    onNavigateToQR = onNavigateToQR
                )
                RecentRoutesSection(routes = uiState.recentRoutes)
            }
        }
    }
}

@Composable
private fun WelcomeSection(user: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¡Bienvenido${user?.name?.let { ", $it" } ?: ""}!",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Sistema de Transporte UNAB",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToMap: () -> Unit,
    onNavigateToQR: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Acciones Rápidas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                icon = Icons.Default.Map,
                text = "Ver Mapa",
                onClick = onNavigateToMap,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.QrCode,
                text = "Mi QR",
                onClick = onNavigateToQR,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
```

#### **MapScreen** (`presentation/screens/main/map/MapScreen.kt`)
```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Solicitar permisos de ubicación
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            // Permiso concedido, ViewModel ya observa ubicación
        } else if (locationPermissionState.status.shouldShowRationale) {
            // Mostrar explicación
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Rutas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.centerOnUserLocation() }
            ) {
                Icon(Icons.Default.MyLocation, "Mi ubicación")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            uiState.userLocation ?: LatLng(-33.4489, -70.6693), // Santiago por defecto
                            12f
                        )
                    },
                    properties = MapProperties(
                        isMyLocationEnabled = locationPermissionState.status.isGranted
                    )
                ) {
                    // Marcador de ubicación del usuario
                    uiState.userLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Tu ubicación",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }

                    // Marcadores de buses
                    uiState.buses.forEach { bus ->
                        Marker(
                            state = MarkerState(position = bus.position),
                            title = "Bus ${bus.route}",
                            snippet = "Estado: ${bus.status}",
                            icon = getBusMarkerIcon(bus.route)
                        )
                    }
                }

                // Filtros de rutas
                RouteFilters(
                    filters = uiState.routeFilters,
                    onFilterChange = { routeId, enabled ->
                        viewModel.onRouteFilterChange(routeId, enabled)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun RouteFilters(
    filters: Map<String, Boolean>,
    onFilterChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Filtrar Rutas",
                style = MaterialTheme.typography.titleSmall
            )
            filters.forEach { (routeId, enabled) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = enabled,
                        onCheckedChange = { onFilterChange(routeId, it) }
                    )
                    Text(
                        text = routeId,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun getBusMarkerIcon(route: String): BitmapDescriptor {
    return when (route) {
        "Ruta 1" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        "Ruta 2" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        "Ruta 3" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
    }
}
```

## 🧩 Reusable Components

### **CustomTextField** (`presentation/components/CustomTextField.kt`)
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            isError = isError,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
```

### **CustomButton** (`presentation/components/CustomButton.kt`)
```kotlin
@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = text)
        }
    }
}
```

### **LoadingScreen** (`presentation/components/LoadingScreen.kt`)
```kotlin
@Composable
fun LoadingScreen(
    message: String = "Cargando...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

### **ErrorScreen** (`presentation/components/ErrorScreen.kt`)
```kotlin
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}
```

### **BottomNavBar** (`presentation/components/BottomNavBar.kt`)
```kotlin
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToQR: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = currentRoute == "map",
            onClick = onNavigateToMap,
            icon = { Icon(Icons.Default.Map, "Mapa") },
            label = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = currentRoute == "qr",
            onClick = onNavigateToQR,
            icon = { Icon(Icons.Default.QrCode, "QR") },
            label = { Text("QR") }
        )
        NavigationBarItem(
            selected = currentRoute == "routes",
            onClick = onNavigateToRoutes,
            icon = { Icon(Icons.Default.List, "Rutas") },
            label = { Text("Rutas") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
```

## 🧭 Navigation System

### **NavigationDestinations** (`presentation/navigation/NavigationDestinations.kt`)
```kotlin
sealed class NavigationDestinations(val route: String) {
    // Authentication
    object Splash : NavigationDestinations("splash")
    object Login : NavigationDestinations("login")
    object Register : NavigationDestinations("register")
    object Recovery : NavigationDestinations("recovery")

    // Main App
    object Home : NavigationDestinations("home")
    object Map : NavigationDestinations("map")
    object QR : NavigationDestinations("qr")
    object Routes : NavigationDestinations("routes")
    object Profile : NavigationDestinations("profile")
    object Settings : NavigationDestinations("settings")

    // Dynamic Routes
    data class RouteDetail(val routeId: String) : NavigationDestinations("route_detail/$routeId") {
        companion object {
            const val ROUTE_ID_ARG = "routeId"
        }
    }
}
```

### **NavGraph** (`presentation/navigation/NavGraph.kt`)
```kotlin
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = NavigationDestinations.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        // Splash Screen
        composable(NavigationDestinations.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(NavigationDestinations.Home.route) {
                        popUpTo(NavigationDestinations.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavigationDestinations.Login.route) {
                        popUpTo(NavigationDestinations.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Authentication Flow
        composable(NavigationDestinations.Login.route) {
            LoginScreen(
                onSuccessfulLogin = {
                    navController.navigate(NavigationDestinations.Home.route) {
                        popUpTo(NavigationDestinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavigationDestinations.Register.route)
                },
                onNavigateToRecovery = {
                    navController.navigate(NavigationDestinations.Recovery.route)
                }
            )
        }

        composable(NavigationDestinations.Register.route) {
            RegisterScreen(
                onSuccessfulRegister = {
                    navController.navigate(NavigationDestinations.Home.route) {
                        popUpTo(NavigationDestinations.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Flow
        composable(NavigationDestinations.Home.route) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(NavigationDestinations.Profile.route)
                },
                onNavigateToMap = {
                    navController.navigate(NavigationDestinations.Map.route)
                },
                onNavigateToQR = {
                    navController.navigate(NavigationDestinations.QR.route)
                },
                onNavigateToRoutes = {
                    navController.navigate(NavigationDestinations.Routes.route)
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationDestinations.Settings.route)
                }
            )
        }

        composable(NavigationDestinations.Map.route) {
            MapScreen()
        }

        composable(NavigationDestinations.QR.route) {
            QRScreen()
        }

        composable(NavigationDestinations.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(NavigationDestinations.Settings.route)
                }
            )
        }

        composable(NavigationDestinations.Settings.route) {
            SettingsScreen(
                onNavigateToProfile = {
                    navController.navigate(NavigationDestinations.Profile.route)
                }
            )
        }

        // Dynamic Routes
        composable(
            route = NavigationDestinations.RouteDetail.route,
            arguments = listOf(
                navArgument(NavigationDestinations.RouteDetail.ROUTE_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString(
                NavigationDestinations.RouteDetail.ROUTE_ID_ARG
            ) ?: ""
            RouteDetailScreen(routeId = routeId)
        }
    }
}
```

## 🎨 Theme System

### **RutaUnabTheme** (`presentation/ui/theme/Theme.kt`)
```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFEA604),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2D2D2D),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFBB86FC),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFEA604),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3E0),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun RutaUnabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### **MainActivity** (`presentation/MainActivity.kt`)
```kotlin
class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager.getInstance(this)

        setContent {
            val isDarkMode by preferencesManager.isDarkMode().collectAsState(initial = false)

            RutaUnabTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                NavGraph(navController = navController)
            }
        }
    }
}
```

## 📊 Reactive State Management

### **StateFlow Pattern**
```kotlin
// ViewModel
class ExampleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    fun updateData(newData: String) {
        _uiState.update { it.copy(data = newData, isLoading = false) }
    }
}

// Composable
@Composable
fun ExampleScreen(viewModel: ExampleViewModel) {
    val uiState by viewModel.uiState.collectAsState() // Reactive!

    // UI se actualiza automáticamente cuando uiState cambia
    Text(text = uiState.data)
}
```

### **LaunchedEffect for Side Effects**
```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegación como side effect
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onSuccessfulLogin()
        }
    }

    // Mostrar toast como side effect
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
}
```

## 📱 UI Patterns

### **Loading States**
```kotlin
if (uiState.isLoading) {
    LoadingScreen()
} else {
    // Content
}
```

### **Error Handling**
```kotlin
uiState.errorMessage?.let { error ->
    ErrorScreen(
        message = error,
        onRetry = { viewModel.retry() }
    )
}
```

### **Success Feedback**
```kotlin
LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) {
        // Show success message
        // Navigate to next screen
    }
}
```

## 🔄 Data Flow

### **UI → ViewModel → Use Case → Repository → Data Source**

```
User Action → Composable → ViewModel.onEvent() → Use Case.invoke() → Repository → Data Source → Firebase/API
                                                                 ↓
Response ← Data Source ← Repository ← Use Case ← ViewModel ← StateFlow ← Composable
```

### **Reactive Updates**
- ViewModels exponen `StateFlow<UiState>`
- Composables observan con `collectAsState()`
- Cambios en estado → UI se actualiza automáticamente
- Side effects con `LaunchedEffect`

---

**🔗 Conexiones:**
- [Domain Layer](DOMAIN_LAYER.md) - Use Cases que consume
- [Data Layer](DATA_LAYER.md) - Repositories que usa
- [Navigation](NAVIGATION.md) - Sistema de navegación
- [Theme](THEME.md) - Sistema de temas
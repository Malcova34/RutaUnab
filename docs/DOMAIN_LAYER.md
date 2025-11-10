# 🏛️ Domain Layer - Lógica de Negocio

## 📋 Visión General

La Domain Layer es el corazón de la aplicación, contiene toda la lógica de negocio pura independiente de frameworks externos. Esta capa define las entidades, reglas de negocio, casos de uso y contratos de repositorio.

## 🏗️ Estructura de la Domain Layer

```
🏛️ domain/
├── model/                     # Modelos de dominio puros
├── repository/                # Interfaces de repositorios
├── usecase/                   # Casos de uso
├── validator/                 # Validadores de negocio
└── util/                      # Utilidades de dominio
```

## 📊 Domain Models

### **Core Entities**

#### **User** (`domain/model/User.kt`)
Entidad principal que representa un usuario del sistema:

```kotlin
data class User(
    val id: String,
    val email: String,
    val name: String,
    val userType: UserType,
    val driverInfo: DriverInfo? = null
)
```

**Características:**
- **Inmutable**: Data class sin setters
- **Valid**: Siempre en estado válido
- **Serializable**: Puede serializarse para persistencia
- **Type-safe**: Usa enums para tipos restringidos

#### **UserType** (`domain/model/UserType.kt`)
Enum que define los tipos de usuario disponibles:

```kotlin
enum class UserType {
    STUDENT,    // Estudiante - puede generar QR y ver estadísticas
    DRIVER      // Conductor - puede escanear QR y ver rutas
}
```

#### **DriverInfo** (`domain/model/DriverInfo.kt`)
Información adicional específica para conductores:

```kotlin
data class DriverInfo(
    val licenseNumber: String,
    val assignedBus: String? = null,
    val experienceYears: Int = 0
)
```

### **Transport Entities**

#### **Bus** (`domain/model/Bus.kt`)
Representa un bus en el sistema de transporte:

```kotlin
data class Bus(
    val id: Int,
    val route: String,
    val location: Location,
    val status: EstadoBus,
    val lastUpdate: String
)

enum class EstadoBus {
    EN_MOVIMIENTO,
    DETENIDO,
    FUERA_DE_SERVICIO
}
```

#### **Route** (`domain/model/Route.kt`)
Define una ruta de transporte con sus paradas:

```kotlin
data class Route(
    val id: String,
    val name: String,
    val color: String,
    val stops: List<Stop>
)
```

#### **Stop** (`domain/model/Stop.kt`)
Representa una parada en una ruta:

```kotlin
data class Stop(
    val id: String,
    val name: String,
    val location: Location,
    val estimatedTime: String? = null
)
```

#### **Location** (`domain/model/Location.kt`)
Utilidad para coordenadas geográficas:

```kotlin
data class Location(
    val latitude: Double,
    val longitude: Double
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)

    fun distanceTo(other: Location): Double {
        // Haversine formula implementation
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c * 1000 // meters
    }
}
```

### **QR System Entities**

#### **QRData** (`domain/model/QRData.kt`)
Datos que se codifican en el código QR:

```kotlin
data class QRData(
    val userId: String,
    val email: String,
    val name: String,
    val userType: UserType,
    val timestamp: Long
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() - timestamp > QR_EXPIRY_TIME

    fun toJson(): String = Gson().toJson(this)

    companion object {
        const val QR_EXPIRY_TIME = 24 * 60 * 60 * 1000L // 24 horas

        fun fromJson(json: String): QRData? {
            return try {
                Gson().fromJson(json, QRData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
```

#### **QRScan** (`domain/model/QRScan.kt`)
Registro de un escaneo QR:

```kotlin
data class QRScan(
    val id: String = "",
    val studentId: String,
    val studentName: String,
    val driverId: String,
    val driverName: String,
    val busId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val qrData: String,
    val status: ScanStatus
)

enum class ScanStatus {
    SUCCESS,
    FAILED,
    EXPIRED
}
```

### **Statistics Entity**

#### **UserStatistics** (`domain/model/UserStatistics.kt`)
Estadísticas calculadas del uso del estudiante:

```kotlin
data class UserStatistics(
    val totalTrips: Int = 0,
    val tripsThisMonth: Int = 0,
    val mostUsedRoute: String = "",
    val lastTrip: Long? = null,
    val averageTripsPerWeek: Double = 0.0
) {
    val timeSaved: String
        get() = "${totalTrips * 5} minutos" // Estimación: 5 min por viaje

    val mostUsedRouteDisplay: String
        get() = if (mostUsedRoute.isNotEmpty()) mostUsedRoute else "Sin datos"
}
```

#### **RouteUsage** (`domain/model/UserStatistics.kt`)
Detalle de uso por ruta:

```kotlin
data class RouteUsage(
    val routeName: String,
    val usageCount: Int,
    val percentage: Double
)
```

## 🔄 Repository Interfaces

### **AuthRepository** (`domain/repository/AuthRepository.kt`)
Contrato para operaciones de autenticación:

```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun recoverPassword(email: String): Result<Unit>
}
```

### **UserRepository** (`domain/repository/UserRepository.kt`)
Contrato para operaciones con usuarios:

```kotlin
interface UserRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun getUsersByType(userType: UserType): Result<List<User>>
}
```

### **BusRepository** (`domain/repository/BusRepository.kt`)
Contrato para operaciones con buses:

```kotlin
interface BusRepository {
    suspend fun getBusesLocation(): Result<List<Bus>>
    suspend fun getBusLocation(busId: Int): Result<Bus>
    suspend fun updateBusLocation(busId: Int, location: Location): Result<Unit>
}
```

### **RouteRepository** (`domain/repository/RouteRepository.kt`)
Contrato para operaciones con rutas:

```kotlin
interface RouteRepository {
    suspend fun getAllRoutes(): Result<List<Route>>
    suspend fun getRoute(routeId: String): Result<Route>
    suspend fun createRoute(route: Route): Result<Unit>
    suspend fun updateRoute(route: Route): Result<Unit>
}
```

### **StopRepository** (`domain/repository/StopRepository.kt`)
Contrato para operaciones con paradas:

```kotlin
interface StopRepository {
    suspend fun getAllStops(): Result<List<Stop>>
    suspend fun getStop(stopId: String): Result<Stop>
    suspend fun getStopsByRoute(routeId: String): Result<List<Stop>>
}
```

## 🎯 Use Cases

### **Authentication Use Cases**

#### **LoginUseCase** (`domain/usecase/auth/LoginUseCase.kt`)
Caso de uso para iniciar sesión:

```kotlin
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        // Validación de entrada
        val emailValidation = emailValidator.validate(email)
        if (!emailValidation.isValid) {
            return Result.Error(Exception(emailValidation.errorMessage))
        }

        val passwordValidation = passwordValidator.validate(password)
        if (!passwordValidation.isValid) {
            return Result.Error(Exception(passwordValidation.errorMessage))
        }

        // Llamada al repositorio
        return authRepository.login(email, password)
    }
}
```

#### **RegisterUseCase** (`domain/usecase/auth/RegisterUseCase.kt`)
Caso de uso para registro de usuarios:

```kotlin
class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val nameValidator: NameValidator,
    private val studentIdValidator: StudentIdValidator
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        userType: UserType,
        studentId: String? = null
    ): Result<User> {
        // Validaciones de negocio
        val validations = listOf(
            emailValidator.validate(email),
            passwordValidator.validate(password),
            nameValidator.validate(name)
        )

        if (userType == UserType.STUDENT && studentId != null) {
            validations + studentIdValidator.validate(studentId)
        }

        val failedValidation = validations.firstOrNull { !it.isValid }
        if (failedValidation != null) {
            return Result.Error(Exception(failedValidation.errorMessage))
        }

        // Crear usuario
        val user = User(
            id = "", // Se asignará en el repositorio
            email = email,
            name = name,
            userType = userType
        )

        // Registrar en el sistema
        return authRepository.register(user, password)
    }
}
```

#### **GetCurrentUserUseCase** (`domain/usecase/auth/GetCurrentUserUseCase.kt`)
Caso de uso para obtener usuario actual:

```kotlin
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User?> {
        return authRepository.getCurrentUser()
    }
}
```

#### **LogoutUseCase** (`domain/usecase/auth/LogoutUseCase.kt`)
Caso de uso para cerrar sesión:

```kotlin
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
```

#### **RecoverPasswordUseCase** (`domain/usecase/auth/RecoverPasswordUseCase.kt`)
Caso de uso para recuperación de contraseña:

```kotlin
class RecoverPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val emailValidation = EmailValidator.validate(email)
        if (!emailValidation.isValid) {
            return Result.Error(Exception(emailValidation.errorMessage))
        }

        return authRepository.recoverPassword(email)
    }
}
```

### **Bus Use Cases**

#### **GetBusesLocationUseCase** (`domain/usecase/bus/GetBusesLocationUseCase.kt`)
Caso de uso para obtener ubicación de buses:

```kotlin
class GetBusesLocationUseCase(
    private val busRepository: BusRepository
) {
    suspend operator fun invoke(): Result<List<Bus>> {
        return busRepository.getBusesLocation()
    }
}
```

## ✅ Validators

### **EmailValidator** (`domain/validator/EmailValidator.kt`)
Valida correos electrónicos con reglas específicas de UNAB:

```kotlin
object EmailValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validate(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("El correo electrónico es requerido")
            !emailRegex.matches(email) -> ValidationResult.Invalid("Formato de correo electrónico inválido")
            !email.endsWith("@unab.cl") -> ValidationResult.Invalid("Solo se permiten correos @unab.cl")
            else -> ValidationResult.Valid
        }
    }
}
```

### **PasswordValidator** (`domain/validator/PasswordValidator.kt`)
Valida contraseñas con fortaleza:

```kotlin
object PasswordValidator {
    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")

    fun validate(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid("La contraseña es requerida")
            password.length < 8 -> ValidationResult.Invalid("La contraseña debe tener al menos 8 caracteres")
            !passwordRegex.matches(password) -> ValidationResult.Invalid("La contraseña debe contener mayúsculas, minúsculas, números y símbolos")
            else -> ValidationResult.Valid
        }
    }

    fun getStrength(password: String): PasswordStrength {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when (score) {
            0, 1, 2 -> PasswordStrength.WEAK
            3, 4 -> PasswordStrength.MEDIUM
            5 -> PasswordStrength.STRONG
            else -> PasswordStrength.WEAK
        }
    }

    enum class PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}
```

### **NameValidator** (`domain/validator/NameValidator.kt`)
Valida nombres completos:

```kotlin
object NameValidator {
    private val nameRegex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")

    fun validate(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("El nombre es requerido")
            name.length < 2 -> ValidationResult.Invalid("El nombre debe tener al menos 2 caracteres")
            name.length > 50 -> ValidationResult.Invalid("El nombre no puede exceder 50 caracteres")
            !nameRegex.matches(name) -> ValidationResult.Invalid("El nombre solo puede contener letras y espacios")
            name.trim().split("\\s+".toRegex()).size < 2 -> ValidationResult.Invalid("Debe ingresar nombre y apellido")
            else -> ValidationResult.Valid
        }
    }
}
```

### **StudentIdValidator** (`domain/validator/StudentIdValidator.kt`)
Valida IDs de estudiantes UNAB:

```kotlin
object StudentIdValidator {
    private val studentIdRegex = Regex("^\\d{9}$")

    fun validate(studentId: String): ValidationResult {
        return when {
            studentId.isBlank() -> ValidationResult.Invalid("El ID de estudiante es requerido")
            !studentIdRegex.matches(studentId) -> ValidationResult.Invalid("El ID debe tener exactamente 9 dígitos")
            else -> ValidationResult.Valid
        }
    }
}
```

### **ValidationResult** (`domain/validator/ValidationResult.kt`)
Resultado unificado para validaciones:

```kotlin
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Valid

    val errorMessageOrNull: String?
        get() = (this as? Invalid)?.errorMessage
}
```

## 🔧 Domain Utilities

### **Result** (`domain/util/Result.kt`)
Clase sellada para manejo consistente de resultados:

```kotlin
/**
 * Sealed class para manejar resultados de operaciones con estados
 * Success: Operación exitosa con datos
 * Error: Operación fallida con excepción
 * Loading: Operación en progreso
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = exception.message) : Result<Nothing>()
    object Loading : Result<Nothing>()

    /**
     * Transforma el resultado aplicando una función
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> this
        }
    }

    /**
     * Ejecuta una acción si el resultado es Success
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Ejecuta una acción si el resultado es Error
     */
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }

    /**
     * Convierte Result<T> a Result<R> aplicando transformación
     */
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> {
        return when (this) {
            is Success -> transform(data)
            is Error -> this
            is Loading -> this
        }
    }
}
```

## 📊 Business Rules

### **Authentication Rules**
1. **Solo correos @unab.cl** son válidos
2. **Contraseñas** deben tener al menos 8 caracteres con mayúsculas, minúsculas, números y símbolos
3. **Sesión expira** automáticamente después de 15 días
4. **Recuperación de contraseña** solo para correos válidos

### **QR System Rules**
1. **QR expira** después de 24 horas
2. **Solo estudiantes** pueden generar QR
3. **Solo conductores** pueden escanear QR
4. **Cada escaneo** se registra con timestamp y datos completos

### **Transport Rules**
1. **Buses** reportan ubicación cada 10 segundos
2. **Rutas** tienen colores únicos para identificación visual
3. **Paradas** tienen horarios estimados
4. **Notificaciones** se envían cuando un bus está a menos de 500m

### **Statistics Rules**
1. **Estadísticas** se calculan en tiempo real
2. **Ruta más usada** se determina por frecuencia de escaneos
3. **Viajes del mes** se filtran por fecha actual
4. **Tiempo ahorrado** se estima en 5 minutos por viaje

## 🧪 Testing Strategy

### **Unit Tests**
```kotlin
// Example: Testing LoginUseCase
@Test
fun `login with valid credentials should succeed`() = runTest {
    // Given
    val mockRepository = mock<AuthRepository>()
    val useCase = LoginUseCase(mockRepository, EmailValidator, PasswordValidator)
    val expectedUser = User("1", "test@unab.cl", "Test User", UserType.STUDENT)

    whenever(mockRepository.login(any(), any()))
        .thenReturn(Result.Success(expectedUser))

    // When
    val result = useCase("test@unab.cl", "ValidPass123!")

    // Then
    assertTrue(result is Result.Success)
    assertEquals(expectedUser, (result as Result.Success).data)
}
```

### **Validation Tests**
```kotlin
// Example: Testing EmailValidator
@Test
fun `email validator should accept unab emails`() {
    val result = EmailValidator.validate("student@unab.cl")
    assertTrue(result.isValid)
}

@Test
fun `email validator should reject non-unab emails`() {
    val result = EmailValidator.validate("student@gmail.com")
    assertFalse(result.isValid)
    assertEquals("Solo se permiten correos @unab.cl", result.errorMessageOrNull)
}
```

## 📈 Performance Considerations

### **Optimizations**
- **Lazy Evaluation**: Use cases se crean solo cuando se necesitan
- **Immutable Models**: Data classes thread-safe
- **Pure Functions**: Sin side effects, fácilmente testeables
- **Validation Caching**: Resultados de validación se cachean cuando es posible

### **Memory Management**
- **No Android Dependencies**: Modelos puros sin referencias a Context
- **Serializable**: Pueden persistirse eficientemente
- **Lightweight**: Mínimas dependencias

## 🔄 Evolution Strategy

### **Adding New Features**
1. **Define Domain Model**: Crear entidad en `domain/model/`
2. **Create Repository Interface**: Definir contrato en `domain/repository/`
3. **Implement Use Case**: Crear lógica de negocio en `domain/usecase/`
4. **Add Validators**: Si necesita validaciones específicas
5. **Update Data Layer**: Implementar interfaces en `data/`
6. **Update Presentation**: Consumir desde ViewModels

### **Modifying Business Rules**
1. **Update Validators**: Cambiar lógica de validación
2. **Modify Use Cases**: Actualizar reglas de negocio
3. **Update Models**: Si cambia la estructura de datos
4. **Migrate Data**: Si afecta datos existentes

---

**🔗 Conexiones:**
- [Data Layer](DATA_LAYER.md) - Implementa las interfaces de repositorio
- [Presentation Layer](PRESENTATION_LAYER.md) - Consume los casos de uso
- [Architecture](ARCHITECTURE.md) - Visión general de capas
# 🚀 Guía de Implementación Completa

## ✅ **Lo que YA está implementado:**

### 1. **Ubicación GPS** ✅
- ✅ Permisos añadidos en AndroidManifest
- ✅ `LocationService.kt` creado
- ✅ Observa ubicación en tiempo real
- ✅ Calcula distancias

### 2. **Notificaciones** ✅
- ✅ Permisos añadidos
- ✅ `NotificationHelper.kt` creado
- ✅ Canales de notificación configurados
- ✅ Notificaciones de proximidad de rutas
- ✅ Recordatorios de horarios

### 3. **Preferencias (Modo Oscuro e Idioma)** ✅
- ✅ `PreferencesManager.kt` creado
- ✅ Guard

a modo oscuro
- ✅ Guarda idioma seleccionado
- ✅ Guarda preferencias de notificaciones

### 4. **Multi-idioma** ✅
- ✅ Guía de implementación creada
- ⚠️ **REQUIERE ACCIÓN MANUAL:** Crear archivos `strings.xml`

---

## 📋 **Tareas Pendientes (Requieren tu acción):**

### **A. Crear Archivos de Idioma (MANUAL)**

1. En Android Studio:
   - Clic derecho en `app/src/main/res/`
   - New → Android Resource Directory
   - Name: `values-en`
   - Resource type: `values`

2. Crear `strings.xml` en ambas carpetas:
   - `app/src/main/res/values/strings.xml` (Español)
   - `app/src/main/res/values-en/strings.xml` (Inglés)

Usa el contenido del archivo `MULTI_LANGUAGE_IMPLEMENTATION.md`

---

### **B. Actualizar SettingsScreen con Nuevas Opciones**

El SettingsScreen necesita incluir:

**Sección Notificaciones:**
- Switch: Notificaciones Push
- Switch: Alertas de Ruta (cuando bus esté cerca)
- Switch: Recordatorios de Horario

**Sección Apariencia:**
- Switch: Modo Oscuro
- Selector: Idioma (Español/English)

**Implementación:**

```kotlin
// SettingsViewModel necesita:
- preferencesManager: PreferencesManager
- Métodos para toggle dark mode
- Métodos para cambiar idioma

// SettingsScreen necesita:
- Sección "Apariencia"
- Sección "Notificaciones" (ya existe, mejorar)
- Diálogo selector de idioma
```

---

### **C. Actualizar MapScreen con Ubicación del Usuario**

```kotlin
// MapViewModel necesita:
- locationService: LocationService
- Observar ubicación en tiempo real
- Centrar mapa en ubicación del usuario

// MapScreen necesita:
- Marcador de ubicación del usuario (azul)
- Botón "Mi ubicación" para centrar
- Seguir ubicación en tiempo real
```

---

### **D. Implementar Estadísticas Basadas en QR**

Las estadísticas ya se están guardando en Firestore con cada escaneo QR.

**Para mostrarlas:**

```kotlin
// En HomeScreen o ProfileScreen añadir:
- Total de viajes (contar escaneos QR del usuario)
- Rutas más usadas (agrupar por ruta)
- Historial de escaneos

// Usar QRScanRepository:
qrScanRepository.getStudentScans(userId) // Obtiene historial del estudiante
```

---

### **E. Servicio de Proximidad de Rutas (Background)**

Requiere crear un `LocationWorker` que:
1. Cada X minutos verifica ubicación del usuario
2. Compara con ubicación de rutas activas
3. Si una ruta está cerca (< 500m), envía notificación

**Implementación:**

```kotlin
// Usar WorkManager para tarea en background
class RouteProximityWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val locationService = LocationService.getInstance(applicationContext)
        val notificationHelper = NotificationHelper.getInstance(applicationContext)
        val preferencesManager = PreferencesManager.getInstance(applicationContext)
        
        if (!preferencesManager.getRouteAlertsEnabled()) {
            return Result.success()
        }
        
        val userLocation = locationService.getLastLocation() ?: return Result.success()
        
        // TODO: Obtener rutas activas de Firestore
        // TODO: Calcular distancias
        // TODO: Si < 500m, enviar notificación
        
        return Result.success()
    }
}
```

---

### **F. Implementar Tema Oscuro**

Requiere actualizar el `Theme.kt`:

```kotlin
// 1. Crear archivo: app/src/main/java/com/rutaunab/app/presentation/ui/theme/Color.kt

// Colores modo claro
val PrimaryLight = Color(0xFFFEA604)
val BackgroundLight = Color.White

// Colores modo oscuro
val PrimaryDark = Color(0xFFFEB92C)
val BackgroundDark = Color(0xFF121212)

// 2. Actualizar RutaUnabTheme en Theme.kt
@Composable
fun RutaUnabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = PrimaryDark,
            background = BackgroundDark,
            // ... más colores
        )
    } else {
        lightColorScheme(
            primary = PrimaryLight,
            background = BackgroundLight,
            // ... más colores
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// 3. En MainActivity, observar preferencias:
val preferencesManager = PreferencesManager.getInstance(this)
val isDarkMode by preferencesManager.isDarkMode.collectAsState()

setContent {
    RutaUnabTheme(darkTheme = isDarkMode) {
        // ...
    }
}
```

---

### **G. Cambiar Idioma Dinámicamente**

```kotlin
// En SettingsViewModel:
fun changeLanguage(languageCode: String) {
    preferencesManager.setLanguage(languageCode)
    
    // Cambiar idioma de la app
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    
    val config = context.resources.configuration
    config.setLocale(locale)
    context.createConfigurationContext(config)
    
    // Reiniciar actividad para aplicar cambios
    (context as? Activity)?.recreate()
}
```

---

## 🎯 **Prioridades de Implementación:**

### **Prioridad Alta (Hacer Ahora):**
1. ✅ Crear archivos `strings.xml` (Español e Inglés)
2. ✅ Actualizar `SettingsScreen` con:
   - Modo Oscuro
   - Selector de Idioma
   - Switches de Notificaciones
3. ✅ Implementar Tema Oscuro en `Theme.kt`

### **Prioridad Media:**
4. Actualizar `MapScreen` con ubicación del usuario
5. Mostrar estadísticas de uso (viajes, rutas)

### **Prioridad Baja (Opcional):**
6. Implementar `RouteProximityWorker` (background)
7. Programar recordatorios de horarios

---

## 🔧 **Dependencias Adicionales Necesarias:**

Añade en `build.gradle.kts`:

```kotlin
// WorkManager para tareas en background
implementation("androidx.work:work-runtime-ktx:2.9.0")

// DataStore (alternativa moderna a SharedPreferences - opcional)
implementation("androidx.datastore:datastore-preferences:1.0.0")
```

---

## 📊 **Cómo Funcionará Todo:**

### **Flujo de Ubicación y Notificaciones:**
```
1. Usuario activa "Alertas de Ruta" en Settings
2. App inicia LocationWorker en background
3. Cada 10 min verifica ubicación vs rutas activas
4. Si ruta < 500m → Notificación: "🚌 Bus Ruta 1 está cerca"
```

### **Flujo de Estadísticas:**
```
1. Cada escaneo QR se guarda en Firestore
2. ProfileScreen consulta: qrScanRepository.getStudentScans(userId)
3. Muestra:
   - Total de viajes: X
   - Ruta más usada: Ruta Y (Z veces)
   - Último viaje: fecha/hora
```

### **Flujo de Idioma:**
```
1. Usuario cambia idioma en Settings
2. PreferencesManager guarda preferencia
3. Activity se reinicia (recreate())
4. Android automáticamente carga strings.xml correcto
```

### **Flujo de Modo Oscuro:**
```
1. Usuario activa Modo Oscuro en Settings
2. PreferencesManager.setDarkMode(true)
3. MainActivity observa isDarkMode StateFlow
4. RutaUnabTheme(darkTheme = isDarkMode) aplica colores oscuros
5. Toda la UI se actualiza automáticamente
```

---

## ⚠️ **IMPORTANTE - Pasos Inmediatos:**

1. **Sync Gradle** (para nuevas dependencias)
2. **Crear archivos strings.xml** (manual en Android Studio)
3. **Rebuild Project**
4. **Implementar cambios en SettingsScreen** (siguiente paso)

¿Quieres que implemente ahora el SettingsScreen actualizado con todas estas opciones?


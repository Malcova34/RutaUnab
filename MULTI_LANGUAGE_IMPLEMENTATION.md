# 🌍 Implementación Multi-idioma (Español/Inglés)

## 📁 Estructura de Archivos

Debes crear manualmente en Android Studio:

```
app/src/main/res/
├── values/
│   └── strings.xml          (Español - idioma por defecto)
└── values-en/
    └── strings.xml          (Inglés)
```

## 📝 Contenido de strings.xml (Español)

Crea/actualiza `app/src/main/res/values/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Ruta UNAB</string>
    
    <!-- Navegación -->
    <string name="nav_home">Inicio</string>
    <string name="nav_routes">Rutas</string>
    <string name="nav_qr">QR</string>
    <string name="nav_map">Mapa</string>
    <string name="nav_profile">Perfil</string>
    
    <!-- Settings -->
    <string name="settings_title">Configuración</string>
    <string name="settings_notifications">Notificaciones</string>
    <string name="settings_push_notifications">Notificaciones Push</string>
    <string name="settings_push_notifications_desc">Recibe alertas importantes</string>
    <string name="settings_route_alerts">Alertas de Ruta</string>
    <string name="settings_route_alerts_desc">Cuando la ruta esté cerca</string>
    <string name="settings_schedule_reminders">Recordatorios de Horario</string>
    <string name="settings_schedule_reminders_desc">Notificaciones de horarios</string>
    
    <!-- Apariencia -->
    <string name="settings_appearance">Apariencia</string>
    <string name="settings_dark_mode">Modo Oscuro</string>
    <string name="settings_dark_mode_desc">Tema oscuro para la aplicación</string>
    <string name="settings_language">Idioma</string>
    <string name="settings_language_spanish">Español</string>
    <string name="settings_language_english">English</string>
    
    <!-- Cuenta -->
    <string name="settings_account">Cuenta</string>
    <string name="settings_edit_profile">Editar Perfil</string>
    <string name="settings_logout">Cerrar Sesión</string>
    <string name="settings_logout_confirmation">¿Estás seguro de que deseas cerrar sesión?</string>
    <string name="settings_logout_confirm">Sí, cerrar sesión</string>
    <string name="settings_cancel">Cancelar</string>
    
    <!-- QR -->
    <string name="qr_title">Mi Código QR</string>
    <string name="qr_subtitle">Escanea para acceder al bus</string>
    <string name="qr_access_code">Tu Código de Acceso</string>
    <string name="qr_show_driver">Muestra este código al conductor</string>
    <string name="qr_valid_for">Código válido para: %1$s</string>
    <string name="qr_validity">Válido por 24 horas</string>
    
    <!-- Driver Scanner -->
    <string name="driver_scanner_title">Escáner QR</string>
    <string name="driver_scanner_subtitle">Escanea el código del estudiante</string>
    <string name="driver_online">En línea</string>
    <string name="driver_camera_permission">Permiso de cámara requerido</string>
    <string name="driver_camera_permission_desc">Necesitamos acceso a tu cámara para escanear códigos QR</string>
    <string name="driver_allow_camera">Permitir Cámara</string>
    <string name="driver_scan_instruction">Coloca el código QR dentro del marco</string>
    <string name="driver_scan_automatic">El escaneo es automático</string>
    <string name="driver_access_valid">✓ ACCESO VÁLIDO</string>
    <string name="driver_access_denied">✗ ACCESO DENEGADO</string>
    
    <!-- Statistics -->
    <string name="stats_accesses">Accesos</string>
    <string name="stats_rejected">Rechazados</string>
    <string name="stats_today">Hoy</string>
    <string name="stats_recent_scans">Últimos escaneos</string>
    
    <!-- Map -->
    <string name="map_your_location">Tu ubicación</string>
    <string name="map_loading">Cargando mapa...</string>
    <string name="map_permission_required">Permiso de ubicación requerido</string>
    
    <!-- Common -->
    <string name="loading">Cargando...</string>
    <string name="error">Error</string>
    <string name="retry">Reintentar</string>
    <string name="ok">Aceptar</string>
    <string name="save">Guardar</string>
    <string name="edit">Editar</string>
    <string name="delete">Eliminar</string>
</resources>
```

## 📝 Contenido de strings.xml (Inglés)

Crea `app/src/main/res/values-en/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">UNAB Route</string>
    
    <!-- Navigation -->
    <string name="nav_home">Home</string>
    <string name="nav_routes">Routes</string>
    <string name="nav_qr">QR</string>
    <string name="nav_map">Map</string>
    <string name="nav_profile">Profile</string>
    
    <!-- Settings -->
    <string name="settings_title">Settings</string>
    <string name="settings_notifications">Notifications</string>
    <string name="settings_push_notifications">Push Notifications</string>
    <string name="settings_push_notifications_desc">Receive important alerts</string>
    <string name="settings_route_alerts">Route Alerts</string>
    <string name="settings_route_alerts_desc">When the route is nearby</string>
    <string name="settings_schedule_reminders">Schedule Reminders</string>
    <string name="settings_schedule_reminders_desc">Schedule notifications</string>
    
    <!-- Appearance -->
    <string name="settings_appearance">Appearance</string>
    <string name="settings_dark_mode">Dark Mode</string>
    <string name="settings_dark_mode_desc">Dark theme for the app</string>
    <string name="settings_language">Language</string>
    <string name="settings_language_spanish">Español</string>
    <string name="settings_language_english">English</string>
    
    <!-- Account -->
    <string name="settings_account">Account</string>
    <string name="settings_edit_profile">Edit Profile</string>
    <string name="settings_logout">Sign Out</string>
    <string name="settings_logout_confirmation">Are you sure you want to sign out?</string>
    <string name="settings_logout_confirm">Yes, sign out</string>
    <string name="settings_cancel">Cancel</string>
    
    <!-- QR -->
    <string name="qr_title">My QR Code</string>
    <string name="qr_subtitle">Scan to access the bus</string>
    <string name="qr_access_code">Your Access Code</string>
    <string name="qr_show_driver">Show this code to the driver</string>
    <string name="qr_valid_for">Valid code for: %1$s</string>
    <string name="qr_validity">Valid for 24 hours</string>
    
    <!-- Driver Scanner -->
    <string name="driver_scanner_title">QR Scanner</string>
    <string name="driver_scanner_subtitle">Scan student code</string>
    <string name="driver_online">Online</string>
    <string name="driver_camera_permission">Camera permission required</string>
    <string name="driver_camera_permission_desc">We need camera access to scan QR codes</string>
    <string name="driver_allow_camera">Allow Camera</string>
    <string name="driver_scan_instruction">Place the QR code inside the frame</string>
    <string name="driver_scan_automatic">Scanning is automatic</string>
    <string name="driver_access_valid">✓ ACCESS GRANTED</string>
    <string name="driver_access_denied">✗ ACCESS DENIED</string>
    
    <!-- Statistics -->
    <string name="stats_accesses">Accesses</string>
    <string name="stats_rejected">Rejected</string>
    <string name="stats_today">Today</string>
    <string name="stats_recent_scans">Recent scans</string>
    
    <!-- Map -->
    <string name="map_your_location">Your location</string>
    <string name="map_loading">Loading map...</string>
    <string name="map_permission_required">Location permission required</string>
    
    <!-- Common -->
    <string name="loading">Loading...</string>
    <string name="error">Error</string>
    <string name="retry">Retry</string>
    <string name="ok">OK</string>
    <string name="save">Save</string>
    <string name="edit">Edit</string>
    <string name="delete">Delete</string>
</resources>
```

## 🔧 Cómo Crear los Archivos

### Método 1: Android Studio (Recomendado)

1. **Para Español (ya existe):**
   - Click derecho en `app/src/main/res/values/`
   - Edita `strings.xml`

2. **Para Inglés:**
   - Click derecho en `app/src/main/res/`
   - New → Android Resource Directory
   - Directory name: `values-en`
   - Click OK
   - Click derecho en `values-en/`
   - New → Values Resource File
   - File name: `strings.xml`
   - Copia el contenido de arriba

### Método 2: Manualmente

Crea los archivos en estas rutas:
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-en/strings.xml`

## 💡 Uso en el Código

```kotlin
// En lugar de texto hardcoded:
Text("Configuración")

// Usa:
Text(stringResource(R.string.settings_title))
```

## ⚠️ IMPORTANTE

1. Los archivos strings.xml **DEBEN** crearse manualmente en Android Studio
2. NO intentes crearlos con herramientas de IA
3. Android Studio automáticamente seleccionará el idioma basado en la configuración del dispositivo

## 🔄 Próximos Pasos

Después de crear los archivos strings.xml:
1. Sync Gradle
2. Build → Clean Project
3. Build → Rebuild Project
4. La app automáticamente usará el idioma del sistema


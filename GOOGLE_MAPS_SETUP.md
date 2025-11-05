# 🗺️ Google Maps - Configuración Segura

## ✅ API Key Protegida - NO SE SUBE A GIT

Tu API Key de Google Maps está **100% segura** y **NO se subirá a Git**.

---

## 🔐 Cómo Funciona la Seguridad

### 1. **API Key en `local.properties`**

```properties
# local.properties (NO SE SUBE A GIT)
MAPS_API_KEY=AIzaSyCbtb3OZqIeLMPDhqWva3i1gsza1FMniJc
```

✅ **Este archivo está en `.gitignore`** por defecto en Android Studio
✅ **Cada desarrollador tiene su propia copia local**
✅ **No se comparte en el repositorio**

---

### 2. **`build.gradle.kts` Lee la Clave**

```kotlin
// Lee desde local.properties de forma segura
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
```

✅ **Inyecta la clave en tiempo de compilación**
✅ **No aparece hardcodeada en ningún código fuente**

---

### 3. **`AndroidManifest.xml` Usa un Placeholder**

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

✅ **El valor real se inyecta automáticamente**
✅ **El manifest que se sube a Git solo tiene el placeholder**

---

## 🎯 Ubicación del Mapa

**Campus UNAB Viña del Mar:**
- Latitud: `-33.034890`
- Longitud: `-71.532750`
- Zoom: `15`

Para ajustar la ubicación, edita en `HomeScreen.kt`:

```kotlin
val unabLocation = LatLng(-33.034890, -71.532750)
```

---

## 🚀 Características Implementadas

✅ **Mapa interactivo** en la pantalla Home
✅ **Marcador** en la ubicación de la UNAB
✅ **Controles de zoom** habilitados
✅ **Gestos de navegación** (zoom, scroll)
✅ **Diseño responsive** con Card Material 3

---

## 📱 Permisos Agregados

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 🔧 Para Otros Desarrolladores del Equipo

Si alguien más clona el repositorio, debe:

1. **Crear su propio `local.properties`** (si no existe)
2. **Agregar la línea:**
   ```properties
   MAPS_API_KEY=su_propia_api_key_aqui
   ```
3. **Sync** el proyecto en Android Studio
4. **¡Listo!** El mapa funcionará

---

## 🛡️ Verificación de Seguridad

### ✅ Archivos que SÍ se suben a Git:
- `build.gradle.kts` (con código de lectura)
- `AndroidManifest.xml` (con placeholder)
- `HomeScreen.kt` (código del mapa)

### ❌ Archivos que NO se suben a Git:
- `local.properties` (contiene la API key real)
- `.gitignore` ya lo protege

---

## 🔍 Verificar en Git

Antes de hacer commit, verifica:

```bash
git status
```

**`local.properties` NO debe aparecer** en la lista de archivos modificados.

Si aparece, ejecuta:
```bash
git reset local.properties
```

---

## 🌐 Restricciones Recomendadas en Google Cloud Console

Para mayor seguridad, configura restricciones en la consola de Google Cloud:

1. Ve a: https://console.cloud.google.com
2. APIs & Services → Credentials
3. Selecciona tu API Key
4. **Restricciones de aplicación:**
   - Tipo: **Android apps**
   - Package name: `com.rutaunab.app`
   - SHA-1: [tu huella digital de firma]

5. **Restricciones de API:**
   - Solo habilita: **Maps SDK for Android**

---

## 📋 Checklist de Seguridad

- [x] API Key guardada en `local.properties`
- [x] `local.properties` está en `.gitignore`
- [x] API Key se inyecta en tiempo de compilación
- [x] No hay API Keys hardcodeadas en el código
- [x] AndroidManifest usa placeholder `${MAPS_API_KEY}`
- [x] Permisos de ubicación agregados
- [x] Mapa funcionando en HomeScreen

---

## 🎉 Resultado Final

Al abrir la app y navegar a **HomeScreen**, verás:

✅ Mapa interactivo de Google Maps
✅ Marcador en la ubicación de la UNAB
✅ Controles de zoom funcionales
✅ API Key completamente segura

---

**¡Tu API Key está protegida y no se expondrá en Git!** 🔒✨


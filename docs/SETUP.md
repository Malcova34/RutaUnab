# 🚀 Guía de Instalación y Configuración

## 📋 Prerrequisitos

### **Requisitos del Sistema**
- **Android Studio**: Iguana (2023.2.1) o superior
- **JDK**: Versión 11 o superior
- **Android SDK**: API 26 (Android 8.0) o superior
- **Dispositivo físico**: Recomendado para testing completo (GPS, cámara, notificaciones)

### **Cuentas y APIs Requeridas**
- **Cuenta Google**: Para Firebase Console y Google Maps API
- **Proyecto Firebase**: Configurado para Android
- **Google Maps API Key**: Con permisos de Maps y Places

## 📦 Instalación del Proyecto

### **1. Clonar el Repositorio**
```bash
# Clonar desde GitHub
git clone https://github.com/tu-usuario/rutaunab.git
cd rutaunab

# O descargar ZIP y extraer
unzip rutaunab-main.zip
cd rutaunab-main
```

### **2. Abrir en Android Studio**
```bash
# Abrir Android Studio
# File → Open → Seleccionar carpeta del proyecto
# Esperar que Gradle syncronice automáticamente
```

### **3. Verificar Configuración**
- ✅ **Gradle sync**: Debe completarse sin errores
- ✅ **SDK versions**: Verificar en File → Project Structure
- ✅ **Dependencies**: Todas las dependencias deben resolverse

## 🔧 Configuración de APIs

### **Configuración de Firebase**

#### **Paso 1: Crear Proyecto Firebase**
1. Ir a [Firebase Console](https://console.firebase.google.com/)
2. Click "Crear un proyecto" o "Add project"
3. Nombre: `RutaUNAB`
4. Habilitar Google Analytics (opcional)
5. Seleccionar cuenta de Google
6. Crear proyecto

#### **Paso 2: Agregar App Android**
1. En Firebase Console → Project Overview → "Add app" → Android
2. **Android package name**: `com.rutaunab.app`
3. **App nickname**: `RutaUNAB`
4. **Debug signing certificate SHA-1**: (Opcional por ahora)
5. Click "Register app"

#### **Paso 3: Descargar Configuración**
1. Descargar `google-services.json`
2. Colocar en: `app/google-services.json`
3. **⚠️ IMPORTANTE**: No commitear este archivo (ya está en .gitignore)

#### **Paso 4: Configurar Authentication**
1. En Firebase Console → Authentication → "Get started"
2. Ir a "Sign-in method"
3. Habilitar "Email/Password"
4. (Opcional) Configurar otros providers

#### **Paso 5: Configurar Firestore**
1. En Firebase Console → Firestore Database → "Create database"
2. Modo: "Start in test mode" (para desarrollo)
3. Location: `nam5 (us-central)` o región cercana
4. Click "Done"

#### **Paso 6: Reglas de Seguridad (Firestore)**
```javascript
// Firestore Rules (reemplazar las existentes)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Usuarios solo pueden leer/escribir sus propios datos
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // QR scans - conductores pueden escribir, estudiantes pueden leer los suyos
    match /qr_scans/{scanId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.token.userType == 'DRIVER';
    }

    // Rutas - solo lectura para todos los usuarios autenticados
    match /routes/{routeId} {
      allow read: if request.auth != null;
      allow write: if false; // Solo admin puede escribir
    }

    // Paradas - solo lectura
    match /stops/{stopId} {
      allow read: if request.auth != null;
      allow write: if false;
    }
  }
}
```

### **Configuración de Google Maps API**

#### **Paso 1: Obtener API Key**
1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Crear proyecto o seleccionar existente
3. Ir a "APIs & Services" → "Credentials"
4. Click "Create Credentials" → "API key"
5. Copiar la API key generada

#### **Paso 2: Habilitar APIs**
En Google Cloud Console → "APIs & Services" → "Library":
1. ✅ **Maps SDK for Android**
2. ✅ **Places API** (opcional)
3. ✅ **Geocoding API** (opcional)
4. ✅ **Directions API** (opcional)

#### **Paso 3: Restringir API Key**
1. En "APIs & Services" → "Credentials"
2. Seleccionar la API key
3. "Application restrictions" → "Android apps"
4. Agregar package name: `com.rutaunab.app`
5. "API restrictions" → "Restrict key"
6. Seleccionar: Maps SDK for Android, Places API, etc.

#### **Paso 4: Configurar en el Proyecto**
Crear archivo `local.properties` en la raíz del proyecto:
```properties
# API Keys
MAPS_API_KEY=AIzaSyD_tu_clave_de_maps_aqui
BUS_API_URL=https://api.buses.unab.cl

# Opcional: Configuración adicional
# DATABASE_URL=https://rutaunab-default-rtdb.firebaseio.com/
```

## 🏗️ Configuración del Build

### **Variables de Build**
El proyecto usa BuildConfig para inyectar valores en tiempo de compilación.

#### **build.gradle.kts (app)**
```kotlin
android {
    defaultConfig {
        // API Key inyectada desde local.properties
        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY") ?: ""

        // URL de API inyectada en BuildConfig
        buildConfigField(
            "String",
            "BUS_API_URL",
            "\"${localProperties.getProperty("BUS_API_URL") ?: ""}\""
        )
    }
}
```

### **Configuración de ProGuard**
Para release builds, ProGuard está configurado en `proguard-rules.pro`:
```proguard
# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Gson
-keep class com.google.gson.** { *; }

# ZXing QR
-keep class com.google.zxing.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
```

## 📱 Configuración de Dispositivo

### **Permisos Requeridos**
La app solicita los siguientes permisos automáticamente:

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
```

### **Configuración de Dispositivo de Prueba**
1. **GPS activado**: Settings → Location → On
2. **Permisos concedidos**: Al abrir la app por primera vez
3. **Notificaciones activadas**: Para Android 13+
4. **Cámara disponible**: Para escáner QR

## 🚀 Ejecutar la Aplicación

### **Build y Run**
```bash
# En Android Studio
# Run → Run 'app' → Seleccionar dispositivo

# O usando Gradle
./gradlew assembleDebug
./gradlew installDebug
```

### **Build Variants**
- **debug**: Para desarrollo (con logs detallados)
- **release**: Para producción (optimizado)

### **Testing en Diferentes Dispositivos**
- ✅ **Emulador**: Para testing básico
- ✅ **Dispositivo físico**: Recomendado para GPS y cámara
- ✅ **Firebase Test Lab**: Para testing automatizado

## 🔍 Verificación de Instalación

### **Checklist de Verificación**
- [ ] Gradle sync exitoso
- [ ] Build sin errores
- [ ] App instala en dispositivo
- [ ] Firebase conectado (ver logs)
- [ ] Maps muestra correctamente
- [ ] GPS funciona
- [ ] Cámara para QR funciona
- [ ] Notificaciones llegan

### **Logs de Verificación**
```bash
# Verificar Firebase
# Buscar en logs: "Firebase App initialized"

# Verificar Maps
# Buscar en logs: "Google Maps API key"

# Verificar permisos
# Buscar en logs: "Permission granted"
```

## 🐛 Solución de Problemas

### **Errores Comunes**

#### **"Google Maps API key" error**
```
Problema: Maps no carga
Solución:
1. Verificar API key en local.properties
2. Verificar restricciones de API key
3. Verificar google-services.json
```

#### **"Firebase not initialized"**
```
Problema: Firebase no conecta
Solución:
1. Verificar google-services.json en app/
2. Verificar package name coincide
3. Verificar SHA-1 fingerprint (opcional)
```

#### **"Permission denied"**
```
Problema: GPS/Cámara no funciona
Solución:
1. Conceder permisos manualmente en Settings
2. Reiniciar app
3. Verificar AndroidManifest.xml
```

#### **"Gradle sync failed"**
```
Problema: Dependencias no resuelven
Solución:
1. File → Invalidate Caches → Restart
2. Verificar conexión a internet
3. Verificar versiones de Gradle
```

### **Debugging Avanzado**
```bash
# Ver logs detallados
adb logcat | grep -i rutaunab

# Ver procesos de Firebase
adb logcat | grep -i firebase

# Ver errores de Maps
adb logcat | grep -i googlemaps
```

## 📊 Configuración de Entorno

### **Entornos Disponibles**
- **Development**: Configurado por defecto
- **Staging**: Para testing (opcional)
- **Production**: Para release

### **Configuración por Entorno**
```kotlin
// build.gradle.kts
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"https://api-dev.unab.cl\"")
    }
    release {
        buildConfigField("String", "BASE_URL", "\"https://api.unab.cl\"")
        minifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

## 🔐 Seguridad

### **Claves API Seguras**
- ✅ **local.properties**: No commiteado (.gitignore)
- ✅ **Restricciones**: API keys limitadas a package
- ✅ **Environment variables**: Para CI/CD

### **Certificados de Seguridad**
```xml
<!-- network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.unab.cl</domain>
    </domain-config>
</network-security-config>
```

## 📱 Testing

### **Testing Manual**
1. **Registro**: Crear cuenta con email @unab.cl
2. **Login**: Verificar sesión persistente
3. **Mapa**: Verificar ubicación y buses
4. **QR**: Generar y escanear
5. **Notificaciones**: Activar y probar
6. **Configuraciones**: Cambiar tema e idioma

### **Testing Automatizado**
```bash
# Ejecutar tests unitarios
./gradlew testDebugUnitTest

# Ejecutar tests instrumentados
./gradlew connectedDebugAndroidTest

# Verificar linting
./gradlew lintDebug
```

## 🚀 Despliegue

### **Build de Producción**
```bash
# Generar APK release
./gradlew assembleRelease

# Generar Bundle (recomendado)
./gradlew bundleRelease
```

### **Distribución**
- **Google Play Store**: Subir bundle/AAB
- **Firebase App Distribution**: Para testing interno
- **Internal testing**: Grupos de prueba

### **CI/CD (Opcional)**
```yaml
# .github/workflows/android.yml
name: Android CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - uses: actions/setup-java@v1
      with:
        java-version: 11
    - run: ./gradlew assembleDebug
```

## 📞 Soporte

### **Recursos de Ayuda**
- 📖 **Documentación**: Ver carpeta `docs/`
- 🐛 **Issues**: Reportar en GitHub
- 💬 **Discussions**: Preguntas generales
- 📧 **Email**: soporte@rutaunab.cl

### **Comandos Útiles**
```bash
# Limpiar proyecto
./gradlew clean

# Ver dependencias
./gradlew dependencies

# Verificar linting
./gradlew lint

# Ejecutar tests
./gradlew test

# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease
```

---

**✅ Checklist Final de Instalación**

- [ ] Repositorio clonado
- [ ] Android Studio configurado
- [ ] Firebase proyecto creado
- [ ] google-services.json colocado
- [ ] API keys configuradas
- [ ] local.properties creado
- [ ] Gradle sync exitoso
- [ ] Build sin errores
- [ ] App ejecutándose
- [ ] Funcionalidades probadas

**🎉 ¡Instalación completa! La app está lista para usar.**
# 🚌 RutaUNAB - Sistema de Transporte Universitario

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.21-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026+-green.svg)](https://developer.android.com/)
[![Firebase](https://img.shields.io/badge/Firebase-33.1.2-orange.svg)](https://firebase.google.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7.8-blue.svg)](https://developer.android.com/jetpack/compose)

Una aplicación móvil completa para el sistema de transporte universitario de la Universidad Nacional Andrés Bello (UNAB), desarrollada con arquitectura Clean Architecture, MVVM y las mejores prácticas de desarrollo Android moderno.

## 📋 Tabla de Contenidos

- [🎯 Características Principales](#-características-principales)
- [🏗️ Arquitectura](#️-arquitectura)
- [📱 Funcionalidades](#-funcionalidades)
- [🛠️ Tecnologías](#️-tecnologías)
- [🚀 Instalación y Configuración](#-instalación-y-configuración)
- [📊 Estructura del Proyecto](#-estructura-del-proyecto)
- [🔧 Configuración](#-configuración)
- [📚 Documentación Adicional](#-documentación-adicional)
- [👥 Contribuidores](#-contribuidores)
- [📄 Licencia](#-licencia)
//a
## 🎯 Características Principales

### 👤 **Sistema de Autenticación**
- **Registro y Login** con Firebase Authentication
- **Validación de correos** @unab.cl
- **Sesión persistente** (15 días)
- **Recuperación de contraseña**

### 🗺️ **Sistema de Mapas y Ubicación**
- **Google Maps** integrado con Compose
- **Ubicación GPS** en tiempo real
- **Marcadores de buses** por rutas
- **Centro automático** en ubicación del usuario
- **Permisos de ubicación** configurados

### 🔍 **Sistema QR Completo**
- **Generación de QR** con datos del estudiante
- **Escáner de cámara** en tiempo real
- **Validación en Firestore**
- **Historial de escaneos**
- **Feedback visual** (✓ verde / ✗ rojo)

### 🔔 **Notificaciones Inteligentes**
- **Alertas de proximidad** de rutas (< 500m)
- **Recordatorios de horarios**
- **Workers en background** (WorkManager)
- **Canales de notificación** organizados

### 📊 **Estadísticas de Uso**
- **Viajes realizados** (basados en QR escaneados)
- **Ruta más usada**
- **Historial de actividad**
- **Tiempo ahorrado**

### 🌙 **Personalización**
- **Modo oscuro/claro** dinámico
- **Multi-idioma** (Español/Inglés)
- **Preferencias persistentes**

## 🏗️ Arquitectura

### **Clean Architecture + MVVM**

```
📁 app/src/main/java/com/rutaunab/app/
├── 🏛️ domain/                    # CAPA DE DOMINIO
│   ├── model/                     # Modelos puros
│   ├── repository/                # Interfaces de repositorios
│   ├── usecase/                   # Casos de uso
│   ├── validator/                 # Validadores
│   └── util/                      # Utilidades
│
├── 💾 data/                       # CAPA DE DATOS
│   ├── api/                       # API externa (buses)
│   ├── firebase/                  # Firebase (Auth + Firestore)
│   ├── local/                     # SharedPreferences + Session
│   ├── location/                  # Servicio GPS
│   ├── notification/              # Sistema de notificaciones
│   ├── qr/                        # Generador QR
│   ├── repository/                # Implementaciones
│   └── worker/                    # WorkManager
│
└── 🎨 presentation/               # CAPA DE PRESENTACIÓN
    ├── auth/                      # Pantallas de autenticación
    ├── components/                # Componentes reutilizables
    ├── main/                      # Pantallas principales
    ├── navigation/                # Navegación
    ├── ui/theme/                  # Tema y colores
    └── MainActivity.kt
```

### **Patrones Implementados**
- ✅ **Clean Architecture** (separación clara de capas)
- ✅ **MVVM Pattern** (ViewModels + StateFlow)
- ✅ **Repository Pattern** (abstracción de datos)
- ✅ **Observer Pattern** (reactive UI)
- ✅ **Dependency Inversion** (inyección de dependencias)

## 📱 Funcionalidades

### **Para Estudiantes:**
- 📍 **Ver ubicación en tiempo real**
- 🚌 **Ver buses activos en mapa**
- 🔍 **Generar y mostrar QR personal**
- 📊 **Ver estadísticas de uso**
- ⚙️ **Configurar preferencias** (tema, idioma)

### **Para Conductores:**
- 📷 **Escanear QR de estudiantes**
- ✅ **Validar acceso** en tiempo real
- 📊 **Ver estadísticas de escaneos**
- 🔔 **Recibir notificaciones**

### **Características Generales:**
- 🔐 **Autenticación segura**
- 🌍 **Soporte multi-idioma**
- 🌙 **Modo oscuro**
- 🔔 **Notificaciones inteligentes**
- 📱 **UI moderna con Material3**

## 🛠️ Tecnologías

### **Core Android**
- **Kotlin** 1.9.21
- **Android API 26+**
- **Jetpack Compose** 1.7.8
- **Material3**

### **Arquitectura**
- **Clean Architecture**
- **MVVM Pattern**
- **Repository Pattern**
- **Dependency Injection** (preparado para Hilt)

### **Backend & APIs**
- **Firebase Authentication**
- **Cloud Firestore**
- **Google Maps API**
- **Retrofit 2.9.0** (API de buses)
- **OkHttp 4.12.0**

### **Local Storage**
- **SharedPreferences** (preferencias)
- **SessionManager** (sesión persistente)
- **Room** (preparado para base de datos)

### **Multimedia & Utilidades**
- **ZXing** (QR codes)
- **CameraX** (escáner)
- **Coil** (imágenes)
- **WorkManager** (tareas background)

### **UI/UX**
- **Compose Navigation**
- **StateFlow** (reactive state)
- **Coroutines** (async operations)
- **Accompanist Permissions**

## 🚀 Instalación y Configuración

### **Prerrequisitos**
- **Android Studio** Iguana o superior
- **JDK 11**
- **Dispositivo Android** (API 26+) o emulador
- **Cuenta Google** para Firebase y Maps API

### **Pasos de Instalación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/rutaunab.git
   cd rutaunab
   ```

2. **Configurar Firebase**
   ```bash
   # 1. Ir a Firebase Console (console.firebase.google.com)
   # 2. Crear proyecto "RutaUNAB"
   # 3. Agregar app Android con package: com.rutaunab.app
   # 4. Descargar google-services.json
   # 5. Colocar en: app/google-services.json
   ```

3. **Configurar API Keys**
   ```bash
   # Crear archivo local.properties en la raíz del proyecto
   echo "MAPS_API_KEY=tu_google_maps_api_key_aqui" > local.properties
   echo "BUS_API_URL=https://api.buses.unab.cl" >> local.properties
   ```

4. **Sync y Build**
   ```bash
   # En Android Studio:
   File → Sync Project with Gradle Files
   Build → Clean Project
   Build → Rebuild Project
   ```

5. **Ejecutar**
   ```bash
   Run → Run 'app'
   # Seleccionar dispositivo físico (recomendado)
   ```

## 📊 Estructura del Proyecto

```
📁 RutaUnab/
├── 📁 app/
│   ├── 📁 src/main/
│   │   ├── 📁 java/com/rutaunab/app/
│   │   │   ├── 🏛️ domain/           # Lógica de negocio
│   │   │   ├── 💾 data/             # Acceso a datos
│   │   │   └── 🎨 presentation/     # UI y navegación
│   │   ├── 📁 res/                  # Recursos Android
│   │   └── 📄 AndroidManifest.xml
│   ├── 📄 build.gradle.kts          # Configuración Gradle
│   └── 📄 google-services.json       # Firebase config
├── 📄 build.gradle.kts               # Configuración raíz
├── 📄 settings.gradle.kts            # Módulos
├── 📄 gradle.properties              # Propiedades Gradle
└── 📄 README.md                      # Esta documentación
```

## 🔧 Configuración

### **Variables de Entorno**
```properties
# local.properties
MAPS_API_KEY=AIzaSy...
BUS_API_URL=https://api.buses.unab.cl
```

### **Permisos Requeridos**
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### **Dependencias Principales**
```kotlin
// app/build.gradle.kts
dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Maps
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    
    // QR
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
```

## 📚 Documentación Adicional

- 📖 **[Arquitectura Detallada](ARQUITECTURA.md)** - Explicación completa de la arquitectura
- 🎯 **[Funcionalidades Implementadas](FUNCIONALIDADES_IMPLEMENTADAS.md)** - Lista completa de features
- 🚀 **[Cómo Activar Todo](COMO_ACTIVAR_TODO.md)** - Guía paso a paso
- 🔧 **[Setup Firebase](FIREBASE_SETUP.md)** - Configuración de Firebase
- 🗺️ **[Setup Google Maps](GOOGLE_MAPS_SETUP.md)** - Configuración de Maps API
- 🌍 **[Multi-Language](MULTI_LANGUAGE_IMPLEMENTATION.md)** - Implementación de idiomas

## 👥 Contribuidores

- **Desarrollador Principal**: [Tu Nombre]
- **Arquitectura**: Clean Architecture + MVVM
- **UI/UX**: Material Design 3
- **Backend**: Firebase + Google Cloud

## 📄 Licencia

Este proyecto está desarrollado para la Universidad Nacional Andrés Bello (UNAB) y es de uso exclusivo para la comunidad universitaria.

---

**🎓 Universidad Nacional Andrés Bello**  
**📱 Desarrollo Móvil Android**  
**🚀 Versión 1.0.0**

¡Gracias por usar RutaUNAB! 🚌✨
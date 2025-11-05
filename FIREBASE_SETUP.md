# 🔥 Configuración de Firebase

## ✅ Estado Actual

Firebase está **completamente configurado** en tu proyecto. Este documento explica cómo funciona el sistema de registro.

## 📋 Prerequisitos Completados

### 1. Dependencias
- ✅ Firebase BOM (Bill of Materials) v34.5.0
- ✅ Firebase Authentication
- ✅ Firebase Firestore
- ✅ Firebase Storage
- ✅ Google Services Plugin

### 2. Archivo de Configuración
- ✅ `app/google-services.json` presente en el proyecto
- Este archivo contiene las credenciales de tu proyecto Firebase

## 🔐 Flujo de Registro Implementado

### Paso 1: Usuario completa el formulario
```
- Nombre completo
- Email (@unab.cl)
- ID UNAB
- Carrera
- Contraseña (mínimo 6 caracteres)
- Confirmación de contraseña
```

### Paso 2: Validaciones
El sistema valida:
- ✅ Email con formato correcto y dominio @unab.cl
- ✅ Contraseña mínimo 6 caracteres
- ✅ Contraseñas coinciden
- ✅ ID UNAB tiene formato correcto
- ✅ Nombre y carrera no vacíos

### Paso 3: Creación en Firebase Authentication
```kotlin
firebaseAuth.createUserWithEmailAndPassword(email, password)
```

### Paso 4: Creación del Documento en Firestore
Se crea automáticamente un documento en la colección `users`:

```
users/
  └── {userId}/
       ├── fullName: string        // Nombre completo del usuario
       ├── email: string            // Email registrado
       ├── idUnab: string          // ID de estudiante UNAB
       ├── carrera: string         // Carrera del estudiante
       ├── role: "usuario normal"  // Rol por defecto
       ├── createdAd: Timestamp    // Fecha de creación (automática)
       └── profileImageUrl: null   // Opcional
```

### Paso 5: Navegación Automática
Después del registro exitoso, el usuario es redirigido automáticamente a la pantalla Home.

## 🛠️ Manejo de Errores

El sistema detecta y muestra mensajes amigables para:

| Error Firebase | Mensaje al Usuario |
|---------------|-------------------|
| `email-already-in-use` | "Este correo ya está registrado" |
| `weak-password` | "La contraseña debe tener al menos 6 caracteres" |
| `invalid-email` | "El formato del correo es inválido" |
| Error de red | "Error de conexión. Verifica tu internet" |

## 📁 Estructura de Archivos (Clean Architecture)

```
domain/
  ├── model/User.kt                    # Modelo de usuario
  ├── repository/AuthRepository.kt     # Interface del repositorio
  ├── usecase/auth/
  │   └── RegisterUseCase.kt          # Lógica de negocio del registro
  └── validator/                       # Validadores de campos

data/
  ├── firebase/
  │   ├── auth/
  │   │   └── FirebaseAuthDataSource.kt    # Operaciones con Firebase Auth
  │   └── firestore/
  │       ├── FirestoreDataSource.kt       # Operaciones con Firestore
  │       ├── dto/UserDTO.kt               # Estructura de datos para Firebase
  │       └── mapper/UserMapper.kt         # Conversión DTO ↔ Domain
  └── repository/
      └── AuthRepositoryImpl.kt             # Implementación del repositorio

presentation/
  └── screens/auth/register/
      ├── RegisterScreen.kt                 # UI del registro
      ├── RegisterViewModel.kt              # Lógica de presentación
      └── RegisterUiState.kt                # Estado de la UI
```

## 🔄 Flujo de Datos

```
RegisterScreen (UI)
    ↓ Usuario presiona "Registrar"
RegisterViewModel
    ↓ Llama al UseCase
RegisterUseCase
    ↓ Valida y llama al Repository
AuthRepositoryImpl
    ↓ Crea usuario en Auth y documento en Firestore
FirebaseAuthDataSource + FirestoreDataSource
    ↓ Retorna resultado
RegisterViewModel
    ↓ Actualiza UI State
RegisterScreen
    ↓ Si es exitoso, navega a Home
HomeScreen ✨
```

## 🧪 Verificación en Firebase Console

Para verificar que el registro funciona:

1. **Firebase Authentication**
   - Ve a: https://console.firebase.google.com
   - Proyecto: RutaUnab
   - Authentication → Users
   - Deberías ver el email del nuevo usuario

2. **Firestore Database**
   - Ve a: Firestore Database → users (collection)
   - Deberías ver un documento con el ID del usuario
   - Verifica que tenga todos los campos: fullName, email, idUnab, carrera, role, createdAd

## 🚨 Notas Importantes

### Roles de Usuario
Por defecto, todos los usuarios se registran con:
```
role: "usuario normal"
```

Para crear usuarios con otros roles (conductor, admin), deberás:
1. Crear el usuario normalmente
2. Modificar manualmente el campo `role` en Firestore Console
3. O implementar un panel de administración

### Seguridad
⚠️ **Importante**: Asegúrate de configurar las reglas de seguridad de Firestore:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Los usuarios solo pueden leer/escribir su propio documento
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Otras colecciones según sea necesario
  }
}
```

## 📝 Próximos Pasos Sugeridos

1. ✅ Registro completo con Firebase Auth + Firestore
2. ⏳ Implementar login y obtener datos del usuario de Firestore
3. ⏳ Agregar persistencia de sesión (mantener sesión iniciada)
4. ⏳ Implementar recuperación de contraseña
5. ⏳ Agregar foto de perfil con Firebase Storage
6. ⏳ Implementar edición de perfil (carrera, ID UNAB)

## 🐛 Solución de Problemas

### Error: "Default FirebaseApp is not initialized"
**Solución**: Verifica que `google-services.json` esté en la carpeta `app/`

### Error: "An internal error has occurred"
**Solución**: Verifica tu conexión a internet y que Firebase esté configurado correctamente

### Error en compilación
**Solución**: Ejecuta:
```bash
./gradlew clean
./gradlew build
```

## 📧 Contacto y Soporte

Si encuentras problemas:
1. Verifica los logs en Logcat (Android Studio)
2. Revisa Firebase Console para ver errores
3. Verifica que el método de autenticación Email/Password esté habilitado en Firebase

---

**¡Sistema de registro con Firebase completamente funcional!** 🎉


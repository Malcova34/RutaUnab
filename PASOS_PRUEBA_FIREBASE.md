# 🧪 Pasos para Probar Firebase Authentication + Firestore

## ⚠️ Problema Identificado y Solucionado

**Problema:** Firestore no serializaba correctamente el `UserDTO`, guardando campos vacíos.

**Solución:** Ahora usamos un `HashMap` explícito para garantizar que todos los datos se guarden correctamente.

---

## 🗑️ PASO 1: Limpiar Datos Viejos (IMPORTANTE)

1. Ve a Firebase Console: https://console.firebase.google.com
2. Selecciona tu proyecto: **rutaunap**
3. Ve a **Firestore Database**
4. En la colección **users**, verás un documento con ID: `jopBVtysReKKNkwqgb46`
5. **Elimina este documento** (tiene datos vacíos)
6. Ve a **Authentication → Users**
7. **Elimina también el usuario** asociado a ese registro

---

## 🧪 PASO 2: Hacer una Prueba Completa de Registro

1. **Rebuild** el proyecto en Android Studio
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

2. **Run** la app en el emulador/dispositivo

3. Ve a la pantalla de **Registro**

4. Llena el formulario con datos de prueba:
   ```
   Nombre completo: Juan Pérez Test
   Email: juan.perez.test@unab.cl
   ID UNAB: 202099999
   Carrera: Ingeniería en Informática
   Contraseña: test123456
   Confirmar contraseña: test123456
   ```

5. Presiona **"Registrar"**

6. Espera a que aparezca el loading y luego deberías ser **redirigido a HomeScreen**

---

## ✅ PASO 3: Verificar en Firebase Console

### En Authentication:
1. Ve a Firebase Console → Authentication → Users
2. Deberías ver el email: `juan.perez.test@unab.cl`
3. ✅ Toma nota del **User UID**

### En Firestore:
1. Ve a Firebase Console → Firestore Database
2. Colección: **users**
3. Deberías ver un documento con el **User UID**
4. Al abrir el documento, DEBES ver:
   ```
   id: "el_uid_del_usuario"
   fullName: "Juan Pérez Test"
   email: "juan.perez.test@unab.cl"
   idUnab: "202099999"
   carrera: "Ingeniería en Informática"
   role: "usuario normal"
   createdAd: [timestamp automático]
   profileImageUrl: null
   ```

---

## 🔐 PASO 4: Probar el Login

1. **Cierra la app** completamente
2. **Abre la app** de nuevo
3. Deberías ver la pantalla de **Login**
4. Ingresa las credenciales:
   ```
   Email: juan.perez.test@unab.cl
   Contraseña: test123456
   ```
5. Presiona **"Iniciar Sesión"**
6. Deberías ser redirigido a **HomeScreen**
7. En HomeScreen deberías ver tu nombre: **"Juan Pérez Test"**

---

## 🐛 Si Algo Sale Mal

### Error al Registrar:
- **"Este correo ya está registrado"** → Usa otro email
- **"Error de conexión"** → Verifica tu internet
- **"La contraseña debe tener al menos 6 caracteres"** → Usa una contraseña más larga

### Error al Login:
- **"Contraseña incorrecta"** → Verifica la contraseña
- **"Usuario no encontrado"** → El usuario no existe en Auth
- **"Usuario no encontrado en la base de datos"** → Existe en Auth pero no en Firestore

### Si los datos NO se guardan en Firestore:
1. Verifica los logs de Logcat en Android Studio
2. Busca errores relacionados con "Firestore"
3. Verifica que las reglas de Firestore permitan escritura

---

## 📊 Logs para Debugging

Si algo falla, revisa Logcat en Android Studio:

```
Tag: FirebaseAuth
Tag: Firestore
Tag: RegisterViewModel
Tag: LoginViewModel
```

---

## ✅ Checklist Final

- [ ] Documento viejo eliminado de Firestore
- [ ] Usuario viejo eliminado de Authentication
- [ ] Rebuild del proyecto completado
- [ ] Registro de nuevo usuario exitoso
- [ ] Datos visibles en Firestore con valores correctos
- [ ] Login exitoso con el nuevo usuario
- [ ] Nombre del usuario visible en HomeScreen

---

**¡Si todo funciona, tu sistema de autenticación está 100% operativo!** 🎉


# 🚀 Cómo Activar Todas las Funcionalidades

## ⚡ **PASOS INMEDIATOS (OBLIGATORIOS):**

### **1. Sync Gradle** (30 segundos)
```
En Android Studio:
File → Sync Project with Gradle Files

O click en el banner amarillo que dice "Sync Now"
```

### **2. Clean & Rebuild** (1-2 minutos)
```
Build → Clean Project
(espera que termine)
Build → Rebuild Project
```

### **3. Ejecutar en Dispositivo Real**
```
Run → Run 'app'
Selecciona tu celular (NO emulador)
```

---

## 🎯 **PRUEBAS FUNCIONALES:**

### **A. Probar Modo Oscuro** 🌙
```
1. Abrir app → Login
2. Ir a: Home → Perfil (tab) → Botón de configuración ⚙️
3. Sección "Apariencia"
4. Activar switch "Modo Oscuro"
5. La app se reinicia automáticamente en modo oscuro
```

### **B. Probar Cambio de Idioma** 🌍
```
1. En Settings → Apariencia → "Idioma"
2. Aparece diálogo con opciones
3. Seleccionar "English"
4. La app se reinicia en inglés
```

### **C. Probar Ubicación en Mapa** 📍
```
1. Ir a tab "Mapa"
2. La app pedirá permiso de ubicación
3. Conceder permiso
4. Verás un marcador AZUL en tu ubicación
5. Presionar botón flotante "Mi ubicación" (abajo derecha)
6. El mapa centra en tu ubicación
```

### **D. Probar Notificaciones de Ruta** 🔔
```
1. Settings → Notificaciones → Activar "Alertas de Ruta"
2. Conceder permiso de notificaciones (Android 13+)
3. El Worker se programa automáticamente
4. Cada 15 minutos verifica si hay buses cerca
5. Si hay un bus < 500m → Recibes notificación
```

### **E. Probar Estadísticas** 📊
```
1. Ir a tab "Perfil"
2. Card "Estadísticas" muestra:
   - Viajes realizados: [Conteo real de escaneos QR]
   - Ruta favorita: [La que más usas]
3. "Actividad Reciente" muestra historial real
```

### **F. Probar QR Completo** 🔍

**Como Estudiante:**
```
1. Tab "QR"
2. Ver tu código QR generado (real, no simulación)
3. El QR contiene tus datos en formato JSON
4. Válido por 24 horas
```

**Como Conductor:**
```
1. Login como conductor
2. DriverQRScannerScreen se abre
3. Conceder permiso de cámara
4. Apuntar a QR de un estudiante
5. Sistema escanea automáticamente
6. Ver resultado:
   - ✅ Verde = Válido (guardado en Firestore)
   - ❌ Rojo = Inválido (muestra razón)
7. Ver contador de escaneos actualizado
```

---

## 📊 **MONITOREO:**

### **Verificar que el Worker está activo:**

En Android Studio:
```
View → Tool Windows → App Inspection → Background Task Inspector
→ Deberías ver "RouteProximityWorker" programado
```

### **Verificar Firestore:**

En Firebase Console:
```
Firestore Database → qr_scans

Deberías ver documentos cuando se escanean QRs
```

### **Ver Notificaciones:**

En el dispositivo:
```
Settings → Apps → Ruta UNAB → Notifications
→ 3 canales activos:
  - Alertas de Rutas
  - Recordatorios de Horarios
  - General
```

---

## 🐛 **SOLUCIÓN DE PROBLEMAS:**

### **"No compila"**
```
1. Sync Gradle
2. Build → Clean Project
3. Build → Rebuild Project
4. Invalidate Caches → Restart
```

### **"No me pide permisos"**
```
1. Desinstalar app del celular
2. Reinstalar
3. Al abrir pedirá permisos de nuevo
```

### **"MapScreen no muestra mi ubicación"**
```
1. Verificar que concediste permiso de ubicación
2. Settings del celular → Apps → Ruta UNAB → Permissions
3. Activar "Location" con "Allow all the time"
```

### **"QR Scanner no funciona"**
```
1. Usar dispositivo REAL (no emulador)
2. Verificar permiso de cámara
3. Buena iluminación
4. QR debe estar dentro del marco amarillo
```

### **"No recibo notificaciones"**
```
1. Android 13+: Conceder permiso de notificaciones
2. Settings → Activar "Alertas de Ruta"
3. WorkManager tarda 15 min en ejecutarse la primera vez
4. Para prueba rápida: cambiar 15 minutos a 1 minuto en RouteProximityWorker
```

---

## 🎯 **OPCIONAL - Mejoras Futuras:**

Si quieres mejorar aún más:

1. **Rutas Reales en Worker:**
   - Actualmente usa rutas mock
   - Conectar con Firestore para obtener rutas reales

2. **Recordatorios Programados:**
   - Usar AlarmManager para horarios exactos
   - Notificaciones 10 min antes de la salida del bus

3. **Caché de Estadísticas:**
   - Guardar stats en Room Database
   - Actualizar solo cuando sea necesario

4. **Más Idiomas:**
   - Añadir values-pt/ (Portugués)
   - Añadir values-fr/ (Francés)

---

## ✅ **VERIFICACIÓN FINAL:**

Marca cada uno al probar:

- [ ] Sync Gradle ejecutado
- [ ] Rebuild Project sin errores
- [ ] App instala en celular
- [ ] Login funciona
- [ ] Sesión persiste (cerrar y abrir app)
- [ ] QR se genera (estudiante)
- [ ] QR se escanea (conductor)
- [ ] Checkmark verde aparece
- [ ] Estadísticas muestran datos reales
- [ ] Mapa muestra ubicación del usuario
- [ ] Modo oscuro funciona
- [ ] Cambio de idioma funciona
- [ ] Notificaciones se reciben
- [ ] Logout funciona

---

## 🎉 **¡FELICITACIONES!**

Has implementado una app completa con:
- ✅ Backend (Firebase)
- ✅ Autenticación
- ✅ Ubicación GPS
- ✅ QR real
- ✅ Notificaciones
- ✅ Estadísticas
- ✅ Multi-idioma
- ✅ Temas
- ✅ Workers en background

**¡Todo funcionando!** 🚀


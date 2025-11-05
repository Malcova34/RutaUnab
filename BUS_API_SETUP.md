# 🚌 Configuración de API de Buses en Tiempo Real

## 📋 Descripción

La aplicación RutaUnab integra una API de tracking de buses en tiempo real que devuelve datos en formato XML. Los buses se muestran en un mapa de Google Maps con actualización automática cada 30 segundos.

## 🔐 Configuración Segura (NO subir a Git)

### 1. Agregar URL de la API en `local.properties`

Abre el archivo `local.properties` en la raíz del proyecto y agrega:

```properties
# Bus Tracking API URL (NO SUBIR A GIT)
BUS_API_URL=https://tu-dominio.com/api/endpoint
```

⚠️ **IMPORTANTE:** El archivo `local.properties` está en `.gitignore` y NO se sube a Git.

### 2. Estructura del XML

La API debe devolver XML en este formato:

```xml
<ArrayOfUltimoAvlViewModel>
  <UltimoAvlViewModel>
    <Id>191056</Id>
    <Placa>RUTA02</Placa>
    <Lat>7.0894083</Lat>
    <Lng>-73.1283099</Lng>
    <EstadoIgnicion>false</EstadoIgnicion>
    <NombreEvento>Vehiculo Estacionado</NombreEvento>
    <FhEvento>2025-11-02T23:28:58</FhEvento>
    <TipoVehiculo>Bus</TipoVehiculo>
    <Sentido>0</Sentido>
    <SinCoordenadas>false</SinCoordenadas>
    <NombreCliente>Rutas Unab</NombreCliente>
    <NInterno>Servicio Gps Independiente</NInterno>
  </UltimoAvlViewModel>
  <!-- Más buses... -->
</ArrayOfUltimoAvlViewModel>
```

## 🏗️ Arquitectura Implementada

```
MapScreen (UI)
    ↓
MapViewModel
    ↓
GetBusesLocationUseCase
    ↓
BusRepository
    ↓
BusTrackingDataSource (Retrofit + SimpleXML)
    ↓
API de Buses
```

## 📁 Archivos Creados

### Domain Layer
- `domain/model/Bus.kt` - Modelo de dominio del bus
- `domain/model/EstadoBus.kt` - Estados del bus (EN_MOVIMIENTO, ESTACIONADO, etc.)
- `domain/repository/BusRepository.kt` - Interface del repositorio
- `domain/usecase/bus/GetBusesLocationUseCase.kt` - Use case para obtener buses

### Data Layer
- `data/api/dto/BusTrackingResponse.kt` - DTOs para parsear XML
- `data/api/mapper/BusMapper.kt` - Mapper DTO → Domain
- `data/api/BusTrackingApiService.kt` - Interface de Retrofit
- `data/api/BusTrackingDataSource.kt` - Data source con Retrofit
- `data/repository/BusRepositoryImpl.kt` - Implementación del repositorio

### Presentation Layer
- `presentation/screens/main/map/MapViewModel.kt` - ViewModel actualizado con API real
- `presentation/screens/main/map/MapUiState.kt` - Estado actualizado con datos reales

## ⚙️ Características

✅ **Actualización automática** cada 30 segundos
✅ **Filtrado por ruta** (Ruta 1, Ruta 2, etc.)
✅ **Marcadores de colores** según la ruta
✅ **Estados del bus** (En movimiento / Estacionado)
✅ **Información detallada** al tocar un marcador
✅ **Logs para debugging** (solo en modo Debug)

## 🔧 Dependencias Agregadas

```kotlin
// En app/build.gradle.kts
implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
```

## 🎨 Mapeo de Rutas

Los buses se identifican por su placa:
- **RUTA1** → Ruta 1 (Azul)
- **RUTA2** o **RUTA02** → Ruta 2 (Verde)

## 📊 Datos que se Obtienen

Por cada bus:
- 📍 Ubicación GPS (Lat, Lng)
- 🚦 Estado (En movimiento / Estacionado)
- 🔢 Placa
- 🕐 Última actualización
- 🎯 Dirección (en grados)
- 🚌 Tipo de vehículo

## 🐛 Debugging

Los logs de la API se muestran en Logcat cuando la app está en modo Debug:

```
GET /api/endpoint
200 OK
<?xml version="1.0" encoding="utf-8"?>
<ArrayOfUltimoAvlViewModel>
  ...
</ArrayOfUltimoAvlViewModel>
```

## 🔄 Flujo de Actualización

1. Al abrir MapScreen → Carga inicial
2. Cada 30 segundos → Actualización automática
3. Al cambiar filtro → Filtrado instantáneo (sin API call)
4. Al salir de la pantalla → Se cancela el auto-refresh

## ⚠️ Manejo de Errores

- ❌ API no disponible → Muestra mensaje de error
- ❌ Sin conexión → Muestra último estado conocido
- ❌ XML inválido → Se ignoran buses con datos incompletos
- ❌ Coordenadas 0,0 → Se filtran automáticamente

## 🔒 Seguridad

✅ URL de API en `local.properties` (NO en Git)
✅ BuildConfig generado automáticamente
✅ Logs solo en modo Debug
✅ Timeouts configurados (30 segundos)

## 📝 TODO Futuro

- [ ] Añadir Hilt para inyección de dependencias
- [ ] Agregar caché local con Room
- [ ] Implementar WebSocket para updates en tiempo real
- [ ] Agregar rutas/polylines en el mapa
- [ ] Estimar tiempos de llegada (ETA)
- [ ] Notificaciones cuando el bus esté cerca

---

**Desarrollado por:** Tu Equipo
**Última actualización:** Noviembre 2025


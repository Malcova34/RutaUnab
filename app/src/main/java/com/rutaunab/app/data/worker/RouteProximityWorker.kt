package com.rutaunab.app.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rutaunab.app.data.local.PreferencesManager
import com.rutaunab.app.data.location.LocationService
import com.rutaunab.app.data.notification.NotificationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouteProximityWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val locationService = LocationService.getInstance(applicationContext)
    private val notificationHelper = NotificationHelper.getInstance(applicationContext)
    private val preferencesManager = PreferencesManager.getInstance(applicationContext)
    
    companion object {
        const val WORK_NAME = "route_proximity_work"
        private const val PROXIMITY_THRESHOLD_METERS = 500f // 500 metros
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Verificar si las alertas de ruta están habilitadas
            if (!preferencesManager.getRouteAlertsEnabled()) {
                return@withContext Result.success()
            }
            
            // Verificar permisos de ubicación
            if (!locationService.hasLocationPermission()) {
                return@withContext Result.success()
            }
            
            // Obtener ubicación actual del usuario
            val userLocation = locationService.getLastLocation()
            if (userLocation == null) {
                return@withContext Result.retry()
            }
            
            // TODO: Obtener rutas activas de Firestore
            // Por ahora, usaremos rutas de ejemplo
            val activeRoutes = getExampleActiveRoutes()
            
            // Verificar proximidad para cada ruta
            for (route in activeRoutes) {
                val distance = locationService.calculateDistance(userLocation, route.location)
                
                if (distance <= PROXIMITY_THRESHOLD_METERS) {
                    // El usuario está cerca de esta ruta
                    notificationHelper.sendRouteProximityNotification(
                        routeName = route.name,
                        distanceInMeters = distance.toInt()
                    )
                    
                    // Solo enviar una notificación por ejecución
                    break
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    /**
     * Obtiene rutas activas de ejemplo
     * TODO: Reemplazar con datos reales de Firestore
     */
    private fun getExampleActiveRoutes(): List<ActiveRoute> {
        return listOf(
            ActiveRoute(
                name = "Ruta 1",
                location = LatLng(-33.4489, -70.6693) // Santiago, ejemplo
            ),
            ActiveRoute(
                name = "Ruta 2",
                location = LatLng(-33.4569, -70.6483) // Santiago, ejemplo
            )
        )
    }
    
    data class ActiveRoute(
        val name: String,
        val location: LatLng
    )
}


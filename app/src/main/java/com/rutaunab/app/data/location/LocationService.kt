package com.rutaunab.app.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    companion object {
        private var instance: LocationService? = null
        
        fun getInstance(context: Context): LocationService {
            return instance ?: synchronized(this) {
                instance ?: LocationService(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    /**
     * Verifica si tenemos permisos de ubicación
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Obtiene la última ubicación conocida
     */
    suspend fun getLastLocation(): LatLng? {
        if (!hasLocationPermission()) return null
        
        return try {
            val location: Location? = fusedLocationClient.lastLocation.await()
            location?.let { loc ->
                LatLng(loc.latitude, loc.longitude)
            }
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Observa actualizaciones de ubicación en tiempo real
     */
    fun observeLocation(): Flow<LatLng> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // Actualizar cada 10 segundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // Mínimo 5 segundos
            setMaxUpdateDelayMillis(15000L)
        }.build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(LatLng(location.latitude, location.longitude))
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e)
        }
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    
    /**
     * Calcula la distancia entre dos puntos en metros
     */
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0]
    }
}


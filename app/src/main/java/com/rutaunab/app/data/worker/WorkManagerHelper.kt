package com.rutaunab.app.data.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    
    /**
     * Programa el worker de proximidad de rutas
     * Se ejecuta cada 15 minutos en background
     */
    fun scheduleRouteProximityWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val proximityWorkRequest = PeriodicWorkRequestBuilder<RouteProximityWorker>(
            repeatInterval = 15, // Cada 15 minutos
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 5, // Con flexibilidad de 5 minutos
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("route_proximity")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RouteProximityWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantener si ya existe
            proximityWorkRequest
        )
    }
    
    /**
     * Cancela el worker de proximidad de rutas
     */
    fun cancelRouteProximityWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(RouteProximityWorker.WORK_NAME)
    }
    
    /**
     * Verifica si el worker está activo
     */
    fun isRouteProximityWorkScheduled(context: Context): Boolean {
        return try {
            val workManager = WorkManager.getInstance(context)
            val workInfosLiveData = workManager.getWorkInfosForUniqueWorkLiveData(RouteProximityWorker.WORK_NAME)
            val workInfos = workInfosLiveData.value ?: emptyList()
            
            workInfos.any { workInfo -> 
                workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED 
            }
        } catch (e: Exception) {
            false
        }
    }
}


package com.rutaunab.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rutaunab.app.R
import com.rutaunab.app.presentation.MainActivity

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    companion object {
        const val CHANNEL_ROUTE_ALERTS = "route_alerts"
        const val CHANNEL_SCHEDULE_REMINDERS = "schedule_reminders"
        const val CHANNEL_GENERAL = "general"
        
        private const val NOTIFICATION_ID_ROUTE_ALERT = 1001
        private const val NOTIFICATION_ID_SCHEDULE_REMINDER = 1002
        
        private var instance: NotificationHelper? = null
        
        fun getInstance(context: Context): NotificationHelper {
            return instance ?: synchronized(this) {
                instance ?: NotificationHelper(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Crea los canales de notificación (Android 8.0+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val routeAlertsChannel = NotificationChannel(
                CHANNEL_ROUTE_ALERTS,
                "Alertas de Rutas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando una ruta está cerca de tu ubicación"
                enableVibration(true)
                enableLights(true)
            }
            
            val scheduleRemindersChannel = NotificationChannel(
                CHANNEL_SCHEDULE_REMINDERS,
                "Recordatorios de Horarios",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios de horarios de buses"
                enableVibration(true)
            }
            
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones generales de la app"
            }
            
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(routeAlertsChannel)
            manager.createNotificationChannel(scheduleRemindersChannel)
            manager.createNotificationChannel(generalChannel)
        }
    }
    
    /**
     * Envía notificación de ruta cercana
     */
    fun sendRouteProximityNotification(routeName: String, distanceInMeters: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val distanceText = if (distanceInMeters < 1000) {
            "$distanceInMeters metros"
        } else {
            "${distanceInMeters / 1000} km"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ROUTE_ALERTS)
            .setSmallIcon(R.drawable.img_icon_unab) // Asegúrate de tener este ícono
            .setContentTitle("🚌 Bus Cerca")
            .setContentText("$routeName está a $distanceText de tu ubicación")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        if (hasNotificationPermission()) {
            notificationManager.notify(NOTIFICATION_ID_ROUTE_ALERT, notification)
        }
    }
    
    /**
     * Envía recordatorio de horario
     */
    fun sendScheduleReminder(routeName: String, departureTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_SCHEDULE_REMINDERS)
            .setSmallIcon(R.drawable.img_icon_unab)
            .setContentTitle("⏰ Recordatorio de Horario")
            .setContentText("$routeName sale a las $departureTime")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$routeName sale a las $departureTime. No olvides estar listo.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        if (hasNotificationPermission()) {
            notificationManager.notify(NOTIFICATION_ID_SCHEDULE_REMINDER, notification)
        }
    }
    
    /**
     * Verifica si tenemos permiso de notificaciones (Android 13+)
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    /**
     * Cancela todas las notificaciones
     */
    fun cancelAll() {
        notificationManager.cancelAll()
    }
}


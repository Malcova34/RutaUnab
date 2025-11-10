package com.rutaunab.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rutaunab.app.domain.model.QRData
import com.rutaunab.app.domain.model.QRScan
import com.rutaunab.app.domain.model.ScanStatus
import com.rutaunab.app.domain.util.Result
import kotlinx.coroutines.tasks.await
import java.util.*

class QRScanRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    companion object {
        private const val QR_SCANS_COLLECTION = "qr_scans"
        private const val DUPLICATE_THRESHOLD_MINUTES = 30 // No permitir duplicados en 30 min
    }
    
    /**
     * Valida y registra un escaneo de QR
     */
    suspend fun validateAndRegisterScan(
        qrContent: String,
        driverId: String,
        driverName: String,
        busId: String
    ): Result<QRScan> {
        return try {
            // 1. Parsear el contenido del QR
            val qrData = QRData.fromJson(qrContent)
                ?: return Result.Error(
                    Exception("QR inválido"),
                    "Código QR no válido o corrupto"
                )
            
            // 2. Verificar si el QR ha expirado
            if (qrData.isExpired()) {
                return Result.Error(
                    Exception("QR expirado"),
                    "Este código QR ha expirado. Genera uno nuevo."
                )
            }
            
            // 3. Verificar si ya fue escaneado recientemente (duplicado)
            val isDuplicate = checkForDuplicate(qrData.userId, driverId)
            if (isDuplicate) {
                return Result.Error(
                    Exception("QR duplicado"),
                    "Este estudiante ya fue registrado recientemente"
                )
            }
            
            // 4. Crear el registro del escaneo
            val scanId = UUID.randomUUID().toString()
            val qrScan = QRScan(
                id = scanId,
                studentId = qrData.userId,
                studentName = qrData.userName,
                driverId = driverId,
                driverName = driverName,
                busId = busId,
                timestamp = Timestamp.now(),
                qrData = qrContent,
                status = ScanStatus.SUCCESS
            )
            
            // 5. Guardar en Firestore
            val scanData = hashMapOf(
                "id" to qrScan.id,
                "studentId" to qrScan.studentId,
                "studentName" to qrScan.studentName,
                "driverId" to qrScan.driverId,
                "driverName" to qrScan.driverName,
                "busId" to qrScan.busId,
                "timestamp" to qrScan.timestamp,
                "qrData" to qrScan.qrData,
                "status" to qrScan.status.name
            )
            
            firestore.collection(QR_SCANS_COLLECTION)
                .document(scanId)
                .set(scanData)
                .await()
            
            Result.Success(qrScan)
            
        } catch (e: Exception) {
            Result.Error(e, "Error al validar el código QR: ${e.message}")
        }
    }
    
    /**
     * Verifica si existe un escaneo duplicado reciente
     */
    private suspend fun checkForDuplicate(studentId: String, driverId: String): Boolean {
        return try {
            val threshold = Calendar.getInstance().apply {
                add(Calendar.MINUTE, -DUPLICATE_THRESHOLD_MINUTES)
            }
            
            val querySnapshot = firestore.collection(QR_SCANS_COLLECTION)
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("driverId", driverId)
                .whereGreaterThan("timestamp", Timestamp(threshold.time))
                .limit(1)
                .get()
                .await()
            
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false // En caso de error, permitir el escaneo
        }
    }
    
    /**
     * Obtiene el historial de escaneos de un conductor
     */
    suspend fun getDriverScans(driverId: String, limit: Int = 50): Result<List<QRScan>> {
        return try {
            val querySnapshot = firestore.collection(QR_SCANS_COLLECTION)
                .whereEqualTo("driverId", driverId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val scans = querySnapshot.documents.mapNotNull { doc ->
                try {
                    QRScan(
                        id = doc.getString("id") ?: "",
                        studentId = doc.getString("studentId") ?: "",
                        studentName = doc.getString("studentName") ?: "",
                        driverId = doc.getString("driverId") ?: "",
                        driverName = doc.getString("driverName") ?: "",
                        busId = doc.getString("busId") ?: "",
                        timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now(),
                        qrData = doc.getString("qrData") ?: "",
                        status = ScanStatus.valueOf(doc.getString("status") ?: "SUCCESS")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(scans)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener historial: ${e.message}")
        }
    }
    
    /**
     * Obtiene los escaneos de hoy para un conductor
     */
    suspend fun getTodayScansCount(driverId: String): Int {
        return try {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            
            val querySnapshot = firestore.collection(QR_SCANS_COLLECTION)
                .whereEqualTo("driverId", driverId)
                .whereGreaterThan("timestamp", Timestamp(today.time))
                .get()
                .await()
            
            querySnapshot.size()
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Obtiene el historial de escaneos de un estudiante
     */
    suspend fun getStudentScans(studentId: String, limit: Int = 50): Result<List<QRScan>> {
        return try {
            val querySnapshot = firestore.collection(QR_SCANS_COLLECTION)
                .whereEqualTo("studentId", studentId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val scans = querySnapshot.documents.mapNotNull { doc ->
                try {
                    QRScan(
                        id = doc.getString("id") ?: "",
                        studentId = doc.getString("studentId") ?: "",
                        studentName = doc.getString("studentName") ?: "",
                        driverId = doc.getString("driverId") ?: "",
                        driverName = doc.getString("driverName") ?: "",
                        busId = doc.getString("busId") ?: "",
                        timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now(),
                        qrData = doc.getString("qrData") ?: "",
                        status = ScanStatus.valueOf(doc.getString("status") ?: "SUCCESS")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(scans)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener historial: ${e.message}")
        }
    }
    
    /**
     * Obtiene estadísticas de uso de un estudiante
     */
    suspend fun getStudentStatistics(studentId: String): Result<com.rutaunab.app.domain.model.UserStatistics> {
        return try {
            val allScans = getStudentScans(studentId, 1000)
            
            when (allScans) {
                is Result.Success -> {
                    val scans = allScans.data
                    val totalTrips = scans.size
                    
                    // Contar viajes de este mes
                    val thisMonth = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }
                    val tripsThisMonth = scans.count { 
                        it.timestamp.toDate().after(thisMonth.time) 
                    }
                    
                    // Agrupar por ruta (usando busId como proxy de ruta)
                    val routeUsage = scans.groupingBy { it.busId }.eachCount()
                    val mostUsedRoute = routeUsage.maxByOrNull { it.value }?.key ?: "N/A"
                    
                    // Última fecha de viaje
                    val lastTripDate = scans.firstOrNull()?.timestamp?.toDate()?.let { date ->
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        sdf.format(date)
                    }
                    
                    // Promedio de viajes por semana (últimos 30 días)
                    val thirtyDaysAgo = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_MONTH, -30)
                    }
                    val recentTrips = scans.count { 
                        it.timestamp.toDate().after(thirtyDaysAgo.time) 
                    }
                    val averagePerWeek = (recentTrips.toDouble() / 30.0) * 7.0
                    
                    Result.Success(
                        com.rutaunab.app.domain.model.UserStatistics(
                            totalTrips = totalTrips,
                            tripsThisMonth = tripsThisMonth,
                            mostUsedRoute = mostUsedRoute,
                            routeUsageCounts = routeUsage,
                            lastTripDate = lastTripDate,
                            averageTripsPerWeek = averagePerWeek
                        )
                    )
                }
                is Result.Error -> allScans
                else -> Result.Error(Exception("Error desconocido"), "Error al obtener estadísticas")
            }
        } catch (e: Exception) {
            Result.Error(e, "Error al calcular estadísticas: ${e.message}")
        }
    }
}


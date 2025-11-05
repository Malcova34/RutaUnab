package com.rutaunab.app.data.api

import android.util.Log
import com.rutaunab.app.BuildConfig
import com.rutaunab.app.data.api.dto.BusApiDTO
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Data source para la API de tracking de buses
 */
class BusTrackingDataSource {
    
    private val apiService: BusTrackingApiService?
    private val isApiConfigured: Boolean
    
    init {
        // Validar que la URL de la API esté configurada
        val apiUrl = BuildConfig.BUS_API_URL
        isApiConfigured = apiUrl.isNotBlank() && apiUrl.startsWith("http")
        
        if (!isApiConfigured) {
            Log.w(TAG, "⚠️ BUS_API_URL no está configurada correctamente. La API de buses no funcionará.")
            apiService = null
        } else {
            Log.d(TAG, "✅ API de buses configurada: $apiUrl")
            
            // Logging interceptor para debug
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
            
            // OkHttp client con timeouts
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
            
            // Retrofit con conversor JSON (Gson)
            val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            apiService = retrofit.create(BusTrackingApiService::class.java)
        }
    }
    
    /**
     * Obtiene la ubicación actual de todos los buses
     */
    suspend fun getBusesLocation(): List<BusApiDTO>? {
        if (!isApiConfigured || apiService == null) {
            Log.w(TAG, "⚠️ API no configurada. Retornando null.")
            return null
        }
        
        return try {
            Log.d(TAG, "🚌 Obteniendo ubicación de buses...")
            val response = apiService.getBusesLocation()
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "✅ Respuesta exitosa. Buses encontrados: ${body?.size ?: 0}")
                
                // Log de los primeros 2 buses para debugging
                body?.take(2)?.forEach { bus ->
                    Log.d(TAG, "  📍 Bus: ${bus.placa} - Lat: ${bus.lat}, Lng: ${bus.lng}, Estado: ${bus.estadoIgnicion}")
                }
                
                body
            } else {
                Log.e(TAG, "❌ Error en respuesta: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al obtener buses: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    companion object {
        private const val TAG = "BusTrackingAPI"
    }
}


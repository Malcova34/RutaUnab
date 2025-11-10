package com.rutaunab.app.domain.model

import com.google.gson.Gson

data class QRData(
    val userId: String,
    val userName: String,
    val studentId: String,
    val email: String,
    val generatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 horas
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
    
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }
    
    companion object {
        fun fromJson(json: String): QRData? {
            return try {
                Gson().fromJson(json, QRData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}


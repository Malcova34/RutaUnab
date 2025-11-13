package com.rutaunab.app.presentation.screens.main.qr

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rutaunab.app.data.firebase.auth.FirebaseAuthDataSource
import com.rutaunab.app.data.firebase.firestore.FirestoreDataSource
import com.rutaunab.app.data.qr.QRCodeGenerator
import com.rutaunab.app.data.repository.AuthRepositoryImpl
import com.rutaunab.app.domain.model.QRData
import com.rutaunab.app.domain.usecase.auth.GetCurrentUserUseCase
import com.rutaunab.app.domain.util.onSuccess
import com.rutaunab.app.domain.util.onError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QRViewModel(

    private val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(
        AuthRepositoryImpl(
            FirebaseAuthDataSource(),
            FirestoreDataSource()
        )
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRUiState())
    val uiState: StateFlow<QRUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // getCurrentUser() retorna Result<User?>, NO Flow, por eso NO se usa .collect()
            val result = getCurrentUserUseCase()
            
            result.onSuccess { user ->
                if (user != null) {
                    // Crear datos del QR con toda la información del usuario
                    val qrData = QRData(
                        userId = user.id,
                        userName = user.name,
                        studentId = user.studentId,
                        email = user.email
                    )
                    
                    // Convertir a JSON
                    val qrCodeData = qrData.toJson()
                    
                    // Generar el bitmap del QR
                    val qrBitmap = QRCodeGenerator.generateQRCode(qrCodeData, 512)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            qrCode = qrCodeData,
                            qrBitmap = qrBitmap
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo cargar la información del usuario"
                        )
                    }
                }
            }.onError { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            }
        }
    }
    
    /**
     * Regenera el código QR (útil si expiró)
     */
    fun regenerateQRCode() {
        loadUserData()
    }
}


package com.rutaunab.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.rutaunab.app.data.local.PreferencesManager
import com.rutaunab.app.presentation.navigation.NavGraph
import com.rutaunab.app.presentation.ui.theme.RutaUnabTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtener instancia del gestor de preferencias para el tema
        val preferencesManager = PreferencesManager.getInstance(this)

        setContent {
            // Observar cambios en el modo oscuro
            val isDarkMode by preferencesManager.isDarkMode.collectAsState()

            // Aplicar tema basado en preferencias del usuario
            RutaUnabTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Configurar navegación principal
                    NavGraph()
                }
            }
        }
    }
}


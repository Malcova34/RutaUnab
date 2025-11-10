package com.rutaunab.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rutaunab.app.presentation.screens.auth.login.LoginScreen
import com.rutaunab.app.presentation.screens.auth.login.LoginViewModel
import com.rutaunab.app.presentation.screens.auth.register.RegisterScreen
import com.rutaunab.app.presentation.screens.auth.register.RegisterViewModel
import com.rutaunab.app.presentation.screens.auth.recovery.RecoveryScreen
import com.rutaunab.app.presentation.screens.splash.SplashScreen
import com.rutaunab.app.presentation.screens.home.HomeScreen
import com.rutaunab.app.presentation.screens.main.route.RoutesScreen
import com.rutaunab.app.presentation.screens.main.qr.QRScreen
import com.rutaunab.app.presentation.screens.main.map.MapScreen
import com.rutaunab.app.presentation.screens.main.map.MapViewModel
import com.rutaunab.app.presentation.screens.main.profile.ProfileScreen
import com.rutaunab.app.presentation.screens.main.profile.EditProfileScreen
import com.rutaunab.app.presentation.screens.main.settings.SettingsScreen
import com.rutaunab.app.presentation.screens.main.settings.SettingsViewModel
import com.rutaunab.app.presentation.screens.driver.DriverQRScannerScreen
import com.rutaunab.app.presentation.screens.driver.DriverQRScannerViewModel
import com.rutaunab.app.presentation.screens.driver.DriverProfileScreen
import com.rutaunab.app.presentation.screens.driver.DriverProfileViewModel



@Composable
fun NavGraph(){

    val navController = rememberNavController()
    val startDestination = Routes.SPLASH

    NavHost(navController = navController, startDestination = startDestination ){

        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = { hasSession, userType ->
                    if (hasSession && userType != null) {
                        // Si hay sesión válida, redirigir según el tipo de usuario
                        val destination = when (userType) {
                            "DRIVER" -> Routes.DRIVER_QR_SCANNER
                            "ADMIN" -> Routes.HOME
                            else -> Routes.HOME // STUDENT
                        }
                        navController.navigate(destination) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        // Si no hay sesión, ir al login
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.LOGIN){
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel {
                LoginViewModel(context)
            }
            LoginScreen(
                viewModel = loginViewModel,
                onClickRegister = { navController.navigate(Routes.REGISTER) },
                onClickRecovery = { navController.navigate(Routes.RECOVERY) },
                onSuccesfullLogin = { userType ->
                    // Redirigir según el rol del usuario
                    val destination = when (userType) {
                        com.rutaunab.app.domain.model.UserType.DRIVER -> Routes.DRIVER_QR_SCANNER
                        com.rutaunab.app.domain.model.UserType.ADMIN -> Routes.HOME // Admin usa interfaz normal por ahora
                        else -> Routes.HOME // STUDENT usa interfaz normal
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER){
            val context = LocalContext.current
            val registerViewModel: RegisterViewModel = viewModel {
                RegisterViewModel(context)
            }
            RegisterScreen(
                viewModel = registerViewModel,
                onClickBack = { navController.popBackStack() },
                onSuccesfulRegister = {
                    // Navegar a Home después del registro exitoso
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RECOVERY){
            RecoveryScreen(
                onClickBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME){
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToRoutes = { navController.navigate(Routes.ROUTES) },
                onNavigateToQR = { navController.navigate(Routes.QR) },
                onNavigateToMap = { navController.navigate(Routes.MAP) }
            )
        }

        composable(Routes.ROUTES){
            RoutesScreen(
                onNavigateToHome = { navController.navigate(Routes.HOME) },
                onNavigateToRoutes = { /* Ya estamos en Routes */ },
                onNavigateToQR = { navController.navigate(Routes.QR) },
                onNavigateToMap = { navController.navigate(Routes.MAP) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.QR){
            QRScreen(
                onNavigateToHome = { navController.navigate(Routes.HOME) },
                onNavigateToRoutes = { navController.navigate(Routes.ROUTES) },
                onNavigateToQR = { /* Ya estamos en QR */ },
                onNavigateToMap = { navController.navigate(Routes.MAP) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.MAP){
            val context = LocalContext.current
            val mapViewModel: MapViewModel = viewModel {
                MapViewModel(context)
            }
            MapScreen(
                viewModel = mapViewModel,
                onNavigateToHome = { navController.navigate(Routes.HOME) },
                onNavigateToRoutes = { navController.navigate(Routes.ROUTES) },
                onNavigateToQR = { navController.navigate(Routes.QR) },
                onNavigateToMap = { /* Ya estamos en Map */ },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.PROFILE){
            ProfileScreen(
                onNavigateToEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToHome = { navController.navigate(Routes.HOME) },
                onNavigateToRoutes = { navController.navigate(Routes.ROUTES) },
                onNavigateToQR = { navController.navigate(Routes.QR) },
                onNavigateToMap = { navController.navigate(Routes.MAP) }
            )
        }

        composable(Routes.EDIT_PROFILE){
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS){
            val context = LocalContext.current
            val settingsViewModel: SettingsViewModel = viewModel {
                SettingsViewModel(context)
            }
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToHome = { navController.navigate(Routes.HOME) },
                onNavigateToRoutes = { navController.navigate(Routes.ROUTES) },
                onNavigateToQR = { navController.navigate(Routes.QR) },
                onNavigateToMap = { navController.navigate(Routes.MAP) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Driver Routes
        composable(Routes.DRIVER_QR_SCANNER){
            val context = LocalContext.current
            val driverQRScannerViewModel: DriverQRScannerViewModel = viewModel {
                DriverQRScannerViewModel(context)
            }
            DriverQRScannerScreen(
                viewModel = driverQRScannerViewModel,
                onNavigateToProfile = { navController.navigate(Routes.DRIVER_PROFILE) },
                onNavigateToScanner = { /* Ya estamos aquí */ }
            )
        }

        composable(Routes.DRIVER_PROFILE){
            val context = LocalContext.current
            val driverProfileViewModel: DriverProfileViewModel = viewModel {
                DriverProfileViewModel(context)
            }
            DriverProfileScreen(
                viewModel = driverProfileViewModel,
                onNavigateToScanner = { navController.navigate(Routes.DRIVER_QR_SCANNER) },
                onNavigateToProfile = { /* Ya estamos aquí */ },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

    }


}
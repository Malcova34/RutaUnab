package com.rutaunab.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rutaunab.app.presentation.screens.auth.login.LoginScreen
import com.rutaunab.app.presentation.screens.auth.register.RegisterScreen
import com.rutaunab.app.presentation.screens.auth.recovery.RecoveryScreen
import com.rutaunab.app.presentation.screens.splash.SplashScreen
import com.rutaunab.app.presentation.screens.home.HomeScreen
import com.rutaunab.app.presentation.screens.main.route.RoutesScreen
import com.rutaunab.app.presentation.screens.main.qr.QRScreen
import com.rutaunab.app.presentation.screens.main.map.MapScreen



@Composable
fun NavGraph(){

    val navController = rememberNavController()
    val startDestination = Routes.SPLASH

    NavHost(navController = navController, startDestination = startDestination ){

        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN){
            LoginScreen(
                onClickRegister = { navController.navigate(Routes.REGISTER) },
                onClickRecovery = { navController.navigate(Routes.RECOVERY) },
                onSuccesfullLogin = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER){
            RegisterScreen(
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
            MapScreen(
                onNavigateToHome = { navController.navigate(Routes.HOME) },
                onNavigateToRoutes = { navController.navigate(Routes.ROUTES) },
                onNavigateToQR = { navController.navigate(Routes.QR) },
                onNavigateToMap = { /* Ya estamos en Map */ },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.PROFILE){
            // ProfileScreen (Por implementar)
        }

    }


}
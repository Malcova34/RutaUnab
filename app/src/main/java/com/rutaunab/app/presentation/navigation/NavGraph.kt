package com.rutaunab.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rutaunab.app.presentation.screens.auth.LoginScreen
import com.rutaunab.app.presentation.screens.auth.RegisterScreen
import com.rutaunab.app.presentation.screens.auth.RecoveryScreen
import com.rutaunab.app.presentation.screens.splash.SplashScreen



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
                    // TODO: Navegar a la pantalla principal cuando esté lista
                    // navController.navigate(Routes.HOME)
                }
            )
        }

        composable(Routes.REGISTER){
            RegisterScreen(
                onClickBack = { navController.popBackStack() },
                onSuccesfulRegister = {
                    // Navegar de regreso al login después del registro exitoso
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.RECOVERY){
            RecoveryScreen(
                onClickBack = { navController.popBackStack() }
            )
        }

    }


}
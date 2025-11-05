package com.rutaunab.app.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

fun NavController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
    }
}

fun NavController.navigateToLogin() {
    navigateAndClearBackStack(NavigationDestinations.Login.route)
}

fun NavController.navigateToHome() {
    navigateAndClearBackStack(NavigationDestinations.Home.route)
}


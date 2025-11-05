package com.rutaunab.app.presentation.navigation

sealed class NavigationDestinations(val route: String) {
    // Auth
    object Splash : NavigationDestinations(Routes.SPLASH)
    object Login : NavigationDestinations(Routes.LOGIN)
    object Register : NavigationDestinations(Routes.REGISTER)
    object Recovery : NavigationDestinations(Routes.RECOVERY)
    
    // Main
    object Home : NavigationDestinations("home")
    object Map : NavigationDestinations("map")
    
    // Routes
    object RoutesList : NavigationDestinations("routes_list")
    data class RouteDetail(val routeId: String) : NavigationDestinations("route_detail/$routeId") {
        companion object {
            const val ROUTE_WITH_ARGS = "route_detail/{routeId}"
        }
    }
    
    // Stops
    object StopsList : NavigationDestinations("stops_list")
    
    // Profile
    object Profile : NavigationDestinations("profile")
    object EditProfile : NavigationDestinations("edit_profile")
    
    // Driver
    object DriverHome : NavigationDestinations("driver_home")
}


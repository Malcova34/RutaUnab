package com.rutaunab.app.data.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.rutaunab.app.data.firebase.firestore.dto.RouteDTO
import com.rutaunab.app.data.firebase.firestore.dto.StopDTO
import com.rutaunab.app.data.firebase.firestore.dto.UserDTO
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    companion object {
        private const val USERS_COLLECTION = "users"
        // private const val BUSES_COLLECTION = "buses"  // TODO: Implementar en el futuro si se necesita
        private const val ROUTES_COLLECTION = "routes"
        private const val STOPS_COLLECTION = "stops"
    }
    
    // ========== USER OPERATIONS ==========
    
    suspend fun createUser(userId: String, userDTO: UserDTO) {
        // Crear un mapa explícito para asegurar que todos los datos se guarden
        val userData = hashMapOf<String, Any?>(
            "id" to userDTO.id,
            "fullName" to userDTO.fullName,
            "email" to userDTO.email,
            "idUnab" to userDTO.idUnab,
            "carrera" to userDTO.carrera,
            "role" to userDTO.role,
            "profileImageUrl" to userDTO.profileImageUrl,
            "createdAd" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(userData)
            .await()
    }
    
    suspend fun getUser(userId: String): UserDTO? {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject<UserDTO>()
    }
    
    suspend fun updateUser(userId: String, userDTO: UserDTO) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(userDTO)
            .await()
    }
    
    suspend fun deleteUser(userId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .delete()
            .await()
    }
    
    // ========== BUS OPERATIONS ==========
    // TODO: Implementar operaciones de buses en Firestore si se necesita en el futuro
    // Por ahora, los buses se obtienen desde la API XML en tiempo real
    
    /*
    suspend fun getAllBuses(): List<BusDTO> {
        return firestore.collection(BUSES_COLLECTION)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<BusDTO>() }
    }
    
    fun observeActiveBuses(): Flow<List<BusDTO>> = callbackFlow {
        val listener = firestore.collection(BUSES_COLLECTION)
            .whereEqualTo("status", "ACTIVE")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val buses = snapshot?.documents?.mapNotNull { it.toObject<BusDTO>() } ?: emptyList()
                trySend(buses)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun updateBusLocation(busId: String, location: LocationDTO) {
        firestore.collection(BUSES_COLLECTION)
            .document(busId)
            .update("currentLocation", location, "lastUpdated", System.currentTimeMillis())
            .await()
    }
    */
    
    // ========== ROUTE OPERATIONS ==========
    
    suspend fun getAllRoutes(): List<RouteDTO> {
        return firestore.collection(ROUTES_COLLECTION)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<RouteDTO>() }
    }
    
    suspend fun getRoute(routeId: String): RouteDTO? {
        return firestore.collection(ROUTES_COLLECTION)
            .document(routeId)
            .get()
            .await()
            .toObject<RouteDTO>()
    }
    
    // ========== STOP OPERATIONS ==========
    
    suspend fun getStopsByRoute(routeId: String): List<StopDTO> {
        return firestore.collection(STOPS_COLLECTION)
            .whereArrayContains("routeIds", routeId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<StopDTO>() }
            .sortedBy { it.order }
    }
    
    suspend fun getAllStops(): List<StopDTO> {
        return firestore.collection(STOPS_COLLECTION)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<StopDTO>() }
    }
}


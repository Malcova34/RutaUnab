package com.rutaunab.app.data.firebase.firestore.mapper

import com.rutaunab.app.data.firebase.firestore.dto.DriverInfoDTO
import com.rutaunab.app.data.firebase.firestore.dto.UserDTO
import com.rutaunab.app.domain.model.DriverInfo
import com.rutaunab.app.domain.model.User
import com.rutaunab.app.domain.model.UserType

object UserMapper {
    
    fun toDomain(dto: UserDTO): User {
        return User(
            id = dto.id,
            name = dto.fullName,
            email = dto.email,
            studentId = dto.idUnab,
            career = dto.carrera,
            userType = dto.role.toUserType(),
            driverInfo = dto.driverInfo?.toDomain(),
            profileImageUrl = dto.profileImageUrl,
            createdAt = dto.createdAd?.toDate()?.time ?: System.currentTimeMillis()
        )
    }
    
    fun toDTO(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            fullName = user.name,
            email = user.email,
            idUnab = user.studentId,
            carrera = user.career,
            role = user.userType.toRoleString(),
            driverInfo = user.driverInfo?.toDTO(),
            profileImageUrl = user.profileImageUrl,
            createdAd = null // ServerTimestamp se maneja automáticamente
        )
    }
    
    private fun String.toUserType(): UserType {
        return when (this.lowercase()) {
            "conductor", "driver" -> UserType.DRIVER
            "admin", "administrador" -> UserType.ADMIN
            else -> UserType.STUDENT // "usuario normal" y otros → STUDENT
        }
    }
    
    private fun UserType.toRoleString(): String {
        return when (this) {
            UserType.STUDENT -> "usuario normal"
            UserType.DRIVER -> "conductor"
            UserType.ADMIN -> "admin"
        }
    }
    
    private fun DriverInfoDTO.toDomain(): DriverInfo {
        return DriverInfo(
            licenseNumber = licenseNumber,
            licenseType = licenseType,
            assignedBusId = assignedBusId,
            yearsOfExperience = yearsOfExperience,
            rating = rating,
            totalTrips = totalTrips
        )
    }
    
    private fun DriverInfo.toDTO(): DriverInfoDTO {
        return DriverInfoDTO(
            licenseNumber = licenseNumber,
            licenseType = licenseType,
            assignedBusId = assignedBusId,
            yearsOfExperience = yearsOfExperience,
            rating = rating,
            totalTrips = totalTrips
        )
    }
}


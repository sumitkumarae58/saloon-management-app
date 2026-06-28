package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class NetworkLoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class NetworkLoginResponse(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "email") val email: String,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "role") val role: String
)

@JsonClass(generateAdapter = true)
data class NetworkRegisterRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "phone") val phone: String?,
    @Json(name = "role") val role: String
)

@JsonClass(generateAdapter = true)
data class NetworkSalon(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "address") val address: String,
    @Json(name = "city") val city: String,
    @Json(name = "pincode") val pincode: String,
    @Json(name = "phone") val phone: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "profileImageUrl") val profileImageUrl: String?,
    @Json(name = "rating") val rating: Double,
    @Json(name = "totalReviews") val totalReviews: Int
)

@JsonClass(generateAdapter = true)
data class NetworkBarber(
    @Json(name = "id") val id: String,
    @Json(name = "specialization") val specialization: String?,
    @Json(name = "experienceYears") val experienceYears: Int?,
    @Json(name = "rating") val rating: Double,
    @Json(name = "totalReviews") val totalReviews: Int,
    @Json(name = "user") val user: NetworkUser
)

@JsonClass(generateAdapter = true)
data class NetworkUser(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "phone") val phone: String?,
    @Json(name = "profilePictureUrl") val profilePictureUrl: String?
)

@JsonClass(generateAdapter = true)
data class NetworkAvailabilitySlot(
    @Json(name = "id") val id: String,
    @Json(name = "date") val date: String,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "endTime") val endTime: String,
    @Json(name = "isAvailable") val isAvailable: Boolean,
    @Json(name = "isBlocked") val isBlocked: Boolean
)

@JsonClass(generateAdapter = true)
data class NetworkBookingRequest(
    @Json(name = "salonId") val salonId: String,
    @Json(name = "barberId") val barberId: String?,
    @Json(name = "serviceId") val serviceId: String,
    @Json(name = "appointmentDate") val appointmentDate: String,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "notes") val notes: String?
)

@JsonClass(generateAdapter = true)
data class NetworkAppointment(
    @Json(name = "id") val id: String,
    @Json(name = "appointmentDate") val appointmentDate: String,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "endTime") val endTime: String,
    @Json(name = "status") val status: String,
    @Json(name = "paymentStatus") val paymentStatus: String,
    @Json(name = "paymentMethod") val paymentMethod: String,
    @Json(name = "notes") val notes: String?,
    @Json(name = "salon") val salon: NetworkSalon,
    @Json(name = "barber") val barber: NetworkBarber?
)

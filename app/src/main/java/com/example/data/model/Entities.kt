package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salons")
data class SalonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val description: String,
    val rating: Double,
    val imageResName: String,
    val phone: String,
    val openingHours: String
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val salonId: Int,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val description: String,
    val category: String
)

@Entity(tableName = "staff")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val salonId: Int,
    val name: String,
    val role: String,
    val rating: Double,
    val bio: String,
    val avatarResName: String
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val salonId: Int,
    val salonName: String,
    val serviceId: Int,
    val serviceName: String,
    val servicePrice: Double,
    val staffId: Int,
    val staffName: String,
    val date: String, // format: "YYYY-MM-DD"
    val timeSlot: String, // format: "HH:MM AM/PM"
    val status: String, // "Pending", "Confirmed", "Completed", "Cancelled"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

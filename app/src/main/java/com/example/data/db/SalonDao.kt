package com.example.data.db

import androidx.room.*
import com.example.data.model.BookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.StaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalonDao {

    // --- Salons ---
    @Query("SELECT * FROM salons ORDER BY rating DESC")
    fun getAllSalons(): Flow<List<SalonEntity>>

    @Query("SELECT * FROM salons WHERE id = :id LIMIT 1")
    suspend fun getSalonById(id: Int): SalonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalons(salons: List<SalonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalon(salon: SalonEntity): Long

    @Query("DELETE FROM salons")
    suspend fun deleteAllSalons()

    // --- Services ---
    @Query("SELECT * FROM services WHERE salonId = :salonId")
    fun getServicesBySalon(salonId: Int): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    // --- Staff ---
    @Query("SELECT * FROM staff WHERE salonId = :salonId")
    fun getStaffBySalon(salonId: Int): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffList(staff: List<StaffEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity): Long

    @Query("DELETE FROM staff")
    suspend fun deleteAllStaff()

    @Delete
    suspend fun deleteStaff(staff: StaffEntity)

    // --- Bookings ---
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE customerEmail = :email ORDER BY timestamp DESC")
    fun getBookingsByCustomer(email: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE staffId = :staffId ORDER BY date ASC, timeSlot ASC")
    fun getBookingsByStaff(staffId: Int): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
}
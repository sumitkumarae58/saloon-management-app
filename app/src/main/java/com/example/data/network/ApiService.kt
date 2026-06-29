package com.example.data.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Authentication ---
    @POST("api/auth/register")
    suspend fun register(@Body request: NetworkRegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: NetworkLoginRequest): Response<NetworkLoginResponse>

    // --- Customer Operations ---
    @GET("api/customer/salons")
    suspend fun getSalons(@Query("query") query: String?): Response<List<NetworkSalon>>

    @GET("api/customer/salons/{id}")
    suspend fun getSalonById(@Path("id") salonId: String): Response<NetworkSalon>

    @GET("api/customer/salons/{id}/barbers")
    suspend fun getBarbersBySalon(@Path("id") salonId: String): Response<List<NetworkBarber>>

    @GET("api/customer/salons/{id}/services")
    suspend fun getServicesBySalon(@Path("id") salonId: String): Response<List<NetworkService>>

    @GET("api/customer/barbers/{barberId}/availability")
    suspend fun getBarberAvailability(
        @Path("barberId") barberId: String,
        @Query("date") date: String
    ): Response<List<NetworkAvailabilitySlot>>

    @POST("api/customer/appointments")
    suspend fun createAppointment(@Body request: NetworkBookingRequest): Response<NetworkAppointment>

    @GET("api/customer/appointments")
    suspend fun getCustomerAppointments(): Response<List<NetworkAppointment>>

    @PUT("api/customer/appointments/{id}/cancel")
    suspend fun cancelAppointment(@Path("id") appointmentId: String): Response<String>

    // --- Owner Operations ---
    @POST("api/owner/salon")
    suspend fun createSalon(
        @Body request: NetworkCreateSalonRequest,
        @Query("ownerId") ownerId: String
    ): Response<NetworkSalon>

    @POST("api/owner/barbers/create")
    suspend fun createBarber(
        @Query("salonId") salonId: String,
        @Query("fullName") fullName: String,
        @Query("specialization") specialization: String,
        @Query("experienceYears") experienceYears: Int
    ): Response<NetworkBarber>

    @POST("api/owner/services")
    suspend fun createService(
        @Query("salonId") salonId: String,
        @Query("name") name: String,
        @Query("description") description: String,
        @Query("durationMinutes") durationMinutes: Int,
        @Query("price") price: Double,
        @Query("categoryName") categoryName: String
    ): Response<NetworkService>
}

package com.example.data.repository

import com.example.data.db.SalonDao
import com.example.data.model.BookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.StaffEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SalonRepository(private val salonDao: SalonDao) {

    val allSalons: Flow<List<SalonEntity>> = salonDao.getAllSalons()
    val allBookings: Flow<List<BookingEntity>> = salonDao.getAllBookings()
    val allServices: Flow<List<ServiceEntity>> = salonDao.getAllServices()
    val allStaff: Flow<List<StaffEntity>> = salonDao.getAllStaff()

    fun getServicesForSalon(salonId: Int): Flow<List<ServiceEntity>> = salonDao.getServicesBySalon(salonId)
    fun getStaffForSalon(salonId: Int): Flow<List<StaffEntity>> = salonDao.getStaffBySalon(salonId)
    fun getBookingsForCustomer(email: String): Flow<List<BookingEntity>> = salonDao.getBookingsByCustomer(email)
    fun getBookingsForStaff(staffId: Int): Flow<List<BookingEntity>> = salonDao.getBookingsByStaff(staffId)

    suspend fun getSalonById(id: Int): SalonEntity? = salonDao.getSalonById(id)

    suspend fun insertSalon(salon: SalonEntity): Long {
        return salonDao.insertSalon(salon)
    }

    suspend fun clearAllSalons() {
        salonDao.deleteAllSalons()
    }

    suspend fun clearAllServices() {
        salonDao.deleteAllServices()
    }

    suspend fun clearAllStaff() {
        salonDao.deleteAllStaff()
    }

    suspend fun insertSalons(salons: List<SalonEntity>) {
        salonDao.insertSalons(salons)
    }

    suspend fun insertServices(services: List<ServiceEntity>) {
        salonDao.insertServices(services)
    }

    suspend fun insertStaffList(staffList: List<StaffEntity>) {
        salonDao.insertStaffList(staffList)
    }

    suspend fun bookAppointment(booking: BookingEntity): Long {
        return salonDao.insertBooking(booking)
    }

    suspend fun updateBookingStatus(bookingId: Int, status: String) {
        salonDao.updateBookingStatus(bookingId, status)
    }

    suspend fun insertBooking(booking: BookingEntity) {
        salonDao.insertBooking(booking)
    }

    suspend fun insertService(service: ServiceEntity) {
        salonDao.insertService(service)
    }

    suspend fun deleteService(service: ServiceEntity) {
        salonDao.deleteService(service)
    }

    suspend fun insertStaff(staff: StaffEntity) {
        salonDao.insertStaff(staff)
    }

    suspend fun deleteStaff(staff: StaffEntity) {
        salonDao.deleteStaff(staff)
    }

    suspend fun deleteBooking(booking: BookingEntity) {
        salonDao.deleteBooking(booking)
    }

    suspend fun seedDatabaseIfEmpty() {
        val existingSalons = allSalons.first()
        if (existingSalons.isNotEmpty()) return // Database already seeded

        // 1. Seed Salons
        val salons = listOf(
            SalonEntity(
                name = "Salon Luxe & Grooming",
                address = "124 Grand Avenue, Suite A",
                description = "A sophisticated wellness and grooming space designed for the modern professional. We pair artisan craftsmanship with the finest organic products to curate a premier sensory experience.",
                rating = 4.9,
                imageResName = "img_salon_hero", // references img_salon_hero.jpg
                phone = "(555) 234-5678",
                openingHours = "08:00 AM - 08:00 PM"
            ),
            SalonEntity(
                name = "The Velvet Lounge",
                address = "52 Pine Boulevard",
                description = "Experience premium pampering, luxury hair styling, facial therapy, and custom cosmetic designs in our tranquil, boutique space.",
                rating = 4.8,
                imageResName = "img_barber_tools", // references img_barber_tools.jpg
                phone = "(555) 987-6543",
                openingHours = "09:00 AM - 07:00 PM"
            ),
            SalonEntity(
                name = "Studio Noir Barbers",
                address = "88 Industrial Way",
                description = "Urban minimalism and classic barbering. Specializing in skin fades, straight razor shaves, and industrial slate textures.",
                rating = 4.7,
                imageResName = "img_barber_tools",
                phone = "(555) 456-7890",
                openingHours = "10:00 AM - 09:00 PM"
            )
        )
        salonDao.insertSalons(salons)

        // Fetch inserted salons to map their generated IDs
        val insertedSalons = allSalons.first()
        val mainSalonId = insertedSalons.firstOrNull { it.name == "Salon Luxe & Grooming" }?.id ?: 1
        val velvetSalonId = insertedSalons.firstOrNull { it.name == "The Velvet Lounge" }?.id ?: 2
        val noirSalonId = insertedSalons.firstOrNull { it.name == "Studio Noir Barbers" }?.id ?: 3

        // 2. Seed Services
        val services = listOf(
            // Main Salon Services
            ServiceEntity(
                salonId = mainSalonId,
                name = "Signature Cut & Style",
                price = 45.0,
                durationMinutes = 45,
                description = "Tailored haircut with shampoo, warm neck shave, and custom design styling with premium pomade.",
                category = "Hair"
            ),
            ServiceEntity(
                salonId = mainSalonId,
                name = "Classic Beard Trim & Oil",
                price = 30.0,
                durationMinutes = 30,
                description = "Beard sculpting, clean cheek line straight-razor detailing, and luxury hydration oils.",
                category = "Beard"
            ),
            ServiceEntity(
                salonId = mainSalonId,
                name = "The Royal Grooming Combo",
                price = 85.0,
                durationMinutes = 90,
                description = "Our flagship service. Signature Haircut, Hot Towel Straight Razor Shave, and Charcoal Clay Purifying Facial.",
                category = "Combo"
            ),
            ServiceEntity(
                salonId = mainSalonId,
                name = "Charcoal Pore Cleansing Facial",
                price = 40.0,
                durationMinutes = 40,
                description = "Deep tissue facial wash, black charcoal purifying clay mask, steamed herbal towels, and ice-sphere hydration.",
                category = "Facial"
            ),
            ServiceEntity(
                salonId = mainSalonId,
                name = "Straight Razor Hot Towel Shave",
                price = 35.0,
                durationMinutes = 35,
                description = "A nostalgic shaving experience with multi-layer lathering, pre-shave oils, steamed towel therapy, and soothing balms.",
                category = "Shave"
            ),

            // Velvet Lounge Services
            ServiceEntity(
                salonId = velvetSalonId,
                name = "Luxury Velvet Blowout",
                price = 60.0,
                durationMinutes = 60,
                description = "Volumizing wash, scalp massage, and custom blow dry styling for maximum movement and shine.",
                category = "Hair"
            ),
            ServiceEntity(
                salonId = velvetSalonId,
                name = "Gold Hydration Facial",
                price = 75.0,
                durationMinutes = 50,
                description = "24k gold infused mask, intense botanical hydration serums, and lymphatic facial massage.",
                category = "Facial"
            ),

            // Studio Noir Services
            ServiceEntity(
                salonId = noirSalonId,
                name = "Skin Fade & Edge detailing",
                price = 40.0,
                durationMinutes = 40,
                description = "High precision zero-fade or skin-fade, razor line detailing, and matte clay finish.",
                category = "Hair"
            ),
            ServiceEntity(
                salonId = noirSalonId,
                name = "Hot Razor Shave & Head Shave",
                price = 50.0,
                durationMinutes = 45,
                description = "Complete head straight-razor shave with menthol cold therapy and tea tree oil conditioning.",
                category = "Shave"
            )
        )
        salonDao.insertServices(services)

        // 3. Seed Staff / Barbers
        val staff = listOf(
            // Main Salon
            StaffEntity(
                salonId = mainSalonId,
                name = "Marcus Vance",
                role = "Master Barber",
                rating = 4.95,
                bio = "With over 12 years of luxury grooming experience in London and New York, Marcus specializes in precision scissor cuts and classic straight razor work.",
                avatarResName = "marcus"
            ),
            StaffEntity(
                salonId = mainSalonId,
                name = "Sophia Loren",
                role = "Senior Stylist",
                rating = 4.90,
                bio = "Sophia is a creative stylist passionate about contemporary textures, dynamic coloring, and custom modern blowouts.",
                avatarResName = "sophia"
            ),
            StaffEntity(
                salonId = mainSalonId,
                name = "Devon Cole",
                role = "Grooming Specialist",
                rating = 4.85,
                bio = "Devon focuses on healthy skin and facial wellness. He has curated our facial massage techniques and specialized beard hydration treatments.",
                avatarResName = "devon"
            ),

            // Velvet Lounge
            StaffEntity(
                salonId = velvetSalonId,
                name = "Amara Smith",
                role = "Cosmetic & Facial Specialist",
                rating = 4.92,
                bio = "Amara combines modern dermatological techniques with wellness practices to deliver refreshing, transformative facials.",
                avatarResName = "amara"
            ),

            // Studio Noir
            StaffEntity(
                salonId = noirSalonId,
                name = "Zane Miller",
                role = "Precision Barber",
                rating = 4.88,
                bio = "Zane is a fade virtuoso. Known for high-contrast skin fades, geometric beard lineups, and custom urban hair patterns.",
                avatarResName = "zane"
            )
        )
        salonDao.insertStaffList(staff)

        // Fetch inserted staff members to map booking assignments
        val insertedStaff = allStaff.first()
        val marcus = insertedStaff.firstOrNull { it.name == "Marcus Vance" }
        val sophia = insertedStaff.firstOrNull { it.name == "Sophia Loren" }
        val devon = insertedStaff.firstOrNull { it.name == "Devon Cole" }

        val insertedServices = allServices.first()
        val cutService = insertedServices.firstOrNull { it.name == "Signature Cut & Style" }
        val beardService = insertedServices.firstOrNull { it.name == "Classic Beard Trim & Oil" }
        val comboService = insertedServices.firstOrNull { it.name == "The Royal Grooming Combo" }

        // 4. Seed Bookings / Appointments (For Analytics and Calendar view)
        // Set dates relative to "2026-06-28" (Sunday) or "2026-06-27" (Saturday)
        if (marcus != null && sophia != null && devon != null && cutService != null && beardService != null && comboService != null) {
            val bookings = listOf(
                // Upcoming Confirmed (Today / Tomorrow / Next week)
                BookingEntity(
                    customerName = "James Carter",
                    customerEmail = "james.carter@example.com",
                    customerPhone = "(555) 111-2222",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-28", // Sunday
                    timeSlot = "09:00 AM",
                    status = "Confirmed",
                    notes = "Needs dry texturizing. Prefers tea tree shampoo.",
                    timestamp = System.currentTimeMillis() - 86400000 * 2 // booked 2 days ago
                ),
                BookingEntity(
                    customerName = "Liam Neeson",
                    customerEmail = "liam.n@example.com",
                    customerPhone = "(555) 333-4444",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = comboService.id,
                    serviceName = comboService.name,
                    servicePrice = comboService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-28", // Sunday
                    timeSlot = "11:00 AM",
                    status = "Confirmed",
                    notes = "Prepping for a photoshoot. Let's do the full razor detailing.",
                    timestamp = System.currentTimeMillis() - 86400000 * 3
                ),
                BookingEntity(
                    customerName = "Evelyn Reed",
                    customerEmail = "evelyn@example.com",
                    customerPhone = "(555) 555-6666",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = sophia.id,
                    staffName = sophia.name,
                    date = "2026-06-28", // Sunday
                    timeSlot = "01:00 PM",
                    status = "Pending",
                    notes = "First time with Sophia.",
                    timestamp = System.currentTimeMillis() - 3600000 * 4
                ),
                BookingEntity(
                    customerName = "Oliver Bennett",
                    customerEmail = "oliver.b@example.com",
                    customerPhone = "(555) 777-8888",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = beardService.id,
                    serviceName = beardService.name,
                    servicePrice = beardService.price,
                    staffId = devon.id,
                    staffName = devon.name,
                    date = "2026-06-28", // Sunday
                    timeSlot = "03:00 PM",
                    status = "Confirmed",
                    notes = "Prefers extra peppermint hot towel therapy.",
                    timestamp = System.currentTimeMillis() - 3600000 * 12
                ),

                // Upcoming Next Day - June 29
                BookingEntity(
                    customerName = "Henry Cavill",
                    customerEmail = "henry@example.com",
                    customerPhone = "(555) 222-3333",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-29", // Monday
                    timeSlot = "10:00 AM",
                    status = "Confirmed",
                    notes = "Classic contour trim.",
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                BookingEntity(
                    customerName = "Robert Downey",
                    customerEmail = "rdj@example.com",
                    customerPhone = "(555) 999-0000",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = beardService.id,
                    serviceName = beardService.name,
                    servicePrice = beardService.price,
                    staffId = devon.id,
                    staffName = devon.name,
                    date = "2026-06-29", // Monday
                    timeSlot = "11:30 AM",
                    status = "Confirmed",
                    notes = "Keep mustache styled and trimmed clean.",
                    timestamp = System.currentTimeMillis() - 3600000
                ),

                // Past Completed Bookings (For Analytics - Revenue calculations!)
                BookingEntity(
                    customerName = "Bruce Wayne",
                    customerEmail = "bruce@waynecorp.com",
                    customerPhone = "(555) 007-1939",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = comboService.id,
                    serviceName = comboService.name,
                    servicePrice = comboService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-25", // Past
                    timeSlot = "11:00 AM",
                    status = "Completed",
                    notes = "Discreet, high-end care only.",
                    timestamp = System.currentTimeMillis() - 86400000 * 3
                ),
                BookingEntity(
                    customerName = "Peter Parker",
                    customerEmail = "peter.p@example.com",
                    customerPhone = "(555) 123-4567",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = sophia.id,
                    staffName = sophia.name,
                    date = "2026-06-26", // Past
                    timeSlot = "02:30 PM",
                    status = "Completed",
                    notes = "Student discount? Simple, active style.",
                    timestamp = System.currentTimeMillis() - 86400000 * 2
                ),
                BookingEntity(
                    customerName = "Clark Kent",
                    customerEmail = "clark@dailyplanet.com",
                    customerPhone = "(555) 888-9999",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-26", // Past
                    timeSlot = "09:00 AM",
                    status = "Completed",
                    notes = "Neat side part. Keep curl intact.",
                    timestamp = System.currentTimeMillis() - 86400000 * 2
                ),
                BookingEntity(
                    customerName = "Miles Morales",
                    customerEmail = "miles@example.com",
                    customerPhone = "(555) 444-1111",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = sophia.id,
                    staffName = sophia.name,
                    date = "2026-06-27", // Past (Yesterday)
                    timeSlot = "10:30 AM",
                    status = "Completed",
                    notes = "Textured crop style fade.",
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                BookingEntity(
                    customerName = "Steve Rogers",
                    customerEmail = "steve@example.com",
                    customerPhone = "(555) 194-2011",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = cutService.id,
                    serviceName = cutService.name,
                    servicePrice = cutService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-27", // Past (Yesterday)
                    timeSlot = "04:00 PM",
                    status = "Completed",
                    notes = "Military trim styling.",
                    timestamp = System.currentTimeMillis() - 86400000
                ),

                // Past Cancelled (for dashboards status visualizers)
                BookingEntity(
                    customerName = "Tony Stark",
                    customerEmail = "tony@starkindustries.com",
                    customerPhone = "(555) 300-3000",
                    salonId = mainSalonId,
                    salonName = "Salon Luxe & Grooming",
                    serviceId = comboService.id,
                    serviceName = comboService.name,
                    servicePrice = comboService.price,
                    staffId = marcus.id,
                    staffName = marcus.name,
                    date = "2026-06-27", // Cancelled yesterday
                    timeSlot = "02:00 PM",
                    status = "Cancelled",
                    notes = "Stuck in a meeting overseas. Will reschedule.",
                    timestamp = System.currentTimeMillis() - 86400000
                )
            )

            for (booking in bookings) {
                salonDao.insertBooking(booking)
            }
        }
    }
}

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.SalonDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.StaffEntity
import com.example.data.repository.SalonRepository
import com.example.data.network.RetrofitClient
import com.example.data.network.NetworkLoginRequest
import com.example.data.network.NetworkRegisterRequest
import com.example.data.network.NetworkBookingRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SalonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalonRepository

    // Initial database flows
    val allSalons: StateFlow<List<SalonEntity>>
    val allBookings: StateFlow<List<BookingEntity>>
    val allServices: StateFlow<List<ServiceEntity>>
    val allStaff: StateFlow<List<StaffEntity>>

    init {
        val database = SalonDatabase.getDatabase(application)
        repository = SalonRepository(database.salonDao())

        // Seed DB on start
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }

        // Background sync check with the backend
        viewModelScope.launch {
            try {
                android.util.Log.d("SalonNetwork", "Checking connection to backend at http://10.0.2.2:8080/...")
                val loginResponse = RetrofitClient.apiService.login(
                    NetworkLoginRequest("customer1@email.com", "password123")
                )
                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val token = loginResponse.body()!!.accessToken
                    RetrofitClient.setAuthToken(token)
                    android.util.Log.d("SalonNetwork", "Successfully connected to Spring Boot backend! Authenticated as customer1@email.com.")
                    
                    val salonsResponse = RetrofitClient.apiService.getSalons(null)
                    if (salonsResponse.isSuccessful && salonsResponse.body() != null) {
                        val salonsList = salonsResponse.body()!!
                        android.util.Log.d("SalonNetwork", "Retrieved ${salonsList.size} salons from backend. Starting database synchronization...")
                        
                        // Clear existing mock/seeded local database entries so we load fresh data from your PostgreSQL backend
                        repository.clearAllSalons()
                        repository.clearAllServices()
                        repository.clearAllStaff()
                        
                        for (netSalon in salonsList) {
                            val salonEntity = SalonEntity(
                                name = netSalon.name,
                                address = netSalon.address,
                                description = netSalon.description ?: "A premier grooming and wellness lounge.",
                                rating = netSalon.rating,
                                phone = netSalon.phone ?: "(555) 123-4567",
                                openingHours = "09:00 AM - 07:00 PM",
                                imageResName = if (netSalon.name.contains("Premium", ignoreCase = true)) "img_salon_hero" else "img_barber_tools",
                                backendUuid = netSalon.id
                            )
                            val localSalonId = repository.insertSalon(salonEntity).toInt()
                            android.util.Log.d("SalonNetwork", "Synced salon to local DB: ${netSalon.name} (Local ID: $localSalonId, Backend UUID: ${netSalon.id})")
                            
                            // 1. Fetch and Sync barbers for this salon from backend
                            val barbersResponse = RetrofitClient.apiService.getBarbersBySalon(netSalon.id)
                            if (barbersResponse.isSuccessful && barbersResponse.body() != null) {
                                val barbersList = barbersResponse.body()!!
                                for (barber in barbersList) {
                                    val staffEntity = StaffEntity(
                                        salonId = localSalonId,
                                        name = "${barber.user.firstName} ${barber.user.lastName}",
                                        role = barber.specialization ?: "Expert Stylist",
                                        rating = barber.rating,
                                        bio = "With ${barber.experienceYears ?: 5} years of professional experience, specializing in premium grooming.",
                                        avatarResName = "marcus",
                                        backendUuid = barber.id
                                    )
                                    repository.insertStaff(staffEntity)
                                }
                                android.util.Log.d("SalonNetwork", "Synced ${barbersList.size} barbers for ${netSalon.name}")
                            }
                            
                            // 2. Fetch and Sync services for this salon from backend
                            val servicesResponse = RetrofitClient.apiService.getServicesBySalon(netSalon.id)
                            if (servicesResponse.isSuccessful && servicesResponse.body() != null) {
                                val servicesList = servicesResponse.body()!!
                                for (service in servicesList) {
                                    val serviceEntity = ServiceEntity(
                                        salonId = localSalonId,
                                        name = service.name,
                                        price = service.price,
                                        durationMinutes = service.durationMinutes,
                                        description = service.description ?: "Premium professional salon treatment.",
                                        category = service.category,
                                        backendUuid = service.id
                                    )
                                    repository.insertService(serviceEntity)
                                }
                                android.util.Log.d("SalonNetwork", "Synced ${servicesList.size} services for ${netSalon.name}")
                            } else {
                                // Fallback services if the backend services table returns empty (e.g. initial setup)
                                android.util.Log.w("SalonNetwork", "Could not fetch services for ${netSalon.name}. Seeding default services linked to backend...")
                                val fallbackServices = listOf(
                                    ServiceEntity(
                                        salonId = localSalonId,
                                        name = "Classic Haircut",
                                        price = 25.0,
                                        durationMinutes = 30,
                                        description = "Traditional haircut with scissors and clippers.",
                                        category = "Hair",
                                        backendUuid = "11111111-1111-1111-1111-111111111111"
                                    ),
                                    ServiceEntity(
                                        salonId = localSalonId,
                                        name = "Skin Fade",
                                        price = 35.0,
                                        durationMinutes = 45,
                                        description = "Modern skin fade with precision styling.",
                                        category = "Hair",
                                        backendUuid = "22222222-2222-2222-2222-222222222222"
                                    ),
                                    ServiceEntity(
                                        salonId = localSalonId,
                                        name = "Beard Trim & Shape",
                                        price = 15.0,
                                        durationMinutes = 20,
                                        description = "Professional beard trimming and shaping.",
                                        category = "Beard",
                                        backendUuid = "33333333-3333-3333-3333-333333333333"
                                    )
                                )
                                for (service in fallbackServices) {
                                    repository.insertService(service)
                                }
                            }
                        }
                        android.util.Log.i("SalonNetwork", "Database synchronization completed successfully! Local UI is now using real data.")
                    }
                } else {
                    android.util.Log.w("SalonNetwork", "Backend reachable but login failed: ${loginResponse.message()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SalonNetwork", "Could not connect to backend server. Running in offline/local-only mode. Details: ${e.message}")
            }
        }

        allSalons = repository.allSalons
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allBookings = repository.allBookings
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allServices = repository.allServices
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allStaff = repository.allStaff
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // --- Active UI Role State ---
    private val _currentRole = MutableStateFlow("Customer") // "Customer", "Salon Owner", "Barber", "Platform Admin"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    fun switchRole(role: String) {
        _currentRole.value = role
    }

    // --- Customer Experience States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    private val _selectedSalon = MutableStateFlow<SalonEntity?>(null)
    val selectedSalon: StateFlow<SalonEntity?> = _selectedSalon.asStateFlow()

    fun selectSalon(salon: SalonEntity?) {
        _selectedSalon.value = salon
        if (salon == null) {
            resetBookingWizard()
        }
    }

    // --- Booking Wizard State ---
    private val _bookingStep = MutableStateFlow(1) // 1: Service, 2: Barber, 3: Date & Time, 4: Personal Info
    val bookingStep: StateFlow<Int> = _bookingStep.asStateFlow()

    private val _bookingService = MutableStateFlow<ServiceEntity?>(null)
    val bookingService: StateFlow<ServiceEntity?> = _bookingService.asStateFlow()

    private val _bookingStaff = MutableStateFlow<StaffEntity?>(null) // null represents "Any Available"
    val bookingStaff: StateFlow<StaffEntity?> = _bookingStaff.asStateFlow()

    private val _bookingDate = MutableStateFlow("2026-06-28") // Format: "YYYY-MM-DD"
    val bookingDate: StateFlow<String> = _bookingDate.asStateFlow()

    private val _bookingTime = MutableStateFlow<String?>(null) // Format: "HH:MM AM/PM"
    val bookingTime: StateFlow<String?> = _bookingTime.asStateFlow()

    private val _bookingSuccess = MutableStateFlow(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess.asStateFlow()

    // Default Customer Email to view their appointments on Customer screen
    private val _customerEmailInput = MutableStateFlow("james.carter@example.com")
    val customerEmailInput: StateFlow<String> = _customerEmailInput.asStateFlow()

    fun updateCustomerEmail(email: String) {
        _customerEmailInput.value = email
    }

    val customerBookings: StateFlow<List<BookingEntity>> = _customerEmailInput
        .flatMapLatest { email ->
            repository.getBookingsForCustomer(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Booking Wizards Actions ---
    fun nextBookingStep() {
        if (_bookingStep.value < 4) {
            _bookingStep.value += 1
        }
    }

    fun prevBookingStep() {
        if (_bookingStep.value > 1) {
            _bookingStep.value -= 1
        }
    }

    fun selectBookingService(service: ServiceEntity) {
        _bookingService.value = service
        _bookingStep.value = 2
    }

    fun selectBookingStaff(staff: StaffEntity?) {
        _bookingStaff.value = staff
        _bookingStep.value = 3
    }

    fun selectBookingDateTime(date: String, time: String) {
        _bookingDate.value = date
        _bookingTime.value = time
        _bookingStep.value = 4
    }

    fun resetBookingWizard() {
        _bookingStep.value = 1
        _bookingService.value = null
        _bookingStaff.value = null
        _bookingDate.value = "2026-06-28"
        _bookingTime.value = null
        _bookingSuccess.value = false
    }

    private fun convertTimeFormatTo24h(time12h: String): String {
        try {
            val cleanTime = time12h.trim()
            val parts = cleanTime.split(" ")
            if (parts.size < 2) return cleanTime
            val timePart = parts[0]
            val amPm = parts[1].uppercase()
            val timeParts = timePart.split(":")
            var hour = timeParts[0].toInt()
            val minute = timeParts[1]
            
            if (amPm == "PM" && hour < 12) hour += 12
            if (amPm == "AM" && hour == 12) hour = 0
            
            return String.format("%02d:%s:00", hour, minute)
        } catch (e: Exception) {
            return "10:00:00"
        }
    }

    fun submitBooking(name: String, email: String, phone: String, notes: String) {
        val salon = _selectedSalon.value ?: return
        val service = _bookingService.value ?: return
        val staff = _bookingStaff.value

        viewModelScope.launch {
            val booking = BookingEntity(
                customerName = name,
                customerEmail = email,
                customerPhone = phone,
                salonId = salon.id,
                salonName = salon.name,
                serviceId = service.id,
                serviceName = service.name,
                servicePrice = service.price,
                staffId = staff?.id ?: 0,
                staffName = staff?.name ?: "Any Available Barber",
                date = _bookingDate.value,
                timeSlot = _bookingTime.value ?: "10:00 AM",
                status = "Pending",
                notes = notes
            )
            repository.bookAppointment(booking)
            _bookingSuccess.value = true
            _customerEmailInput.value = email // Switch login to this user

            // --- Synchronize with backend if connected ---
            try {
                val loginEmail = if (email.contains("@")) email else "customer1@email.com"
                android.util.Log.d("SalonNetwork", "Syncing booking to backend for $loginEmail...")
                
                // 1. Authenticate user
                val loginResponse = RetrofitClient.apiService.login(
                    NetworkLoginRequest(loginEmail, "password123")
                )
                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    RetrofitClient.setAuthToken(loginResponse.body()!!.accessToken)
                    android.util.Log.d("SalonNetwork", "Successfully logged in as $loginEmail")
                } else {
                    // Try registering user if login fails (first-time user!)
                    android.util.Log.d("SalonNetwork", "Login failed. Trying to register user $loginEmail...")
                    val names = name.split(" ")
                    val firstName = names.firstOrNull() ?: "Customer"
                    val lastName = if (names.size > 1) names.last() else "User"
                    val registerResponse = RetrofitClient.apiService.register(
                        NetworkRegisterRequest(
                            email = loginEmail,
                            password = "password123",
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone,
                            role = "ROLE_CUSTOMER"
                        )
                    )
                    if (registerResponse.isSuccessful) {
                        android.util.Log.d("SalonNetwork", "Registration successful. Logging in user $loginEmail...")
                        val retryLogin = RetrofitClient.apiService.login(
                            NetworkLoginRequest(loginEmail, "password123")
                        )
                        if (retryLogin.isSuccessful && retryLogin.body() != null) {
                            RetrofitClient.setAuthToken(retryLogin.body()!!.accessToken)
                        }
                    } else {
                        android.util.Log.e("SalonNetwork", "Failed to register user: ${registerResponse.errorBody()?.string()}")
                    }
                }
                
                // 2. Resolve backend IDs directly from synchronized database entities
                val finalSalonId = salon.backendUuid ?: "00000000-0000-0000-0000-000000000001"
                val finalServiceId = service.backendUuid ?: "11111111-1111-1111-1111-111111111111"
                val finalBarberId = staff?.backendUuid
                
                val bookingRequest = NetworkBookingRequest(
                    salonId = finalSalonId,
                    barberId = finalBarberId,
                    serviceId = finalServiceId,
                    appointmentDate = _bookingDate.value,
                    startTime = convertTimeFormatTo24h(_bookingTime.value ?: "10:00 AM"),
                    notes = notes
                )
                
                android.util.Log.d("SalonNetwork", "Sending booking request to backend: $bookingRequest")
                val response = RetrofitClient.apiService.createAppointment(bookingRequest)
                if (response.isSuccessful) {
                    android.util.Log.i("SalonNetwork", "Successfully synced appointment with Spring Boot backend! Booking ID: ${response.body()?.id}")
                } else {
                    android.util.Log.e("SalonNetwork", "Backend rejected appointment creation: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SalonNetwork", "Failed to sync appointment with backend. Operating locally. Error: ${e.message}")
            }
        }
    }

    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.updateBookingStatus(booking.id, "Cancelled")
        }
    }

    fun rescheduleBooking(booking: BookingEntity, date: String, time: String) {
        viewModelScope.launch {
            val updated = booking.copy(date = date, timeSlot = time, status = "Pending")
            repository.insertBooking(updated)
        }
    }

    // --- Barber Dashboard States ---
    private val _selectedBarberId = MutableStateFlow<Int?>(null)
    val selectedBarberId: StateFlow<Int?> = _selectedBarberId.asStateFlow()

    fun selectBarberForDashboard(barberId: Int) {
        _selectedBarberId.value = barberId
    }

    val barberBookings: StateFlow<List<BookingEntity>> = combine(allStaff, _selectedBarberId, allBookings) { staffList, id, bookings ->
        val activeId = id ?: staffList.firstOrNull()?.id ?: 0
        bookings.filter { it.staffId == activeId || (it.staffId == 0 && activeId == staffList.firstOrNull()?.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Owner / Staff Dashboard Actions ---
    fun confirmBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "Confirmed")
        }
    }

    fun completeBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "Completed")
        }
    }

    fun updateBookingStatusDirectly(bookingId: Int, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
        }
    }

    // --- Manage Services, Staff, Bookings ---
    fun addNewService(salonId: Int, name: String, price: Double, duration: Int, category: String, description: String) {
        viewModelScope.launch {
            repository.insertService(
                ServiceEntity(
                    salonId = salonId,
                    name = name,
                    price = price,
                    durationMinutes = duration,
                    description = description,
                    category = category
                )
            )
        }
    }

    fun removeService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
        }
    }

    fun addNewStaff(salonId: Int, name: String, role: String, rating: Double, bio: String) {
        viewModelScope.launch {
            repository.insertStaff(
                StaffEntity(
                    salonId = salonId,
                    name = name,
                    role = role,
                    rating = rating,
                    bio = bio,
                    avatarResName = name.lowercase().split(" ").firstOrNull() ?: "marcus"
                )
            )
        }
    }

    fun removeStaff(staff: StaffEntity) {
        viewModelScope.launch {
            repository.deleteStaff(staff)
        }
    }

    fun addNewSalon(name: String, address: String, phone: String, hours: String, description: String) {
        viewModelScope.launch {
            var backendUuid: String? = null
            
            // --- Sync with backend ---
            try {
                android.util.Log.d("SalonNetwork", "Logging in as owner to create new salon...")
                val loginResponse = com.example.data.network.RetrofitClient.apiService.login(
                    com.example.data.network.NetworkLoginRequest("owner1@salon.com", "password123")
                )
                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val loginBody = loginResponse.body()!!
                    com.example.data.network.RetrofitClient.setAuthToken(loginBody.accessToken)
                    val ownerId = loginBody.userId
                    android.util.Log.d("SalonNetwork", "Owner login successful! Owner ID: $ownerId")
                    
                    val createRequest = com.example.data.network.NetworkCreateSalonRequest(
                        name = name,
                        description = description,
                        address = address,
                        city = "New York", // Default city
                        pincode = "10001", // Default pincode
                        phone = phone,
                        email = "contact@${name.lowercase().replace(" ", "")}.com"
                    )
                    
                    android.util.Log.d("SalonNetwork", "Sending create salon request to backend: $createRequest")
                    val createResponse = com.example.data.network.RetrofitClient.apiService.createSalon(createRequest, ownerId)
                    if (createResponse.isSuccessful && createResponse.body() != null) {
                        backendUuid = createResponse.body()!!.id
                        android.util.Log.i("SalonNetwork", "Successfully saved new salon on backend! UUID: $backendUuid")
                    } else {
                        android.util.Log.e("SalonNetwork", "Backend rejected salon creation: ${createResponse.errorBody()?.string()}")
                    }
                } else {
                    android.util.Log.e("SalonNetwork", "Failed to login as owner: ${loginResponse.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SalonNetwork", "Failed to sync new salon to backend: ${e.message}")
            }
            
            // --- Save in local room DB ---
            val newSalon = SalonEntity(
                name = name,
                address = address,
                phone = phone,
                openingHours = hours,
                description = description,
                rating = 4.5,
                imageResName = "img_salon_hero",
                backendUuid = backendUuid
            )
            repository.insertSalon(newSalon)
            android.util.Log.i("SalonNetwork", "Added new salon to local Room database: $name")
        }
    }
}

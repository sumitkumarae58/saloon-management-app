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
}

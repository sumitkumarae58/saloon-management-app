package com.salonbooking.controller.customer;

import com.salonbooking.dto.BookingRequest;
import com.salonbooking.entity.Appointment;
import com.salonbooking.entity.AvailabilitySlot;
import com.salonbooking.entity.Barber;
import com.salonbooking.entity.Salon;
import com.salonbooking.service.AppointmentService;
import com.salonbooking.service.BarberService;
import com.salonbooking.service.SalonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@Tag(name = "Customer Booking Interface", description = "Endpoints for search, salon discovery, stylist schedules, and securing bookings.")
public class CustomerController {

    private final SalonService salonService;
    private final BarberService barberService;
    private final AppointmentService appointmentService;

    public CustomerController(SalonService salonService, BarberService barberService,
                              AppointmentService appointmentService) {
        this.salonService = salonService;
        this.barberService = barberService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/salons")
    @Operation(summary = "Search registered Salons", description = "Discovers active salons. Accepts matching search queries.")
    public ResponseEntity<List<Salon>> searchSalons(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(salonService.searchSalons(query));
    }

    @GetMapping("/salons/{id}")
    @Operation(summary = "Get Salon details", description = "Fetch a salon profile including specifications, ratings, and locations.")
    public ResponseEntity<Salon> getSalon(@PathVariable UUID id) {
        return ResponseEntity.ok(salonService.getSalonById(id));
    }

    @GetMapping("/salons/{id}/barbers")
    @Operation(summary = "Get Salon barbers", description = "Lists active stylists registered with a specific salon.")
    public ResponseEntity<List<Barber>> getBarbers(@PathVariable UUID id) {
        return ResponseEntity.ok(barberService.getBarbersBySalon(id));
    }

    @GetMapping("/barbers/{barberId}/availability")
    @Operation(summary = "Get Stylist availability slots", description = "Lists unblocked booking intervals for a barber on a chosen day.")
    public ResponseEntity<List<AvailabilitySlot>> getAvailability(
            @PathVariable UUID barberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(barberService.getAvailableSlots(barberId, date));
    }

    @PostMapping("/appointments")
    @Operation(summary = "Schedule Appointment", description = "Books and schedules an appointment block. Resolves user context from login session.")
    public ResponseEntity<Appointment> bookAppointment(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        String customerEmail = authentication.getName(); // Securely resolved from active JWT context
        Appointment booking = appointmentService.bookAppointment(request, customerEmail);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @GetMapping("/appointments")
    @Operation(summary = "My Appointments History", description = "Lists scheduled/historic bookings for the logged-in user.")
    public ResponseEntity<List<Appointment>> getMyAppointments(Authentication authentication) {
        String customerEmail = authentication.getName();
        return ResponseEntity.ok(appointmentService.getAppointmentsByCustomer(customerEmail));
    }

    @PutMapping("/appointments/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Changes appointment status to CANCELLED.")
    public ResponseEntity<String> cancelAppointment(@PathVariable UUID id) {
        appointmentService.updateStatus(id, "CANCELLED");
        return ResponseEntity.ok("Appointment has been cancelled successfully.");
    }
}

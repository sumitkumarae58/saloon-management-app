package com.salonbooking.controller.customer;

import com.salonbooking.dto.*;
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
import java.util.stream.Collectors;

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
    public ResponseEntity<List<SalonDto>> searchSalons(@RequestParam(required = false) String query) {
        List<SalonDto> salons = salonService.searchSalons(query).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salons);
    }

    @GetMapping("/salons/{id}")
    @Operation(summary = "Get Salon details", description = "Fetch a salon profile including specifications, ratings, and locations.")
    public ResponseEntity<SalonDto> getSalon(@PathVariable UUID id) {
        Salon salon = salonService.getSalonById(id);
        return ResponseEntity.ok(DtoMapper.toDto(salon));
    }

    @GetMapping("/salons/{id}/barbers")
    @Operation(summary = "Get Salon barbers", description = "Lists active stylists registered with a specific salon.")
    public ResponseEntity<List<BarberDto>> getBarbers(@PathVariable UUID id) {
        List<BarberDto> barbers = barberService.getBarbersBySalon(id).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(barbers);
    }

    @GetMapping("/barbers/{barberId}/availability")
    @Operation(summary = "Get Stylist availability slots", description = "Lists unblocked booking intervals for a barber on a chosen day.")
    public ResponseEntity<List<AvailabilitySlotDto>> getAvailability(
            @PathVariable UUID barberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailabilitySlotDto> slots = barberService.getAvailableSlots(barberId, date).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/appointments")
    @Operation(summary = "Schedule Appointment", description = "Books and schedules an appointment block. Resolves user context from login session.")
    public ResponseEntity<AppointmentDto> bookAppointment(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        String customerEmail = authentication.getName(); // Securely resolved from active JWT context
        Appointment booking = appointmentService.bookAppointment(request, customerEmail);
        return new ResponseEntity<>(DtoMapper.toDto(booking), HttpStatus.CREATED);
    }

    @GetMapping("/appointments")
    @Operation(summary = "My Appointments History", description = "Lists scheduled/historic bookings for the logged-in user.")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        String customerEmail = authentication.getName();
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByCustomer(customerEmail).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointments/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Changes appointment status to CANCELLED.")
    public ResponseEntity<String> cancelAppointment(@PathVariable UUID id) {
        appointmentService.updateStatus(id, "CANCELLED");
        return ResponseEntity.ok("Appointment has been cancelled successfully.");
    }
}
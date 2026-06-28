package com.salonbooking.controller.owner;

import com.salonbooking.entity.Barber;
import com.salonbooking.entity.Salon;
import com.salonbooking.entity.Appointment;
import com.salonbooking.service.BarberService;
import com.salonbooking.service.SalonService;
import com.salonbooking.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owner")
@Tag(name = "Salon Owner Interface", description = "Endpoints restricted to SALON_OWNER role for salon and staff management.")
public class OwnerController {

    private final SalonService salonService;
    private final BarberService barberService;
    private final AppointmentService appointmentService;

    public OwnerController(SalonService salonService, BarberService barberService,
                           AppointmentService appointmentService) {
        this.salonService = salonService;
        this.barberService = barberService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/salon")
    @Operation(summary = "Register a new Salon profile", description = "Creates a brand new salon establishment.")
    public ResponseEntity<Salon> createSalon(@RequestBody Salon salon, @RequestParam UUID ownerId) {
        Salon created = salonService.createSalon(salon, ownerId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/barbers")
    @Operation(summary = "Affiliate a barber", description = "Associates an active user containing BARBER role with a salon profile.")
    public ResponseEntity<Barber> registerBarber(
            @RequestParam UUID salonId,
            @RequestParam UUID userId,
            @RequestParam String specialization,
            @RequestParam Integer experienceYears) {
        Barber barber = barberService.registerBarber(salonId, userId, specialization, experienceYears);
        return new ResponseEntity<>(barber, HttpStatus.CREATED);
    }

    @GetMapping("/appointments")
    @Operation(summary = "Fetch Salon bookings", description = "Lists complete transactional bookings associated with a specific salon.")
    public ResponseEntity<List<Appointment>> getSalonAppointments(@RequestParam UUID salonId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsBySalon(salonId));
    }
}

package com.salonbooking.controller.barber;

import com.salonbooking.entity.Appointment;
import com.salonbooking.entity.AvailabilitySlot;
import com.salonbooking.service.AppointmentService;
import com.salonbooking.service.BarberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/barber")
@Tag(name = "Barber Interface", description = "Endpoints restricted to ROLE_BARBER for stylist schedule and booking operations.")
public class BarberController {

    private final AppointmentService appointmentService;
    private final BarberService barberService;

    public BarberController(AppointmentService appointmentService, BarberService barberService) {
        this.appointmentService = appointmentService;
        this.barberService = barberService;
    }

    @GetMapping("/appointments")
    @Operation(summary = "View assigned appointments", description = "Fetch schedule list for the logged-in barber user.")
    public ResponseEntity<List<Appointment>> getMyAppointments(@RequestParam UUID userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByBarber(userId));
    }

    @PutMapping("/appointments/{id}/accept")
    @Operation(summary = "Accept an appointment", description = "Transitions booking status from PENDING to CONFIRMED.")
    public ResponseEntity<String> acceptAppointment(@PathVariable UUID id) {
        appointmentService.updateStatus(id, "CONFIRMED");
        return ResponseEntity.ok("Appointment confirmed successfully.");
    }

    @PutMapping("/appointments/{id}/complete")
    @Operation(summary = "Complete an appointment", description = "Marks booking state as COMPLETED.")
    public ResponseEntity<String> completeAppointment(@PathVariable UUID id) {
        appointmentService.updateStatus(id, "COMPLETED");
        return ResponseEntity.ok("Appointment marked as completed.");
    }

    @PostMapping("/availability")
    @Operation(summary = "Create Availability Slot", description = "Configures custom active hours for booking.")
    public ResponseEntity<AvailabilitySlot> createSlot(
            @RequestParam UUID barberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime start,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime end) {
        AvailabilitySlot slot = barberService.createAvailabilitySlot(barberId, date, start, end);
        return new ResponseEntity<>(slot, HttpStatus.CREATED);
    }

    @PutMapping("/availability/{slotId}/block")
    @Operation(summary = "Block or Unblock slot", description = "Toggles leave periods for slot scheduling.")
    public ResponseEntity<String> blockSlot(@PathVariable UUID slotId, @RequestParam boolean block) {
        barberService.blockAvailabilitySlot(slotId, block);
        String action = block ? "blocked" : "unblocked";
        return ResponseEntity.ok("Availability slot " + action + " successfully.");
    }
}

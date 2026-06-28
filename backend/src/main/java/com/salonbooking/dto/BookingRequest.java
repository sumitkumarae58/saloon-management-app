package com.salonbooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class BookingRequest {
    @NotNull(message = "Salon ID is required")
    private UUID salonId;

    private UUID barberId; // Optional: can be null if user requests "any available"

    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    private String notes;
}

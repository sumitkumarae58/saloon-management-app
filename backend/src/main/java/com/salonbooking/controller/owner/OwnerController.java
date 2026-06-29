package com.salonbooking.controller.owner;

import com.salonbooking.dto.*;
import com.salonbooking.entity.Appointment;
import com.salonbooking.entity.Barber;
import com.salonbooking.entity.Salon;
import com.salonbooking.entity.User;
import com.salonbooking.entity.Role;
import com.salonbooking.service.BarberService;
import com.salonbooking.service.SalonService;
import com.salonbooking.service.AppointmentService;
import com.salonbooking.repository.ServiceRepository;
import com.salonbooking.repository.CategoryRepository;
import com.salonbooking.repository.UserRepository;
import com.salonbooking.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner")
@Tag(name = "Salon Owner Interface", description = "Endpoints restricted to SALON_OWNER role for salon and staff management.")
public class OwnerController {

    private final SalonService salonService;
    private final BarberService barberService;
    private final AppointmentService appointmentService;
    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public OwnerController(SalonService salonService, BarberService barberService,
                           AppointmentService appointmentService, ServiceRepository serviceRepository,
                           CategoryRepository categoryRepository, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.salonService = salonService;
        this.barberService = barberService;
        this.appointmentService = appointmentService;
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/salon")
    @Operation(summary = "Register a new Salon profile", description = "Creates a brand new salon establishment.")
    public ResponseEntity<SalonDto> createSalon(@RequestBody Salon salon, @RequestParam UUID ownerId) {
        Salon created = salonService.createSalon(salon, ownerId);
        return new ResponseEntity<>(DtoMapper::toDto(created), HttpStatus.CREATED);
    }

    @PostMapping("/barbers")
    @Operation(summary = "Affiliate a barber", description = "Associates an active user containing BARBER role with a salon profile.")
    public ResponseEntity<BarberDto> registerBarber(
            @RequestParam UUID salonId,
            @RequestParam UUID userId,
            @RequestParam String specialization,
            @RequestParam Integer experienceYears) {
        Barber barber = barberService.registerBarber(salonId, userId, specialization, experienceYears);
        return new ResponseEntity<>(DtoMapper::toDto(barber), HttpStatus.CREATED);
    }

    @PostMapping("/barbers/create")
    @Operation(summary = "Create and affiliate a brand new barber directly", description = "Creates a user account and immediately associates it with a salon.")
    public ResponseEntity<BarberDto> createAndRegisterBarber(
            @RequestParam UUID salonId,
            @RequestParam String fullName,
            @RequestParam String specialization,
            @RequestParam Integer experienceYears) {
        String email = fullName.toLowerCase().replaceAll("\\s+", "") + "@salon.com";
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Role barberRole = roleRepository.findByName("ROLE_BARBER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_BARBER")));
            
            String[] parts = fullName.split(" ");
            String first = parts[0];
            String last = parts.length > 1 ? parts[parts.length - 1] : "Barber";
            
            User u = User.builder()
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .firstName(first)
                .lastName(last)
                .role(barberRole)
                .isActive(true)
                .build();
            return userRepository.save(u);
        });

        Barber barber = barberService.registerBarber(salonId, user.getId(), specialization, experienceYears);
        return new ResponseEntity<>(DtoMapper::toDto(barber), HttpStatus.CREATED);
    }

    @PostMapping("/services")
    @Operation(summary = "Create a new Service for a Salon", description = "Creates a custom service associated with a salon.")
    public ResponseEntity<ServiceDto> createService(
            @RequestParam UUID salonId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Integer durationMinutes,
            @RequestParam java.math.BigDecimal price,
            @RequestParam String categoryName) {
        Salon salon = salonService.getSalonById(salonId);
        com.salonbooking.entity.Category category = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElseGet(() -> {
                    com.salonbooking.entity.Category cat = new com.salonbooking.entity.Category();
                    cat.setName(categoryName);
                    return categoryRepository.save(cat);
                });

        com.salonbooking.entity.Service service = com.salonbooking.entity.Service.builder()
                .salon(salon)
                .name(name)
                .description(description)
                .durationMinutes(durationMinutes)
                .price(price)
                .category(category)
                .isActive(true)
                .build();

        com.salonbooking.entity.Service saved = serviceRepository.save(service);
        return new ResponseEntity<>(DtoMapper::toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/appointments")
    @Operation(summary = "Fetch Salon bookings", description = "Lists complete transactional bookings associated with a specific salon.")
    public ResponseEntity<List<AppointmentDto>> getSalonAppointments(@RequestParam UUID salonId) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsBySalon(salonId).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointments);
    }
}

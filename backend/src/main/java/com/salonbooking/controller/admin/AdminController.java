package com.salonbooking.controller.admin;

import com.salonbooking.dto.DtoMapper;
import com.salonbooking.dto.SalonDto;
import com.salonbooking.service.SalonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Super Admin Interface", description = "Endpoints restricted to SUPER_ADMIN role for platform health management.")
public class AdminController {

    private final SalonService salonService;

    public AdminController(SalonService salonService) {
        this.salonService = salonService;
    }

    @GetMapping("/salons")
    @Operation(summary = "List all salons", description = "Fetch complete list of registered salons across the platform.")
    public ResponseEntity<List<SalonDto>> getAllSalons() {
        List<SalonDto> salons = salonService.getAllActiveSalons().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salons);
    }

    @PostMapping("/salons/{id}/suspend")
    @Operation(summary = "Suspend or Unsuspend a Salon", description = "Blocks or activates a salon establish from appearing on search indexing.")
    public ResponseEntity<String> suspendSalon(
            @PathVariable UUID id,
            @RequestParam boolean suspend) {
        salonService.suspendSalon(id, suspend);
        String action = suspend ? "suspended" : "activated";
        return ResponseEntity.ok("Salon status updated successfully to: " + action);
    }
}

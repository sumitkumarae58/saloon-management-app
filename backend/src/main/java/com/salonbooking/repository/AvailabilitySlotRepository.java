package com.salonbooking.repository;

import com.salonbooking.entity.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {
    List<AvailabilitySlot> findByBarberIdAndDate(UUID barberId, LocalDate date);
    
    List<AvailabilitySlot> findByBarberIdAndDateAndIsAvailableTrueAndIsBlockedFalse(UUID barberId, LocalDate date);
}

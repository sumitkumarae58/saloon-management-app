package com.salonbooking.repository;

import com.salonbooking.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, UUID> {
    List<WorkingHours> findBySalonId(UUID salonId);
    
    Optional<WorkingHours> findBySalonIdAndDayOfWeek(UUID salonId, Integer dayOfWeek);
}

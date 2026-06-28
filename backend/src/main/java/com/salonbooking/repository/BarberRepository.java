package com.salonbooking.repository;

import com.salonbooking.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BarberRepository extends JpaRepository<Barber, UUID> {
    List<Barber> findBySalonIdAndIsActiveTrue(UUID salonId);
    
    List<Barber> findBySalonId(UUID salonId);

    Optional<Barber> findByUserId(UUID userId);
}

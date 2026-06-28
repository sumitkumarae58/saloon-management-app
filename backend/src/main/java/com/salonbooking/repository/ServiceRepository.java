package com.salonbooking.repository;

import com.salonbooking.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    List<Service> findBySalonIdAndIsActiveTrue(UUID salonId);
    
    List<Service> findBySalonId(UUID salonId);
}

package com.salonbooking.repository;

import com.salonbooking.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SalonRepository extends JpaRepository<Salon, UUID> {
    @Query("SELECT s FROM Salon s WHERE s.deletedAt IS NULL AND s.isActive = true AND s.isSuspended = false")
    List<Salon> findAllActiveAndNonSuspended();

    @Query("SELECT s FROM Salon s WHERE s.deletedAt IS NULL AND s.owner.id = :ownerId")
    List<Salon> findByOwnerId(UUID ownerId);

    @Query("SELECT s FROM Salon s WHERE s.deletedAt IS NULL AND s.isActive = true AND s.isSuspended = false AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Salon> searchSalons(String query);
}

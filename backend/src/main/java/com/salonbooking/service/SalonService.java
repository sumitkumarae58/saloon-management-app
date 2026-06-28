package com.salonbooking.service;

import com.salonbooking.entity.Salon;
import com.salonbooking.entity.User;
import com.salonbooking.exception.ResourceNotFoundException;
import com.salonbooking.repository.SalonRepository;
import com.salonbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SalonService {

    private final SalonRepository salonRepository;
    private final UserRepository userRepository;

    public SalonService(SalonRepository salonRepository, UserRepository userRepository) {
        this.salonRepository = salonRepository;
        this.userRepository = userRepository;
    }

    public List<Salon> getAllActiveSalons() {
        return salonRepository.findAllActiveAndNonSuspended();
    }

    public Salon getSalonById(UUID id) {
        return salonRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with ID: " + id));
    }

    public List<Salon> searchSalons(String query) {
        if (query == null || query.isBlank()) {
            return getAllActiveSalons();
        }
        return salonRepository.searchSalons(query);
    }

    @Transactional
    public Salon createSalon(Salon salon, UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + ownerId));
        
        salon.setOwner(owner);
        return salonRepository.save(salon);
    }

    @Transactional
    public void suspendSalon(UUID id, boolean suspend) {
        Salon salon = getSalonById(id);
        salon.setIsSuspended(suspend);
        salonRepository.save(salon);
    }
}

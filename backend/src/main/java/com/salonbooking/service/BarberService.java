package com.salonbooking.service;

import com.salonbooking.entity.AvailabilitySlot;
import com.salonbooking.entity.Barber;
import com.salonbooking.entity.Salon;
import com.salonbooking.entity.User;
import com.salonbooking.exception.BadRequestException;
import com.salonbooking.exception.ResourceNotFoundException;
import com.salonbooking.repository.AvailabilitySlotRepository;
import com.salonbooking.repository.BarberRepository;
import com.salonbooking.repository.SalonRepository;
import com.salonbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class BarberService {

    private final BarberRepository barberRepository;
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;
    private final AvailabilitySlotRepository slotRepository;

    public BarberService(BarberRepository barberRepository, SalonRepository salonRepository,
                         UserRepository userRepository, AvailabilitySlotRepository slotRepository) {
        this.barberRepository = barberRepository;
        this.salonRepository = salonRepository;
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
    }

    public List<Barber> getBarbersBySalon(UUID salonId) {
        return barberRepository.findBySalonIdAndIsActiveTrue(salonId);
    }

    @Transactional
    public Barber registerBarber(UUID salonId, UUID userId, String specialization, Integer experienceYears) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.getRole().getName().equals("ROLE_BARBER")) {
            throw new BadRequestException("User does not have the barber role assigned.");
        }

        Barber barber = Barber.builder()
                .salon(salon)
                .user(user)
                .specialization(specialization)
                .experienceYears(experienceYears)
                .build();

        return barberRepository.save(barber);
    }

    public List<AvailabilitySlot> getAvailableSlots(UUID barberId, LocalDate date) {
        return slotRepository.findByBarberIdAndDateAndIsAvailableTrueAndIsBlockedFalse(barberId, date);
    }

    @Transactional
    public AvailabilitySlot createAvailabilitySlot(UUID barberId, LocalDate date, LocalTime start, LocalTime end) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found."));

        AvailabilitySlot slot = AvailabilitySlot.builder()
                .barber(barber)
                .date(date)
                .startTime(start)
                .endTime(end)
                .isAvailable(true)
                .isBlocked(false)
                .build();

        return slotRepository.save(slot);
    }

    @Transactional
    public void blockAvailabilitySlot(UUID slotId, boolean blocked) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found."));
        slot.setIsBlocked(blocked);
        if (blocked) {
            slot.setIsAvailable(false);
        }
        slotRepository.save(slot);
    }
}

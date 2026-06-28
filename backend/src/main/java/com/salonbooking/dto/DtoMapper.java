package com.salonbooking.dto;

import com.salonbooking.entity.*;

public class DtoMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }

    public static SalonDto toDto(Salon salon) {
        if (salon == null) return null;
        return SalonDto.builder()
                .id(salon.getId())
                .name(salon.getName())
                .description(salon.getDescription())
                .address(salon.getAddress())
                .city(salon.getCity())
                .pincode(salon.getPincode())
                .phone(salon.getPhone())
                .email(salon.getEmail())
                .profileImageUrl(salon.getProfileImageUrl())
                .rating(salon.getRating())
                .totalReviews(salon.getTotalReviews())
                .isActive(salon.getIsActive())
                .isSuspended(salon.getIsSuspended())
                .build();
    }

    public static BarberDto toDto(Barber barber) {
        if (barber == null) return null;
        return BarberDto.builder()
                .id(barber.getId())
                .specialization(barber.getSpecialization())
                .experienceYears(barber.getExperienceYears())
                .rating(barber.getRating())
                .totalReviews(barber.getTotalReviews())
                .user(toDto(barber.getUser()))
                .build();
    }

    public static AvailabilitySlotDto toDto(AvailabilitySlot slot) {
        if (slot == null) return null;
        return AvailabilitySlotDto.builder()
                .id(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .isAvailable(slot.getIsAvailable())
                .isBlocked(slot.getIsBlocked())
                .build();
    }

    public static AppointmentDto toDto(Appointment app) {
        if (app == null) return null;
        return AppointmentDto.builder()
                .id(app.getId())
                .appointmentDate(app.getAppointmentDate())
                .startTime(app.getStartTime())
                .endTime(app.getEndTime())
                .status(app.getStatus())
                .paymentStatus(app.getPaymentStatus())
                .paymentMethod(app.getPaymentMethod())
                .notes(app.getNotes())
                .salon(toDto(app.getSalon()))
                .barber(toDto(app.getBarber()))
                .build();
    }
}

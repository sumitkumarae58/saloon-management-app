package com.salonbooking.repository;

import com.salonbooking.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.email = :email ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<Appointment> findByCustomerEmail(String email);

    List<Appointment> findByCustomerId(UUID customerId);

    List<Appointment> findBySalonId(UUID salonId);

    List<Appointment> findByBarberId(UUID barberId);

    @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId " +
           "AND a.appointmentDate = :date " +
           "AND a.status <> 'CANCELLED'")
    List<Appointment> findActiveAppointmentsByBarberAndDate(UUID barberId, LocalDate date);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.barber.id = :barberId " +
           "AND a.appointmentDate = :date " +
           "AND a.status <> 'CANCELLED' " +
           "AND ((a.startTime <= :startTime AND a.endTime > :startTime) " +
           "OR (a.startTime < :endTime AND a.endTime >= :endTime) " +
           "OR (:startTime <= a.startTime AND :endTime >= a.endTime))")
    boolean existsConflictingBooking(UUID barberId, LocalDate date, LocalTime startTime, LocalTime endTime);
}

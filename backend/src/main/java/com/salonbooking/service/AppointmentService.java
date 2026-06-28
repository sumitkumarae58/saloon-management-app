package com.salonbooking.service;

import com.salonbooking.dto.BookingRequest;
import com.salonbooking.entity.*;
import com.salonbooking.exception.BadRequestException;
import com.salonbooking.exception.ResourceNotFoundException;
import com.salonbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final ServiceRepository serviceRepository;
    private final WorkingHoursRepository workingHoursRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              SalonRepository salonRepository,
                              UserRepository userRepository,
                              BarberRepository barberRepository,
                              ServiceRepository serviceRepository,
                              WorkingHoursRepository workingHoursRepository) {
        this.appointmentRepository = appointmentRepository;
        this.salonRepository = salonRepository;
        this.userRepository = userRepository;
        this.barberRepository = barberRepository;
        this.serviceRepository = serviceRepository;
        this.workingHoursRepository = workingHoursRepository;
    }

    @Transactional
    public Appointment bookAppointment(BookingRequest request, String customerEmail) {
        User customer = userRepository.findByEmailActive(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));

        Salon salon = salonRepository.findById(request.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found."));

        com.salonbooking.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found."));

        // 1. Calculate End Time from service duration
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusMinutes(service.getDurationMinutes());

        // 2. Validate Working Hours for the salon
        int dayOfWeek = request.getAppointmentDate().getDayOfWeek().getValue(); // 1 (Mon) - 7 (Sun)
        WorkingHours workingHours = workingHoursRepository.findBySalonIdAndDayOfWeek(salon.getId(), dayOfWeek)
                .orElseThrow(() -> new BadRequestException("Salon is closed or working hours are not set on this day."));

        if (workingHours.getIsClosed()) {
            throw new BadRequestException("Salon is closed on this day.");
        }
        if (startTime.isBefore(workingHours.getOpeningTime()) || endTime.isAfter(workingHours.getClosingTime())) {
            throw new BadRequestException("Selected time slot is outside the salon's working hours (" +
                    workingHours.getOpeningTime() + " - " + workingHours.getClosingTime() + ").");
        }

        // 3. Resolve Barber
        Barber assignedBarber = null;
        if (request.getBarberId() != null) {
            assignedBarber = barberRepository.findById(request.getBarberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Selected barber not found."));
            if (!assignedBarber.getSalon().getId().equals(salon.getId())) {
                throw new BadRequestException("Selected barber does not belong to this salon.");
            }

            // Verify if there is any scheduling conflict for this specific barber
            boolean conflict = appointmentRepository.existsConflictingBooking(
                    assignedBarber.getId(), request.getAppointmentDate(), startTime, endTime
            );
            if (conflict) {
                throw new BadRequestException("Selected slot is already booked for this barber.");
            }
        } else {
            // "Any Barber" auto-allocation strategy
            List<Barber> barbers = barberRepository.findBySalonIdAndIsActiveTrue(salon.getId());
            if (barbers.isEmpty()) {
                throw new BadRequestException("No active barbers available in this salon.");
            }

            for (Barber b : barbers) {
                boolean conflict = appointmentRepository.existsConflictingBooking(
                        b.getId(), request.getAppointmentDate(), startTime, endTime
                );
                if (!conflict) {
                    assignedBarber = b;
                    break;
                }
            }

            if (assignedBarber == null) {
                throw new BadRequestException("No available barbers could be found for the selected time slot.");
            }
        }

        // 4. Build and Save Appointment
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .salon(salon)
                .barber(assignedBarber)
                .service(service)
                .appointmentDate(request.getAppointmentDate())
                .startTime(startTime)
                .endTime(endTime)
                .notes(request.getNotes())
                .status("CONFIRMED") // Autoconfirmed for MVP
                .paymentStatus("PENDING")
                .paymentMethod("CASH")
                .build();

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByCustomer(String email) {
        return appointmentRepository.findByCustomerEmail(email);
    }

    public List<Appointment> getAppointmentsBySalon(UUID salonId) {
        return appointmentRepository.findBySalonId(salonId);
    }

    public List<Appointment> getAppointmentsByBarber(UUID barberUserId) {
        Barber barber = barberRepository.findByUserId(barberUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Barber profile not found."));
        return appointmentRepository.findByBarberId(barber.getId());
    }

    @Transactional
    public void updateStatus(UUID id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found."));
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }
}

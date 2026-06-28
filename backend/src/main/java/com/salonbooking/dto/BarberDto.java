package com.salonbooking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberDto {
    private UUID id;
    private String specialization;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer totalReviews;
    private UserDto user;
}

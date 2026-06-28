package com.salonbooking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonDto {
    private UUID id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String pincode;
    private String phone;
    private String email;
    private String profileImageUrl;
    private BigDecimal rating;
    private Integer totalReviews;
    private Boolean isActive;
    private Boolean isSuspended;
}

package com.salonbooking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDto {
    private UUID id;
    private UUID salonId;
    private String name;
    private String description;
    private Integer durationMinutes;
    private BigDecimal price;
    private String category;
}

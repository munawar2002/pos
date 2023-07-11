package com.mjtech.pos.dto;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericFromDto {
    private Integer id;
    private String name;
    private String description;

    @Transient
    private String action;
}

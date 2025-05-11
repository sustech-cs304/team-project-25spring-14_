package com.example.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    @NotNull
    private Integer resourceId;
    @NotBlank
    private String reason;
    @NotNull
    private String resourceType;
    @NotNull
    private Integer reporteeId;
}

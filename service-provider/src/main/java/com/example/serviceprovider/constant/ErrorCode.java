package com.example.serviceprovider.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SEV_404_001("SEV-404-001"), // Service not found
    SEV_400_001("SEV-400-001"), // Invalid Argument
    SEV_400_002("SEV-400-002"), // Duplicated URL
    SEV_500("SEV-500");

    private String value;
}

package com.example.serviceprovider.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    SEV_200("SEV-200"),
    SEV_201("SEV-201");

    private String value;
}

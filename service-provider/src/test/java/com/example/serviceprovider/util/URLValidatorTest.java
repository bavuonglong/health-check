package com.example.serviceprovider.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLValidatorTest {

    @Test
    public void givenValidURL_whenValidateURL_thenReturnTrue() {
        String validURL = "https://www.google.com";
        assertTrue(URLValidator.isValidUrl(validURL));
    }

    @Test
    public void givenInValidURL_whenValidateURL_thenReturnFalse() {
        String invalidURL = "xzy";
        assertFalse(URLValidator.isValidUrl(invalidURL));
    }
}
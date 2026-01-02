package com.ams.dtos.documentDto;


import java.time.LocalDate;

public record DocumentCareGridDto(String fileName, String bussName, LocalDate date) {
}

package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.dto.response.ErrorResponseDto;
import com.askfar.fakepaymentprovider.exception.BusinessException;
import com.askfar.fakepaymentprovider.exception.NotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handeValidationException(ValidationException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponseDto.builder()
                               .message(ex.getMessage())
                               .timestamp(LocalDateTime.now())
                               .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFoundException(NotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponseDto.builder()
                               .message(ex.getMessage())
                               .timestamp(LocalDateTime.now())
                               .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponseDto.builder()
                               .message(ex.getMessage())
                               .timestamp(LocalDateTime.now())
                               .build();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleBusinessException(BusinessException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponseDto.builder()
                               .message(ex.getMessage())
                               .timestamp(LocalDateTime.now())
                               .build();
    }
}
package com.askfar.fakepaymentprovider.exception;

import com.askfar.fakepaymentprovider.dto.response.ErrorResponseDto;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = {ValidationException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handeValidationException(Exception ex) {
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

    @ExceptionHandler(BusinessUnknownException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleBusinessException(BusinessUnknownException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponseDto.builder()
                               .message(ex.getMessage())
                               .timestamp(LocalDateTime.now())
                               .build();
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleSecurityException(SecurityException ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponseDto.builder()
                               .message(ex.getMessage())
                               .timestamp(LocalDateTime.now())
                               .build();
    }
}

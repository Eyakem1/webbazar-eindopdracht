package com.webbazar.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void conflict_mapsTo409() {
        GlobalExceptionHandler h = new GlobalExceptionHandler();
        ProblemDetail pd = h.conflict(new ConflictException("ORDER_NOT_PAYABLE","msg"));
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(pd.getTitle()).isEqualTo("ORDER_NOT_PAYABLE");
        assertThat(pd.getDetail()).isEqualTo("msg");
    }
}

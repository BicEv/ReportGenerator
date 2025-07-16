package ru.bicev.ReportGenerator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.bicev.ReportGenerator.dto.ReportDto;

@ExtendWith(SpringExtension.class)
public class ReportDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenNameIsNull() {
        ReportDto dto = new ReportDto(null, LocalDate.now(), new BigDecimal("100"));

        Set<ConstraintViolation<ReportDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        ReportDto dto = new ReportDto("", LocalDate.now(), new BigDecimal("100"));

        Set<ConstraintViolation<ReportDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenDateIsNull() {
        ReportDto dto = new ReportDto("Sam", null, new BigDecimal("100"));

        Set<ConstraintViolation<ReportDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenAmountIsNull() {
        ReportDto dto = new ReportDto("Sam", LocalDate.now(), null);

        Set<ConstraintViolation<ReportDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenAmountIsNegative() {
        ReportDto dto = new ReportDto("Sam", LocalDate.now(), new BigDecimal("-500"));

        Set<ConstraintViolation<ReportDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSucceedValidationWhenOk() {
        ReportDto dto = new ReportDto("Samuel L. Jackson", LocalDate.now(), new BigDecimal("300"));

        Set<ConstraintViolation<ReportDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());

    }

}

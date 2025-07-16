package ru.bicev.ReportGenerator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ru.bicev.ReportGenerator.dto.ReportDto;
import ru.bicev.ReportGenerator.generator.ExcelReportGenerator;
import ru.bicev.ReportGenerator.util.ReportData;

@ExtendWith(SpringExtension.class)
public class ExcelReportGeneratorTest {

    private ExcelReportGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ExcelReportGenerator();
    }

    @Test
    void shouldGenerateExcelReportFile() throws IOException {
        List<ReportData> data = List.of(
                new ReportDto("Alice", LocalDate.of(2010, 6, 3), new BigDecimal("100")),
                new ReportDto("Bob", LocalDate.of(2012, 7, 10), new BigDecimal("200")),
                new ReportDto("Clark", LocalDate.of(2014, 8, 12), new BigDecimal("300")));

        File file = generator.generateReport(data, "test-report");

        assertTrue(file.exists());
        assertTrue(file.getName().endsWith(".xlsx"));

        file.delete();
    }

    @Test
    void shouldThrowExceptionWhenDataIsEmptyOrNull() throws IOException {

        List<ReportData> dataEmpty = new ArrayList<>();
        List<ReportData> dataNull = null;

        assertThrows(IllegalArgumentException.class, () -> generator.generateReport(dataEmpty, "test-report"));
        assertThrows(IllegalArgumentException.class, () -> generator.generateReport(dataNull, "test-report"));
    }

}

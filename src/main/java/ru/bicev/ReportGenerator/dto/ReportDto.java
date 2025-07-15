package ru.bicev.ReportGenerator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bicev.ReportGenerator.util.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto implements ReportData {

    @NotEmpty
    private String name;

    @NotNull
    private LocalDate date;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @Override
    public List<String> getHeaders() {
        return List.of("Name", "Date", "Amount");
    }

    @Override
    public List<String> getRow() {
        return List.of(name, date.toString(), amount.toString());
    }

}

package ru.bicev.ReportGenerator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bicev.ReportGenerator.util.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto implements ReportData {

    private String name;
    private LocalDate date;
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

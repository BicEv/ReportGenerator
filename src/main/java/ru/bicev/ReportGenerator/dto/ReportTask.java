package ru.bicev.ReportGenerator.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bicev.ReportGenerator.util.ReportData;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportTask {

    @NotBlank(message = "reportId can not be empty")
    private String reportId;

    @NotEmpty(message = "List of reports can not be empty")
    @Valid
    private List<? extends ReportData> data;

}

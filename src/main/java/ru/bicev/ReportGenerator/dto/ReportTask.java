package ru.bicev.ReportGenerator.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bicev.ReportGenerator.util.ReportData;

@Schema(description = "Data for the report generation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportTask {

    @Schema(description = "Report ID", example = "Sales-Report")
    @NotBlank(message = "reportId can not be empty")
    private String reportId;

    @Schema(description = "List of ReportData to be processed")
    @NotEmpty(message = "List of reports can not be empty")
    @Valid
    private List<? extends ReportData> data;

}

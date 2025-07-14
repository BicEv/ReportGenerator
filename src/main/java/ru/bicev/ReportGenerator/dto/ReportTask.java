package ru.bicev.ReportGenerator.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.bicev.ReportGenerator.util.ReportData;

@Data
@AllArgsConstructor

public class ReportTask {

    private String reportId;
    private List<? extends ReportData> data;

}

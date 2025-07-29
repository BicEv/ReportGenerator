package ru.bicev.ReportGenerator.util;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public abstract class AbstractReportData implements ReportData {

    @NotEmpty
    protected List<String> headers;

    @NotEmpty
    protected List<String> row;

    @Override
    public List<String> getHeaders() {
        return headers;
    }

    @Override
    public List<String> getRow() {
        return row;
    }

}

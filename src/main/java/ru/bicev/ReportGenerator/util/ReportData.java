package ru.bicev.ReportGenerator.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
public interface ReportData {
    List<String> getHeaders();

    List<String> getRow();

}

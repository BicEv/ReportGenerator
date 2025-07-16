package ru.bicev.ReportGenerator.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "report")
@Data
public class ReportSubtypesProperties {
    private List<Subtype> subtypes;

    @Data
    public static class Subtype {
        private String className;
        private String name;

    }
}

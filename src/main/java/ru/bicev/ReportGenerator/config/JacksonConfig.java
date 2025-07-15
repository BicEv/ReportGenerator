package ru.bicev.ReportGenerator.config;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import jakarta.annotation.PostConstruct;
import ru.bicev.ReportGenerator.dto.ReportDto;

@Configuration
public class JacksonConfig {

    private final ObjectMapper objectMapper;

    public JacksonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void setUpSubtypes() {
        objectMapper.registerSubtypes(new NamedType(ReportDto.class, "report"));
    }

}

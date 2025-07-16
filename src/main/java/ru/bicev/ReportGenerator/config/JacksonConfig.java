package ru.bicev.ReportGenerator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import jakarta.annotation.PostConstruct;

@Configuration
public class JacksonConfig {

    private final ObjectMapper objectMapper;
    private final ReportSubtypesProperties properties;
    private static final Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    public JacksonConfig(ObjectMapper objectMapper, ReportSubtypesProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @PostConstruct
    public void setUpSubtypes() {

        for (ReportSubtypesProperties.Subtype subtype : properties.getSubtypes()) {
            try {
                Class<?> clazz = Class.forName(subtype.getClassName());
                objectMapper.registerSubtypes(new NamedType(clazz, subtype.getName()));
                logger.info("Registered subtype: {}", subtype.getClassName());
            } catch (ClassNotFoundException e) {
                logger.error("Failed to load subtype: {}", subtype.getClassName());
                throw new IllegalStateException("Failed to load subtype: " + subtype.getClassName(), e);
            }
        }

    }

}

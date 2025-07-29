package ru.bicev.ReportGenerator.util;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task status", example = "DONE")
public enum TaskStatus {
    PENDING, IN_PROGRESS, DONE, NOT_FOUND, FAILED

}

package ru.bicev.ReportGenerator.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.bicev.ReportGenerator.dto.ReportTask;
import ru.bicev.ReportGenerator.queue.ReportTaskQueue;
import ru.bicev.ReportGenerator.util.TaskStatus;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report API", description = "Report generation and downloading")
public class ReportController {

    private final ReportTaskQueue queue;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportTaskQueue queue) {
        this.queue = queue;
    }

    @Operation(summary = "Create a report", description = "Creates a report generation task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Task accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping
    public ResponseEntity<Void> submitReportTask(@RequestBody @Valid ReportTask reportTask) {

        queue.submitTask(reportTask);

        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Download a report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report was found and attached to a response"),
            @ApiResponse(responseCode = "404", description = "Report with such ID was not found")
    })
    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> getFileById(@PathVariable String reportId) {
        File file = queue.getReportFile(reportId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report is missing or not ready"));
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

    @Operation(summary = "Get task status by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status for the ID"),
            @ApiResponse(responseCode = "404", description = "Task with such ID was not found")
    })
    @GetMapping("/{reportId}")
    public ResponseEntity<TaskStatus> getStatusById(@PathVariable String reportId) {
        TaskStatus status = queue.getStatus(reportId);
        if (status == TaskStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report ID not found");
        }
        return ResponseEntity.ok().body(status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Invalid data: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected error occured: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + ex.getMessage());
    }

}

package ru.bicev.ReportGenerator.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

import jakarta.validation.Valid;
import ru.bicev.ReportGenerator.dto.ReportTask;
import ru.bicev.ReportGenerator.queue.ReportTaskQueue;
import ru.bicev.ReportGenerator.util.TaskStatus;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportTaskQueue queue;

    public ReportController(ReportTaskQueue queue) {
        this.queue = queue;
    }

    @PutMapping
    public ResponseEntity<Void> submitReportTask(@RequestBody @Valid ReportTask reportTask) {

        queue.submitTask(reportTask);

        return ResponseEntity.accepted().build();
    }

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
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);

    }

}

package ru.bicev.ReportGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.bicev.ReportGenerator.dto.ReportDto;
import ru.bicev.ReportGenerator.dto.ReportTask;
import ru.bicev.ReportGenerator.generator.ExcelReportGenerator;
import ru.bicev.ReportGenerator.queue.ReportTaskQueue;
import ru.bicev.ReportGenerator.util.TaskStatus;

public class ReportTaskQueueTest {

    private ReportTaskQueue queue;

    @BeforeEach
    void setUp() {
        ExcelReportGenerator fakeGenerator = new ExcelReportGenerator() {
            @Override
            public File generateReport(List data, String reportId) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return new File("fake.xlsx");
            }
        };

        queue = new ReportTaskQueue(fakeGenerator);
        queue.initWorkers();
    }

    @AfterEach
    void shutDown() {
        queue.shutdown();
    }

    @Test
    void shouldProcessTaskAndUpdateStatusToDone() throws InterruptedException {
        String reportId = "test-id";

        ReportTask task = new ReportTask(reportId, List.of(
                new ReportDto("Alice", LocalDate.of(2007, 6, 14), new BigDecimal("500"))));

        queue.submitTask(task);

        TaskStatus status = null;

        int retries = 20;
        while (retries-- > 0) {
            status = queue.getStatus(reportId);
            if (status == TaskStatus.DONE || status == TaskStatus.FAILED) {
                break;
            }
            Thread.sleep(100);
        }

        assertEquals(TaskStatus.DONE, status);

    }

    @Test
    void shouldReturnFileWhenTaskIsDone() throws InterruptedException {
        String reportId = "test-id";

        ReportTask task = new ReportTask(reportId, List.of(
                new ReportDto("Alice", LocalDate.of(2007, 6, 14), new BigDecimal("500"))));

        queue.submitTask(task);

        int retries = 20;
        while (retries-- > 0) {
            if (queue.getStatus(reportId) == TaskStatus.DONE)
                break;
            Thread.sleep(100);
        }

        Optional<File> optFile = queue.getReportFile(reportId);

        assertTrue(optFile.isPresent());
        assertEquals("fake.xlsx", optFile.get().getName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenReportIdIsInvalid() {
        Optional<File> optFile = queue.getReportFile("ivalid-id");

        assertTrue(optFile.isEmpty());
    }

    @Test
    void shouldReturnStatusNotFoundWhenInvalidReportId() {
        String reportId = "invalid-id";

        TaskStatus status = queue.getStatus(reportId);

        assertEquals(TaskStatus.NOT_FOUND, status);
    }

    @Test
    void shouldMarkAsFailedOnException() throws InterruptedException {
        ExcelReportGenerator brokenGen = new ExcelReportGenerator() {
            @Override
            public File generateReport(List data, String reportId) {
                throw new RuntimeException("Simulated failure");
            }
        };

        ReportTaskQueue brokenQueue = new ReportTaskQueue(brokenGen);
        brokenQueue.initWorkers();

        String reportId = "broken-id";
        ReportTask task = new ReportTask(reportId, List.of(
                new ReportDto("Alice", LocalDate.of(2007, 6, 14), new BigDecimal("500"))));

        brokenQueue.submitTask(task);

        int retries = 20;
        TaskStatus status = null;
        while (retries-- > 0) {
            status = brokenQueue.getStatus(reportId);
            if (status == TaskStatus.FAILED)
                break;
            Thread.sleep(100);
        }

        assertEquals(TaskStatus.FAILED, status);
        brokenQueue.shutdown();
    }

}

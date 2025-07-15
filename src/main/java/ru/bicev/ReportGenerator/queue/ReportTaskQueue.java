package ru.bicev.ReportGenerator.queue;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ru.bicev.ReportGenerator.dto.ReportTask;
import ru.bicev.ReportGenerator.generator.ExcelReportGenerator;
import ru.bicev.ReportGenerator.util.TaskStatus;

@Component
public class ReportTaskQueue {

    private final BlockingQueue<ReportTask> queue = new LinkedBlockingQueue<>();
    private final Map<String, TaskStatus> statusMap = new ConcurrentHashMap<>();
    private final Map<String, File> filesMap = new ConcurrentHashMap<>();

    private final ExecutorService executor;
    private final ExcelReportGenerator generator;

    private final int threads = Runtime.getRuntime().availableProcessors();

    private static final Logger logger = LoggerFactory.getLogger(ReportTaskQueue.class);

    public ReportTaskQueue(ExcelReportGenerator generator) {
        this.generator = generator;
        executor = Executors.newFixedThreadPool(threads);
    }

    @PostConstruct
    public void initWorkers() {

        logger.info("Initializing executor service");

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                ReportTask task = null;
                while (true) {
                    try {
                        task = queue.take();
                        statusMap.put(task.getReportId(), TaskStatus.IN_PROGRESS);
                        File file = generator.generateReport(task.getData(), task.getReportId());
                        statusMap.put(task.getReportId(), TaskStatus.DONE);
                        filesMap.put(task.getReportId(), file);
                    } catch (Exception e) {

                        if (task != null) {
                            logger.error("Error with task: {}", task.getReportId(), e);
                            statusMap.put(task.getReportId(), TaskStatus.FAILED);
                        } else {
                            logger.error("Error with null task", e);
                        }
                    }

                }
            });
        }
    }

    @PreDestroy
    public void shutdown() {

        logger.info("Shutting down executor service");

        executor.shutdown();

    }

    public Optional<File> getReportFile(String reportId) {
        return Optional.ofNullable(filesMap.getOrDefault(reportId, null));
    }

    public void submitTask(ReportTask task) {
        statusMap.put(task.getReportId(), TaskStatus.PENDING);
        queue.offer(task);
        logger.info("Task submited in queue: {}", task.getReportId());
    }

    public TaskStatus getStatus(String taskId) {
        return statusMap.getOrDefault(taskId, TaskStatus.NOT_FOUND);
    }

}

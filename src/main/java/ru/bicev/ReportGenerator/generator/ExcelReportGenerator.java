package ru.bicev.ReportGenerator.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import ru.bicev.ReportGenerator.util.ReportData;

@Component
public class ExcelReportGenerator {

    public File generateReport(List<? extends ReportData> data, String reportId) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("List of reports is empty or null");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        createHeaderRows(data.get(0), workbook, sheet);
        fillDataRows(data, sheet);

        Path reportsDir = Paths.get("reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }

        File file = reportsDir.resolve("report_" + reportId + ".xlsx").toFile();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }

        workbook.close();

        return file;
    }

    private void createHeaderRows(ReportData reportData, Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        List<String> headers = reportData.getHeaders();

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < reportData.getHeaders().size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }

        for (int i = 0; i < reportData.getHeaders().size(); i++) {
            sheet.autoSizeColumn(i);
        }

    }

    private void fillDataRows(List<? extends ReportData> data, Sheet sheet) {
        for (int rowIdx = 0; rowIdx < data.size(); rowIdx++) {
            ReportData rowData = data.get(rowIdx);
            List<String> rowValues = rowData.getRow();

            Row row = sheet.createRow(rowIdx + 1);
            for (int cellIdx = 0; cellIdx < rowValues.size(); cellIdx++) {
                row.createCell(cellIdx).setCellValue(rowValues.get(cellIdx));
            }
        }
    }

}

package com.mjtech.pos.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class JasperReportService {

    @Autowired
    private DataSource dataSource;

    public void generateReport(String filePath, Map<String, Object> parameters) throws Exception {
        // Load the report template
        File reportFile = new File(getClass().getResource(filePath).getFile());
        JasperReport jasperReport = JasperCompileManager.compileReport(reportFile.getAbsolutePath());

        // Set parameters for the report (if needed)
        parameters = new HashMap<>();
        parameters.put("param1", "Value1");
        parameters.put("param2", "Value2");

        // Provide data source (if needed)
        // JRDataSource dataSource = ...;

        // Fill the report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

        // Export the report to PDF
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("output.pdf"));
        exporter.exportReport();
        File tempFile = new File("output.pdf");

        try {
            // Save the PDF content to a temporary file
          //  File tempFile = File.createTempFile("report", ".pdf");
            Path tempFilePath = tempFile.toPath();
          //  Files.write(tempFilePath, pdfBytes, StandardOpenOption.CREATE);

            // Open the PDF using a PDF viewer
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", tempFile.getAbsolutePath());
            } else if (os.contains("mac")) {
                processBuilder = new ProcessBuilder("open", tempFile.getAbsolutePath());
            } else if (os.contains("nix") || os.contains("nux")) {
                processBuilder = new ProcessBuilder("xdg-open", tempFile.getAbsolutePath());
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + os);
            }
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
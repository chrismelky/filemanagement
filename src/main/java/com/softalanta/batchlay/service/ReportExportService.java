package com.softalanta.batchlay.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportExportService {

    private final
    DataSource dataSource;

    public Map<String, Object> getParams() {
        return params;
    }

    public JasperReport getJasperReport() {
        return jasperReport;
    }

    private Map<String, Object> params;

    private JasperReport jasperReport;

    @Autowired
    public ReportExportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public File exportToPdf() throws SQLException, IOException, JRException {

        File tmpFile = Files.createTempFile("userReport",".pdf").toFile();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);

        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor("chris");
        exportConfig.setEncrypted(true);
        exportConfig.setAllowedPermissionsHint("PRINTING");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();

        return  tmpFile;
    }

    public File exportXls() throws IOException, JRException, SQLException {

        File tmpFile = Files.createTempFile("userReport",".xls").toFile();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setSheetNames(new String[] { "Employee Data" });

        exporter.setConfiguration(reportConfig);
        exporter.exportReport();

        return tmpFile;
    }

    public File exportDoc() throws IOException, JRException, SQLException {

        File tmpFile = Files.createTempFile("userReport",".doc").toFile();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimpleDocxExporterConfiguration reportConfig = new SimpleDocxExporterConfiguration();
        reportConfig.setMetadataTitle("Doc baby");

        exporter.setConfiguration(reportConfig);
        exporter.exportReport();

        return tmpFile;
    }

    public ReportExportService setParams(Map<String, Object> parameters) {
        this.params = parameters;
        return this;
    }

    public ReportExportService setJasperReport(JasperReport jasperReport) {
        this.jasperReport = jasperReport;
        return this;
    }
}

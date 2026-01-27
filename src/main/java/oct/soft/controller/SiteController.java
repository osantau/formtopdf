/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oct.soft.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import oct.soft.dto.FormDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author osantau
 */
@Controller
public class SiteController {

    @Value("${report.file}")
    String reportFile;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("formDto", new FormDto());
        return "index";
    }

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@ModelAttribute FormDto formDto) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", formDto.getName());
        params.put("email", formDto.getEmail());
        params.put("phone", formDto.getPhone());
        params.put("message", formDto.getMessage());
        params.put("logoPath","reports/tiger8trns.png");
        JasperPrint jp = JasperFillManager.fillReport(reportFile, params, new JREmptyDataSource());
        
        // Export to byte[]
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jp);       
        // Save to disk
        Path pdfDir = Paths.get("pdf");
        Files.createDirectories(pdfDir);
        String fileName = "report_" + System.currentTimeMillis() + ".pdf";
        Path pdfPath = pdfDir.resolve(
                fileName
        );
        Files.write(pdfPath, pdfBytes);
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jp));
exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File("report.docx")));
exporter.exportReport();
        // Send to browser
 /* Download instead of inline headers.setContentDisposition(
    ContentDisposition
        .attachment()
        .filename("report.pdf")
        .build()
);*/
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename="+fileName+""
            )
            .body(pdfBytes);
    }
}

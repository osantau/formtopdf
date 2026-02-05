/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oct.soft;

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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author osantau
 */
@Service
public class PdfService {

    @Value("${report.file}")
    String reportFile;

    public byte[] generatePdf(Map<String,String> formParams, String fileName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.putAll(formParams);
        JasperPrint jp = JasperFillManager.fillReport(reportFile, params, new JREmptyDataSource());
        // Export to byte[]
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jp);
        // Save to disk
        Path pdfDir = Paths.get("pdf");
        Files.createDirectories(pdfDir);        
        Path pdfPath = pdfDir.resolve(
                fileName+".pdf"
        );
        Files.write(pdfPath, pdfBytes);
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jp));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(fileName+".docx")));
        exporter.exportReport();
        return pdfBytes;
    }
}

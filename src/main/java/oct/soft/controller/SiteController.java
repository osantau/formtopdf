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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import oct.soft.PdfService;
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

    private final PdfService pdfService;

    public SiteController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("formDto", new FormDto());
        return "index";
    }

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(HttpServletRequest req) throws Exception {
        String fileName = "report_" + System.currentTimeMillis();
        // Send to browser
        /* Download instead of inline headers.setContentDisposition(
    ContentDisposition
        .attachment()
        .filename("report.pdf")
        .build()
);*/
        Enumeration<String> paraNames = req.getParameterNames();
        Map<String,String> formParams = new HashMap<>();
        while(paraNames.hasMoreElements())
        {
            String pName = paraNames.nextElement();
            formParams.put(pName,req.getParameterValues(pName)[0]);
        }
        System.out.println(formParams);
        byte[] pdfBytes = pdfService.generatePdf(formParams, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=" + fileName + ".pdf"
                )
                .body(pdfBytes);
    }
    
    @PostMapping("/handle-form")
    public String handleForm(HttpServletRequest req)
    {   
        Enumeration<String> paraNames = req.getParameterNames();
        while(paraNames.hasMoreElements())
        {
            String pName = paraNames.nextElement();
            System.out.println(pName + " : "+req.getParameterValues(pName)[0]);
        }
        return "success";
    }
}

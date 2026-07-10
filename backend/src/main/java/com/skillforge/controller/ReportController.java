package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.service.RecruiterPortalService;
import com.skillforge.service.StudentPlacementService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Positive;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final StudentPlacementService placementService;
    private final RecruiterPortalService recruiterPortalService;

    public ReportController(StudentPlacementService placementService, RecruiterPortalService recruiterPortalService) {
        this.placementService = placementService;
        this.recruiterPortalService = recruiterPortalService;
    }

    @GetMapping(value = "/students/me/placement.csv", produces = "text/csv")
    @PreAuthorize("hasRole('STUDENT')")
    public byte[] studentPlacementCsv(@AuthenticationPrincipal com.skillforge.security.UserPrincipal principal, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student-placement.csv");
        StringBuilder csv = new StringBuilder("type,id,title,status\n");
        placementService.appliedJobs(principal.getId()).forEach(item -> csv.append("applied,").append(item.getId()).append(",").append(item.getJob().getTitle()).append(",").append(item.getStatus()).append("\n"));
        placementService.offers(principal.getId()).forEach(item -> csv.append("offer,").append(item.getId()).append(",").append(item.getOfferTitle()).append(",OFFER\n"));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/recruiter/jobs/{jobId}/applicants.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasRole('RECRUITER')")
    public byte[] recruiterApplicantsXlsx(@AuthenticationPrincipal com.skillforge.security.UserPrincipal principal, @PathVariable @Positive Long jobId, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=job-applicants.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Applicants");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Application ID");
            header.createCell(1).setCellValue("Student ID");
            header.createCell(2).setCellValue("Status");
            int rowIndex = 1;
            for (var application : recruiterPortalService.applicants(principal.getId(), jobId)) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(application.getId());
                row.createCell(1).setCellValue(application.getStudentId());
                row.createCell(2).setCellValue(application.getStatus().name());
            }
            workbook.write(response.getOutputStream());
            return new byte[0];
        }
    }

    @GetMapping(value = "/admin/placement-report.csv", produces = "text/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public byte[] adminReport(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=placement-report.csv");
        return "metric,value\nplaceholder,0\n".getBytes(StandardCharsets.UTF_8);
    }
}
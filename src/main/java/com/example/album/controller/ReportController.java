package com.example.album.controller;

import com.example.album.dto.ReportRequestDTO;
import com.example.album.entity.Report;
import com.example.album.entity.Result;
import com.example.album.service.ReportService;
import com.example.album.utils.ThreadLocalUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("/user")
    public Result createReport(@Valid @RequestBody ReportRequestDTO request) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer reporterId = (Integer) claims.get("id");
        reportService.createReport(reporterId, request);
        return Result.success();
    }

    @GetMapping("/admin")
    public Result<List<Report>> getAllReports() {
        // 1. 解析 JWT Token
        Map<String, Object> claims = ThreadLocalUtil.get();

        // 2. 检查用户是否是管理员
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error("权限不足，只有管理员可以处理举报信息");
        }

        List<Report> reports = reportService.getAllReports();
        return Result.success(reports);
    }

    @PutMapping("/admin/{reportId}")
    public Result updateReportStatus(@PathVariable Integer reportId, @RequestParam String status, @RequestParam Integer reporteeId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error("权限不足，只有管理员可以处理举报信息");
        }

        String reviewedBy = (String) claims.get("username");
        reportService.updateReportStatus(reportId, status, reviewedBy);
        reportService.disableUser(reporteeId);
        return Result.success();
    }

    @PutMapping("/admin/active/{reporteeId}")
    public Result activeUser(@PathVariable Integer reporteeId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error("权限不足，只有管理员可以处理举报信息");
        }

        reportService.activeUser(reporteeId);
        return Result.success();
    }

    @PutMapping("/user/mend")
    public Result updateIsMended() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer resourceId = (Integer) claims.get("id");
        reportService.updateIsCorrected(resourceId);
        return Result.success();
    }
}

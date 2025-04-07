package com.example.album.service;

import com.example.album.dto.ReportRequestDTO;
import com.example.album.entity.Report;

import java.util.List;

public interface ReportService {
    void createReport(Integer reporterId, ReportRequestDTO request);

    List<Report> getAllReports();

    void updateReportStatus(Integer reportId, String status, String reviewedBy);

    void disableUser(Integer userId);

    void activeUser(Integer userId);

    void updateIsCorrected(Integer resourceId);
}

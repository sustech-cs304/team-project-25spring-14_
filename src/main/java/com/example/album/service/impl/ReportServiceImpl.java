package com.example.album.service.impl;

import com.example.album.dto.ReportRequestDTO;
import com.example.album.entity.Report;
import com.example.album.mapper.ReportMapper;
import com.example.album.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public void createReport(Integer reporterId, ReportRequestDTO request) {
        // 将举报信息插入到tb_report表中
        reportMapper.insertReport(reporterId, request.getResourceId(), request.getReporteeId(), request.getReason(), request.getResourceType());
    }

    @Override
    public List<Report> getAllReports() {
        return reportMapper.getAllReports();
    }

    @Override
    public void updateReportStatus(Integer reportId, String status, String reviewedBy) {
        reportMapper.updateReportStatus(reportId, status, reviewedBy);
    }

    @Override
    public void disableUser(Integer userId) {
        reportMapper.disableUser(userId);
    }

    @Override
    public void activeUser(Integer userId) {
        reportMapper.activeUser(userId);
    }

    @Override
    public void updateIsCorrected(Integer resourceId) {
        reportMapper.updateIsCorrected(resourceId);
    }
}

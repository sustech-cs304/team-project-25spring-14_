package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.Report;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {

    //search the report page by page
    @Select("SELECT * FROM tb_report ORDER BY created_at DESC")
    IPage<Report> selectReportPage(Page<Report> page);

    //search the report that is pending
    @Select("SELECT * FROM tb_report WHERE status = 'pending' ORDER BY created_at")
    IPage<Report> selectPendingReports(Page<Report> page);

    //update the status
    @Update("UPDATE tb_report SET status = #{status}, reviewed_by = #{reviewedBy} WHERE report_id = #{reportId}")
    int updateReportStatus( Integer reportId, String status, String reviewedBy);

    //insert the report
    @Insert("INSERT INTO tb_report (reporter_id, resource_id, reportee_id, reason, resource_type) " +
            "VALUES (#{reporterId}, #{resourceId}, #{reporteeId}, #{reason}, #{resourceType}::resource_type)")
    void insertReport(Integer reporterId, Integer resourceId, Integer reporteeId, String reason, String resourceType);

    //get all reports
    @Select("SELECT * FROM tb_report")
    List<Report> getAllReports();

    //update the user status
    @Update("UPDATE tb_user SET status = 'disabled' WHERE user_id = #{userId}")
    int disableUser(Integer userId);

    //update the user status
    @Update("UPDATE tb_user SET status = 'active' WHERE user_id = #{userId}")
    int activeUser(Integer userId);

    @Update("UPDATE tb_report SET is_corrected = true WHERE resource_id = #{resourceId}")
    int updateIsCorrected(Integer resourceId);
}
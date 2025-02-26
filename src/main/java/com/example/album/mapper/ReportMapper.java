package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    int updateReportStatus(@Param("reportId") Integer reportId,
                           @Param("status") String status,
                           @Param("reviewedBy") Integer reviewedBy);
}
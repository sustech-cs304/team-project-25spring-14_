package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.AdminLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminLogMapper extends BaseMapper<AdminLog> {

    /**
     * 分页查询管理员日志
     */
    @Select("SELECT * FROM tb_admin_log ORDER BY created_at DESC")
    IPage<AdminLog> selectLogPage(Page<AdminLog> page);

    /**
     * 查询特定管理员的操作日志
     */
    @Select("SELECT * FROM tb_admin_log WHERE admin_id = #{adminId} ORDER BY created_at DESC")
    IPage<AdminLog> selectLogPageByAdminId(Page<AdminLog> page, @Param("adminId") Integer adminId);
}
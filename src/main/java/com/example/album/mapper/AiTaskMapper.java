package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.AiTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AiTaskMapper extends BaseMapper<AiTask> {

    /**
     * 查询特定照片的所有任务
     */
    @Select("SELECT * FROM tb_ai_task WHERE photo_id = #{photoId}")
    List<AiTask> selectByPhotoId(@Param("photoId") Integer photoId);

    /**
     * 查询待处理的任务
     */
    @Select("SELECT * FROM tb_ai_task WHERE status = 'pending' ORDER BY created_at")
    List<AiTask> selectPendingTasks();

    /**
     * 更新任务状态
     */
    @Update("UPDATE tb_ai_task SET status = #{status}, completed_at = CURRENT_TIMESTAMP WHERE task_id = #{taskId}")
    int updateTaskStatus(@Param("taskId") Integer taskId, @Param("status") String status);
}
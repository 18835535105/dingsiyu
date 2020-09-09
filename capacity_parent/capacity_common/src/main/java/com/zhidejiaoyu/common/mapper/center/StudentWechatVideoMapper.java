package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.StudentWechatVideo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-27
 */
public interface StudentWechatVideoMapper extends BaseMapper<StudentWechatVideo> {

    /**
     * 删除学生观看记录
     *
     * @param userUuid
     */
    @Delete("delete from student_wechat_video where student_uuid = #{userUuid}")
    void deleteByStudentUuid(@Param("userUuid") String userUuid);

    /**
     * 更新视频观看状态
     *
     * @param userUuid
     * @param state
     */
    @Update("update student_wechat_video set state = #{state} where user_uuid = #{userUuid}")
    void updateStateByUuid(@Param("userUuid") String userUuid, @Param("state") int state);

    /**
     * 查询当前学生观看过的视频
     *
     * @param uuid
     * @return
     */
    List<StudentWechatVideo> selectByUuid(@Param("uuid") String uuid);
}

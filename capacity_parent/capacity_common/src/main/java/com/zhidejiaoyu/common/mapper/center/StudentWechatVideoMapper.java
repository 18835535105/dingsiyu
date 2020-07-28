package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.StudentWechatVideo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

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
}

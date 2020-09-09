package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.WechatVideo;
import com.zhidejiaoyu.common.vo.study.video.VideoCourseVO;
import com.zhidejiaoyu.common.vo.study.video.VideoListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-27
 */
public interface WechatVideoMapper extends BaseMapper<WechatVideo> {

    /**
     * 查询下个应学习的视频
     *
     * @param userUuid
     * @param grade
     * @return
     */
    WechatVideo selectNextVideo(@Param("userUuid") String userUuid, @Param("grade") String grade);

    /**
     * 查询指定年级的课程
     *
     * @param grades
     * @param nextGrade
     * @return
     */
    List<VideoCourseVO> selectByGradesAndNextGrade(@Param("grades") List<String> grades, @Param("nextGrade") String nextGrade);

    /**
     * 查询指定课程的视频信息
     *
     * @param grade
     * @param label
     * @return
     */
    List<VideoListVO> selectByGradeAndLabel(@Param("grade") String grade, @Param("label") String label);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CourseConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
public interface CourseConfigMapper extends BaseMapper<CourseConfig> {

    /**
     * 查询用户指定的配置信息
     *
     * @param userId
     * @param type
     * @param oneKeyLearn
     * @return
     */
    List<CourseConfig> selectByUserIdAndTypeAndOneKeyLearn(@Param("userId") Long userId, @Param("type") int type, @Param("oneKeyLearn") int oneKeyLearn);

    List<Long> selectByUserId(@Param("userId") Long userId,@Param("gradeList") List<String> gradeList);

    /**
     * 查看是否有排课的内容
     * @param userId
     */
    int countByUserIdAndType(@Param("userId") Long userId,@Param("type")int type);
}

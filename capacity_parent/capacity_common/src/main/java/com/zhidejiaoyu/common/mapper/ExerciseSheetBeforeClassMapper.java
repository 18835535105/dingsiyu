package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.ExerciseSheetBeforeClass;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课前测试习题表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-09-09
 */
@Repository
public interface ExerciseSheetBeforeClassMapper extends BaseMapper<ExerciseSheetBeforeClass> {

    /**
     * 查询当前单元的所有学前测试数据
     *
     * @param jointName
     * @return
     */
    List<ExerciseSheetBeforeClass> selectByJointName(String jointName);
}

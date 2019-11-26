package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CapacityStudentUnit;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 智能版学生当前学习课程和单元记录表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-12
 */
@Repository
public interface CapacityStudentUnitMapper extends BaseMapper<CapacityStudentUnit> {

    CapacityStudentUnit selGetSentenceByStudentIdAndType(Long studentId);

    /**
     * 获取学生当前模块正在学习的课程和单元
     *
     * @param studentId
     * @param type  学习模块：1：单词模块；2：例句听力；3：例句默写；4：例句翻译
     * @return
     */
    CapacityStudentUnit selectByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);

    /**
     * 清除学生指定类型的正在学习课程信息
     *
     * @param studentId
     * @param type
     */
    void deleteByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") int type);


    CapacityStudentUnit selSymbolByStudentId(Long id);

    CapacityStudentUnit selLetterByStudentId(Long studentId);

    CapacityStudentUnit selByStudentIdAndType(@Param("studentId") Long id,@Param("type") Integer type);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}

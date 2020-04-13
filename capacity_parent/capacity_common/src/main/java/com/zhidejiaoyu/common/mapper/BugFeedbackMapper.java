package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.BugFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-04-13
 */
public interface BugFeedbackMapper extends BaseMapper<BugFeedback> {

    int countByStudentIdAndDate(@Param("studentId")Long studentId,@Param("date") Date date);
}

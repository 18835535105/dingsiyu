package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-06-02
 */
public interface CurrentDayOfStudyMapper extends BaseMapper<CurrentDayOfStudy> {


    List<CurrentDayOfStudy> selectByDate(@Param("date") String date);

    /**
     * 查询学生指定二维码序号的信息
     *
     * @param studentId
     * @param num
     * @return
     */
    CurrentDayOfStudy selectByStudentIdAndQrCodeNum(@Param("studentId") Long studentId, @Param("num") Integer num);
}

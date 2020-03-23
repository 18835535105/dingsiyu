package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentPayConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 微信抽奖 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
public interface StudentPayConfigMapper extends BaseMapper<StudentPayConfig> {

    StudentPayConfig selectByWenXiIdAndDate(@Param("openId") String openId,@Param("date") Date date);

    List<Map<String,Object>> selectWeChatNameAndWeChatImgUrlByStudentId(@Param("studentId") Long studentId);
}

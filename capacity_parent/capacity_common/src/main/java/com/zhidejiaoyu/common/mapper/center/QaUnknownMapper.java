package com.zhidejiaoyu.common.mapper.center;

import com.zhidejiaoyu.common.pojo.center.QaUnknown;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-21
 */
public interface QaUnknownMapper extends BaseMapper<QaUnknown> {

    /**
     * 判断当前问题是否已在未知问题表中
     *
     * @param question
     * @return
     */
    @Select("select count(id) from qa_unknown where question = #{question} ")
    int countByQuestion(@Param("question") String question);
}

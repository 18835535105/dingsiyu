package com.zhidejiaoyu.common.mapper.center;

import com.zhidejiaoyu.common.pojo.center.QaAutoLearn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-21
 */
public interface QaAutoLearnMapper extends BaseMapper<QaAutoLearn> {

    /**
     * 根据问题查询答案
     *
     * @param question
     * @return key:answer  答案
     * key:id   问题id
     */
    List<Map<String, Object>> selectByQuestion(String question);
}

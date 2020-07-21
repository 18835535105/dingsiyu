package com.zhidejiaoyu.common.mapper.center;

import com.zhidejiaoyu.common.pojo.center.QaQuestion;
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
public interface QaQuestionMapper extends BaseMapper<QaQuestion> {

    /**
     * 查询关键字及其对应的问题
     *
     * @return <ul>
     *     <li>key:id  问题id</li>
     *     <li>key:answer 问题答案</li>
     *     <li>key:keyWords 关键词</li>
     * </ul>
     */
    List<Map<String, Object>> selectKeyWordsAndQuestion();
}

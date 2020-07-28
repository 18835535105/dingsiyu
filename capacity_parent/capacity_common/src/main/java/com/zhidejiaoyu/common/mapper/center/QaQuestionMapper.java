package com.zhidejiaoyu.common.mapper.center;

import com.zhidejiaoyu.common.pojo.center.QaQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * <li>key:id  问题id</li>
     * <li>key:answer 问题答案</li>
     * <li>key:keyWords 关键词</li>
     * <li>key:url 答案读音</li>
     * <li>key:question 问题描述</li>
     * </ul>
     */
    List<Map<String, Object>> selectKeyWordsAndQuestion();

    /**
     * 查询当前问题是否已经存在
     *
     * @param question
     * @return
     */
    @Select("select count(id) from qa_question where question = #{question} limit 1")
    int countByQuestion(@Param("question") String question);
}

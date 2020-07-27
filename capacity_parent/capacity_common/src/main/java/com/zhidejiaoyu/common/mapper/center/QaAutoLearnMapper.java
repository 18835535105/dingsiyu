package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.QaAutoLearn;
import com.zhidejiaoyu.common.vo.wechat.qy.QaVO;

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
     * @return key:question  问题
     * key:id   问题id
     * key:url  答案读音地址
     * key:answer  答案
     */
    QaVO selectByQuestion(String question);
}

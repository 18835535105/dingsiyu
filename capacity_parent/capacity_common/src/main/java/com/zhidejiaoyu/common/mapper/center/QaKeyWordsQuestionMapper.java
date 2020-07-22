package com.zhidejiaoyu.common.mapper.center;

import com.zhidejiaoyu.common.pojo.center.QaKeyWordsQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-21
 */
public interface QaKeyWordsQuestionMapper extends BaseMapper<QaKeyWordsQuestion> {

    QaKeyWordsQuestion selectByKeyIdAndQuestionId(@Param("keyId") String keyId,@Param("questionId") String questionId);
}

package com.zhidejiaoyu.common.Vo.testbeforstudy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 小学本单元测试
 *
 * @author: wuchenxi
 * @Date: 2019-09-16 16:11
 */
@Data
/*@Builder
@NoArgsConstructor*/
public class SmallCurrentUnitTest implements Serializable {

    /**
     * 大标题类型
     */
    private Integer topic;

   /* *
     *
     */
    private List<QuestionAndAnswer> questionAndAnswerList;
}

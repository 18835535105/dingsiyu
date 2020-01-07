package com.zhidejiaoyu.student.business.flow.common;

import java.util.HashMap;
import java.util.Map;

/**
 * studentFlowNew中flowName与learnNew中modelType值映射
 *
 * @author: wuchenxi
 * @date: 2020/1/7 14:46:46
 */
public class FlowNameToLearnModelType {
    public static final Map<String, Integer> FLOW_NEW_TO_LEARN_MODEL_TYPE = new HashMap<>(16);

    static {
        FLOW_NEW_TO_LEARN_MODEL_TYPE.put("流程1", 1);
        FLOW_NEW_TO_LEARN_MODEL_TYPE.put("流程2", 1);
        FLOW_NEW_TO_LEARN_MODEL_TYPE.put("流程3", 2);
        FLOW_NEW_TO_LEARN_MODEL_TYPE.put("流程4", 2);
        FLOW_NEW_TO_LEARN_MODEL_TYPE.put("流程5", 3);
        FLOW_NEW_TO_LEARN_MODEL_TYPE.put("流程6", 4);
    }
}

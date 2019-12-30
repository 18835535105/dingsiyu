package com.zhidejiaoyu.common.dto;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import lombok.Data;

import javax.servlet.http.HttpSession;

/**
 * 获取流程节点 dto
 *
 * @author wuchenxi
 * @date 2019-07-18
 */
@Data
public class NodeDto {

    /**
     * 请求参数
     */
    private Long courseId;

    /**
     * 请求参数
     */
    private Long unitId;

    /**
     * 请求参数<br>
     * 当前流程节点 id
     */
    private Long nodeId;

    /**
     * 请求参数<br>
     * 得分
     */
    private Long grade;

    /**
     * 1：容易；2：困难
     */
    private Integer easyOrHard;

    /**
     * 当前学习所在分组
     */
    private Integer group;

    /**
     * 2：单词；3：句型；4：课文；5：语法
     */
    private Integer modelType;

    /**
     * 区分一键排课流程和自由学习流程
     * 1：一键排课
     * 2：自由学习
     */
    private Integer type;

    /**
     * 非请求参数
     */
    private StudyFlowNew studyFlowNew;

    /**
     * 非请求参数
     */
    private HttpSession session;

    /**
     * 非请求参数
     */
    private Student student;

    /**
     * 非请求参数<br>
     * 是否执行正确情况的节点<br>
     * true：走 nextTrueFlow 节点 <br>
     * false:走 nextFalseFlow 节点
     */
    private String trueFlow;
}

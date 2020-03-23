package com.zhidejiaoyu.student.business.flow.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.LearnHistoryMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.StudentFlowNewMapper;
import com.zhidejiaoyu.common.pojo.LearnHistory;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 初始化数据
 *
 * @author: wuchenxi
 * @date: 2020/1/3 17:57:57
 */
@Component
public class InitData {

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    /**
     * studentFlowNew中type与learnHistory中type中的type对应关系
     */
    private static final Map<Integer, Integer> MAP = new HashMap<>(16);

    static {
        MAP.put(2, 1);
        MAP.put(3, 2);
        MAP.put(4, 4);
        MAP.put(5, 3);
    }

    /**
     * 初始化学生流程
     *
     * @param dto 需要参数：nodeId，learnNew
     */
    public void initStudentFlow(NodeDto dto) {
        studentFlowNewMapper.insert(StudentFlowNew.builder()
                .currentFlowId(dto.getNodeId())
                .learnId(dto.getLearnNew().getId())
                .studentId(dto.getLearnNew().getStudentId())
                .updateTime(new Date())
                .type(dto.getModelType() == null ? 1 : dto.getModelType())
                .build());
    }

    /**
     * 一键学习，学完一个group，保存或者更新已有学习历史记录
     *
     * @param dto
     * @param count
     * @param type  learnHistory type值
     */
    public void saveOrUpdateOneKeyLearnHistory(NodeDto dto, Integer count, int type) {
        if (count > 0) {
            // 查询已完成表中是否已有当前group信息，有更新，没有新增
            LearnHistory learnHistory = learnHistoryMapper.selectOne(new QueryWrapper<LearnHistory>()
                    .eq("student_id", dto.getStudent().getId())
                    .eq("course_id", dto.getCourseId())
                    .eq("unit_id", dto.getUnitId())
                    .eq("`group`", dto.getGroup())
                    .eq("easy_or_hard", dto.getEasyOrHard())
                    .eq("type", type)
            );
            if (learnHistory != null) {
                learnHistory.setState(1);
                learnHistory.setUpdateTime(new Date());
                learnHistory.setStudyCount(learnHistory.getStudyCount() + 1);
                learnHistoryMapper.updateById(learnHistory);
            } else {
                learnHistoryMapper.insert(LearnHistory.builder()
                        .studentId(dto.getStudent().getId())
                        .courseId(dto.getCourseId())
                        .unitId(dto.getUnitId())
                        .group(dto.getGroup())
                        .easyOrHard(dto.getEasyOrHard())
                        .type(type)
                        .state(1)
                        .studyCount(1)
                        .updateTime(new Date())
                        .build());
            }
        }
    }

    /**
     * 自由学习，学完一个group，保存或者更新已有学习历史记录
     *
     * @param dto
     */
    public void saveOrUpdateFreeLearnHistory(NodeDto dto) {
        this.saveOrUpdateOneKeyLearnHistory(dto, 1, MAP.get(dto.getModelType()));
    }

    /**
     * 保存学习表
     *
     * @param dto       需要参数：easyOrHard，student, unitId, courseId
     * @param group
     * @param modelType
     * @return
     */
    public LearnNew saveLearnNew(NodeDto dto, Integer group, int modelType) {
        LearnNew learnNew = LearnNew.builder()
                .easyOrHard(dto.getEasyOrHard())
                .group(group)
                .studentId(dto.getStudent().getId())
                .unitId(dto.getUnitId())
                .updateTime(new Date())
                .courseId(dto.getCourseId())
                .modelType(modelType)
                .build();
        learnNewMapper.insert(learnNew);
        return learnNew;
    }

    /**
     * 如果学习表中没有最高优先级的数据，新增
     *
     * @param studentStudyPlanNew
     * @param modelType           学习的模块
     */
    public LearnNew saveLearn(StudentStudyPlanNew studentStudyPlanNew, int modelType) {
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentStudyPlanNew.getStudentId(),
                studentStudyPlanNew.getUnitId(), studentStudyPlanNew.getEasyOrHard(), modelType);
        if (learnNew == null) {
            learnNew = LearnNew.builder()
                    .courseId(studentStudyPlanNew.getCourseId())
                    .easyOrHard(studentStudyPlanNew.getEasyOrHard())
                    .group(1)
                    .studentId(studentStudyPlanNew.getStudentId())
                    .unitId(studentStudyPlanNew.getUnitId())
                    .updateTime(new Date())
                    .modelType(modelType)
                    .build();
            learnNewMapper.insert(learnNew);
        }
        return learnNew;
    }

}

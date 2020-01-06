package com.zhidejiaoyu.student.business.flow.common;

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
     * 初始化学生流程
     *
     * @param dto
     */
    public void initStudentFlow(NodeDto dto) {
        studentFlowNewMapper.insert(StudentFlowNew.builder()
                .currentFlowId(dto.getNodeId())
                .learnId(dto.getLearnNew().getId())
                .studentId(dto.getStudent().getId())
                .updateTime(new Date())
                .type(dto.getModelType() == null ? 1 : dto.getModelType())
                .build());
    }

    /**
     * 一键学习，学完一个group，保存或者更新已有学习历史记录
     *
     * @param dto
     * @param count
     * @param type               learnHistory type值
     */
    public void saveOrUpdateOneKeyLearnHistory(NodeDto dto, Integer count, int type) {
        if (count > 0) {
            // 查询已完成表中是否已有当前group信息，有更新，没有新增
            LearnHistory.LearnHistoryBuilder builder = LearnHistory.builder()
                    .studentId(dto.getStudent().getId())
                    .courseId(dto.getCourseId())
                    .unitId(dto.getUnitId())
                    .group(dto.getGroup())
                    .easyOrHard(dto.getEasyOrHard())
                    .type(type);
            LearnHistory learnHistory = learnHistoryMapper.selectOne(builder.build());
            if (learnHistory != null) {
                learnHistory.setState(1);
                learnHistory.setUpdateTime(new Date());
                learnHistory.setStudyCount(learnHistory.getStudyCount() + 1);
                learnHistoryMapper.updateById(learnHistory);
            } else {
                learnHistoryMapper.insert(builder
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
        this.saveOrUpdateOneKeyLearnHistory(dto, 1, dto.getModelType());
    }

    /**
     * 保存学习表
     *
     * @param dto
     * @param group
     * @return
     */
    public LearnNew saveLearnNew(NodeDto dto, Integer group) {
        LearnNew learnNew = LearnNew.builder()
                .easyOrHard(dto.getEasyOrHard())
                .group(group)
                .studentId(dto.getStudent().getId())
                .unitId(dto.getUnitId())
                .updateTime(new Date())
                .courseId(dto.getCourseId())
                .build();
        learnNewMapper.insert(learnNew);
        return learnNew;
    }

    /**
     * 如果学习表中没有最高优先级的数据，新增
     *
     * @param studentStudyPlanNew
     */
    public LearnNew saveLearn(StudentStudyPlanNew studentStudyPlanNew) {
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentStudyPlanNew.getStudentId(), studentStudyPlanNew.getUnitId(),
                studentStudyPlanNew.getEasyOrHard());
        if (learnNew == null) {
            learnNew = LearnNew.builder()
                    .courseId(studentStudyPlanNew.getCourseId())
                    .easyOrHard(studentStudyPlanNew.getEasyOrHard())
                    .group(1)
                    .studentId(studentStudyPlanNew.getStudentId())
                    .unitId(studentStudyPlanNew.getUnitId())
                    .updateTime(new Date())
                    .build();
            learnNewMapper.insert(learnNew);
        }
        return learnNew;
    }

}
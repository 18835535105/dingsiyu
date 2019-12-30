package com.zhidejiaoyu.student.business.syntax.service.impl;

import com.zhidejiaoyu.common.vo.syntax.SyntaxCourseVo;
import com.zhidejiaoyu.common.vo.syntax.TopicVO;
import com.zhidejiaoyu.common.vo.syntax.WriteSyntaxVO;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.learnmodel.LearnModelInfo;
import com.zhidejiaoyu.student.business.syntax.needview.WriteNeedView;
import com.zhidejiaoyu.student.business.syntax.savelearn.SaveLearnInfo;
import com.zhidejiaoyu.student.business.syntax.service.LearnSyntaxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 写语法
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 17:52
 */
@Service("writeSyntaxService")
public class WriteSyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements LearnSyntaxService {

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private SyntaxRedisOpt syntaxRedisOpt;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private SaveLearnInfo saveLearnInfo;

    @Resource
    private WriteNeedView writeNeedView;

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private ExecutorService executorService;

    @Resource
    private MedalAwardAsync medalAwardAsync;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private LearnModelInfo learnModelInfo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.WRITE_SYNTAX);
        int total = syntaxRedisOpt.getTotalSyntaxContentWithUnitId(unitId, 1, SyntaxModelNameConstant.WRITE_SYNTAX);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.WRITE_SYNTAX)
                .build();

        ServerResponse<Object> studyCapacity = writeNeedView.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse<Object> newSyntaxTopic = this.getNewSyntaxTopic(dto);
        if (!Objects.isNull(newSyntaxTopic)) {
            return newSyntaxTopic;
        }

        ServerResponse<Object> serverResponse = writeNeedView.getNextNotGoldTime(dto);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元写语法模块内容都已掌握，进入下一个单元或者完成当前课程
        if (initNextUnitOrCourse(student, dto)) {
            return ServerResponse.createBySuccess(ResponseCode.COURSE_FINISH);
        }
       /* SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId + 1);
        // 说明当前单元写语法模块内容都已掌握，进入下一单元语法游戏模块
        learnModelInfo.packageStudentStudySyntax(unitId, student, SyntaxModelNameConstant.GAME);
        if (syntaxUnit != null) {
            // 返回下一个单元的信息
            return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH.getCode(), SyntaxCourseVo.builder()
                    .unitId(syntaxUnit.getId())
                    .unitName(syntaxUnit.getUnitName())
                    .unitIndex(syntaxUnit.getUnitIndex())
                    .build());
        }*/
        return ServerResponse.createBySuccess(ResponseCode.COURSE_FINISH);
    }

    private boolean initNextUnitOrCourse(Student student, NeedViewDTO dto) {
        Long unitId = syntaxUnitMapper.selectMaxUnitIdByUnitId(dto.getUnitId());
        if (Objects.equals(unitId, dto.getUnitId())) {
            Long courseId = syntaxUnitMapper.selectCourseIdByUnitId(dto.getUnitId());
            // 当前课程学习完毕
            // 将当前课程的学习记录置为已学习
            learnMapper.updateSyntaxToLearnedByCourseId(student.getId(), courseId);
            // 清除学生语法记忆追踪信息
            studyCapacityMapper.deleteSyntaxByStudentIdAndCourseId(student.getId(), courseId);
            // 删除当前课程的语法节点信息
            studentStudySyntaxMapper.deleteByCourseId(student.getId(), courseId);
            // 课程学习完奖励勋章
            this.saveMonsterMedal(student, courseId);
            // 将当前课程学习计划置为已完成状态
            this.updateStudentStudyPlanToComplete(student, courseId);
            return true;
        }
        return false;
    }

    private void updateStudentStudyPlanToComplete(Student student, Long courseId) {
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selectByStudentIdAndCourseId(student.getId(), courseId, 7);
        if (!CollectionUtils.isEmpty(studentStudyPlans)) {
            StudentStudyPlan studentStudyPlan = studentStudyPlans.get(0);
            studentStudyPlan.setComplete(2);
            studentStudyPlanMapper.updateById(studentStudyPlan);
        }
    }

    /**
     * 奖励怪物征服奖章
     *
     * @param student
     * @param courseId
     */
    private void saveMonsterMedal(Student student, Long courseId) {
        executorService.execute(() -> medalAwardAsync.monsterMedal(student, courseId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveLearnSyntax(Learn learn, Boolean known) {
        Student student = super.getStudent(HttpUtil.getHttpSession());
        learn.setStudentId(student.getId());
        learn.setStudyModel(SyntaxModelNameConstant.WRITE_SYNTAX);
        //saveLearnInfo.saveSyntax(learn, known, StudyCapacityTypeConstant.WRITE_SYNTAX);
        return null;
    }


    /**
     * 获取下一个需要学习的内容
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> getNewSyntaxTopic(NeedViewDTO dto) {
        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectNextByUnitIdAndType(dto.getStudentId(), dto.getUnitId(), dto.getGroup(), SyntaxModelNameConstant.WRITE_SYNTAX);
        if (!Objects.isNull(syntaxTopic)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(syntaxTopic.getId());
            return ServerResponse.createBySuccess(WriteSyntaxVO.builder()
                    .knowledgePoint(LearnSyntaxServiceImpl.getContent(knowledgePoint))
                    .topic(TopicVO.builder()
                            .answer(syntaxTopic.getAnswer())
                            .title(syntaxTopic.getTopic())
                            .build())
                    .memoryDifficult(0)
                    .memoryStrength(0)
                    .plan(dto.getPlan())
                    .studyNew(true)
                    .id(syntaxTopic.getId())
                    .total(dto.getTotal())
                    .model(syntaxTopic.getModel())
                    .build());
        }
        return null;
    }
}

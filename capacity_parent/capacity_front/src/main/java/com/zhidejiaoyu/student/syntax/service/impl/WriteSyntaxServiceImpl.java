package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.WriteSyntaxVO;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.needview.WriteNeedView;
import com.zhidejiaoyu.student.syntax.savelearn.SaveLearnInfo;
import com.zhidejiaoyu.student.syntax.service.LearnSyntaxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author: wuchenxi
 * @Date: 2019/10/31 17:52
 */
@Service("writeSyntaxService")
public class WriteSyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements LearnSyntaxService {

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private SyntaxRedisOpt syntaxRedisOpt;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxMemoryDifficulty syntaxMemoryDifficulty;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private SaveLearnInfo saveLearnInfo;

    @Resource
    private WriteNeedView writeNeedView;

    @Resource
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse getLearnSyntax(Long unitId) {

        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.WRITE_SYNTAX);
        int total = syntaxRedisOpt.getTotalSyntaxContentWithUnitId(unitId, SyntaxModelNameConstant.WRITE_SYNTAX);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.WRITE_SYNTAX)
                .build();

        ServerResponse studyCapacity = writeNeedView.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse newSyntaxTopic = this.getNewSyntaxTopic(dto);
        if (!Objects.isNull(newSyntaxTopic)) {
            return newSyntaxTopic;
        }

        ServerResponse serverResponse = writeNeedView.getNextNotGoldTime(dto);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元写语法模块内容都已掌握，进入下一个单元或者完成当前课程
        if (initNextUnitOrCourse(student)) {
            return ServerResponse.createBySuccess(ResponseCode.COURSE_FINISH);
        }
        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
    }

    private boolean initNextUnitOrCourse(Student student) {
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(), 6);
        if (capacityStudentUnit != null) {
            if (Objects.equals(capacityStudentUnit.getUnitId(), capacityStudentUnit.getEndunit())) {
                // 当前课程学习完毕
                // 将当前课程的学习记录置为已学习
                learnMapper.updateSyntaxToLearnedByCourseId(capacityStudentUnit.getStudentId(), capacityStudentUnit.getCourseId());
                // 清除学生语法记忆追踪信息
                studyCapacityMapper.deleteSyntaxByStudentIdAndCourseId(capacityStudentUnit.getStudentId(), capacityStudentUnit.getCourseId());
                return true;
            }
            capacityStudentUnit.setUnitId(capacityStudentUnit.getUnitId() + 1);
            capacityStudentUnitMapper.updateById(capacityStudentUnit);
        }
        return false;
    }

    @Override
    public ServerResponse saveLearnSyntax(Learn learn, Boolean known) {
        return null;
    }


    /**
     * 获取下一个需要学习的内容
     *
     * @param dto
     * @return
     */
    private ServerResponse getNewSyntaxTopic(NeedViewDTO dto) {
        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectNextByUnitIdAndType(dto.getStudentId(), dto.getUnitId(), SyntaxModelNameConstant.WRITE_SYNTAX);
        if (!Objects.isNull(syntaxTopic)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(syntaxTopic.getId());
            return ServerResponse.createBySuccess(WriteSyntaxVO.builder()
                    .answer(syntaxTopic.getAnswer())
                    .content(knowledgePoint.getContent())
                    .memoryDifficult(0)
                    .memoryStrength(0)
                    .plan(dto.getPlan())
                    .studyNew(true)
                    .id(syntaxTopic.getId())
                    .syntaxName(knowledgePoint.getName())
                    .title(syntaxTopic.getTopic().replace("$&$", "___"))
                    .total(dto.getTotal())
                    .build());
        }
        return null;
    }
}

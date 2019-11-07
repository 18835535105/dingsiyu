package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.game.GameVO;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.needview.SelectNeedView;
import com.zhidejiaoyu.student.syntax.savelearn.SaveLearnInfo;
import com.zhidejiaoyu.student.syntax.service.LearnSyntaxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 选语法
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 17:52
 */
@Service("selectSyntaxService")
public class SelectSyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements LearnSyntaxService {

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

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
    private SelectNeedView selectNeedView;

    @Override
    public ServerResponse getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.SELECT_SYNTAX);
        int total = syntaxRedisOpt.getTotalSyntaxContentWithUnitId(unitId, SyntaxModelNameConstant.SELECT_SYNTAX);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.SELECT_SYNTAX)
                .build();

        ServerResponse studyCapacity = selectNeedView.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse newSyntaxTopic = this.getNewSyntaxTopic(dto);
        if (!Objects.isNull(newSyntaxTopic)) {
            return newSyntaxTopic;
        }

        // 获取没有达到黄金记忆点的语法内容
        ServerResponse serverResponse = selectNeedView.getNextNotGoldTime(dto);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元学语法模块内容都已掌握，进入语法游戏模块
        LearnSyntaxServiceImpl.packageStudentStudySyntax(unitId, student, SyntaxModelNameConstant.GAME, studentStudySyntaxMapper, syntaxUnitMapper, learnMapper);

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveLearnSyntax(Learn learn, Boolean known) {
        Student student = super.getStudent(HttpUtil.getHttpSession());
        learn.setStudentId(student.getId());
        learn.setStudyModel(SyntaxModelNameConstant.SELECT_SYNTAX);
        return saveLearnInfo.saveSyntax(learn, known, StudyCapacityTypeConstant.SELECT_SYNTAX);
    }

    private ServerResponse getNewSyntaxTopic(NeedViewDTO dto) {
        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectNextByUnitIdAndType(dto.getStudentId(), dto.getUnitId(), SyntaxModelNameConstant.SELECT_SYNTAX);
        if (!Objects.isNull(syntaxTopic)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(syntaxTopic.getId());
            LearnSyntaxVO knowledgePoint1 = this.packageNewKnowledgePoint(dto, knowledgePoint);
            GameVO selects = selectNeedView.getSelections(syntaxTopic);
            return ServerResponse.createBySuccess(selectNeedView.packageSelectSyntaxVO(knowledgePoint1, selects, syntaxTopic));
        }
        return null;
    }

    /**
     * 封装新学习的知识点内容
     *
     * @param dto
     * @param knowledgePoint
     * @return
     */
    private LearnSyntaxVO packageNewKnowledgePoint(NeedViewDTO dto, KnowledgePoint knowledgePoint) {
        return LearnSyntaxVO.builder()
                .id(knowledgePoint.getId())
                .content(knowledgePoint.getContent())
                .syntaxName(knowledgePoint.getName())
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(true)
                .memoryDifficult(0)
                .memoryStrength(0)
                .build();
    }

}

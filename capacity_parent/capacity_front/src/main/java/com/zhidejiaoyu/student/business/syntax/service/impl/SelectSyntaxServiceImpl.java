package com.zhidejiaoyu.student.business.syntax.service.impl;

import com.zhidejiaoyu.common.dto.syntax.SaveSyntaxDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.vo.syntax.game.GameVO;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.needview.SelectNeedView;
import com.zhidejiaoyu.student.business.syntax.savelearn.SaveLearnInfo;
import com.zhidejiaoyu.student.business.syntax.service.LearnSyntaxService;
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
    private SyntaxRedisOpt syntaxRedisOpt;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private SaveLearnInfo saveLearnInfo;

    @Resource
    private SelectNeedView selectNeedView;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Override
    public ServerResponse<Object> getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, 1,4);

        int plan = learnExtendMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.SELECT_SYNTAX);
        int total = syntaxRedisOpt.getTotalSyntaxContentWithUnitId(unitId, SyntaxModelNameConstant.SELECT_SYNTAX);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .group(learnNew.getGroup())
                .type(StudyCapacityTypeConstant.SELECT_SYNTAX)
                .build();

        ServerResponse<Object> studyCapacity = selectNeedView.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse<Object> newSyntaxTopic = this.getNewSyntaxTopic(dto);
        if (!Objects.isNull(newSyntaxTopic)) {
            return newSyntaxTopic;
        }

        // 获取没有达到黄金记忆点的语法内容
        ServerResponse<Object> serverResponse = selectNeedView.getNextNotGoldTime(dto);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveLearnSyntax(SaveSyntaxDTO dto) {
        Student student = super.getStudent(HttpUtil.getHttpSession());
        dto.setStudent(student);
        dto.setStudyModel(SyntaxModelNameConstant.SELECT_SYNTAX);
        dto.setEasyOrHard(1);
        dto.setType(StudyCapacityTypeConstant.SELECT_SYNTAX);
        return saveLearnInfo.saveSyntax(dto);
    }

    private ServerResponse<Object> getNewSyntaxTopic(NeedViewDTO dto) {
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
                .content(LearnSyntaxServiceImpl.getContent(knowledgePoint))
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(true)
                .memoryDifficult(0)
                .memoryStrength(0)
                .build();
    }

}

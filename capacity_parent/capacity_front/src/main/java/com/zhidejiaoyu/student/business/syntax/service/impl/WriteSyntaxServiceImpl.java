package com.zhidejiaoyu.student.business.syntax.service.impl;

import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.dto.syntax.SaveSyntaxDTO;
import com.zhidejiaoyu.common.mapper.KnowledgePointMapper;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.mapper.SyntaxUnitMapper;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.pojo.SyntaxUnit;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.syntax.TopicVO;
import com.zhidejiaoyu.common.vo.syntax.WriteSyntaxVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.needview.WriteNeedView;
import com.zhidejiaoyu.student.business.syntax.savelearn.SaveLearnInfo;
import com.zhidejiaoyu.student.business.syntax.service.LearnSyntaxService;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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
    private ExecutorService executorService;

    @Resource
    private MedalAwardAsync medalAwardAsync;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnExtendMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.WRITE_SYNTAX);
        int total = syntaxRedisOpt.getTotalSyntaxContentWithUnitId(unitId, SyntaxModelNameConstant.WRITE_SYNTAX);

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

        this.saveMonsterMedal(student, unitId);
        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
    }

    /**
     * 奖励怪物征服奖章
     *
     * @param student
     * @param unitId
     */
    private void saveMonsterMedal(Student student, Long unitId) {
        executorService.execute(() -> {
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            medalAwardAsync.monsterMedal(student, syntaxUnit.getCourseId());
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveLearnSyntax(SaveSyntaxDTO dto) {
        Student student = super.getStudent(HttpUtil.getHttpSession());
        dto.setStudent(student);
        dto.setStudyModel(SyntaxModelNameConstant.WRITE_SYNTAX);
        dto.setEasyOrHard(2);
        dto.setType(StudyCapacityTypeConstant.WRITE_SYNTAX);
        return saveLearnInfo.saveSyntax(dto);
    }


    /**
     * 获取下一个需要学习的内容
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> getNewSyntaxTopic(NeedViewDTO dto) {
        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectNextByUnitIdAndType(dto.getStudentId(), dto.getUnitId(),
                SyntaxModelNameConstant.WRITE_SYNTAX);
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
                    .analysis(syntaxTopic.getWriteAnalysis())
                    .build());
        }
        return null;
    }
}

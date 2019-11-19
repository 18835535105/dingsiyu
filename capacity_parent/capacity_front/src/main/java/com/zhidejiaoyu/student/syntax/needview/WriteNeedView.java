package com.zhidejiaoyu.student.syntax.needview;

import com.zhidejiaoyu.common.Vo.syntax.TopicVO;
import com.zhidejiaoyu.common.Vo.syntax.WriteSyntaxVO;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.KnowledgePointMapper;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.syntax.service.impl.LearnSyntaxServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 获取写语法需要复习的内容
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 10:16
 */
@Component
public class WriteNeedView implements INeedView {

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private SyntaxMemoryDifficulty syntaxMemoryDifficulty;

    @Override
    public ServerResponse getNeedView(NeedViewDTO dto) {
        StudyCapacity studyCapacity = studyCapacityMapper.selectLargerThanGoldTimeWithStudentIdAndUnitId(dto);
        return this.packageWriteSyntaxNeedView(dto, studyCapacity);
    }

    /**
     * 获取没有达到黄金记忆点的知识点
     *
     * @param dto
     * @return
     */
    @Override
    public ServerResponse getNextNotGoldTime(NeedViewDTO dto) {
        // 获取没有达到黄金记忆点的语法内容
        StudyCapacity studyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        return this.packageWriteSyntaxNeedView(dto, studyCapacity);
    }

    private ServerResponse packageWriteSyntaxNeedView(NeedViewDTO dto, StudyCapacity studyCapacity) {

        if (!Objects.isNull(studyCapacity)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(studyCapacity.getWordId());

            SyntaxTopic syntaxTopic = syntaxTopicMapper.selectById(studyCapacity.getWordId());

            return ServerResponse.createBySuccess(WriteSyntaxVO.builder()
                    .knowledgePoint(LearnSyntaxServiceImpl.getContent(knowledgePoint))
                    .topic(TopicVO.builder()
                            .answer(syntaxTopic.getAnswer())
                            .title(syntaxTopic.getTopic())
                            .build())
                    .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                    .memoryStrength(getMemoryStrength(studyCapacity))
                    .plan(dto.getPlan())
                    .studyNew(false)
                    .id(syntaxTopic.getId())
                    .total(dto.getTotal())
                    .build());
        }
        return null;
    }
}

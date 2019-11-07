package com.zhidejiaoyu.student.syntax.needview;

import com.zhidejiaoyu.common.Vo.syntax.KnowledgePointVO;
import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.SelectSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.game.GameSelect;
import com.zhidejiaoyu.common.Vo.syntax.game.GameVO;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.KnowledgePointMapper;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.zhidejiaoyu.student.syntax.service.impl.SyntaxGameServiceImpl.getGameSelects;

/**
 * 获取选语法需要复习的内容
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 10:09
 */
@Component
public class SelectNeedView implements INeedView {

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
        return this.packageSelectSyntaxNeedView(dto, studyCapacity);
    }

    @Override
    public ServerResponse getNextNotGoldTime(NeedViewDTO dto) {
        StudyCapacity studyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        return this.packageSelectSyntaxNeedView(dto, studyCapacity);
    }

    /**
     * 获取选语法需要复习的数据
     *
     * @param dto
     * @param studyCapacity
     * @return
     */
    private ServerResponse packageSelectSyntaxNeedView(NeedViewDTO dto, StudyCapacity studyCapacity) {
        if (!Objects.isNull(studyCapacity)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(studyCapacity.getWordId());

            SyntaxTopic syntaxTopic = syntaxTopicMapper.selectById(studyCapacity.getWordId());

            LearnSyntaxVO knowledgePoint1 = this.getSelectSyntaxKnowledgePoint(dto, studyCapacity, knowledgePoint);
            GameVO selects = getSelections(syntaxTopic);
            return ServerResponse.createBySuccess(this.packageSelectSyntaxVO(knowledgePoint1, selects, syntaxTopic));
        }
        return null;
    }

    /**
     * 封装选语法中知识点内容
     *
     * @param dto
     * @param studyCapacity
     * @param knowledgePoint
     * @return
     */
    private LearnSyntaxVO getSelectSyntaxKnowledgePoint(NeedViewDTO dto, StudyCapacity studyCapacity, KnowledgePoint knowledgePoint) {
        return LearnSyntaxVO.builder()
                .id(studyCapacity.getWordId())
                .content(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getContent())
                .syntaxName(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getName())
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(false)
                .memoryStrength(getMemoryStrength(studyCapacity))
                .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                .build();
    }

    /**
     * 封装试题选项
     *
     * @param syntaxTopic 当前语法题
     * @return
     */
    public GameVO getSelections(SyntaxTopic syntaxTopic) {
        return new GameVO(getTopic(syntaxTopic), this.packageSelectAnswer(syntaxTopic));
    }

    /**
     * 封装选语法需要返回的数据
     *
     * @param knowledgePoint
     * @param selects
     * @param syntaxTopic
     * @return
     */
    public Object packageSelectSyntaxVO(LearnSyntaxVO knowledgePoint, GameVO selects, SyntaxTopic syntaxTopic) {
        return SelectSyntaxVO.builder()
                .id(syntaxTopic.getId())
                .total(knowledgePoint.getTotal())
                .plan(knowledgePoint.getPlan())
                .studyNew(knowledgePoint.getStudyNew())
                .memoryDifficult(knowledgePoint.getMemoryDifficult())
                .memoryStrength(knowledgePoint.getMemoryStrength())
                .selects(selects)
                .knowledgePoint(KnowledgePointVO.builder()
                        .content(knowledgePoint.getContent())
                        .syntaxName(knowledgePoint.getSyntaxName())
                        .build())
                .build();
    }

    /**
     * 封装选语法选项
     *
     * @param syntaxTopic
     * @return
     */
    private List<GameSelect> packageSelectAnswer(SyntaxTopic syntaxTopic) {
        return getGameSelects(syntaxTopic);
    }
}

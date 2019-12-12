package com.zhidejiaoyu.student.syntax.needview;

import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.syntax.service.impl.LearnSyntaxServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 获取学语法需要复习的内容
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 13:43
 */
@Component
public class LearnNeedView implements INeedView {

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private SyntaxMemoryDifficulty syntaxMemoryDifficulty;

    @Override
    public ServerResponse getNeedView(NeedViewDTO dto) {
        StudyCapacity studyCapacity = studyCapacityMapper.selectLargerThanGoldTimeWithStudentIdAndUnitId(dto);
        return this.packageNeedViewLearnSyntax(studyCapacity, dto);
    }

    @Override
    public ServerResponse getNextNotGoldTime(NeedViewDTO dto) {
        StudyCapacity nextStudyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        return this.packageNeedViewLearnSyntax(nextStudyCapacity, dto);
    }

    /**
     * 封装需要复习的内容
     *
     * @param studyCapacity
     * @param dto
     * @return
     */
    private ServerResponse packageNeedViewLearnSyntax(StudyCapacity studyCapacity, NeedViewDTO dto) {
        if (!Objects.isNull(studyCapacity)) {
            return ServerResponse.createBySuccess(LearnSyntaxVO.builder()
                    .id(studyCapacity.getWordId())
                    .content(LearnSyntaxServiceImpl.getContent(KnowledgePoint.builder()
                            .content(studyCapacity.getWordChinese())
                            .name(studyCapacity.getWord())
                            .build()))
                    .total(dto.getTotal())
                    .plan(Math.min(dto.getPlan(), dto.getTotal()))
                    .studyNew(false)
                    .memoryStrength((int) Math.round(studyCapacity.getMemoryStrength() * 100))
                    .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                    .build());
        }
        return null;
    }
}

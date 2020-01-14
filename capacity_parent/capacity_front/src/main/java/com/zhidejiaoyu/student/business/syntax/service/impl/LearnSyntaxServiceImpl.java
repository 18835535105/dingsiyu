package com.zhidejiaoyu.student.business.syntax.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.dto.syntax.SaveSyntaxDTO;
import com.zhidejiaoyu.common.mapper.KnowledgePointMapper;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.syntax.KnowledgePointVO;
import com.zhidejiaoyu.common.vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.needview.LearnNeedView;
import com.zhidejiaoyu.student.business.syntax.savelearn.SaveLearnInfo;
import com.zhidejiaoyu.student.business.syntax.service.LearnSyntaxService;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学语法
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 17:43
 */
@Service("learnSyntaxService")
public class LearnSyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements LearnSyntaxService {

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private SyntaxRedisOpt syntaxRedisOpt;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SaveLearnInfo saveLearnInfo;

    @Resource
    private LearnNeedView learnNeedView;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Override
    public ServerResponse<Object> getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        Integer easyOrHard = 1;
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, easyOrHard, 4);
        int plan = learnExtendMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.LEARN_SYNTAX);
        int total = syntaxRedisOpt.getTotalKnowledgePointWithUnitId(unitId);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.LEARN_SYNTAX)
                .group(learnNew.getGroup())
                .build();

        ServerResponse<Object> studyCapacity = learnNeedView.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse<Object> knowledgePoint = this.getNewKnowledgePoint(dto);
        if (!Objects.isNull(knowledgePoint)) {
            return knowledgePoint;
        }

        // 获取没有达到黄金记忆点的生知识点
        ServerResponse<Object> serverResponse = learnNeedView.getNextNotGoldTime(dto);
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
        dto.setStudyModel(SyntaxModelNameConstant.LEARN_SYNTAX);
        dto.setEasyOrHard(1);
        dto.setType(StudyCapacityTypeConstant.LEARN_SYNTAX);
        return saveLearnInfo.saveSyntax(dto);
    }

    /**
     * 获取下一个知识点内容
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> getNewKnowledgePoint(NeedViewDTO dto) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectNextByUnitId(dto.getStudentId(), dto.getUnitId(), dto.getGroup());
        if (!Objects.isNull(knowledgePoint)) {
            return ServerResponse.createBySuccess(this.packageNewKnowledgePoint(dto, knowledgePoint));
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
                .content(getContent(knowledgePoint))
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(true)
                .memoryDifficult(0)
                .memoryStrength(0)
                .build();
    }

    public static KnowledgePointVO getContent(KnowledgePoint knowledgePoint) {
        return KnowledgePointVO.builder()
                .syntaxContent(getContent(knowledgePoint.getContent()))
                .syntaxName(knowledgePoint.getName())
                .build();
    }

    public static List<KnowledgePointVO.SyntaxContent> getContent(String content) {
        // 包含【】##内容
        String[] split = content.split("\\$\\$");
        return Arrays.stream(split).filter(StringUtils::isNotEmpty).map(str -> {
            // 分割成【】   内容
            String[] split1 = str.split("##");
            return KnowledgePointVO.SyntaxContent.builder()
                    .content(split1.length > 1 ? split1[1] : split1[0])
                    .title(split1.length > 1 ? split1[0] : "")
                    .build();
        }).collect(Collectors.toList());
    }
}

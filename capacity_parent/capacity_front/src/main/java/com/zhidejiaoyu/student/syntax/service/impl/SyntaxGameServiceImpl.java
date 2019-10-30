package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.GameVO;
import com.zhidejiaoyu.common.Vo.syntax.game.GameSelect;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.service.impl.TestServiceImpl;
import com.zhidejiaoyu.student.syntax.constant.SyntaxModelNameConstant;
import com.zhidejiaoyu.student.syntax.service.SyntaxGameService;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import com.zhidejiaoyu.student.utils.PetUrlUtil;
import com.zhidejiaoyu.student.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 语法游戏
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 17:14
 */
@Slf4j
@Service
public class SyntaxGameServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxGameService {

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private PetSayUtil petSayUtil;

    @Override
    public ServerResponse getSyntaxGame(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        List<SyntaxTopic> syntaxTopics = syntaxTopicMapper.selectByUnitId(unitId);
        if (CollectionUtils.isEmpty(syntaxTopics)) {
            this.printGetSyntaxGameNoDataLog(unitId);
            return ServerResponse.createByError(500, "未查询到数据！");
        }
        // todo: 有没有可能单元下的语法题不足10个题
        Collections.shuffle(syntaxTopics);

        List<GameVO> returnList = syntaxTopics.parallelStream().limit(10)
                .map(syntaxTopic -> new GameVO(syntaxTopic.getTopic().replace("$&$", "___"), this.getSelect(syntaxTopic, syntaxTopics)))
                .collect(Collectors.toList());

        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveSyntaxGame(TestRecord testRecord) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentId(student.getId(), testRecord.getUnitId());

        // 是否是第一次学习当前单元
        boolean isFirst = false;
        if (Objects.isNull(studentStudySyntax)) {
            // 说明学生还没有学习过当前单元
            isFirst = true;
            this.saveStudentStudySyntax(testRecord, student);
        } else {
            studentStudySyntax.setModel(this.getNextModelNotFirstLearn(testRecord));
            studentStudySyntaxMapper.updateById(studentStudySyntax);
        }

        TestResultVo vo = this.getTestResultVo(testRecord, student, isFirst);

        // 保存测试记录
        this.saveTestRecord(testRecord, student, vo.getGold());

        return ServerResponse.createBySuccess(vo);
    }

    private TestResultVo getTestResultVo(TestRecord testRecord, Student student, boolean isFirst) {
        // 奖励金币， 能量
        int awardGold = 0;
        int awardEnergy = 0;
        if (testRecord.getPoint() >= PointConstant.FIFTY) {
            if (testRecord.getPoint() < PointConstant.SEVENTY) {
                awardEnergy = awardGold = 1;
            } else {
                awardEnergy = awardGold = 3;
            }
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
            student.setEnergy(awardEnergy);
            studentMapper.updateById(student);
        }

        TestResultVo vo = new TestResultVo();
        // 首次学习70分及格；非首次学习80分及格
        vo.setMsg(this.getMessage(student, vo, testRecord, isFirst ? PointConstant.SEVENTY : PointConstant.EIGHTY));
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, testRecord.getPoint(), GenreConstant.UNIT_TEST));
        vo.setGold(awardGold);
        vo.setEnergy(awardEnergy);
        return vo;
    }

    /**
     * 根据学生分数获取响应的说明语
     *
     * @param student
     * @param vo
     * @param testRecord
     * @param pass       及格分数
     * @return
     */
    private String getMessage(Student student, TestResultVo vo, TestRecord testRecord, int pass) {
        return TestServiceImpl.getTestMessage(student, vo, testRecord, pass, petSayUtil);
    }


    private void saveTestRecord(TestRecord testRecord, Student student, int awardGold) {
        testRecord.setAwardGold(awardGold);
        testRecord.setGenre(GenreConstant.SYNTAX_GAME);
        testRecord.setHistoryBadPoint(testRecord.getPoint());
        testRecord.setHistoryBestPoint(testRecord.getPoint());
        testRecord.setQuantity(testRecord.getRightCount() + testRecord.getErrorCount());
        testRecord.setStudyModel(StudyModelConstant.SYNTAX_GAME);
        testRecord.setTestEndTime(new Date());
        testRecord.setTestStartTime((Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME));
        testRecord.setStudentId(student.getId());
        testRecordMapper.insert(testRecord);
    }

    /**
     * 保存学生学习的下个模块记录
     *
     * @param testRecord
     * @param student
     */
    private void saveStudentStudySyntax(TestRecord testRecord, Student student) {
        studentStudySyntaxMapper.insert(StudentStudySyntax.builder()
                .courseId(testRecord.getCourseId())
                .studentId(student.getId())
                .unitId(testRecord.getUnitId())
                .model(this.getNextModelFirstLearn(testRecord))
                .updateTime(new Date())
                .build());
    }

    /**
     * 不是第一次学习当前单元的时候，获取下一个学习模块
     *
     * @param testRecord
     * @return
     */
    private String getNextModelNotFirstLearn(TestRecord testRecord) {
        if (testRecord.getPoint() < PointConstant.THIRTY) {
            return SyntaxModelNameConstant.LEARN_SYNTAX;
        }
        if (testRecord.getPoint() >= PointConstant.EIGHTY) {
            return SyntaxModelNameConstant.WRITE_SYNTAX;
        }
        return SyntaxModelNameConstant.SELECT_SYNTAX;
    }

    /**
     * 首次学习单元，游戏之后判断下个学习模块
     *
     * @param testRecord
     * @return
     */
    private String getNextModelFirstLearn(TestRecord testRecord) {
        if (testRecord.getPoint() == PointConstant.HUNDRED) {
            return SyntaxModelNameConstant.WRITE_SYNTAX;
        }
        if (testRecord.getPoint() >= PointConstant.SEVENTY) {
            return SyntaxModelNameConstant.SELECT_SYNTAX;
        }
        return SyntaxModelNameConstant.LEARN_SYNTAX;

    }

    /**
     * 封装游戏选项
     *
     * @param syntaxTopic  当前正确的语法题信息
     * @param syntaxTopics 当前单元所有的语法题信息
     * @return
     */
    private List<GameSelect> getSelect(SyntaxTopic syntaxTopic, List<SyntaxTopic> syntaxTopics) {
        Collections.shuffle(syntaxTopics);
        // 添加错误答案
        List<GameSelect> collect = syntaxTopics.parallelStream()
                // 错误答案排除正确答案的内容
                .filter(syntaxTopic1 -> !Objects.equals(syntaxTopic.getId(), syntaxTopic1.getId()))
                .limit(3)
                .map(syntaxTopic1 -> new GameSelect(syntaxTopic1.getAnswer(), false))
                .collect(Collectors.toList());
        // 添加正确答案
        collect.add(new GameSelect(syntaxTopic.getAnswer(), true));
        Collections.shuffle(collect);
        return collect;
    }

    /**
     * 语法单元没有数据时打印错误日志
     *
     * @param unitId 语法单元id
     */
    private void printGetSyntaxGameNoDataLog(Long unitId) {
        SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
        if (syntaxUnit == null) {
            log.error("未查询到id=[{}]的语法单元！", unitId);
        } else {
            log.error("语法单元[{}]没有对应的语法内容！", syntaxUnit.getJointName());
        }
    }

}

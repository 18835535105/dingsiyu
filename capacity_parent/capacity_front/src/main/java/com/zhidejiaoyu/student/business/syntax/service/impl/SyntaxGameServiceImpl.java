package com.zhidejiaoyu.student.business.syntax.service.impl;

import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.goldUtil.GoldUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.common.utils.pet.PetUrlUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.TestResultVo;
import com.zhidejiaoyu.common.vo.syntax.game.GameSelect;
import com.zhidejiaoyu.common.vo.syntax.game.GameVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.test.service.impl.TestServiceImpl;
import com.zhidejiaoyu.student.business.syntax.service.SyntaxGameService;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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

    /**
     * 游戏题目数量
     */
    private final int GAME_COUNT = 10;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private PetSayUtil petSayUtil;

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Resource
    private BaiduSpeak baiduSpeak;

    @Override
    public ServerResponse<Object> getSyntaxGame(Long unitId) {
        Student student = super.getStudent(HttpUtil.getHttpSession());
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 判断当前学生学习计划是否为已完成状态，如果是已完成状态，置为未完成状态
        this.updateStudentStudyPlanToUnComplete(unitId, student);

        List<SyntaxTopic> syntaxTopics = this.getSyntaxTopics(unitId);
        if (CollectionUtils.isEmpty(syntaxTopics)) {
            this.printGetSyntaxGameNoDataLog(unitId);
            return ServerResponse.createByError(500, "未查询到数据！");
        }
        List<GameVO> returnList = this.packageSyntaxTopics(syntaxTopics).parallelStream().limit(GAME_COUNT)
                .map(syntaxTopic -> new GameVO(replace(syntaxTopic), this.getSelect(syntaxTopic),baiduSpeak.getLanguagePath(replace(syntaxTopic))))
                .collect(Collectors.toList());

        return ServerResponse.createBySuccess(returnList);
    }

    /**
     * 替换选择题中的$&$占位符
     *
     * @param syntaxTopic
     * @return
     */
    public static String replace(SyntaxTopic syntaxTopic) {
        String topic = StringUtil.replaceSpecialSpaceToNormalSpace(syntaxTopic.getTopic());
        return topic.startsWith("$")
                ? topic.replace("$&$", "___ ")
                : topic.replace("$&$", " ___ ");
    }

    private void updateStudentStudyPlanToUnComplete(Long unitId, Student student) {
        UnitNew unitNew = unitNewMapper.selectById(unitId);
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selectByStudentIdAndCourseId(student.getId(), unitNew.getCourseId(), 7);
        int competeState = 2;
        if (CollectionUtils.isNotEmpty(studentStudyPlans) && Objects.equals(studentStudyPlans.get(0).getComplete(), competeState)) {
            StudentStudyPlan studentStudyPlan = studentStudyPlans.get(0);
            studentStudyPlan.setComplete(1);
            studentStudyPlanMapper.updateById(studentStudyPlan);
        }
    }

    /**
     * 防止选项不够4个
     *
     * @param syntaxTopics
     * @return
     */
    private List<SyntaxTopic> packageSyntaxTopics(List<SyntaxTopic> syntaxTopics) {
        return syntaxTopics.stream().map(this::getSyntaxTopic).collect(Collectors.toList());
    }

    private SyntaxTopic getSyntaxTopic(SyntaxTopic syntaxTopic) {
        int length = syntaxTopic.getOption().split("\\$&\\$").length;
        // 选项个数
        int optionCount = 3;
        if (length < optionCount) {
            syntaxTopic.setOption(syntaxTopic.getOption() + " $&$");
            this.getSyntaxTopic(syntaxTopic);
        }
        return syntaxTopic;
    }

    /**
     * 获取单元下的语法题，不足10个重复学习
     *
     * @param unitId
     * @return
     */
    private List<SyntaxTopic> getSyntaxTopics(Long unitId) {
        List<SyntaxTopic> syntaxTopics = syntaxTopicMapper.selectSelectSyntaxByUnitId(unitId);
        if (CollectionUtils.isEmpty(syntaxTopics)) {
            UnitNew unitNew = unitNewMapper.selectById(unitId);
            log.error("语法单元[{} - {}]没有没有选语法题目！", unitNew.getId(), unitNew.getJointName());
            throw new ServiceException(500, "未查询到游戏题目！");
        }
        List<SyntaxTopic> result = new ArrayList<>(syntaxTopics);
        return this.syntaxTopicsResult(syntaxTopics, result);

    }

    /**
     * 防止题目不足10个
     *
     * @param syntaxTopics
     * @param result
     * @return
     */
    private List<SyntaxTopic> syntaxTopicsResult(List<SyntaxTopic> syntaxTopics, List<SyntaxTopic> result) {
        Collections.shuffle(result);
        if (result.size() < GAME_COUNT) {
            result.addAll(syntaxTopics);
            this.syntaxTopicsResult(syntaxTopics, result);
        }
        return result;
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveSyntaxGame(TestRecord testRecord) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), testRecord.getUnitId());

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

            student.setEnergy(awardEnergy);
            awardGold = GoldUtil.addStudentGold(student, awardGold);
            GoldLogUtil.saveStudyGoldLog(student.getId(), SyntaxModelNameConstant.GAME, awardGold);
        }

        TestResultVo vo = new TestResultVo();
        // 首次学习70分及格；非首次学习80分及格
        vo.setMsg(this.getMessage(student, vo, testRecord, isFirst ? PointConstant.SEVENTY : PointConstant.EIGHTY));
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, testRecord.getPoint(), GenreConstant.UNIT_TEST, null));
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
     * @param syntaxTopic 当前正确的语法题信息
     * @return
     */
    private List<GameSelect> getSelect(SyntaxTopic syntaxTopic) {
        return getGameSelects(syntaxTopic);
    }

    public static List<GameSelect> getGameSelects(SyntaxTopic syntaxTopic) {
        String[] options = syntaxTopic.getOption().split("\\$&\\$");
        List<GameSelect> select = Arrays.stream(options).map(option -> new GameSelect(option.trim(), false)).collect(Collectors.toList());
        select.add(new GameSelect(syntaxTopic.getAnswer().trim(), true));
        Collections.shuffle(select);
        return select;
    }

    /**
     * 语法单元没有数据时打印错误日志
     *
     * @param unitId 语法单元id
     */
    private void printGetSyntaxGameNoDataLog(Long unitId) {
        UnitNew unitNew = unitNewMapper.selectById(unitId);
        if (unitNew == null) {
            log.error("未查询到id=[{}]的语法单元！", unitId);
        } else {
            log.error("语法单元[{}]没有对应的语法内容！", unitNew.getJointName());
        }
    }

}

package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.SelectSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.game.GameSelect;
import com.zhidejiaoyu.common.Vo.syntax.game.GameVO;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.study.memorystrength.SyntaxMemoryStrength;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.constant.GradeNameConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 语法实现类
 *
 * @author liumaoyu
 * @date 2019-10-30
 */
@Service
public class SyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxService {

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;

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
    private SyntaxMemoryStrength syntaxMemoryStrength;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    /**
     * 获取学生学习课程
     *
     * @param session
     * @return
     */
    @Override
    public Object getStudyCourse(HttpSession session) {
        Student student = getStudent(session);
        //获取所有语法课程数据
        List<Map<String, Object>> studyList = studentStudyPlanMapper.selectSyntaxByStudentAndType(student.getId());
        //获取学生所有语法课程学习记录
        Map<Long, Map<String, Object>> longStudentStudySyntaxMap = studentStudySyntaxMapper.selectStudyAllByStudentId(student.getId());
        Map<String, Object> returnMap = new HashMap<>(3);
        List<Map<String, Object>> currentGradeList = new ArrayList<>();
        List<Map<String, Object>> previousGradeList = new ArrayList<>();
        for (Map<String, Object> map : studyList) {
            Map<String, Object> useMap = new HashMap<>(1);
            //添加返回年级及英文年级选项
            String grade = map.get("grade").toString();
            String gradeEnglish = getGradeAndLabelEnglishName(grade);
            String label = map.get("label").toString();
            String labelEnglish = getGradeAndLabelEnglishName(label);
            useMap.put("grade", grade + "(" + label + ")");
            useMap.put("englishGrade", gradeEnglish + "-" + labelEnglish);
            //添加课程id以及单元id名称
            Long courseId = Long.parseLong(map.get("courseId").toString());
            Map<String, Object> studyUnit = longStudentStudySyntaxMap.get(courseId);
            useMap.put("courseId", courseId);
            //判断该单元是否正在学习
            Long unitId;
            if (studyUnit != null) {
                unitId = Long.parseLong(studyUnit.get("unitId").toString());
                useMap.put("model", studyUnit.get("model"));
                useMap.put("battle", 2);
                //计算战斗进度
                useMap.put("combatProgress", getCalculateBattleProgress(courseId, unitId, studyUnit.get("model").toString()));
            } else {
                unitId = Long.parseLong(map.get("startId").toString());
                useMap.put("model", SyntaxModelNameConstant.GAME);
                useMap.put("battle", 1);
                useMap.put("combatProgress", 0);
            }
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            useMap.put("unitId", unitId);
            useMap.put("unitName", syntaxUnit.getUnitName());
            //战斗状态
            int complete = Integer.parseInt(map.get("complete").toString());
            if (complete == 2) {
                useMap.put("battle", 3);
                useMap.put("combatProgress", 100);
            }
            if (grade.equals(student.getGrade())) {
                previousGradeList.add(useMap);
            } else {
                currentGradeList.add(useMap);
            }
        }
        returnMap.put("currentGrade", currentGradeList);
        returnMap.put("previousGrade", previousGradeList);
        returnMap.put("InGrade", student.getGrade());
        return returnMap;
    }

    /**
     * 计算战斗进度
     *
     * @param courseId
     * @param unitId
     * @param model
     */
    private int getCalculateBattleProgress(Long courseId, Long unitId, String model) {
        //获取所有单元id
        List<Long> unitIds = syntaxUnitMapper.selectUnitIdByCourseId(courseId);
        //获取总模块数量
        int modelSize = unitIds.size() * 3;
        //获取已完成模块在所有模块的位置
        int size = 0;
        for (int i = 0; i < unitIds.size(); i++) {
            if (unitIds.get(i).equals(unitId)) {
                size = i + 1;
            }
        }
        int learningSize = (size - 1) * 3;
        if (SyntaxModelNameConstant.SELECT_SYNTAX.equals(model)) {
            learningSize += 1;
        } else if (SyntaxModelNameConstant.WRITE_SYNTAX.equals(model)) {
            learningSize += 2;
        }
        return learningSize / modelSize;
    }


    private String getGradeAndLabelEnglishName(String grade) {
        if (grade == null) {
            return "one";
        }
        if (GradeNameConstant.FIRST_GRADE.equals(grade)) {
            return "one";
        }
        if (GradeNameConstant.SECOND_GRADE.equals(grade)) {
            return "two";
        }
        if (GradeNameConstant.WRITE_GRADE.equals(grade)) {
            return "three";
        }
        if (GradeNameConstant.FOURTH_GRADE.equals(grade)) {
            return "four";
        }
        if (GradeNameConstant.FIFTH_GRADE.equals(grade)) {
            return "five";
        }
        if (GradeNameConstant.SIXTH_GRADE.equals(grade)) {
            return "six";
        }
        if (GradeNameConstant.SEVENTH_GRADE.equals(grade)) {
            return "serven";
        }
        if (GradeNameConstant.EIGHTH_GRADE.equals(grade)) {
            return "eight";
        }
        if (GradeNameConstant.NINTH_GRADE.equals(grade)) {
            return "nine";
        }

        if (GradeNameConstant.SENIOR_ONE.equals(grade)) {
            return "ten";
        }
        if (GradeNameConstant.SENIOR_TWO.equals(grade)) {
            return "eleven";
        }
        if (GradeNameConstant.SENIOR_THREE.equals(grade)) {
            return "twelve";
        }
        if (GradeNameConstant.VOLUME_1.equals(grade)) {
            return "up";
        }
        if (GradeNameConstant.VOLUME_2.equals(grade)) {
            return "down";
        }
        return null;
    }

    @Override
    public ServerResponse getSyntaxNode(Long unitId) {

        Student student = super.getStudent(HttpUtil.getHttpSession());

        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (Objects.isNull(studentStudySyntaxMapper)) {
            return ServerResponse.createBySuccessMessage(SyntaxModelNameConstant.GAME);
        }

        return ServerResponse.createBySuccessMessage(studentStudySyntax.getModel());
    }

    @Override
    public ServerResponse getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.LEARN_SYNTAX);
        int total = syntaxRedisOpt.getTotalKnowledgePointWithUnitId(unitId);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.LEARN_SYNTAX)
                .build();

        ServerResponse studyCapacity = this.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse knowledgePoint = this.getNewKnowledgePoint(dto);
        if (!Objects.isNull(knowledgePoint)) {
            return knowledgePoint;
        }

        // 获取没有达到黄金记忆点的生知识点
        StudyCapacity nextStudyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        ServerResponse serverResponse = this.packageNeedViewLearnSyntax(nextStudyCapacity, dto);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元学语法模块内容都已掌握，进入选语法模块
        this.packageStudentStudySyntax(unitId, student, SyntaxModelNameConstant.SELECT_SYNTAX);

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
    }

    @Override
    public ServerResponse saveLearnSyntax(Learn learn, Boolean known) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        learn.setStudentId(student.getId());
        learn.setStudyModel(SyntaxModelNameConstant.LEARN_SYNTAX);
        Learn learned = learnMapper.selectLearnedSyntaxByUnitIdAndStudyModelAndWordId(learn);
        if (Objects.isNull(learned)) {
            this.saveFirstLearn(learn, known);
        } else {
            this.updateNotFirstLearn(known, learned);
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getSelectSyntax(Long unitId) {
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

        ServerResponse studyCapacity = this.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse newSyntaxTopic = this.getNewSyntaxTopic(dto);
        if (!Objects.isNull(newSyntaxTopic)) {
            return newSyntaxTopic;
        }

        // 获取没有达到黄金记忆点的语法内容
        StudyCapacity nextStudyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        ServerResponse serverResponse = this.packageSelectSyntaxNeedView(dto, nextStudyCapacity);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元学语法模块内容都已掌握，进入语法游戏模块
        this.packageStudentStudySyntax(unitId, student, SyntaxModelNameConstant.GAME);

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);

    }

    private ServerResponse getNewSyntaxTopic(NeedViewDTO dto) {
        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectNextByUnitIdAndType(dto.getStudentId(), dto.getUnitId(), SyntaxModelNameConstant.SELECT_SYNTAX);
        if (!Objects.isNull(syntaxTopic)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(syntaxTopic.getId());
            LearnSyntaxVO knowledgePoint1 = this.packageNewKnowledgePoint(dto, knowledgePoint);
            GameVO selects = this.getGameVO(dto.getUnitId(), syntaxTopic);
            return ServerResponse.createBySuccess(this.packageSelectSyntaxVO(knowledgePoint1, selects));
        }
        return null;
    }

    /**
     * 非第一次学习，更新学习记录及记忆追踪信息
     *
     * @param known
     * @param learned
     */
    private void updateNotFirstLearn(Boolean known, Learn learned) {
        // 非首次学习
        StudyCapacity studyCapacity = studyCapacityMapper.selectByLearn(learned, StudyCapacityTypeConstant.LEARN_SYNTAX);
        if (Objects.isNull(studyCapacity)) {
            // 保存记忆追踪
            this.initStudyCapacity(learned);
        } else {
            studyCapacity.setMemoryStrength(syntaxMemoryStrength.getMemoryStrength(studyCapacity.getMemoryStrength(), known));
            studyCapacity.setPush(GoldMemoryTime.getGoldMemoryTime(studyCapacity.getMemoryStrength(), new Date()));
            studyCapacity.setUpdateTime(new Date());
            if (!known) {
                studyCapacity.setFaultTime(studyCapacity.getFaultTime() + 1);
            }
            studyCapacityMapper.updateById(studyCapacity);
        }

        learned.setStudyCount(learned.getStudyCount() + 1);
        learned.setUpdateTime(new Date());
        learnMapper.updateById(learned);
    }

    /**
     * 第一次学习，新增学习记录、记忆追踪信息
     *
     * @param learn
     * @param known
     */
    private void saveFirstLearn(Learn learn, Boolean known) {
        // 首次学习
        learn.setLearnTime((Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyCount(1);
        learn.setUpdateTime(new Date());
        if (known) {
            learn.setStatus(1);
            learn.setFirstIsKnown(1);
        } else {
            // 保存记忆追踪
            this.initStudyCapacity(learn);

            learn.setStatus(0);
            learn.setFirstIsKnown(0);
        }
        learnMapper.insert(learn);
    }

    private void initStudyCapacity(Learn learn) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectById(learn.getVocabularyId());
        studyCapacityMapper.insert(StudyCapacity.builder()
                .courseId(learn.getCourseId())
                .unitId(learn.getUnitId())
                .studentId(learn.getStudentId())
                .faultTime(1)
                .memoryStrength(0.38)
                .push(GoldMemoryTime.getGoldMemoryTime(0.38, new Date()))
                .updateTime(new Date())
                .type(StudyCapacityTypeConstant.LEARN_SYNTAX)
                .word(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getName())
                .wordChinese(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getContent())
                .wordId(learn.getVocabularyId())
                .build());
    }

    /**
     * 初始化下个模块的数据
     *
     * @param unitId
     * @param student
     * @param nextModelName 下个学习模块名称
     */
    private void packageStudentStudySyntax(Long unitId, Student student, String nextModelName) {
        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (!Objects.isNull(studentStudySyntax)) {
            studentStudySyntax.setModel(nextModelName);
            studentStudySyntax.setUpdateTime(new Date());
            studentStudySyntaxMapper.updateById(studentStudySyntax);
        } else {
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            studentStudySyntaxMapper.insert(StudentStudySyntax.builder()
                    .updateTime(new Date())
                    .model(nextModelName)
                    .unitId(unitId)
                    .studentId(student.getId())
                    .courseId(!Objects.isNull(syntaxUnit) ? syntaxUnit.getCourseId() : null)
                    .build());
        }
    }

    /**
     * 获取下一个知识点内容
     *
     * @param dto
     * @return
     */
    private ServerResponse getNewKnowledgePoint(NeedViewDTO dto) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectNextByUnitId(dto.getStudentId(), dto.getUnitId());
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
                .content(knowledgePoint.getContent())
                .syntaxName(knowledgePoint.getName())
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(true)
                .memoryDifficult(0)
                .memoryStrength(0)
                .build();
    }

    /**
     * 获取需要复习的知识点内容
     *
     * @param dto
     * @return
     */
    private ServerResponse getNeedView(NeedViewDTO dto) {
        StudyCapacity studyCapacity = studyCapacityMapper.selectLargerThanGoldTimeWithStudentIdAndUnitId(dto);
        if (Objects.equals(dto.getType(), StudyCapacityTypeConstant.LEARN_SYNTAX)) {
            return this.packageNeedViewLearnSyntax(studyCapacity, dto);
        }
        if (Objects.equals(dto.getType(), StudyCapacityTypeConstant.SELECT_SYNTAX) && !Objects.isNull(studyCapacity)) {
            return this.packageSelectSyntaxNeedView(dto, studyCapacity);
        }

        return null;
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
            GameVO selects = getGameVO(dto.getUnitId(), syntaxTopic);
            return ServerResponse.createBySuccess(this.packageSelectSyntaxVO(knowledgePoint1, selects));
        }
        return null;
    }

    /**
     * 封装选语法需要返回的数据
     *
     * @param knowledgePoint
     * @param selects
     * @return
     */
    private Object packageSelectSyntaxVO(LearnSyntaxVO knowledgePoint, GameVO selects) {
        SelectSyntaxVO.SelectSyntaxVOBuilder selectSyntaxVOBuilder = SelectSyntaxVO.builder()
                .id(knowledgePoint.getId())
                .total(knowledgePoint.getTotal())
                .plan(knowledgePoint.getPlan())
                .studyNew(knowledgePoint.getStudyNew())
                .memoryDifficult(knowledgePoint.getMemoryDifficult())
                .memoryStrength(knowledgePoint.getMemoryStrength())
                .selects(selects);

        knowledgePoint.setId(null);
        knowledgePoint.setTotal(null);
        knowledgePoint.setPlan(null);
        knowledgePoint.setStudyNew(null);
        knowledgePoint.setMemoryDifficult(null);
        knowledgePoint.setMemoryStrength(null);

        return selectSyntaxVOBuilder.knowledgePoint(knowledgePoint).build();
    }

    /**
     * 封装试题选项
     *
     * @param unitId
     * @param syntaxTopic 当前语法题
     * @return
     */
    private GameVO getGameVO(Long unitId, SyntaxTopic syntaxTopic) {
        return new GameVO(syntaxTopic.getTopic().replace("$&$", "___"), this.packageSelectAnswer(syntaxTopic, unitId));
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
                .memoryStrength((int) Math.round(studyCapacity.getMemoryStrength()))
                .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                .build();
    }

    /**
     * 封装选语法选项
     *
     * @param syntaxTopic
     * @param unitId
     * @return
     */
    private List<GameSelect> packageSelectAnswer(SyntaxTopic syntaxTopic, Long unitId) {
        List<SyntaxTopic> syntaxTopics = syntaxTopicMapper.selectByUnitId(unitId);
        Collections.shuffle(syntaxTopics);
        // 三个错误选项
        List<GameSelect> select = syntaxTopics.stream()
                .filter(syntaxTopic1 -> !Objects.equals(syntaxTopic1.getId(), syntaxTopic.getId()))
                .limit(3)
                .map(syntaxTopic1 -> new GameSelect(syntaxTopic1.getAnswer(), false))
                .collect(Collectors.toList());
        // 一个正确选项
        select.add(new GameSelect(syntaxTopic.getAnswer(), true));
        Collections.shuffle(select);
        return select;
    }

    private ServerResponse packageNeedViewLearnSyntax(StudyCapacity studyCapacity, NeedViewDTO dto) {
        if (!Objects.isNull(studyCapacity)) {
            return ServerResponse.createBySuccess(LearnSyntaxVO.builder()
                    .id(studyCapacity.getWordId())
                    .content(studyCapacity.getWordChinese())
                    .syntaxName(studyCapacity.getWord())
                    .total(dto.getTotal())
                    .plan(Math.min(dto.getPlan(), dto.getTotal()))
                    .studyNew(false)
                    .memoryStrength((int) Math.round(studyCapacity.getMemoryStrength()))
                    .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                    .build());
        }
        return null;
    }
}

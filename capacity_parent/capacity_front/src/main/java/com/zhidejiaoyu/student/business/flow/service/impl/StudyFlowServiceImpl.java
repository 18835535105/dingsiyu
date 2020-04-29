package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.common.*;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author wuchenxi
 */
@Slf4j
@Service(value = "flowService")
public class StudyFlowServiceImpl extends BaseServiceImpl<StudyFlowNewMapper, StudyFlowNew> implements StudyFlowService {

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;

    @Resource
    private UnitTeksNewMapper unitTeksNewMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Resource
    private JudgeNextNode judgeNextNode;

    @Resource
    private SyntaxUnitTopicNewMapper syntaxUnitTopicNewMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private PackageFlowVO packageFlowVO;

    @Resource
    private LogOpt logOpt;

    @Resource
    private JudgeWordPicture judgeWordPicture;

    @Resource
    private InitData initData;

    @Resource
    private FinishGroupOrUnit finishGroupOrUnit;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private RedisOpt redisOpt;

    /**
     * 节点学完, 把下一节初始化到student_flow表, 并把下一节点返回
     *
     * @return id 节点id
     * model_name 节点模块名
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> getNode(NodeDto dto, String isTrueFlow, HttpSession session) {
        Student student = super.getStudent(session);
        dto.setStudent(student);

        // 星球页请求数据，获取当前应该学习的节点数据
        if (dto.getNodeId() == null) {
            return this.getIndexNodeResponse(dto);
        }

        if (Objects.equals(dto.getNodeId(), FlowConstant.GOLD_TEST)) {
            // 金币试卷节点，初始化下一个优先级数据
            return ServerResponse.createBySuccess(finishGroupOrUnit.finishGoldTest(dto));
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        if (studyFlowNew == null) {
            log.error("未查询到id={}的流程信息！", dto.getNodeId());
            throw new ServiceException("未查询到流程信息！");
        }

        String modelName = studyFlowNew.getModelName();
        // 带有“写”、“课文训练”、nodeId=84的流程都属于难流程
        int easyOrHard = this.getEasyOrHard(dto, modelName);
        dto.setEasyOrHard(easyOrHard);
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(),
                dto.getUnitId(), dto.getEasyOrHard(), FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(studyFlowNew.getFlowName()));
        if (learnNew == null) {
            learnNew = initData.saveLearnNew(dto, 1, dto.getModelType());
        }

        // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
        studyCapacityMapper.deleteByStudentIdAndUnitIdAndGroup(student.getId(), dto.getUnitId(), learnNew.getGroup());
        learnExtendMapper.deleteByLearnId(learnNew.getId());

        dto.setGroup(learnNew.getGroup());
        dto.setLearnNew(learnNew);
        dto.setStudyFlowNew(studyFlowNew);
        session.setAttribute(SessionConstant.STUDY_GROUP, dto.getGroup());
        dto.setSession(session);

        // 判断单词游戏
        ServerResponse<Object> response = this.judgeBeforeGame(dto);
        if (response != null) {
            return response;
        }

        // 判断句型游戏
        response = this.judgeSentenceGame(dto);
        if (response != null) {
            return response;
        }

        if (studyFlowNew.getNextTrueFlow() == 0) {
            if (checkNextUnitOrGroup(dto, studyFlowNew)) {
                return ServerResponse.createBySuccess(finishGroupOrUnit.finishOneKeyGroup(dto));
            }
        }
        // 其余正常流程
        if (studyFlowNew.getNextFalseFlow() == null) {
            // 直接进入下个流程节点
            return this.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
        }

        // 判断下个节点
        return judgeNextNode.judgeNextNode(dto, this);
    }

    public int getEasyOrHard(NodeDto dto, String modelName) {
        return modelName.contains("写")
                || Objects.equals(modelName, "课文训练")
                || Objects.equals(84L, dto.getNodeId())
                || dto.getNodeId() == FlowConstant.BEFORE_GROUP_GAME_HARD ? 2 : 1;
    }

    /**
     * 如果当前节点是句型游戏测试，判断下个节点
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> judgeSentenceGame(NodeDto dto) {
        if (Objects.equals(dto.getNodeId(), FlowConstant.SENTENCE_GAME)) {
            if (dto.getGrade() == PointConstant.HUNDRED) {
                // 全部答对
                return this.toAnotherFlow(dto, (int) FlowConstant.SENTENCE_TRANSLATE);
            }

            if (dto.getGrade() >= PointConstant.SIXTY && dto.getGrade() < PointConstant.HUNDRED) {
                return this.toAnotherFlow(dto, (int) FlowConstant.YIN_YI_EXERCISE_TWO);
            }

            if (dto.getGrade() < PointConstant.SIXTY) {
                return this.toAnotherFlow(dto, (int) FlowConstant.YIN_YI_EXERCISE_ONE);
            }
        }
        return null;
    }

    /**
     * 如果当前节点是学前游戏测试，判断下个节点
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> judgeBeforeGame(NodeDto dto) {
        if (Objects.equals(dto.getNodeId(), FlowConstant.BEFORE_GROUP_GAME_EASY)
                || Objects.equals(dto.getNodeId(), FlowConstant.BEFORE_GROUP_GAME_HARD)) {
            Long studentId = dto.getStudent().getId();
            boolean isEasy = dto.getEasyOrHard() == 1;
            if (dto.getGrade() == PointConstant.HUNDRED) {
                // 全部答对
                return isEasy ? this.toAnotherFlow(dto, (int) FlowConstant.SENTENCE_TRANSLATE) : this.toAnotherFlow(dto, (int) FlowConstant.LETTER_WRITE);
            }

            if (dto.getGrade() >= PointConstant.EIGHTY) {
                return isEasy ? this.toAnotherFlow(dto, (int) FlowConstant.PLAYER) : this.toAnotherFlow(dto, (int) FlowConstant.LETTER_WRITE);
            }

            if (dto.getGrade() < PointConstant.EIGHTY) {
                // 将当前group所有单词的记忆强度初始化为50%
                redisOpt.saveFirstFalseAdd(studentId, dto.getUnitId(), dto.getGroup());
                HttpUtil.getHttpSession().setAttribute(SessionConstant.FIRST_FALSE_ADD, true);
                // 从单词播放机继续学习单词流程
                return isEasy ? this.toAnotherFlow(dto, (int) FlowConstant.PLAYER) : this.toAnotherFlow(dto, (int) FlowConstant.LETTER_WRITE);
            }
        }
        return null;
    }

    /**
     * 学生在星球页获取应该学习的节点数据
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> getIndexNodeResponse(NodeDto dto) {
        Student student = dto.getStudent();
        // 在星球页请求，返回当前正在学习的节点信息
        Long studentId = student.getId();

        // 查询一键学习中的语法流程，优先学习（因为语法的课程信息与单词的不同）
        StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByStudentIdAndLearModelType(studentId, 4);

        StudentStudyPlanNew maxFinalLevelStudentStudyPlanNew = null;
        if (studentFlowNew == null) {
            // 查询学生最高优先级数据
            maxFinalLevelStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(studentId);

            maxFinalLevelStudentStudyPlanNew = finishGroupOrUnit.judgeHasGoldTest(dto, maxFinalLevelStudentStudyPlanNew);

            studentFlowNew = studentFlowNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId,
                    maxFinalLevelStudentStudyPlanNew.getUnitId(), maxFinalLevelStudentStudyPlanNew.getEasyOrHard());
        }

        StudyFlowNew studyFlowNew;
        LearnNew learnNew;
        if (studentFlowNew == null) {
            TestRecord testRecord = testRecordMapper.selectByGenre(studentId, GenreConstant.TEST_BEFORE_STUDY);
            if (testRecord == null) {
                throw new ServiceException("学生还没有进行摸底测试，未查询到可以学习的课程！");
            }
            // 如果学生已经进行过摸底测试，初始化流程节点以及学习记录
            studyFlowNew = studyFlowNewMapper.selectById(maxFinalLevelStudentStudyPlanNew.getFlowId());

            learnNew = initData.saveLearn(maxFinalLevelStudentStudyPlanNew, FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(studyFlowNew.getFlowName()));

            studentFlowNewMapper.deleteByLearnId(learnNew.getId());
            initData.initStudentFlow(NodeDto.builder()
                    .student(student)
                    .nodeId(maxFinalLevelStudentStudyPlanNew.getFlowId())
                    .learnNew(learnNew)
                    .build());
        } else {
            studyFlowNew = studyFlowNewMapper.selectById(studentFlowNew.getCurrentFlowId());
            learnNew = learnNewMapper.selectById(studentFlowNew.getLearnId());
        }
        FlowVO vo;
        if (Objects.equals(studyFlowNew.getFlowName(), FlowConstant.FLOW_SIX)) {
            Long maxUnitId = syntaxUnitMapper.selectMaxUnitIdByUnitId(learnNew.getUnitId());
            boolean isLastUnit = Objects.equals(maxUnitId, learnNew.getUnitId());
            vo = packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                    .student(student)
                    .unitId(learnNew.getUnitId())
                    .studyFlowNew(studyFlowNew)
                    .lastUnit(isLastUnit)
                    .build());
        } else {
            vo = this.packageFlowVO(studyFlowNew, student, learnNew.getUnitId());
        }

        FreeFlowServiceImpl.setFirstFalseAdd(studentId, learnNew, redisOpt);
        setOneKeyGroup(learnNew);

        return ServerResponse.createBySuccess(vo);
    }

    public static void setOneKeyGroup(LearnNew learnNew) {
        HttpSession session = HttpUtil.getHttpSession();
        Integer group = learnNew.getGroup();
        session.setAttribute(SessionConstant.ONE_KEY_GROUP, group);
        session.setAttribute(SessionConstant.STUDY_GROUP, group);
    }


    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        return packageFlowVO.packageFlowVO(studyFlowNew, student, unitId);
    }


    /**
     * 进入其他流程
     *
     * @param dto
     * @param nextFlowId 下个节点id
     * @return
     */
    @Override
    public ServerResponse<Object> toAnotherFlow(NodeDto dto, int nextFlowId) {
        FlowVO flowVO;
        StudyFlowNew nextStudyFlowNew;

        if (Objects.equals(dto.getStudyFlowNew().getFlowName(), FlowConstant.FLOW_SIX)) {
            nextStudyFlowNew = studyFlowNewMapper.selectById(nextFlowId);
            flowVO = packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                    .unitId(dto.getUnitId())
                    .studyFlowNew(nextStudyFlowNew)
                    .student(dto.getStudent())
                    .lastUnit(dto.getLastUnit())
                    .build());
        } else {
            // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
            // 下个节点数据
            nextStudyFlowNew = this.getStudyFlow(dto, nextFlowId);

            // 判断当前单元是否含有当前模块的内容，如果没有当前模块的内容学习下个模块的内容
            flowVO = this.judgeHasCurrentModel(nextStudyFlowNew, dto);

            // 如果不相等，说明是跳模块学习，获取最新的节点信息
            if (!Objects.equals((long) nextFlowId, flowVO.getId())) {
                nextStudyFlowNew = studyFlowNewMapper.selectById(flowVO.getId());
            }
        }
        // 如果学习模块改变，修改learnNew中的modelType值
        int modelType = FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(nextStudyFlowNew.getFlowName());
        LearnNew learnNew = dto.getLearnNew();
        if (learnNew != null) {
            if (!Objects.equals(modelType, learnNew.getModelType())) {
                learnNew.setModelType(modelType);
                learnNewMapper.updateById(learnNew);
            }

            studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(nextStudyFlowNew.getId(), learnNew.getId());
        }

        return ServerResponse.createBySuccess(flowVO);
    }

    /**
     * 验证当前单元中的group是否包含当前学习模块，如果不包含，学习下个学习模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private FlowVO judgeHasCurrentModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        String flowName = studyFlowNew.getFlowName();
        // 单词模块
        Long unitId = dto.getUnitId();
        Integer group = dto.getGroup();
        Student student = dto.getStudent();
        if (Objects.equals(flowName, FlowConstant.FLOW_ONE) || Objects.equals(flowName, FlowConstant.FLOW_TWO)) {
            Integer wordCount = unitVocabularyNewMapper.countUnitIdAndGroup(unitId, group);
            if (wordCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSentenceModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 句型模块
        if (Objects.equals(flowName, FlowConstant.FLOW_THREE) || Objects.equals(flowName, FlowConstant.FLOW_FOUR)) {
            Integer sentenceCount = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
            if (sentenceCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            // 没有句型模块判断是否有课文模块
            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 课文模块
        if (Objects.equals(flowName, FlowConstant.FLOW_FIVE)) {
            Integer teksCount = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
            if (teksCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 语法模块
        if (Objects.equals(flowName, FlowConstant.FLOW_SIX)) {
            int syntaxCount = syntaxUnitTopicNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
            if (syntaxCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 学习完当前group
        return finishGroupOrUnit.finishOneKeyGroup(dto);
    }

    private FlowVO judgeHasCurrentModel(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        StudyFlowNew studyFlowNew1 = studyFlowNewMapper.selectById(studyFlowNew.getId());
        return packageFlowVO.packageFlowVO(studyFlowNew1, student, unitId);
    }

    /**
     * 判断当前单元group是否有句型模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private boolean judgeHasSentenceModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        // 没有单词模块判断是否有句型模块
        Integer sentenceCount = unitSentenceNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
        if (sentenceCount > 0) {
            studyFlowNew.setId(dto.getEasyOrHard() == 1 ? FlowConstant.SENTENCE_TRANSLATE : FlowConstant.SENTENCE_WRITE);
            return true;
        }
        return false;
    }

    /**
     * 判断当前单元group有没有句型模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private boolean judgeHasTeksModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        // 没有句型模块判断是否有课文模块
        Integer teksCount = unitTeksNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
        if (teksCount > 0) {
            studyFlowNew.setId(dto.getEasyOrHard() == 1 ? FlowConstant.TEKS_LISTEN : FlowConstant.TEKS_TRAINING);
            return true;
        }
        return false;
    }


    /**
     * @param dto
     * @param nextFlowId 下个流程节点的 id
     * @return
     */
    private StudyFlowNew getStudyFlow(NodeDto dto, int nextFlowId) {
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nextFlowId);

        boolean canStudyWordPicture = judgeWordPicture.judgeWordPicture(dto, studyFlowNew);
        if (canStudyWordPicture) {
            return studyFlowNew;
        }

        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 73;
        // 流程 1 的单词图鉴
        if (nextFlowId == flowOnePicture) {
            UnitNew unitNew = unitNewMapper.selectById(dto.getUnitId());
            Student student = dto.getStudent() == null ? new Student() : dto.getStudent();
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去句型翻译
                int flowId = 85;
                logOpt.changeFlowNodeLog(student, "句型翻译", unitNew, flowId);
                return studyFlowNewMapper.selectById(flowId);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 78L)) {
                int flowId = 71;
                logOpt.changeFlowNodeLog(student, "慧记忆", unitNew, flowId);
                return studyFlowNewMapper.selectById(flowId);
            }
            // 返回流程 1
            int flowId = 70;
            logOpt.changeFlowNodeLog(student, "单词播放机", unitNew, flowId);
            return studyFlowNewMapper.selectById(flowId);
        }

        return studyFlowNew;
    }
}

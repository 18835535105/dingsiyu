package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.common.*;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
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

        // 星球页请求数据，获取当前应该学习的节点数据
        if (dto.getNodeId() == null) {
            return this.getIndexNodeResponse(student);
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        if (studyFlowNew == null) {
            log.error("未查询到id={}的流程信息！", dto.getNodeId());
            throw new ServiceException("未查询到流程信息！");
        }

        String modelName = studyFlowNew.getModelName();
        // 带有“写”、“课文训练”、nodeId=84的流程都属于难流程
        int easyOrHard = modelName.contains("写") || Objects.equals(modelName, "课文训练") || Objects.equals(84L, dto.getNodeId()) ? 2 : 1;
        dto.setEasyOrHard(easyOrHard);
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(student.getId(), dto.getUnitId(), dto.getEasyOrHard());
        if (learnNew != null) {
            // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
            studyCapacityMapper.deleteByStudentIdAndUnitIdAndGroup(student.getId(), dto.getUnitId(), learnNew.getGroup());
            learnExtendMapper.deleteByLearnId(learnNew.getId());
            dto.setGroup(learnNew.getGroup());
            dto.setLearnNew(learnNew);
        } else {
            dto.setGroup(1);
        }
        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

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

    /**
     * 学生在星球页获取应该学习的节点数据
     *
     * @param student
     * @return
     */
    public ServerResponse<Object> getIndexNodeResponse(Student student) {
        // 在星球页请求，返回当前正在学习的节点信息
        StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByStudentIdAndType(student.getId(), 1);

        StudyFlowNew studyFlowNew;
        LearnNew learnNew;
        if (studentFlowNew == null) {
            TestRecord testRecord = testRecordMapper.selectByGenre(student.getId(), GenreConstant.TEST_BEFORE_STUDY);
            if (testRecord == null) {
                throw new ServiceException("学生还没有进行摸底测试，未查询到可以学习的课程！");
            }
            // 如果学生已经进行过摸底测试，初始化流程节点以及学习记录
            StudentStudyPlanNew maxFinalLevelStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(student.getId(), 1).get(0);

            learnNew = initData.saveLearn(maxFinalLevelStudentStudyPlanNew);

            studentFlowNewMapper.deleteByLearnId(learnNew.getId());
            initData.initStudentFlow(NodeDto.builder()
                    .student(student)
                    .nodeId(maxFinalLevelStudentStudyPlanNew.getFlowId())
                    .learnNew(learnNew)
                    .build());
            studyFlowNew = studyFlowNewMapper.selectById(maxFinalLevelStudentStudyPlanNew.getFlowId());
        } else {
            studyFlowNew = studyFlowNewMapper.selectById(studentFlowNew.getCurrentFlowId());
            learnNew = learnNewMapper.selectById(studentFlowNew.getLearnId());
        }

        FlowVO vo = this.packageFlowVO(studyFlowNew, student, learnNew.getUnitId());
        return ServerResponse.createBySuccess(vo);
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

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlowNew studyFlowNew = this.getStudyFlow(dto, nextFlowId);

        // 判断当前单元是否含有当前模块的内容，如果没有当前模块的内容学习下个模块的内容
        FlowVO flowVO = this.judgeHasCurrentModel(studyFlowNew, dto);

        studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(studyFlowNew.getId(), dto.getLearnNew().getId());

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

            if (this.judgeHasSyntaxModel(studyFlowNew, dto)) {
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

            if (this.judgeHasSyntaxModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 课文模块
        if (Objects.equals(flowName, FlowConstant.FLOW_FIVE)) {
            Integer teksCount = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
            if (teksCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSyntaxModel(studyFlowNew, dto)) {
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
     * 判断当前单元group有没有语法模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private boolean judgeHasSyntaxModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        // 没有句型模块判断是否有语法模块
        int syntaxCount = syntaxUnitTopicNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
        if (syntaxCount > 0) {
            // 初始化语法游戏节点
            studyFlowNew.setId(120L);
            return true;
        }
        return false;
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
            studyFlowNew.setId(85L);
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
            studyFlowNew.setId(FlowConstant.TEKS_LISTEN);
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

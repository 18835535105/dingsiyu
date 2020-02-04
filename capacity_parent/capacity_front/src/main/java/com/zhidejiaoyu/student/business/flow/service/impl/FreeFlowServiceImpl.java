package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.CcieUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自由学习节点
 *
 * @author: wuchenxi
 * @date: 2019/12/26 14:33:33
 */
@Slf4j
@Service(value = "freeFlowService")
public class FreeFlowServiceImpl extends BaseServiceImpl<StudyFlowNewMapper, StudyFlowNew> implements StudyFlowService {

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private CcieMapper ccieMapper;

    @Resource
    private CcieUtil ccieUtil;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private JudgeNextNode judgeNextNode;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private PackageFlowVO packageFlowVO;

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
     * 流程名称与 studyCapacity 中 type 的映射
     */
    private static final Map<String, Integer> FLOW_NAME_TO_STUDY_CAPACITY_TYPE = new HashMap<>();

    static {
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("单词图鉴", 1);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("慧记忆", 3);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("慧听写", 4);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("慧默写", 5);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("字母填写", 6);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("句型翻译", 7);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("句型听力", 8);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("音译练习", 9);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("句型默写", 10);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("课文试听", 11);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("课文训练", 12);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("闯关测试", 13);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("学语法", 20);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("选语法", 21);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("写语法", 22);
    }

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

        if (dto.getNodeId() == null) {
            return this.getIndexNodeResponse(dto);
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        Long studentId = student.getId();

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, dto.getUnitId(),
                dto.getEasyOrHard(), dto.getModelType() - 1);

        if (learnNew != null) {
            // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
            String modelName = studyFlowNew.getModelName();
            if (FLOW_NAME_TO_STUDY_CAPACITY_TYPE.containsKey(modelName)) {
                studyCapacityMapper.deleteByStudentIdAndUnitIdAndType(studentId, dto.getUnitId(),
                        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.get(modelName));
            }

            learnExtendMapper.deleteByUnitIdAndStudyModel(learnNew.getId(), this.getModelName(modelName));
            dto.setGroup(learnNew.getGroup());
            dto.setLearnNew(learnNew);
        } else {
            dto.setGroup(1);
        }

        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

        if (studyFlowNew == null) {
            return null;
        }

        // 判断单词游戏前测下个节点
        ServerResponse<Object> res = judgeBeforeGame(dto);
        if (res != null) {
            return res;
        }

        // 判断句型游戏下个节点
        res = judgeSentenceGame(dto);
        if (res != null) {
            return res;
        }

        // 学习下一单元, 前端需要一个弹框提示
        if (studyFlowNew.getNextTrueFlow() == 0) {

            if (checkNextUnitOrGroup(dto, studyFlowNew)) {
                // 保存证书
                this.judgeCourseCcie(dto);

                // 验证当前模块是否有下一个group，如果有初始化，没有说明当前单元学习完毕
                if (learnNew != null) {
                    return finishGroupOrUnit.finishFreeGroup(dto);
                }
                return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
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
     * 如果当前节点是句型游戏测试，判断下个节点
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> judgeSentenceGame(NodeDto dto) {
        if (Objects.equals(dto.getNodeId(), FlowConstant.FREE_SENTENCE_GAME)) {
            if (dto.getGrade() == 5) {
                // 全部答对
                return this.toAnotherFlow(dto, (int) FlowConstant.FREE_SENTENCE_TRANSLATE);
            }

            if (dto.getGrade() >= 3 || dto.getGrade() <= 4) {
                return this.toAnotherFlow(dto, (int) FlowConstant.FREE_YIN_YI_EXERCISE_TWO);
            }

            if (dto.getGrade() <= 2) {
                return this.toAnotherFlow(dto, (int) FlowConstant.FREE_YIN_YI_EXERCISE_ONE);
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
        if (Objects.equals(dto.getNodeId(), FlowConstant.BEFORE_GROUP_GAME)) {
            Long studentId = dto.getStudent().getId();
            boolean isEasy = dto.getEasyOrHard() == 1;
            if (dto.getGrade() == PointConstant.HUNDRED) {
                // 全部答对
                return isEasy ? finishGroupOrUnit.finishFreeGroup(dto) : this.toAnotherFlow(dto, (int) FlowConstant.FREE_LETTER_WRITE);
            }

            if (dto.getGrade() >= PointConstant.EIGHTY) {
                return isEasy ? this.toAnotherFlow(dto, (int) FlowConstant.FREE_PLAYER) : this.toAnotherFlow(dto, (int) FlowConstant.FREE_LETTER_WRITE);
            }

            if (dto.getGrade() < PointConstant.EIGHTY) {
                // 将当前group所有单词的记忆强度初始化为50%
                redisOpt.saveFirstFalseAdd(studentId, dto.getUnitId(), dto.getGroup());
                HttpUtil.getHttpSession().setAttribute(SessionConstant.FIRST_FALSE_ADD, true);
                return isEasy ? this.toAnotherFlow(dto, (int) FlowConstant.FREE_PLAYER) : this.toAnotherFlow(dto, (int) FlowConstant.FREE_LETTER_WRITE);
            }
        }
        return null;
    }

    /**
     * 匹配learnExtend中的studyModel
     *
     * @param modelName
     * @return
     */
    private String getModelName(String modelName) {
        if (modelName.contains("句型")) {
            return modelName.replace("句型", "例句");
        }
        if (Objects.equals("字母填写", modelName)) {
            return "单词填字";
        }
        return modelName;
    }

    /**
     * 学生选择单元的时候返回节点信息
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> getIndexNodeResponse(NodeDto dto) {
        Student student = dto.getStudent();
        Long studentId = student.getId();
        Long unitId = dto.getUnitId();
        Integer easyOrHard = dto.getEasyOrHard();
        Integer modelType = dto.getModelType();

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, unitId, easyOrHard,
                dto.getModelType() - 1);
        if (learnNew != null) {
            StudyFlowNew studyFlowNew = studyFlowNewMapper.selectByLearnId(learnNew.getId());
            if (studyFlowNew != null) {
                FlowVO flowVO;
                if (Objects.equals(studyFlowNew.getFlowName(), FlowConstant.FLOW_SIX)) {
                    flowVO = packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                            .student(student)
                            .unitId(unitId)
                            .lastUnit(false)
                            .studyFlowNew(studyFlowNew)
                            .build());
                } else {
                    flowVO = this.packageFlowVO(studyFlowNew, student, unitId);
                }
                return ServerResponse.createBySuccess(flowVO);
            }
            return this.getFlowVoServerResponse(learnNew, modelType, student);
        }

        if (Objects.equals(dto.getModelType(), 5)) {
            // 语法模块
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            learnNew = initData.saveLearnNew(NodeDto.builder()
                    .student(student)
                    .courseId(syntaxUnit.getCourseId())
                    .unitId(unitId)
                    .easyOrHard(easyOrHard)
                    .build(), 1, dto.getModelType() - 1);
        } else {
            UnitNew unitNew = unitNewMapper.selectById(unitId);
            learnNew = initData.saveLearnNew(NodeDto.builder()
                    .student(student)
                    .courseId(unitNew.getCourseId())
                    .unitId(unitId)
                    .easyOrHard(easyOrHard)
                    .build(), 1, dto.getModelType() - 1);
        }

        boolean firstFalseAdd = redisOpt.getFirstFalseAdd(studentId, learnNew.getUnitId(), learnNew.getGroup());
        if (firstFalseAdd) {
            HttpUtil.getHttpSession().setAttribute(SessionConstant.FIRST_FALSE_ADD, true);
        }

        return this.getFlowVoServerResponse(learnNew, modelType, student);
    }

    private void judgeCourseCcie(NodeDto dto) {
        // 判断学生是否能获取课程证书,每个课程智能获取一个课程证书
        Student student = dto.getStudent();
        Long courseId = dto.getCourseId();
        int count = ccieMapper.countCourseCcieByCourseId(student.getId(), courseId);
        if (count == 0) {
            int unitCount = unitNewMapper.countByCourseId(courseId);
            int learnedUnitCount = learnHistoryMapper.countUnitIdByCourseId(student.getId(), courseId);
            if (learnedUnitCount >= unitCount) {
                // 课程学习完毕，奖励学生课程证书
                Long grade = dto.getGrade();
                ccieUtil.saveCourseCcie(student, courseId, dto.getUnitId(), grade == null ? 0 : Integer.parseInt(grade.toString()));
            }
        }
    }

    /**
     * 进入其他流程
     *
     * @return
     */
    @Override
    public ServerResponse<Object> toAnotherFlow(NodeDto dto, int nextFlowId) {
        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nextFlowId);

        boolean canStudyWordPicture = judgeWordPicture.judgeWordPicture(dto, studyFlowNew);
        if (canStudyWordPicture) {
            // 如果学习模块改变，修改learnNew中的modelType值
            int modelType = FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(studyFlowNew.getFlowName());
            LearnNew learnNew = dto.getLearnNew();
            if (!Objects.equals(modelType, learnNew.getModelType())) {
                learnNew.setModelType(modelType);
                learnNewMapper.updateById(learnNew);
            }

            studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(studyFlowNew.getId(), dto.getLearnNew().getId());
            FlowVO flowVo;
            if (Objects.equals(dto.getStudyFlowNew().getFlowName(), FlowConstant.FLOW_SIX)) {
                flowVo = packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                        .student(student)
                        .unitId(dto.getUnitId())
                        .studyFlowNew(studyFlowNew)
                        .build());
            } else {
                flowVo = this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }
            return ServerResponse.createBySuccess(flowVo);
        }
        return finishGroupOrUnit.finishFreeGroup(dto);
    }

    public ServerResponse<Object> getFlowVoServerResponse(LearnNew learnNew, Integer modelType, Student student) {
        Long studentId = student.getId();

        StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByLearnId(learnNew.getId());
        Long startFlowId = finishGroupOrUnit.getStartFlowId(learnNew.getEasyOrHard(), modelType);
        if (studentFlowNew == null) {
            studentFlowNewMapper.insert(StudentFlowNew.builder()
                    .type(modelType)
                    .updateTime(new Date())
                    .studentId(studentId)
                    .learnId(learnNew.getId())
                    .currentFlowId(startFlowId)
                    .build());
        } else {
            studentFlowNew.setCurrentFlowId(startFlowId);
            studentFlowNew.setUpdateTime(new Date());
            studentFlowNew.setLearnId(learnNew.getId());
            studentFlowNewMapper.updateById(studentFlowNew);
        }
        StudyFlowNew studyFlowNew1 = studyFlowNewMapper.selectById(startFlowId);
        FlowVO flowVO;
        if (Objects.equals(studyFlowNew1.getFlowName(), FlowConstant.FLOW_SIX)) {
            flowVO = packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                    .student(student)
                    .unitId(learnNew.getUnitId())
                    .studyFlowNew(studyFlowNew1)
                    .build());
        } else {
            flowVO = this.packageFlowVO(studyFlowNew1, student, learnNew.getUnitId());
        }
        return ServerResponse.createBySuccess(flowVO);
    }

    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        return packageFlowVO.packageFlowVO(studyFlowNew, student, unitId);
    }
}

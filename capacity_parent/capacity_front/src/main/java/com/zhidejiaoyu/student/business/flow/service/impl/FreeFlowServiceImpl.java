package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.OpenUnitLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.CcieUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowCommonMethod;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 自由学习节点
 *
 * @author: wuchenxi
 * @date: 2019/12/26 14:33:33
 */
@Slf4j
@Service(value = "freeFlowService")
public class FreeFlowServiceImpl extends BaseServiceImpl<StudyFlowNewMapper, StudyFlowNew> implements StudyFlowService {
    /**
     * 进入流程 1 标识
     */
    private static final int TO_FLOW_ONE = -3;

    /**
     * 进入流程 2 标识
     */
    private static final int TO_FLOW_TWO = -1;

    /**
     * 流程名称与 studentStudyPlanNew 中type值的映射
     */
    private static final Map<String, Integer> FLOW_NAME_TO_TYPE = new HashMap<>();

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private OpenUnitLogMapper openUnitLogMapper;

    @Resource
    private CcieMapper ccieMapper;

    @Resource
    private CcieUtil ccieUtil;

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private FlowCommonMethod flowCommonMethod;

    static {
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_ONE, 2);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_TWO, 2);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_THREE, 3);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_FOUR, 3);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_FIVE, 4);
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
        Student student = getStudent(session);
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

        if (studyFlowNew != null) {
            // 学习下一单元, 前端需要一个弹框提示
            if (studyFlowNew.getNextTrueFlow() == 0) {
                // 开启下一单元并且返回需要学习的流程信息
                this.saveOpenUnitLog(student, dto.getUnitId());
                // 保存证书
                this.judgeCourseCcie(dto);
                return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
            } else if (Objects.equals(TO_FLOW_TWO, studyFlowNew.getNextTrueFlow())) {
                // 进入流程2
                return toAnotherFlow(dto, 24);
            } else if (Objects.equals(TO_FLOW_ONE, studyFlowNew.getNextTrueFlow())) {
                // 进入流程1
                // 判断当前单元流程1学习次数，如果没有学习，初始化同一单元的流程1；如果已学习初始化下一单元的流程1
                return toFlowOne(dto);
            } else if (Objects.equals(-2, studyFlowNew.getNextTrueFlow())) {
                // 继续上次流程
                StudyFlowNew lastStudyFlow = studyFlowNewMapper.selectStudentCurrentFlow(dto);
                return ServerResponse.createBySuccess("true", lastStudyFlow);
            } else {
                // 其余正常流程
                if (studyFlowNew.getNextFalseFlow() == null) {
                    // 直接进入下个流程节点
                    return this.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
                } else {
                    // 判断下个节点
                    return flowCommonMethod.judgeNextNode(dto, this);
                }
            }
        }
        return null;
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
     * 前往流程 1 的时候判断是当前单元的流程 1 还是下一单元的流程 1
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> toFlowOne(NodeDto dto) {
        int count = learnNewMapper.countByStudentIdAndFlow(dto.getStudent().getId(), dto.getUnitId(), FlowConstant.FLOW_ONE);
        if (count == 0) {
            return toAnotherFlow(dto, (int) FlowConstant.FREE_PLAYER);
        }
        this.saveOpenUnitLog(dto.getStudent(), dto.getUnitId());
        // 保存证书
        this.judgeCourseCcie(dto);
        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
    }

    /**
     * 进入其他流程
     *
     * @return
     */
    @Override
    public ServerResponse<Object> toAnotherFlow(NodeDto dto, int flowId) {
        Student student = dto.getStudent();

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlowNew studyFlowNew = this.getStudyFlow(dto, flowId);
        studentFlowNewMapper.updateFlowByStudentId(student.getId(), studyFlowNew.getId(), FLOW_NAME_TO_TYPE.get(studyFlowNew.getFlowName()));

        if (Objects.equals(TO_FLOW_ONE, studyFlowNew.getNextTrueFlow())) {
            log.info("[{} -{} -{}] 前往流程 1.", student.getId(), student.getAccount(), student.getStudentName());
            return toFlowOne(dto);
        } else if (Objects.equals(TO_FLOW_TWO, studyFlowNew.getNextTrueFlow())) {
            log.info("[{} -{} -{}] 前往流程 2.", student.getId(), student.getAccount(), student.getStudentName());
            return toAnotherFlow(dto, (int) FlowConstant.FREE_WORD_LISTEN);
        }

        return ServerResponse.createBySuccess("true", this.packageFlowVO(studyFlowNew, dto.getUnitId()));
    }

    @Override
    public ServerResponse<FlowVO> getIndexNode(Long unitId, Integer orHard, Integer easyOrHard) {
        Student student = super.getStudent(HttpUtil.getHttpSession());
//        learnNewMapper.selectIdByStudentIdAndUnitIdAndEasyOrHard(student.getId(), unitId, easyOrHard);

        return null;
    }

    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Long unitId) {
        return flowCommonMethod.packageFlowVO(studyFlowNew, unitId);
    }

    /**
     * @param dto
     * @param flowId 下个流程节点的 id
     * @return
     */
    private StudyFlowNew getStudyFlow(NodeDto dto, int flowId) {
        StudyFlowNew byPrimaryKey = studyFlowNewMapper.selectById(flowId);

        // 如果下个节点不是单词图鉴模块，执行正常流程
        String studyModel = "单词图鉴";
        if (!byPrimaryKey.getModelName().contains(studyModel)) {
            return byPrimaryKey;
        }

        Long unitId = dto.getUnitId();
        if (unitId == null) {
            return byPrimaryKey;
        }

        // 当前单元含有图片的单词个数，如果大于零，执行正常流程，否则跳过单词图鉴模块
        int pictureCount = unitVocabularyNewMapper.countPicture(unitId);
        if (pictureCount > 0) {
            return byPrimaryKey;
        }

        UnitNew unitNew = unitNewMapper.selectById(unitId);
        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        // 需要跳转到的流程 id
        int flowId1;
        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 15;
        // 流程 1 的单词图鉴
        if (flowId == flowOnePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去流程 2 的慧听写
                flowId1 = 18;
                this.changeFlowNodeLog(student, "慧听写", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 22L)) {
                flowId1 = 48;
                this.changeFlowNodeLog(student, "慧记忆", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 9;
            this.changeFlowNodeLog(student, "单词播放机", unitNew, flowId1);
            return studyFlowNewMapper.selectById(flowId1);
        }

        return byPrimaryKey;
    }

    /**
     * 当需要调过单词图鉴节点的时候记录日志
     *
     * @param student
     * @param model   学习模块
     */
    private void changeFlowNodeLog(Student student, String model, UnitNew unit, int flowId) {
        log.info("单元[{}]没有单词图片，学生[{} - {} - {}]进入{}流程，流程 id=[{}]",
                unit.getJointName(), student.getId(), student.getAccount(), student.getStudentName(), model, flowId);
    }

    private void saveOpenUnitLog(Student student, Long unitId) {
        // 保存开启单元日志记录
        OpenUnitLog openUnitLog = new OpenUnitLog();
        openUnitLog.setCreateTime(new Date());
        openUnitLog.setCurrentUnitId(unitId);
        openUnitLog.setStudentId(student.getId());
        try {
            openUnitLogMapper.insert(openUnitLog);
        } catch (Exception e) {
            log.error("学生 {}-{} 保存开启单元日志出错；当前单元 {}，下一单元 {}", student.getId(), student.getStudentName(), unitId, e);
        }
    }

}

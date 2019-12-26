package com.zhidejiaoyu.student.BaseUtil.SaveModel;

import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.common.StudyCapacityLearn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 保存数据结构储存接口
 */
@Component
public class SaveData {
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Autowired
    private MedalAwardAsync medalAwardAsync;
    @Autowired
    private ExecutorService executorService;
    @Resource
    private StudyCapacityLearn studyCapacityLearn;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    /**
     * 记忆难度
     */
    @Resource
    private MemoryDifficultyUtil memoryDifficultyUtil;

    public boolean saveVocabularyModel(Student student, HttpSession session, Long unitId,
                                       Long wordId, boolean isTrue, Integer plan,
                                       Integer total, Long flowId,Integer easyOrHard,Integer type,String studyModel) {

        Date now = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        Long studentId = student.getId();
        LearnExtend learn = new LearnExtend();
        judgeIsFirstStudy(session, student);
        //获取校长id
        learn.setSchoolAdminId(Long.parseLong(teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId()).toString()));
        //获取学生学习当前模块的learn_id
        List<Long> learnIds = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
        //如果有多余的删除
        Long learnId = learnIds.get(0);
        if (learnIds.size() > 1) {
            List<Long> longs = learnIds.subList(1, learnIds.size());
            learnNewMapper.deleteBatchIds(longs);
        }

        //获取learnExtend数据
        List<LearnExtend> learnExtends = learnExtendMapper.selectByLearnIdsAndWordIdAndStudyModel(learnId, wordId, studyModel);
        LearnExtend currentLearn = learnExtends.get(0);
        //如果有多余的删除
        if (learnExtends.size() > 1) {
            List<LearnExtend> extendList = learnExtends.subList(1, learnIds.size());
            List<Long> deleteLong = new ArrayList<>();
            extendList.forEach(extend -> deleteLong.add(extend.getId()));
            learnNewMapper.deleteBatchIds(deleteLong);
        }
        /**
         * 查看慧默写  会听写  单词图鉴是否为上次学习 如果是 删除
         * 开始
         */
        boolean flag = false;
        //查看当前数据是否为以前学习过的数据
        List<StudyCapacity> studyCapacities = studyCapacityMapper.selectByStudentIdAndUnitIdAndWordIdAndType(studentId, unitId, wordId, type);

        flag = studyCapacities.size() > 0 && studyCapacities.get(0).getPush().getTime() < System.currentTimeMillis();

        if (currentLearn == null && flag) {
            studyCapacityMapper.deleteByStudentIdAndUnitIdAndVocabulary(studentId, unitId, wordId, type);
            return true;
        }
        learn.setLearnId(learnId);
        learn.setWordId(wordId);
        // 保存学习记录
        // 第一次学习，如果答对记为熟词，答错记为生词
        LearnNew learnNew = learnNewMapper.selectById(learnId);
        if (currentLearn == null) {
            learn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            learn.setStudyModel(studyModel);
            learn.setStudyCount(1);
            learn.setUpdateTime(now);
            StudyFlowNew currentStudyFlow = this.getCurrentStudyFlowById(flowId);
            if (currentStudyFlow != null) {
                learn.setFlowName(currentStudyFlow.getFlowName());
            }
            if (isTrue) {
                // 如果认识该单词，记为熟词
                learn.setStatus(1);
                learn.setFirstIsKnow(1);
            } else {
                learn.setStatus(0);
                learn.setFirstIsKnow(0);
                // 单词不认识将该单词记入记忆追踪中
                studyCapacityLearn.saveCapacityMemory(learnNew, learn, student, false, type);
            }
            int count = learnExtendMapper.insert(learn);
            // 统计初出茅庐勋章
            executorService.execute(() -> medalAwardAsync.inexperienced(student));
            if (count > 0 && total == (plan + 1)) {
                return true;
            }
            if (count > 0) {
                return true;
            }
        } else {
            learn.setStudyCount(currentLearn.getStudyCount() + 1);
            StudyCapacity studyCapacity;
            if (isTrue) {

                studyCapacity = studyCapacityLearn.saveCapacityMemory(learnNew, learn, student, true, 0);

            } else {

                studyCapacity = studyCapacityLearn.saveCapacityMemory(learnNew, learn, student, false, 0);

            }
            // 计算记忆难度
            int memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(studyCapacity, 1);
            // 更新学习记录
            currentLearn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            session.removeAttribute(TimeConstant.BEGIN_START_TIME);
            currentLearn.setStudyCount(currentLearn.getStudyCount() + 1);
            // 熟词
            currentLearn.setStatus(memoryDifficult == 0 ? 1 : 0);
            currentLearn.setUpdateTime(now);
            int i = learnExtendMapper.updateById(currentLearn);
        }
        return false;
    }


    protected StudyFlowNew getCurrentStudyFlowById(Long flowId) {
        return studyFlowNewMapper.selectById(flowId);
    }

    /**
     * 判断学生是否在本系统首次学习，如果是记录首次学习时间
     *
     * @param session
     * @param student
     */
    public void judgeIsFirstStudy(HttpSession session, Student student) {
        if (student.getFirstStudyTime() == null) {
            // 说明学生是第一次在本系统学习，记录首次学习时间
            student.setFirstStudyTime(new Date());
            studentMapper.updateById(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }
    }

}

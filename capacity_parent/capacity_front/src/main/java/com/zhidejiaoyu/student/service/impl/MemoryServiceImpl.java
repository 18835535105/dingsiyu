package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.study.MemoryStudyVo;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.DurationUtil;
import com.zhidejiaoyu.common.utils.PictureUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.PerceiveEngine;
import com.zhidejiaoyu.student.common.SaveWordLearnAndCapacity;
import com.zhidejiaoyu.student.service.MemoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class MemoryServiceImpl extends BaseServiceImpl<VocabularyMapper, Vocabulary> implements MemoryService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    /**
     * 记忆追踪-慧记忆
     */
    @Autowired
    private CapacityMemoryMapper capacityMemoryMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private SaveWordLearnAndCapacity saveWordLearnAndCapacity;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Override
    public Object getMemoryWord(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        boolean firstStudy = commonMethod.isFirst(student.getId(), "慧记忆");

        if (firstStudy) {
            // 如果是首次学习慧记忆单词，新增一条学习数据，目的是完成引导页之后进入学习页面
            Learn learn = new Learn();
            learn.setStudentId(student.getId());
            learn.setStudyModel("慧记忆");
            learnMapper.insert(learn);
        }

        // 查询学生当前单元下已学习单词的个数，即学习进度
        Long plan = learnMapper.countLearnWord(student.getId(), unitId, "慧记忆");
        // 获取当前单元下的所有单词的总个数
        Long wordCount = unitVocabularyMapper.countByUnitId(unitId);
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
        }

        if (wordCount.equals(plan)) {
            return super.toUnitTest();
        }

        // 查看当前单元下记忆追踪中有无达到黄金记忆点的单词
        CapacityMemoryExample capacityMemoryExample = new CapacityMemoryExample();
        capacityMemoryExample.createCriteria().andStudentIdEqualTo(student.getId()).andUnitIdEqualTo(unitId)
                .andPushLessThan(new Date()).andMemoryStrengthLessThan(1.0);
        capacityMemoryExample.setOrderByClause("push asc");
        List<CapacityMemory> capacityMemories = capacityMemoryMapper.selectByExample(capacityMemoryExample);

        // 有到达黄金记忆点的单词优先复习
        if (capacityMemories.size() > 0) {
            // 返回达到黄金记忆点的单词信息
            CapacityMemory capacityMemory = capacityMemories.get(0);
            return this.returnGoldWord(capacityMemory, plan, firstStudy, wordCount);
        }

        // 如果没有到达黄金记忆点的单词，获取当前学习进度的下一个单词
        return getNextMemoryWord(session, unitId, student, firstStudy, plan, wordCount);
    }

    /**
     * 获取下一个慧记忆单词
     *
     * @param session
     * @param unitId
     * @param student
     * @param firstStudy
     * @param plan       当前学习进度
     * @param wordCount  当前单元单词总数
     * @return 如果当前单词是本单元最后一个单词，返回 null
     */
    private ServerResponse<MemoryStudyVo> getNextMemoryWord(HttpSession session, Long unitId, Student student, boolean firstStudy,
                                                            Long plan, Long wordCount) {
        if (wordCount - 1 >= plan) {
            // 记录学生开始学习该单词的时间
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

            // 查询学习记录本模块学习过的所有单词id
            List<Long> wordIds = learnMapper.selectLearnedWordIdByUnitId(student, unitId, "慧记忆");

            Vocabulary currentStudyWord = vocabularyMapper.selectOneWordNotInIds(wordIds, unitId);

            // 查询单词释义
            String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(unitId, currentStudyWord.getId());

            MemoryStudyVo memoryStudyVo = new MemoryStudyVo();
            memoryStudyVo.setWordId(currentStudyWord.getId());
            memoryStudyVo.setMemoryDifficulty(0);
            memoryStudyVo.setMemoryStrength(0.00);
            memoryStudyVo.setSoundMark(StringUtils.isEmpty(currentStudyWord.getSoundMark()) ? "" : currentStudyWord.getSoundMark());
            memoryStudyVo.setReadUrl(baiduSpeak.getLanguagePath(currentStudyWord.getWord()));
            memoryStudyVo.setWord(currentStudyWord.getWord());
            memoryStudyVo.setSyllable(StringUtils.isEmpty(currentStudyWord.getSyllable()) ? currentStudyWord.getWord() : currentStudyWord.getSyllable());
            memoryStudyVo.setWordChinese(wordChinese);
            memoryStudyVo.setPlan(plan);
            memoryStudyVo.setStudyNew(true);
            memoryStudyVo.setFirstStudy(firstStudy);
            memoryStudyVo.setWordCount(wordCount);
            memoryStudyVo.setEngine(1);
            memoryStudyVo.setWordChineseList(this.getChinese(unitId, currentStudyWord.getId(), wordChinese));
            memoryStudyVo.setImgUrl(PictureUtil.getPictureByUnitId(currentStudyWord, unitId));
            return ServerResponse.createBySuccess(memoryStudyVo);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveMemoryWord(HttpSession session, Learn learn, Boolean known, Integer plan,
                                                 Integer total) {
        Student student = getStudent(session);

        Long studentId = student.getId();

        // 当前课程的最大学习遍数
        List<Long> learnIds = learnMapper.selectLearnIds(studentId, learn, "慧记忆", 1);
        if (learnIds.size() > 1) {
            List<Long> longs = learnIds.subList(1, learnIds.size());
            learnMapper.deleteBatchIds(longs);
        }
        // 查询当前单词的学习记录数据
        Learn currentLearn = learnMapper.selectLearn(studentId, learn, "慧记忆", 1);

        // 统计初出茅庐勋章
        medalAwardAsync.inexperienced(student);
        //查看慧记忆是否为上次学习 如果是删除
        List<CapacityMemory> capacityMemoryList = capacityMemoryMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                learn.getVocabularyId());
        boolean flag = capacityMemoryList.size() > 0 && capacityMemoryList.get(0).getPush().getTime() < System.currentTimeMillis();
        if(currentLearn == null && flag){
            capacityMemoryMapper.deleteByStudentIdAndUnitIdAndVocabularyId(student.getId(),learn.getUnitId(),learn.getVocabularyId());
            return  ServerResponse.createBySuccess();
        }
        // 保存学习记录
        // 第一次学习，如果答对记为熟词，答错记为生词
        if (currentLearn == null) {
            return saveMemoryFirstLearnRecord(session, learn, known, student, studentId);
        } else {
            // 不是第一次学习
            return saveMemoryNotFirstLearnRecord(session, learn, known, student, currentLearn);
        }
    }

    /**
     * “慧记忆”模块中当前单元当前单词不是第一次学习，保存其学习记录和慧追踪记录
     *
     * @param session
     * @param learn        当前单词之前的学习信息
     * @param known        是否熟悉当前单词
     * @param student
     * @param currentLearn 当前单词的学习信息
     * @return
     */
    private ServerResponse<String> saveMemoryNotFirstLearnRecord(HttpSession session, Learn learn, Boolean known,
                                                                 Student student, Learn currentLearn) {
        learn.setStudyCount(currentLearn.getStudyCount() + 1);
        CapacityMemory capacityMemory;
        if (known) {
            capacityMemory = saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 1);
        } else {
            // 单词不认识将该单词记入记忆追踪中
            capacityMemory = saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 1);
        }
        // 计算记忆难度
        int memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(capacityMemory, 1);
        // 更新学习记录
        currentLearn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        currentLearn.setStudyCount(currentLearn.getStudyCount() + 1);

        packageAboutStudyPlan(currentLearn, student.getId(), capacityStudentUnitMapper, studentStudyPlanMapper);

        if (memoryDifficult == 0) {
            // 熟词
            currentLearn.setStatus(1);
        } else {
            currentLearn.setStatus(0);
        }
        currentLearn.setUpdateTime(new Date());
        int i = learnMapper.updateById(currentLearn);
        return i > 0 ? ServerResponse.createBySuccessMessage("学习信息保存成功") : ServerResponse.createByErrorMessage("学习信息保存失败");
    }

    /**
     * “慧记忆”模块首次学习当前单元当前单词，保存其学习记录和慧追踪记录
     *
     * @param session
     * @param learn     当前学习信息
     * @param known     是否熟悉当前单词
     * @param student
     * @param studentId
     * @return
     */
    private ServerResponse<String> saveMemoryFirstLearnRecord(HttpSession session, Learn learn, Boolean known, Student student, Long studentId) {
        Date now = DateUtil.parseYYYYMMDDHHMMSS(new Date());

        if (student.getFirstStudyTime() == null) {
            // 说明学生是第一次在本系统学习，记录首次学习时间
            student.setFirstStudyTime(now);
            studentMapper.updateByPrimaryKeySelective(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }

        // 封装有关学习计划的信息
        packageAboutStudyPlan(learn, studentId, capacityStudentUnitMapper, studentStudyPlanMapper);

        learn.setStudentId(studentId);
        learn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyModel("慧记忆");
        learn.setStudyCount(1);

        // 该课程是否是第一次学习，是第一次学习要保存课程首次学习时间
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(studentId).andCourseIdEqualTo(learn.getCourseId());
        int courseLearnCount = learnMapper.countByExample(learnExample);
        if (courseLearnCount == 0) {
            // 第一次学习该课程，保存首次学习时间
            learn.setFirstStudyTime(now);
        }
        if (known) {
            // 如果认识该单词，记为熟词
            learn.setStatus(1);
            learn.setFirstIsKnown(1);
        } else {
            learn.setStatus(0);
            learn.setFirstIsKnown(0);
            // 单词不认识将该单词记入记忆追踪中
        }
        saveWordLearnAndCapacity.saveCapacityMemory(learn, student, known, 1);
        learn.setLearnCount(1);
        learn.setUpdateTime(now);
        learn.setType(1);
        if (super.getCurrentStudyFlow(studentId) != null) {
            learn.setFlowName(super.getCurrentStudyFlow(studentId).getFlowName());
        }
        learnMapper.insert(learn);
        return ServerResponse.createBySuccess();
    }

    /**
     * 封装有关学习计划的数据
     *
     * @param learn
     * @param studentId
     */
    public static void packageAboutStudyPlan(Learn learn, Long studentId, CapacityStudentUnitMapper capacityStudentUnitMapper,
                                             StudentStudyPlanMapper studentStudyPlanMapper) {
        if (StringUtils.isNotEmpty(learn.getCourseName())) {
            return;
        }
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(studentId, 1);
        if (capacityStudentUnit != null) {
            Long startUnitId = capacityStudentUnit.getStartunit();
            Long endUnitId = capacityStudentUnit.getEndunit();
            learn.setCourseName(capacityStudentUnit.getCourseName());
            learn.setStartUnitId(startUnitId);
            learn.setEndUnitId(endUnitId);

            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selectCurrentPlan(studentId, startUnitId, endUnitId, 1);
            if (studentStudyPlan != null) {
                learn.setLearnCount(studentStudyPlan.getCurrentStudyCount());
                learn.setStudyPlanId(studentStudyPlan.getId());
            }
        }
    }


    /**
     * 返回达到黄金记忆点的单词信息
     *
     * @param plan
     * @param wordCount
     * @param firstStudy
     * @return
     */
    private ServerResponse<MemoryStudyVo> returnGoldWord(CapacityMemory capacityMemory, Long plan, boolean firstStudy,
                                                         Long wordCount) {
        // 计算当前单词的记忆难度
        int memoryDifficulty = memoryDifficultyUtil.getMemoryDifficulty(capacityMemory, 1);
        // 计算当前单词的记忆强度
        double memoryStrength = capacityMemory.getMemoryStrength();
        Vocabulary vocabulary = vocabularyMapper.selectById(capacityMemory.getVocabularyId());

        Long unitId = capacityMemory.getUnitId();
        Long vocabularyId = capacityMemory.getVocabularyId();
        String wordChinese = capacityMemory.getWordChinese();

        MemoryStudyVo memoryStudyVo = new MemoryStudyVo();
        memoryStudyVo.setWordId(vocabularyId);
        memoryStudyVo.setMemoryDifficulty(memoryDifficulty);
        memoryStudyVo.setMemoryStrength(memoryStrength);
        memoryStudyVo.setSoundMark(StringUtils.isEmpty(vocabulary.getSoundMark()) ? "" : vocabulary.getSoundMark());
        memoryStudyVo.setWord(capacityMemory.getWord());
        memoryStudyVo.setSyllable(capacityMemory.getSyllable());
        memoryStudyVo.setWordChinese(wordChinese);
        memoryStudyVo.setPlan(plan);
        memoryStudyVo.setStudyNew(false);
        memoryStudyVo.setFirstStudy(firstStudy);
        memoryStudyVo.setWordCount(wordCount);
        memoryStudyVo.setReadUrl(baiduSpeak.getLanguagePath(capacityMemory.getWord()));
        memoryStudyVo.setEngine(PerceiveEngine.getPerceiveEngine(memoryDifficulty, memoryStrength));
        memoryStudyVo.setWordChineseList(this.getChinese(unitId, vocabularyId, wordChinese));
        memoryStudyVo.setImgUrl(PictureUtil.getPictureByUnitId(vocabulary, unitId));

        return ServerResponse.createBySuccess(memoryStudyVo);

    }

    /**
     * 封装中文干扰项
     *
     * @param unitId
     * @param vocabularyId
     * @param wordChinese
     * @return
     */
    private List<Map<String, Boolean>> getChinese(Long unitId, Long vocabularyId, String wordChinese) {
        return ReviewServiceImpl.getInterferenceChinese(unitId, vocabularyId, wordChinese, unitVocabularyMapper, unitMapper);
    }

    @Override
    public ServerResponse<String> clearFirst(HttpSession session, String studyModel) {
        Student student = getStudent(session);
        commonMethod.clearFirst(student.getId(), studyModel);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> todayTime(HttpSession session) {
        Student student = super.getStudent(session);

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 有效时长  !
        Integer valid = (int) DurationUtil.getTodayValidTime(session);
        // 在线时长 !
        Integer online = (int) DurationUtil.getTodayOnlineTime(session);
        // 今日学习效率 !
        if (valid > online) {
            log.error("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            valid = online - 1;
            result.put("efficiency", "99%");
        } else {
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        }
        result.put("online", online);
        result.put("valid", valid);
        return ServerResponse.createBySuccess(result);
    }


}

package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.PictureUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.learn.PerceiveEngineUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.service.impl.MemoryServiceImpl;
import com.zhidejiaoyu.student.business.service.impl.ReviewServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 单词图鉴获取保存数据
 */
@Service(value = "wordPictorialService")
@Slf4j
public class WordPictorialImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {

    /**
     * 记忆难度
     */
    @Resource
    private MemoryDifficultyUtil memoryDifficultyUtil;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private UnitNewMapper unitNewMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private RedisOpt redisOpt;
    private static Integer model = 1;
    private static Integer type = 1;
    private static Integer easyOrHard=1;
    private String studyModel = "单词图鉴";



    @Override
    public Object getStudy(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        judgeIsFirstStudy(session, student);
        //获取是否有可以学习的单词信息
        int wordCount = unitVocabularyNewMapper.countWordPictureByUnitId(unitId);
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词图鉴信息！", unitId);
            return ServerResponse.createByErrorMessage("The unit no pictures");
        }
        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct = studyCapacityMapper.selectNeedReviewWord(unitId, studentId, DateUtil.DateTime(), 1);
        // 没有需要复习的
        if (correct == null) {
            correct = this.getSudyWords(unitId, studentId, type, model);
            if (correct == null) {
                return super.toUnitTest();
            }
            // 是新单词
            correct.put("studyNew", true);
            // 记忆强度
            correct.put("memoryStrength", 0.00);
        } else {
            // 不是新词
            correct.put("studyNew", false);
            // 记忆强度
            correct.put("memoryStrength", correct.get("memory_strength"));
        }

        // 记录学生开始学习的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 记忆难度
        StudyCapacity cp = new StudyCapacity();
        cp.setStudentId(studentId);
        cp.setUnitId(unitId);
        cp.setWordId(Long.valueOf(correct.get("id").toString()));
        Object faultTime = correct.get("fault_time");
        Object memoryStrength = correct.get("memory_strength");
        cp.setType(type);
        if (faultTime == null) {
            cp.setFaultTime(0);
        } else {
            cp.setFaultTime(Integer.parseInt(faultTime.toString()));
        }
        if (memoryStrength == null) {
            cp.setMemoryStrength(0.0);
        } else {
            cp.setMemoryStrength(Double.parseDouble(memoryStrength.toString()));
        }
        int hard = memoryDifficultyUtil.getMemoryDifficulty(cp, 1);
        correct.put("memoryDifficulty", hard);

        // 认知引擎
        correct.put("engine", PerceiveEngineUtil.getPerceiveEngine(hard, cp.getMemoryStrength()));

        // 读音url
        correct.put("readUrl", baiduSpeak.getLanguagePath(correct.get("word").toString()));

        // 单词图片
        correct.put("recordpicurl", PictureUtil.getPictureByUnitId(ReviewServiceImpl.packagePictureUrl(correct), unitId));

        // 2. 从本课程非本单元下随机获取三个题, 三个作为错题, 并且id不等于正确题id
        List<Map<String, Object>> mapErrorVocabulary = vocabularyMapper.getWordIdNewByCourse(new Long(correct.get("id").toString()), unitId);
        // 四道题
        mapErrorVocabulary.add(correct);
        // 随机打乱顺序
        Collections.shuffle(mapErrorVocabulary);
        // 封装选项正确答案
        Map<Object, Object> subject = new HashMap<>(16);
        for (Map<String, Object> m : mapErrorVocabulary) {
            boolean b = false;
            if (m.get("word").equals(correct.get("word"))) {
                b = true;
            }
            correct.put("type", 2);
            subject.put(m.get("word"), b);
        }
        // 把四个选项添加到correct正确答案数据中
        correct.put("subject", subject);
        // 3. count单元表单词有多少个查询存在图片的    /.
        Integer count = unitNewMapper.countWordByUnitidByPic(unitId);
        correct.put("wordCount", count);
        // 5. 是否是第一次学习单词图鉴，true:第一次学习，进入学习引导页；false：不是第一次学习
        correct.put("firstStudy", redisOpt.getGuideModel(studentId, "单词图鉴"));
        // 记录学生开始学习该单词/例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        return ServerResponse.createBySuccess(correct);
    }


    @Override
    public Object saveStudy(HttpSession session,
                            Long unitId, Long wordId, boolean isTrue,
                            Integer plan, Integer total,Long courseId) {
        Student student = getStudent(session);
        Date now = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        Long studentId = student.getId();

        judgeIsFirstStudy(session, student);
        //获取学生学习当前模块的learn_id
        List<Long> learnIds = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
        //如果有多余的删除
        Long learnId=learnIds.get(0);
        if (learnIds.size() > 1) {
            List<Long> longs = learnIds.subList(1, learnIds.size());
            learnNewMapper.deleteBatchIds(longs);
        }
        //获取learnExtend数据
        List<LearnExtend> learnExtends=learnExtendMapper.selectByLearnIdsAndWordIdAndStudyModel(learnId,wordId,studyModel);
        LearnExtend currentLearn = learnExtends.get(0);
        //如果有多余的删除
        if(learnExtends.size() > 1){
            List<LearnExtend> extendList = learnExtends.subList(1, learnIds.size());
            List<Long> deleteLong=new ArrayList<>();
            extendList.forEach(extend -> deleteLong.add(extend.getId()));
            learnNewMapper.deleteBatchIds(deleteLong);
        }
        /**
         * 查看慧默写  会听写  单词图鉴是否为上次学习 如果是 删除
         * 开始
         */
        boolean flag = false;
        //查看当前数据是否为以前学习过的数据
        studyCapacityMapper.selectByStudentIdAndUnitIdAndWordIdAndType(studentId,unitId,wordId,type);
       /* List<CapacityPicture> capacityPictures = capacityPictureMapper.selectByUnitIdAndId(student.getId(), unitId, wordId);
        flag = capacityPictures.size() > 0 && capacityPictures.get(0).getPush().getTime() < System.currentTimeMillis();

        if (currentLearn == null && flag) {
            capacityPictureMapper.deleteByStudentIdAndUnitIdAndVocabulary(student.getId(), learn.getUnitId(),
                        learn.getVocabularyId());
            return ServerResponse.createBySuccess();
        }
        // 保存学习记录
        // 第一次学习，如果答对记为熟词，答错记为生词
        if (currentLearn == null) {
            learn.setStudentId(studentId);
            learn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            learn.setStudyModel(studyModel);
            learn.setType(1);
            learn.setStudyCount(1);
            learn.setLearnCount(1);
            learn.setUpdateTime(now);
            StudyFlow currentStudyFlow = super.getCurrentStudyFlow(studentId);
            if (currentStudyFlow != null) {
                learn.setFlowName(currentStudyFlow.getFlowName());
            }
            if (courseLearnCount == 0) {
                // 首次学习当前课程，记录课程首次学习时间
                learn.setFirstStudyTime(now);
            }
            if (isTrue) {
                // 如果认识该单词，记为熟词
                learn.setStatus(1);
                learn.setFirstIsKnown(1);
            } else {
                learn.setStatus(0);
                learn.setFirstIsKnown(0);
                // 单词不认识将该单词记入记忆追踪中

                    saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 0);


            }

            MemoryServiceImpl.packageAboutStudyPlan(learn, studentId, capacityStudentUnitMapper, studentStudyPlanMapper);

            int count = learnMapper.insert(learn);

            // 统计初出茅庐勋章
            executorService.execute(() -> medalAwardAsync.inexperienced(student));

            if (count > 0 && total == (plan + 1)) {
                return ServerResponse.createBySuccess();
            }
            if (count > 0) {
                return ServerResponse.createBySuccess();
            }
        } else {
            learn.setStudyCount(currentLearn.getStudyCount() + 1);
            if (isTrue) {

                capacityPicture = (CapacityPicture) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 0);

            } else {

                capacityPicture = (CapacityPicture) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 0);

            }
            // 更新学习记录
            currentLearn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            session.removeAttribute(TimeConstant.BEGIN_START_TIME);

            currentLearn.setStudyCount(currentLearn.getStudyCount() + 1);
            // 熟词
            currentLearn.setStatus(memoryDifficult == 0 ? 1 : 0);

            currentLearn.setUpdateTime(now);

            MemoryServiceImpl.packageAboutStudyPlan(currentLearn, studentId, capacityStudentUnitMapper, studentStudyPlanMapper);

            int i = learnMapper.updateById(currentLearn);


            if (i > 0) {
                return ServerResponse.createBySuccess();
            }
        }*/
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }


    /**
     * 判断学生是否在本系统首次学习，如果是记录首次学习时间
     *
     * @param session
     * @param student
     */
    private void judgeIsFirstStudy(HttpSession session, Student student) {
        if (student.getFirstStudyTime() == null) {
            // 说明学生是第一次在本系统学习，记录首次学习时间
            student.setFirstStudyTime(new Date());
            studentMapper.updateByPrimaryKeySelective(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }
    }
}

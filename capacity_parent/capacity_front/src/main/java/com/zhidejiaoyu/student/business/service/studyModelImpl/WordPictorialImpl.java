package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.PictureUtil;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.learn.PerceiveEngineUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
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
    private SaveData saveData;
    @Resource
    private RedisOpt redisOpt;
    private Integer model = 1;
    private Integer type = 1;
    private Integer easyOrHard = 1;
    private String studyModel = "单词图鉴";


    @Override
    public Object getStudy(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        saveData.judgeIsFirstStudy(session, student);
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
                            Integer plan, Integer total, Long courseId,
                            Long flowId) {
        Student student = getStudent(session);
        if (saveData.saveVocabularyModel(student, session, unitId, wordId, isTrue, plan, total,
                flowId, easyOrHard, type, studyModel)) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }


}

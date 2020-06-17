package com.zhidejiaoyu.student.business.learn.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.study.memorydifficulty.WordMemoryDifficulty;
import com.zhidejiaoyu.common.utils.PictureUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.learn.PerceiveEngineUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.WordPictorialVo;
import com.zhidejiaoyu.student.business.learn.common.SaveData;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.learn.vo.GetVo;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.service.impl.ReviewServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 单词图鉴获取保存数据
 */
@Service(value = "wordPictorialService")
@Slf4j
public class WordPictorialServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private SaveData saveData;
    @Resource
    private RedisOpt redisOpt;
    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private WordMemoryDifficulty wordMemoryDifficulty;

    private Integer model = 1;
    private Integer type = 1;
    private Integer easyOrHard = 1;
    private String studyModel = "单词图鉴";
    private Integer modelType=1;

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        currentDayOfStudyRedisOpt.saveStudyModel(student.getId(), studyModel, unitId);
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        saveData.judgeIsFirstStudy(session, student);
        //获取当前单元下的learnId
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, unitId, easyOrHard,1);
        //获取是否有可以学习的单词信息
        int wordCount = unitVocabularyNewMapper.countWordPictureByUnitId(unitId, learnNew.getGroup());
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词图鉴信息！", unitId);
            return ServerResponse.createByErrorMessage("The unit no pictures");
        }
        WordPictorialVo vo = new WordPictorialVo();
        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct = studyCapacityMapper.selectNeedReviewWord(unitId, studentId, DateUtil.DateTime(), type, easyOrHard, learnNew.getGroup());
        // 没有需要复习的
        if (correct == null) {
            correct = this.getStudyWords(unitId, studentId, type, model, learnNew.getGroup(), studyModel);
            if (correct == null) {
                return super.toUnitTest();
            }
            vo.setStudyNew(true);
            vo.setMemoryStrength("0.00");

        } else {
            vo.setStudyNew(false);
            vo.setMemoryStrength(correct.get("memory_strength").toString());
        }

        // 记录学生开始学习的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 记忆难度
        StudyCapacity studyCapacity = new StudyCapacity();
        studyCapacity.setStudentId(studentId);
        studyCapacity.setUnitId(unitId);
        studyCapacity.setWordId(Long.valueOf(correct.get("id").toString()));
        Object faultTime = correct.get("fault_time");
        Object memoryStrength = correct.get("memory_strength");
        studyCapacity.setType(type);
        if (faultTime == null) {
            studyCapacity.setFaultTime(0);
        } else {
            studyCapacity.setFaultTime(Integer.parseInt(faultTime.toString()));
        }
        if (memoryStrength == null) {
            studyCapacity.setMemoryStrength(0.0);
        } else {
            studyCapacity.setMemoryStrength(Double.parseDouble(memoryStrength.toString()));
        }
        int hard = wordMemoryDifficulty.getMemoryDifficulty(studyCapacity);
        vo.setMemoryDifficulty(hard);
        // 认知引擎
        vo.setEngine(PerceiveEngineUtil.getPerceiveEngine(hard, studyCapacity.getMemoryStrength()));
        // 读音url
        vo.setReadUrl(baiduSpeak.getLanguagePath(correct.get("word").toString()));
        // 单词图片
        vo.setRecordpicurl(PictureUtil.getPictureByUnitId(ReviewServiceImpl.packagePictureUrl(correct), unitId));

        // 2. 从本课程非本单元下随机获取三个题, 三个作为错题, 并且id不等于正确题id
        List<Map<String, Object>> mapErrorVocabulary = vocabularyMapper.getWordIdNewByCourse(new Long(correct.get("id").toString()), unitId);
        // 四道题
        mapErrorVocabulary.add(correct);
        // 随机打乱顺序
        Collections.shuffle(mapErrorVocabulary);
        // 封装选项正确答案
        Map<String, Boolean> subject = new HashMap<>(16);
        for (Map<String, Object> m : mapErrorVocabulary) {
            boolean b = false;
            if (m.get("word").equals(correct.get("word"))) {
                b = true;
            }
            subject.put(m.get("word").toString(), b);
        }
        vo.setType(2);
        // 把四个选项添加到correct正确答案数据中
        vo.setSubject(subject);
        vo.setWordCount(wordCount);
        // 5. 是否是第一次学习单词图鉴，true:第一次学习，进入学习引导页；false：不是第一次学习
        vo.setFirstStudy(redisOpt.getGuideModel(studentId, studyModel));
        // 记录学生开始学习该单词/例句的时间
        vo.setWord(correct.get("word").toString());
        vo.setId(Long.parseLong(correct.get("id").toString()));
        vo.setSound_mark(correct.get("sound_mark") == null ? null : correct.get("sound_mark").toString());
        vo.setMiddlePictureUrl(correct.get("middlePictureUrl") == null ? null : correct.get("middlePictureUrl").toString());
        vo.setSmallPictureUrl(correct.get("smallPictureUrl") == null ? null : correct.get("smallPictureUrl").toString());
        vo.setWord_chinese(correct.get("word_chinese").toString());
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 获取未学过的单元信息
     *
     * @param unitId
     * @param studentId
     * @param type
     * @param model
     * @return
     */
    public Map<String, Object> getStudyWords(Long unitId, Long studentId, Integer type, Integer model, Integer group, String studyModel) {
        Map<String, Object> correct;
        //获取当前单词模块已经学习过的wordId
        List<Long> longs = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, studentId, studyModel, 1);
        // 获取新词
        correct = learnNewMapper.selectStudyMap(studentId, unitId, longs, type, model, group);
        return correct;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object saveStudy(HttpSession session, GetVo getVo) {
        Student student = getStudent(session);
        getVo.setEasyOrHard(easyOrHard);
        getVo.setType(type);
        getVo.setStudyModel(studyModel);
        getVo.setModel(modelType);
        if (saveData.saveVocabularyModel(student, session, getVo)) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }


}

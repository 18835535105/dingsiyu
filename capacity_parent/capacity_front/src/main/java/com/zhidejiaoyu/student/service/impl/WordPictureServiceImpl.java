package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.WordPictureUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.PerceiveEngine;
import com.zhidejiaoyu.student.service.WordPictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class WordPictureServiceImpl extends BaseServiceImpl<VocabularyMapper, Vocabulary> implements WordPictureService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 记忆难度
     */
    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CapacityPictureMapper capacityPictureMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private WordPictureUtil wordPictureUtil;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    /**
     * 获取单词图鉴学习数据
     *
     * @param session
     * @param courseId
     * @param unitId
     * @param plan
     * @return
     */
    @Override
    public Object getWordPicture(HttpSession session, Long courseId, Long unitId, Integer plan) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        judgeIsFirstStudy(session, student);

        int wordCount = unitVocabularyMapper.countWordPictureByUnitId(unitId);
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词图鉴信息！", unitId);
            return ServerResponse.createByErrorMessage("The unit no pictures");
        }

        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct = capacityPictureMapper.selectNeedReviewWord(unitId, studentId, DateUtil.DateTime());

        // 没有需要复习的
        if (correct == null) {
            // 获取新词
            correct = vocabularyMapper.selectPictureWord(studentId, unitId, plan);
            if (correct == null) {
                return super.toUnitTest();
            }
            // 是新单词
            correct.put("studyNew", true);
            // 记忆强度
            correct.put("memoryStrength", 0.00);
        }else{
            // 不是新词
            correct.put("studyNew", false);
            // 记忆强度
            correct.put("memoryStrength", correct.get("memory_strength"));
        }

        // 记录学生开始学习的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 记忆难度
        CapacityPicture cp = new CapacityPicture();
        cp.setStudentId(studentId);
        cp.setUnitId(unitId);
        cp.setVocabularyId(Long.valueOf(correct.get("id").toString()));
        Object fault_time = correct.get("fault_time");
        Object memory_strength = correct.get("memory_strength");
        if(fault_time==null){
            cp.setFaultTime(0);
        }else {
            cp.setFaultTime(Integer.parseInt(fault_time.toString()));
        }
        if(memory_strength==null){
            cp.setMemoryStrength(0.0);
        }else {
            cp.setMemoryStrength(Double.parseDouble(memory_strength.toString()));
        }
        Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cp, 1);
        correct.put("memoryDifficulty", hard);

        // 认知引擎
        correct.put("engine", PerceiveEngine.getPerceiveEngine(hard, cp.getMemoryStrength()));

        // 读音url
        correct.put("readUrl", baiduSpeak.getLanguagePath(correct.get("word").toString()));

        // 2. 从本课程非本单元下随机获取三个题, 三个作为错题, 并且id不等于正确题id
        List<Map<String, Object>> mapErrorVocabulary = vocabularyMapper.getWordIdByCourse(new Long(correct.get("id").toString()), courseId, unitId);
        mapErrorVocabulary.add(correct); // 四道题
        Collections.shuffle(mapErrorVocabulary); // 随机打乱顺序

        // 封装选项正确答案
        Map subject = new HashMap(16);
        for (Map m : mapErrorVocabulary) {

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
        Integer count = unitMapper.countWordByUnitidByPic(unitId);
        correct.put("wordCount", count);

        // 5. 是否是第一次学习单词图鉴，true:第一次学习，进入学习引导页；false：不是第一次学习
        Integer the = learnMapper.theFirstTimeToWordPic(studentId);
        if(the==null) {
            correct.put("firstStudy", true);
            // 初始化一条数据，引导页进行完之后进入学习页面
            Learn learn = new Learn();
            learn.setStudentId(studentId);
            learn.setStudyModel("单词图鉴");
            learnMapper.insert(learn);
        }else {
            correct.put("firstStudy", false);
        }

        // 记录学生开始学习该单词/例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        return ServerResponse.createBySuccess(correct);
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

    /**
     * 单词图鉴单元测试
     * ‘看词选图’、‘听音选图’、‘看图选词’
     * @param session
     * @param unitId 测试的单元
     * @param isTrue 是否确认消费1金币进行测试 true:是；false：否（默认）
     * @return
     */
    @Override
    public ServerResponse<Object> getWordPicUnitTest(HttpSession session, Long unitId, Long courseId, Boolean isTrue) {
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 获取单元下所有有图片的单词
        List<Vocabulary> list = vocabularyMapper.getWordPicAll(unitId);

        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(list.size() * 4);

        // 分题工具类
        Map<String, Object> map = wordPictureUtil.allocationWord(list, listSelect, null);
        return ServerResponse.createBySuccess(map);
    }
}

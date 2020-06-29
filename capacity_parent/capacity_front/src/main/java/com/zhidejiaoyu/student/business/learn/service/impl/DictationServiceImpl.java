package com.zhidejiaoyu.student.business.learn.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.DictationVo;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.learn.common.SaveData;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.learn.vo.GetVo;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "dictationService")
@Slf4j
public class DictationServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Resource
    private SaveData saveData;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;
    @Resource
    private CourseFeignClient courseFeignClient;
    @Resource
    private RedisOpt redisOpt;
    private Integer type = 4;
    private Integer easyOrHard = 2;
    private String studyModel = "慧听写";
    private Integer modelType=1;


    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        // 获取当前学生信息
        Student student = getStudent(session);
        currentDayOfStudyRedisOpt.saveStudyModel(student.getId(), studyModel, unitId);
        Long studentId = student.getId();
        Map<String, Object> map = new HashMap<>();
        saveData.judgeIsFirstStudy(session, student);
        // 记录学生开始学习该单词/例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, unitId, easyOrHard,1);
        // 查询学生当前单元下已学习单词的个数，即学习进度
        Integer plan = learnExtendMapper.countLearnWord(learnNews.getId(), unitId, learnNews.getGroup(), studyModel);
        // 获取当前单元下的所有单词的总个数
        Integer wordCount = courseFeignClient.countLearnVocabularyByUnitIdAndGroup(unitId, learnNews.getGroup());
        DictationVo vo = new DictationVo();
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
        }
        if (wordCount.equals(plan)) {
            return super.toUnitTest();
        }
        //获取单词id
        List<Long> wordIdByUnitIdAndGroup = courseFeignClient.getWordIdByUnitIdAndGroup(unitId, learnNews.getGroup());
        StudyCapacity studyCapacity = studyCapacityMapper.selectLearnHistory(unitId, studentId, DateUtil.DateTime(), type, easyOrHard, wordIdByUnitIdAndGroup);
        // 1. 查询智能听写记忆追踪中是否有需要复习的单词
        Vocabulary vocabulary;
        // 2. 如果记忆追踪中没有需要复习的, 去单词表中取出一个单词,条件是(learn表中单词id不存在的)
        if (studyCapacity == null) {
            // 查询学习记录本模块学习过的所有单词id
            vocabulary = saveData.getVocabulary(unitId, student, learnNews.getGroup(), studyModel);
            // 是新单词
            vo.setStudyNew(true);
            // 记忆强度
            vo.setMemoryStrength(0.00);

        } else {
            vocabulary = vocabularyMapper.selectById(studyCapacity.getWordId());
            // 不是新词
            vo.setStudyNew(false);
            // 记忆强度
            vo.setMemoryStrength(studyCapacity.getMemoryStrength());
        }
        // 单元单词已学完,去单元测试
        if (vocabulary == null) {
            return super.toUnitTest();
        }
        // id
        vo.setId(vocabulary.getId());
        // 单词
        vo.setWord(vocabulary.getWord());
        // 中文意思
        vo.setWordChinese(vocabulary.getWordChinese());
        // 如果单词没音节,把音节字段设置为单词
        vo.setWordyj(StringUtil.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
        // 音标,读音url,词性
        vo.setSoundmark(vocabulary.getSoundMark());
        // 读音url
        vo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
        // 3. count单元表单词有多少个
        vo.setWordCount(wordCount);
        // 4. 该单元已学单词  ./
        vo.setPlan(plan);
        vo.setFirstStudy(redisOpt.getGuideModel(studentId, studyModel));
        return ServerResponse.createBySuccess(vo);

    }

    @Override
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

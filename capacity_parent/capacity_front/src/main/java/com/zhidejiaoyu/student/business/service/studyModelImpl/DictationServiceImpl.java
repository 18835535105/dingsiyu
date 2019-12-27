package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.github.pagehelper.util.StringUtil;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
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
    private RedisOpt redisOpt;
    private Integer type = 4;
    private Integer easyOrHard = 2;
    private String studyModel = "慧听写";


    @Override
    public Object getStudy(HttpSession session, Long unitId) {
        // 获取当前学生信息
        Student student = getStudent(session);
        Long studentId = student.getId();
        Map<String, Object> map = new HashMap<>();
        saveData.judgeIsFirstStudy(session, student);
        // 记录学生开始学习该单词/例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitId(studentId, unitId, easyOrHard);
        // 查询学生当前单元下已学习单词的个数，即学习进度
        Integer plan = learnExtendMapper.countLearnWord(learnNews.getId(), unitId, learnNews.getGroup(), studyModel);
        // 获取当前单元下的所有单词的总个数
        Integer wordCount = unitVocabularyNewMapper.countByUnitId(unitId, learnNews.getGroup());
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
        }
        if (wordCount.equals(plan)) {
            return super.toUnitTest();
        }
        //获取单词id
        StudyCapacity studyCapacity = studyCapacityMapper.selectLearnHistory(unitId, studentId, DateUtil.DateTime(), type, easyOrHard, learnNews.getGroup());
        // 1. 查询智能听写记忆追踪中是否有需要复习的单词
        Vocabulary vocabulary;
        // 2. 如果记忆追踪中没有需要复习的, 去单词表中取出一个单词,条件是(learn表中单词id不存在的)
        if (studyCapacity == null) {
            // 查询学习记录本模块学习过的所有单词id
            vocabulary = saveData.getVocabulary(unitId, student, learnNews.getGroup(), type);
            // 是新单词
            map.put("studyNew", true);
            // 记忆强度
            map.put("memoryStrength", 0.00);
        } else {
            vocabulary = vocabularyMapper.selectById(studyCapacity.getWordId());
            // 不是新词
            map.put("studyNew", false);
            // 记忆强度
            map.put("memoryStrength", vocabulary.getMemory_strength());
        }
        // 单元单词已学完,去单元测试
        if (vocabulary == null) {
            return super.toUnitTest();
        }
        // id
        map.put("id", vocabulary.getId());
        // 单词
        map.put("word", vocabulary.getWord());
        // 中文意思
        map.put("wordChinese", vocabulary.getWordChinese());

        // 如果单词没音节,把音节字段设置为单词
        if (StringUtil.isEmpty(vocabulary.getSyllable())) {
            map.put("wordyj", vocabulary.getWord());
        } else {
            // 音节
            map.put("wordyj", vocabulary.getSyllable());
        }
        // 音标,读音url,词性
        map.put("soundmark", vocabulary.getSoundMark());
        // 读音url
        map.put("readUrl", baiduSpeak.getLanguagePath(vocabulary.getWord()));
        // 3. count单元表单词有多少个    /.
        map.put("wordCount", wordCount);
        // 4. 该单元已学单词  ./
        map.put("plan", plan);
        map.put("firstStudy", redisOpt.getGuideModel(studentId, studyModel));
        return ServerResponse.createBySuccess(map);

    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue,
                            Integer plan, Integer total, Long courseId, Long flowId) {
        Student student = getStudent(session);
        if (saveData.saveVocabularyModel(student, session, unitId, wordId, isTrue, plan, total,
                flowId, easyOrHard, type, studyModel)) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }
}

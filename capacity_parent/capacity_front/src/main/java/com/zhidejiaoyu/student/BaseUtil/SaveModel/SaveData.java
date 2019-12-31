package com.zhidejiaoyu.student.BaseUtil.SaveModel;

import com.zhidejiaoyu.common.award.MedalAwardAsync;
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
import com.zhidejiaoyu.common.vo.WordCompletionStudyVo;
import com.zhidejiaoyu.common.vo.WordWriteStudyVo;
import com.zhidejiaoyu.common.vo.study.MemoryStudyVo;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.StudyCapacityLearn;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * 保存数据结构储存接口
 */
@Component
@Slf4j
public class SaveData extends BaseServiceImpl<LearnNewMapper, LearnNew> {
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private MedalAwardAsync medalAwardAsync;
    @Resource
    private ExecutorService executorService;
    @Resource
    private StudyCapacityLearn studyCapacityLearn;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;
    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private RedisOpt redisOpt;
    @Resource
    private ErrorLearnLogMapper errorLearnLogMapper;

    /**
     * 以字母或数字结尾
     */
    private static final String END_MATCH = ".*[a-zA-Z]$";


    public Object getStudyWord(HttpSession session, Long unitId, Student student,
                               Long studentId, Integer easyOrHard, String studyModel, Integer type) {
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        this.judgeIsFirstStudy(session, student);
        boolean firstStudy = redisOpt.getGuideModel(studentId, studyModel);
        //获取当前单元下的learnId
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

        if (studyCapacity != null) {
            // 返回达到黄金记忆点的单词信息
            return this.returnGoldWord(studyCapacity, plan.longValue(), firstStudy, wordCount.longValue(), type);
        }
        // 如果没有到达黄金记忆点的单词，获取当前学习进度的下一个单词
        return this.getNextMemoryWord(session, unitId, student, firstStudy, plan, wordCount, learnNews.getGroup(), type, studyModel);
    }

    /**
     * 记忆难度
     */
    @Resource
    private MemoryDifficultyUtil memoryDifficultyUtil;

    public boolean saveVocabularyModel(Student student, HttpSession session, Long unitId,
                                       Long wordId, boolean isTrue, Integer plan,
                                       Integer total, Long flowId, Integer easyOrHard, Integer type, String studyModel) {

        Date now = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        Long studentId = student.getId();
        LearnExtend learn = new LearnExtend();
        judgeIsFirstStudy(session, student);
        //获取校长id
        learn.setSchoolAdminId(Long.parseLong(teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId()).toString()));
        //获取学生学习当前模块的learn_id
        List<Long> learnIds = learnNewMapper.selectIdByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
        //如果有多余的删除
        Long learnId = learnIds.get(0);
        if (learnIds.size() > 1) {
            List<Long> longs = learnIds.subList(1, learnIds.size());
            learnNewMapper.deleteBatchIds(longs);
        }

        //获取learnExtend数据
        List<LearnExtend> learnExtends = learnExtendMapper.selectByLearnIdsAndWordIdAndStudyModel(learnId, wordId, studyModel);
        LearnExtend currentLearn = null;
        if (learnExtends.size() > 0) {
            currentLearn = learnExtends.get(0);
            //如果有多余的删除
            if (learnExtends.size() > 1) {
                List<LearnExtend> extendList = learnExtends.subList(1, learnIds.size());
                List<Long> deleteLong = new ArrayList<>();
                extendList.forEach(extend -> deleteLong.add(extend.getId()));
                learnNewMapper.deleteBatchIds(deleteLong);
            }
        }

        //查看慧默写  会听写  单词图鉴是否为上次学习 如果是 删除
        //          开始
        boolean flag;
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
                saveErrorLearnLog(unitId, type, easyOrHard, studyModel, learnNew, learn.getWordId());
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
                studyCapacity = studyCapacityLearn.saveCapacityMemory(learnNew, learn, student, true, type);
            } else {
                studyCapacity = studyCapacityLearn.saveCapacityMemory(learnNew, learn, student, false, type);
                saveErrorLearnLog(unitId, type, easyOrHard, studyModel, learnNew, learnExtends.get(0).getWordId());
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
            return true;
        }
        return false;
    }


    public StudyFlowNew getCurrentStudyFlowById(Long flowId) {
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

    private List<Map<String, Boolean>> getChinese(Long unitId, Long vocabularyId, String wordChinese) {
        List<Map<String, Boolean>> returnList = new ArrayList<>();
        List<String> strings = unitVocabularyNewMapper.selectInterferenceTerm(unitId, vocabularyId, wordChinese);
        Map<String, Boolean> map = new HashMap<>();
        map.put(wordChinese, true);
        returnList.add(map);
        for (String str : strings) {
            Map<String, Boolean> termMap = new HashMap<>();
            termMap.put(str, false);
            returnList.add(termMap);
        }
        Collections.shuffle(returnList);
        return returnList;
    }

    /**
     * 返回达到黄金记忆点的单词信息
     *
     * @param plan
     * @param wordCount
     * @param firstStudy
     * @return
     */
    private ServerResponse<Object> returnGoldWord(StudyCapacity studyCapacity, Long plan, boolean firstStudy,
                                                  Long wordCount, Integer type) {
        // 计算当前单词的记忆难度
        int memoryDifficulty = memoryDifficultyUtil.getMemoryDifficulty(studyCapacity, 1);
        // 计算当前单词的记忆强度
        double memoryStrength = studyCapacity.getMemoryStrength();
        Long vocabularyId = studyCapacity.getWordId();
        Vocabulary vocabulary = vocabularyMapper.selectById(vocabularyId);

        Long unitId = studyCapacity.getUnitId();

        String wordChinese = studyCapacity.getWordChinese();

        if (type.equals(3)) {
            MemoryStudyVo memoryStudyVo = getMemoryStudyVo(studyCapacity.getWord(), studyCapacity.getSyllable(),
                    plan, firstStudy, wordCount, memoryDifficulty, memoryStrength, vocabularyId, vocabulary, unitId, wordChinese, false);
            return ServerResponse.createBySuccess(memoryStudyVo);
        } else if (type.equals(5)) {
            WordWriteStudyVo wordWriteStudyVo = getWordWriteStudyVo(firstStudy, vocabulary, wordChinese, plan, wordCount);
            return ServerResponse.createBySuccess(wordWriteStudyVo);
        } else if (type.equals(6)) {
            WordCompletionStudyVo wordCompletionStudyVo = getWordCompletionStudyVo(firstStudy, memoryDifficulty, vocabulary, wordChinese, plan, wordCount);
            return ServerResponse.createBySuccess(wordCompletionStudyVo);
        }
        return null;
    }

    private WordCompletionStudyVo getWordCompletionStudyVo(boolean firstStudy, int memoryDifficulty, Vocabulary vocabulary, String wordChinese, long longValue, long longValue1) {
        WordCompletionStudyVo wordCompletionStudyVo = new WordCompletionStudyVo();
        String soundMark = StringUtils.isEmpty(vocabulary.getSoundMark()) ? "" : vocabulary.getSoundMark();
        wordCompletionStudyVo.setWordId(vocabulary.getId());
        wordCompletionStudyVo.setMemoryDifficulty(memoryDifficulty);
        wordCompletionStudyVo.setMemoryStrength(0.00);
        wordCompletionStudyVo.setSoundmark(soundMark);
        wordCompletionStudyVo.setWord(vocabulary.getWord());
        wordCompletionStudyVo.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
        wordCompletionStudyVo.setWordChinese(wordChinese);
        wordCompletionStudyVo.setPlan(longValue);
        wordCompletionStudyVo.setStudyNew(true);
        wordCompletionStudyVo.setFirstStudy(firstStudy);
        wordCompletionStudyVo.setWordCount(longValue1);
        wordCompletionStudyVo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
        Map<String, Object> returnMap = new HashMap<>();
        getStudyWordComplets(vocabulary.getWord(), returnMap);
        wordCompletionStudyVo.setWords(returnMap);
        return wordCompletionStudyVo;
    }

    private void getStudyWordComplets(String word, Map<String, Object> returnMap) {
        char[] chars = word.toCharArray();
        word = word.trim();
        List<Integer> letterList = new ArrayList<>();
        int starti = 0;
        List<String> strList = new ArrayList<>();
        for (char ch : chars) {
            strList.add(ch + "");
        }
        for (String letter : strList) {
            if (Pattern.matches(END_MATCH, letter) && !letter.equals(" ")) {
                letterList.add(starti);
            }
            starti++;
        }
        Random random = new Random();
        int size = letterList.size() / 2;
        Map<Integer, Integer> map = new HashMap<>();
        while (map.size() < size) {
            int i = random.nextInt(letterList.size());
            Integer integer = letterList.get(i);
            map.put(integer, integer);
        }
        Set<Integer> integers = map.keySet();
        List<String> returnList = new ArrayList<>();
        List<String> allList = new ArrayList<>();
        // StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strList.size(); i++) {
            if (integers.contains(i)) {
                String letts = strList.get(i);
                returnList.add(letts);
                allList.add("$&$");
                //builder.append("$&$");
            } else {
                allList.add(strList.get(i));
                returnList.add(strList.get(i));
                //builder.append(strList.get(i));
            }
        }
        returnMap.put("getWord", allList);
        returnMap.put("successWords", returnList);
    }

    private MemoryStudyVo getMemoryStudyVo(String word, String syllable, Long plan, boolean firstStudy, Long wordCount, int memoryDifficulty, double memoryStrength, Long vocabularyId, Vocabulary vocabulary, Long unitId, String wordChinese, Boolean studyNew) {

        MemoryStudyVo memoryStudyVo = new MemoryStudyVo();
        memoryStudyVo.setWordId(vocabularyId);
        memoryStudyVo.setMemoryDifficulty(memoryDifficulty);
        memoryStudyVo.setMemoryStrength(memoryStrength);
        memoryStudyVo.setSoundMark(StringUtils.isEmpty(vocabulary.getSoundMark()) ? "" : vocabulary.getSoundMark());
        memoryStudyVo.setWord(word);
        memoryStudyVo.setSyllable(syllable == null ? word : syllable);
        memoryStudyVo.setWordChinese(wordChinese);
        memoryStudyVo.setPlan(plan);
        memoryStudyVo.setStudyNew(studyNew);
        memoryStudyVo.setFirstStudy(firstStudy);
        memoryStudyVo.setWordCount(wordCount);
        memoryStudyVo.setReadUrl(baiduSpeak.getLanguagePath(word));
        memoryStudyVo.setEngine(PerceiveEngineUtil.getPerceiveEngine(memoryDifficulty, memoryStrength));
        memoryStudyVo.setWordChineseList(this.getChinese(unitId, vocabularyId, wordChinese));
        memoryStudyVo.setImgUrl(PictureUtil.getPictureByUnitId(vocabulary, unitId));
        return memoryStudyVo;
    }

    private WordWriteStudyVo getWordWriteStudyVo(boolean firstStudy, Vocabulary vocabulary, String wordChinese, long l, long l2) {
        WordWriteStudyVo wordWriteStudyVo = new WordWriteStudyVo();
        String soundMark = StringUtils.isEmpty(vocabulary.getSoundMark()) ? "" : vocabulary.getSoundMark();
        wordWriteStudyVo.setWordId(vocabulary.getId());
        wordWriteStudyVo.setMemoryStrength(0.00);
        wordWriteStudyVo.setSoundmark(soundMark);
        wordWriteStudyVo.setWord(vocabulary.getWord());
        wordWriteStudyVo.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
        wordWriteStudyVo.setWordChinese(wordChinese);
        wordWriteStudyVo.setPlan(l);
        wordWriteStudyVo.setStudyNew(true);
        wordWriteStudyVo.setFirstStudy(firstStudy);
        wordWriteStudyVo.setWordCount(l2);
        wordWriteStudyVo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
        return wordWriteStudyVo;
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
    private ServerResponse<Object> getNextMemoryWord(HttpSession session, Long unitId, Student student, boolean firstStudy,
                                                     Integer plan, Integer wordCount, Integer group, Integer type, String studyModel) {
        if (wordCount - 1 >= plan) {
            // 记录学生开始学习该单词的时间
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
            Vocabulary currentStudyWord = getVocabulary(unitId, student, group, studyModel);
            // 查询单词释义
            String wordChinese = unitVocabularyNewMapper.selectWordChineseByUnitIdAndWordId(unitId, currentStudyWord.getId());
            if (type.equals(3)) {
                MemoryStudyVo memoryStudyVo = getMemoryStudyVo(currentStudyWord.getWord(), currentStudyWord.getSyllable(),
                        plan.longValue(), firstStudy, wordCount.longValue(), 0,
                        0.00, currentStudyWord.getId(), currentStudyWord, unitId, wordChinese, true);
                return ServerResponse.createBySuccess(memoryStudyVo);
            } else if (type.equals(5)) {
                WordWriteStudyVo wordWriteStudyVo = getWordWriteStudyVo(firstStudy, currentStudyWord, wordChinese, plan.longValue(), wordCount.longValue());
                return ServerResponse.createBySuccess(wordWriteStudyVo);
            } else if (type.equals(6)) {
                WordCompletionStudyVo wordCompletionStudyVo = getWordCompletionStudyVo(firstStudy, 0, currentStudyWord, wordChinese, plan.longValue(), wordCount.longValue());
                return ServerResponse.createBySuccess(wordCompletionStudyVo);
            }
        }
        return null;
    }

    public Vocabulary getVocabulary(Long unitId, Student student, Integer group, String studyModel) {
        // 查询学习记录本模块学习过的所有单词id
        List<Long> wordIds = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, student.getId(), studyModel);
        return vocabularyMapper.selectOneWordNotInIdsNew(wordIds, unitId, group);
    }

    public void saveErrorLearnLog(Long unitId, int type, int easyOrHard, String studyModel, LearnNew learnNew, Long wordId) {
        ErrorLearnLog errorLearnLog = new ErrorLearnLog();
        errorLearnLog.setEasyOrHard(easyOrHard);
        errorLearnLog.setGroup(learnNew.getGroup());
        errorLearnLog.setStudentId(learnNew.getStudentId());
        errorLearnLog.setStudyModel(studyModel);
        errorLearnLog.setGroup(learnNew.getGroup());
        errorLearnLog.setType(type);
        errorLearnLog.setUnitId(unitId);
        errorLearnLog.setUpdateTime(new Date());
        errorLearnLog.setWordId(wordId);
        errorLearnLogMapper.insert(errorLearnLog);
    }

}

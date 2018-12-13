package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.MemoryStrengthUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.student.service.SentenceService;
import com.zhidejiaoyu.student.vo.SentenceWordInfoVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2018/5/21 15:15
 */
@Service
public class SentenceServiceImpl extends BaseServiceImpl<SentenceMapper, Sentence> implements SentenceService {

    private Logger log = LoggerFactory.getLogger(SentenceService.class);

    /**
     * 三小时
     */
    private final int pushRise = 3;
    
    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private MemoryStrengthUtil memoryStrengthUtil;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UnitSentenceMapper unitSentenceMapper;

    @Autowired
    private SentenceTranslateMapper sentenceTranslateMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    /**
     * 例句听力mapper
     */
    @Autowired
    private SentenceListenMapper sentenceListenMapper;

    /**
     * 例句默写mapper
     */
    @Autowired
    private SentenceWriteMapper sentenceWriteMapper;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudyCountMapper studyCountMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Override
    public ServerResponse<SentenceTranslateVo> getSentenceTranslate(HttpSession session, Long unitId, int Intclassify, Integer type) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 转换类型
        String classify = commonMethod.getTestType(Intclassify);

        boolean firstStudy = commonMethod.isFirst(student.getId(), classify);
        if (firstStudy) {
            Learn learn = new Learn();
            learn.setStudentId(student.getId());
            learn.setStudyModel(classify);
            learnMapper.insert(learn);
        }

        // 记录学生开始学习该例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        Long courseId = unitMapper.selectCourseIdByUnitId(unitId);
        // 查询当前课程的学习遍数
        Integer learnCount = studyCountMapper.selectMaxCountByCourseId(student.getId(), courseId);

        // 查询学生当前单元下已学习例句的个数，即学习进度
        Long plan = learnMapper.countLearnWord(student.getId(), unitId, classify, learnCount == null ? 1 : learnCount);

        // 获取当前单元下的所有例句的总个数
        Long sentenceCount = sentenceMapper.countByUnitId(unitId);
        if (sentenceCount == 0) {
            log.error("单元 {} 下没有例句信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有例句！");
        }
        if (sentenceCount.equals(plan)) {
            return ServerResponse.createBySuccess(TestResponseCode.TO_UNIT_TEST.getCode(), TestResponseCode.TO_UNIT_TEST.getMsg());
        }

        // 查看当前单元下记忆追踪中有无达到黄金记忆点的例句
        // 例句翻译
        if ("例句翻译".equals(classify)) {
            SentenceTranslateExample example = new SentenceTranslateExample();
            example.createCriteria().andStudentIdEqualTo(student.getId()).andUnitIdEqualTo(unitId)
                    .andPushLessThan(new Date());
            example.setOrderByClause("push asc");
            List<SentenceTranslate> sentenceTranslates = sentenceTranslateMapper.selectByExample(example);

            // 有到达黄金记忆点的例句优先复习
            if (sentenceTranslates.size() > 0) {
                // 返回达到黄金记忆点的例句信息
                SentenceTranslate sentenceTranslate = sentenceTranslates.get(0);
                return this.returnGoldWord(sentenceTranslate, plan, firstStudy, sentenceCount, null, null, type);
            }

            // 获取当前学习进度的下一个例句
            // 获取当前单元已学习的当前模块的例句id
            List<Long> ids = learnMapper.selectLearnedWordIdByUnitId(student, unitId, "例句翻译", learnCount);
            Sentence sentence = sentenceMapper.selectOneSentenceNotInIds(ids, unitId);
            SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVo(plan, firstStudy, sentenceCount, type, sentence);
            sentenceTranslateVo.setStudyNew(true);
            return ServerResponse.createBySuccess(sentenceTranslateVo);
        }

        if ("例句听力".equals(classify)) {
            SentenceListenExample example = new SentenceListenExample();
            example.createCriteria().andStudentIdEqualTo(student.getId()).andUnitIdEqualTo(unitId)
                    .andPushLessThan(new Date());
            example.setOrderByClause("push asc");
            List<SentenceListen> selectByExample = sentenceListenMapper.selectByExample(example);

            // 有到达黄金记忆点的例句优先复习
            if (selectByExample.size() > 0) {
                // 返回达到黄金记忆点的例句信息
                SentenceListen sentenceListen = selectByExample.get(0);
                return this.returnGoldWord(null, plan, firstStudy, sentenceCount, sentenceListen, null, type);
            }

            // 如果没有到达黄金记忆点的例句
            // 获取当前学习进度的下一个例句
            // 获取当前单元已学习的当前模块的例句id
            List<Long> ids = learnMapper.selectLearnedWordIdByUnitId(student, unitId, "例句听力", learnCount);
            Sentence sentence = sentenceMapper.selectOneSentenceNotInIds(ids, unitId);
            return getSentenceTranslateVoServerResponse(firstStudy, plan, sentenceCount, sentence, type);
        }

        if ("例句默写".equals(classify)) {
            SentenceWriteExample example = new SentenceWriteExample();
            example.createCriteria().andStudentIdEqualTo(student.getId()).andUnitIdEqualTo(unitId)
                    .andPushLessThan(new Date());
            example.setOrderByClause("push asc");
            List<SentenceWrite> selectByExample = sentenceWriteMapper.selectByExample(example);

            // 有到达黄金记忆点的例句优先复习
            if (selectByExample.size() > 0) {
                // 返回达到黄金记忆点的例句信息
                SentenceWrite sentenceWrite = selectByExample.get(0);
                return this.returnGoldWord(null, plan, firstStudy, sentenceCount, null, sentenceWrite, type);
            }

            // 如果没有到达黄金记忆点的例句
            // 获取当前学习进度的下一个例句
            // 获取当前单元已学习的当前模块的例句id
            List<Long> ids = learnMapper.selectLearnedWordIdByUnitId(student, unitId, "例句默写", learnCount);
            Sentence sentence = sentenceMapper.selectOneSentenceNotInIds(ids, unitId);
            return getSentenceTranslateVoServerResponse(firstStudy, plan, sentenceCount, sentence, type);
        }
        return null;
    }

    private ServerResponse<SentenceTranslateVo> getSentenceTranslateVoServerResponse(boolean firstStudy, Long plan, Long sentenceCount, Sentence sentence, Integer type) {
        SentenceTranslateVo sentenceTranslateVo = new SentenceTranslateVo();

        sentenceTranslateVo.setId(sentence.getId());
        sentenceTranslateVo.setMemoryStrength(0.00);
        sentenceTranslateVo.setEnglish(sentence.getCentreExample().replace("#", " "));
        sentenceTranslateVo.setChinese(sentence.getCentreTranslate().replace("*", ""));
        sentenceTranslateVo.setReadUrl(baiduSpeak.getLanguagePath(sentence.getCentreExample().replace("#", " ")));
        if (type == 2) {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
        } else {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
        }
        sentenceTranslateVo.setEnglishList(commonMethod.getEnglishList(sentence.getCentreExample()));
        sentenceTranslateVo.setPlan(plan);
        sentenceTranslateVo.setStudyNew(true);
        sentenceTranslateVo.setFirstStudy(firstStudy);
        sentenceTranslateVo.setSentenceCount(sentenceCount);
        return ServerResponse.createBySuccess(sentenceTranslateVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveSentenceTranslate(HttpSession session, Learn learn, Boolean known, Integer plan,
                                                        Integer total, String classify) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Date now = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        Long studentId = student.getId();
        int count;

        if (student.getFirstStudyTime() == null) {
            // 说明学生是第一次在本系统学习，记录首次学习时间
            student.setFirstStudyTime(now);
            studentMapper.updateByPrimaryKeySelective(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }

        // 该课程是否是第一次学习，是第一次学习要保存课程首次学习时间
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(studentId).andCourseIdEqualTo(learn.getCourseId());
        int courseLearnCount = learnMapper.countByExample(learnExample);

        Integer maxCount = commonMethod.saveStudyCount(session, learn.getCourseId());

        Date learnTime = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);

        // 查询当前例句的学习记录数据
        Learn currentLearn = learnMapper.selectLearn(studentId, learn, classify, maxCount == null ? 1 : maxCount);
        // 保存学习记录
        // 当前课程学习的第一遍，当前例句是第一次学习，如果答对记为熟句，答错记为生句
        if (currentLearn == null) {
            learn.setStudentId(studentId);
            learn.setLearnTime(learnTime);
            learn.setStudyModel(classify);
            learn.setStudyCount(1);
            learn.setLearnCount(1);
            learn.setUpdateTime(now);
            if (courseLearnCount == 0) {
                // 首次学习当前课程，记录课程首次学习时间
                learn.setFirstStudyTime(now);
            }
            if (known) {
                // 如果认识该例句，记为熟句
                learn.setStatus(1);
                learn.setFirstIsKnown(1);
            } else {
                learn.setStatus(0);
                learn.setFirstIsKnown(0);
                // 不认识将该例句记入记忆追踪中
                this.saveCapacityMemory(learn, student, false, classify);
            }
            count = learnMapper.insertSelective(learn);
            if (count > 0 && total == (plan + 1)) {
                return ServerResponse.createBySuccess(TestResponseCode.TO_UNIT_TEST.getCode(), TestResponseCode.TO_UNIT_TEST.getMsg());
            }
            if (count > 0) {
                return ServerResponse.createBySuccess();
            }
        } else {
            // 不是第一次学习
            SentenceListen sentenceListen;
            if (known) {
                sentenceListen = this.saveCapacityMemory(learn, student, true, classify);
            } else {
                // 不认识将该例句记入记忆追踪中
                sentenceListen = this.saveCapacityMemory(learn, student, false, classify);
            }
            // 计算记忆难度
            Integer memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(sentenceListen, 2);
            // 更新学习记录
            currentLearn.setLearnTime(learnTime);
            currentLearn.setStudyCount(currentLearn.getStudyCount() + 1);
            if (memoryDifficult == 0) {
                // 熟词
                currentLearn.setStatus(1);
            } else {
                currentLearn.setStatus(0);
            }
            currentLearn.setLearnCount(maxCount);
            currentLearn.setUpdateTime(now);
            int i = learnMapper.updateByPrimaryKeySelective(currentLearn);
            
            // 默写模块错过三次在记忆时间上再加长三小时
            if("例句默写".equals(classify)) {
            	// 查询错误次数>=3 
            	Integer faultTime = sentenceWriteMapper.getFaultTime(studentId, learn.getExampleId());
            	if(faultTime != null && faultTime >= 3) {
            		// 如果错误次数>=3, 黄金记忆时间推迟3小时
            		sentenceWriteMapper.updatePush(studentId, learn.getExampleId(), pushRise);
            	}
            }
            
            if (i > 0) {
                return ServerResponse.createBySuccess();
            }
        }
        return null;
    }

    @Override
    public ServerResponse<SentenceWordInfoVo> getSentenceWordInfo(HttpSession session, Long unitId, Long courseId, String word) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        SentenceWordInfoVo sentenceWordInfoVo;
        int status = 1;

        // 查询当前单词
        List<Vocabulary> vocabularies = vocabularyMapper.selectIdsByWord(word);
        if (vocabularies.size() == 0) {
            sentenceWordInfoVo = new SentenceWordInfoVo();
            sentenceWordInfoVo.setState(4);
            return ServerResponse.createBySuccess(sentenceWordInfoVo);
        }
        Vocabulary vocabulary = vocabularies.get(0);
        Long wordId = vocabulary.getId();

        // 查询当前单词是否在学生应学单词中，即学生所学课程中是否有当前单词；如果有当前单词，查询出单词所在的单元
        List<UnitVocabulary> unitVocabularies = unitVocabularyMapper.selectByCourseIdAndWordId(courseId, wordId);
        if (unitVocabularies.size() == 0) {
            // 当前单词不在学生应学单词中
            sentenceWordInfoVo = packageSentenceWordInfoVo(courseId, unitId, vocabulary, status);
        } else {
            // 当前单词在学生应学单词中
            // 判断当前单词学生是否已经学习
            List<Learn> learns = learnMapper.selectByStudentIdAndWorldId(student.getId(), wordId);
            if (learns.size() == 0) {
                // 还没有学习过当前单词
                sentenceWordInfoVo = packageSentenceWordInfoVo(courseId, unitId, vocabulary, status);
            } else {
                // 判断当前例句所在单元中是否含有当前单词
                final Long finalUnitId = unitId;
                List<UnitVocabulary> unitVocabularies1 = unitVocabularies.stream().filter(unitVocabulary ->
                        unitVocabulary.getUnitId().equals(finalUnitId)).collect(Collectors.toList());
                // 已经学习过该单词
                if (unitVocabularies1.size() > 0) {
                    // 例句所在单元中包含当前单词，查找当前单元中单词的学习信息
                    status = 2;
                    sentenceWordInfoVo = packageSentenceWordInfoVo(courseId, unitId, vocabulary, status);
                } else {
                    // 例句所在单元不包含当前单词
                    // 判断当前单词在其他单元中是否有熟词记录
                    List<Learn> known = learns.stream().filter(learn -> learn.getStatus() == 1).collect(Collectors.toList());
                    if (known.size() > 0) {
                        // 有熟词记录
                        status = 2;
                        Learn learn = known.get(0);
                        courseId = learn.getCourseId();
                        unitId = learn.getUnitId();
                    }
                    sentenceWordInfoVo = packageSentenceWordInfoVo(courseId, unitId, vocabulary, status);
                }
            }
        }

        return ServerResponse.createBySuccess(sentenceWordInfoVo);
    }

    @Override
    public ServerResponse<String> saveUnknownWord(HttpSession session, Long unitId, Long courseId, Long wordId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 在学习记录中将指定单词置为生词
        learnMapper.updateUnknownWord(student, unitId, courseId, wordId);
        // 查看 记忆追踪-慧听写 中是否含有该单词的信息
        CapacityListen capacityListen = new CapacityListen();
        capacityListen.setStudentId(student.getId());
        capacityListen.setUnitId(unitId);
        capacityListen.setCourseId(courseId);
        capacityListen.setVocabularyId(wordId);
        List<CapacityListen> capacityListens = capacityListenMapper.selectByCapacityListen(capacityListen);
        if (capacityListens.size() > 0) {
            capacityListen = capacityListens.get(0);
            if (capacityListen.getMemoryStrength() < 1) {
                // 当前单词在慧追踪中已经是生词
                return ServerResponse.createByErrorMessage("当前单词已在生词本中！");
            }
            capacityListen.setMemoryStrength(0.12);
            capacityListen.setPush(GoldMemoryTime.getGoldMemoryTime(0.12, new Date()));
            capacityListenMapper.updateByPrimaryKeySelective(capacityListen);
        } else {
            // 将指定单词加入到记忆追踪中
            Vocabulary vocabulary = vocabularyMapper.selectByPrimaryKey(wordId);
            String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(unitId, wordId);
            capacityListen.setMemoryStrength(0.12);
            capacityListen.setPush(GoldMemoryTime.getGoldMemoryTime(0.12, new Date()));
            capacityListen.setWord(vocabulary.getWord());
            capacityListen.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
            capacityListen.setFaultTime(1);
            capacityListen.setWordChinese(wordChinese);
            capacityListenMapper.insertSelective(capacityListen);
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<List<CourseUnitVo>> getLearnCourseAndUnit(HttpSession session, Integer type) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<CourseUnitVo> courseUnitVos = new ArrayList<>();
        CourseUnitVo courseUnitVo;
        List<Map<String, Object>> resultMap;

        // 学生所有课程id及课程名
        List<Map<String, Object>> courses = courseMapper.selectCourseIdAndCourseNameByStudentId(studentId);

        // 学生课程下所有例句的单元id及单元名
        if (courses.size() > 0) {
            List<Long> courseIds = new ArrayList<>(courses.size());
            courses.forEach(map -> courseIds.add((Long) map.get("id")));

            // 获取课程下所有例句的单元信息
            List<Map<String, Object>> sentenceUnits = unitSentenceMapper.selectUnitIdAndNameByCourseIds(studentId, courseIds);

            // 已经进行过单元闯关的单元
            Map<Long, Map<Long, Long>> testMap = null;
            // 还没有学习的单元
            Map<Long, Map<Long, Long>> unLearnMap = null;
            if (sentenceUnits.size() > 0) {
                List<Long> unitIds = new ArrayList<>(sentenceUnits.size());
                sentenceUnits.forEach(map -> unitIds.add((Long) map.get("id")));
                testMap = testRecordMapper.selectHasUnitTest(studentId, unitIds);
                unLearnMap = learnMapper.selectUnlearnUnit(studentId, unitIds);
            }

            for (Map<String, Object> courseMap : courses) {
                courseUnitVo = new CourseUnitVo();
                resultMap = new ArrayList<>();
                courseUnitVo.setCourseId((Long) courseMap.get("id"));
                courseUnitVo.setCourseName(courseMap.get("courseName").toString());

                // 存放单元信息
                Map<String, Object> unitInfoMap;
                for (Map<String, Object> unitMap : sentenceUnits) {
                    unitInfoMap = new HashMap<>(16);
                    unitInfoMap.put("unitId", unitMap.get("id"));
                    unitInfoMap.put("unitName", unitMap.get("unitName"));
                    if (Objects.equals(courseMap.get("id"), unitMap.get("courseId"))) {
                        if (testMap != null && testMap.containsKey(unitMap.get("id"))) {
                            // 当前单元已进行过单元闯关，标记为已学习
                            unitInfoMap.put("state", 4);
                        } else if (unLearnMap != null && unLearnMap.containsKey(unitMap.get("id"))) {
                            // 当前单元还未学习
                            unitInfoMap.put("state", 1);
                        } else {
                            // 正在学习
                            unitInfoMap.put("state", 3);
                        }
                    }
                    resultMap.add(unitInfoMap);
                }
                courseUnitVo.setUnitVos(resultMap);
                courseUnitVos.add(courseUnitVo);
            }
        }
        return ServerResponse.createBySuccess(courseUnitVos);
    }

    /**
     * 封装单词信息
     *
     * @param courseId   单词课程id
     * @param unitId     单词单元id
     * @param vocabulary 单词信息
     * @param status     单词操作状态
     * @return SentenceWordInfoVo
     */
    private SentenceWordInfoVo packageSentenceWordInfoVo(Long courseId, Long unitId, Vocabulary vocabulary, int status) {
        SentenceWordInfoVo sentenceWordInfoVo = new SentenceWordInfoVo();
        sentenceWordInfoVo.setWordId(vocabulary.getId());
        sentenceWordInfoVo.setState(status);
        sentenceWordInfoVo.setWord(vocabulary.getWord());
        sentenceWordInfoVo.setWordChinese(vocabulary.getWordChinese());
        if (status == 2) {
            sentenceWordInfoVo.setCourseId(courseId);
            sentenceWordInfoVo.setUnitId(unitId);
        }
        return sentenceWordInfoVo;
    }

    /**
     * 保存记忆追踪数据
     *
     * @param learn    学习信息
     * @param student  学生信息
     * @param isKnown  是否认识该例句 true：认识；false：不认识
     * @param classify 例句类型
     */
    private SentenceListen saveCapacityMemory(Learn learn, Student student, boolean isKnown, String classify) {
        Sentence sentence = sentenceMapper.selectByPrimaryKey(learn.getExampleId());

        if ("例句翻译".equals(classify)) {
            // 通过学生id，单元id和例句id获取当前例句的记忆追踪信息
            SentenceTranslate sentenceTranslate = sentenceTranslateMapper.selectByStuIdAndUnitIdAndWordId(student.getId(), learn.getUnitId(), sentence.getId());
            if (sentenceTranslate == null) {
                if (!isKnown) {
                    sentenceTranslate = new SentenceTranslate();
                    sentenceTranslate.setCourseId(learn.getCourseId());
                    sentenceTranslate.setFaultTime(1);
                    sentenceTranslate.setMemoryStrength(0.12);
                    sentenceTranslate.setStudentId(student.getId());
                    sentenceTranslate.setUnitId(learn.getUnitId());
                    sentenceTranslate.setVocabularyId(learn.getExampleId());
                    sentenceTranslate.setWord(sentence.getCentreExample());
                    sentenceTranslate.setWordChinese(sentence.getCentreTranslate());

                    Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());
                    sentenceTranslate.setPush(push);
                    sentenceTranslateMapper.insert(sentenceTranslate);
                }

            } else {
                // 认识该例句
                if (isKnown) {
                    // 重新计算黄金记忆点时间
                    double memoryStrength = sentenceTranslate.getMemoryStrength();
                    Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                    sentenceTranslate.setPush(push);

                    // 重新计算记忆强度
                    sentenceTranslate.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, true));
                } else {
                    // 错误次数在原基础上 +1
                    sentenceTranslate.setFaultTime(sentenceTranslate.getFaultTime() + 1);
                    // 重新计算黄金记忆点时间
                    double memoryStrength = sentenceTranslate.getMemoryStrength();
                    Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                    sentenceTranslate.setPush(push);

                    // 重新计算记忆强度
                    sentenceTranslate.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, false));
                }
                sentenceTranslateMapper.updateByPrimaryKeySelective(sentenceTranslate);
                return sentenceTranslate;
            }
        } else if ("例句听力".equals(classify)) {
            // 通过学生id，单元id和例句id获取当前例句的记忆追踪信息
            SentenceListen sentenceListen = sentenceListenMapper.selectByStuIdAndUnitIdAndWordId(student.getId(), learn.getUnitId(), sentence.getId());

            if (sentenceListen == null) {
                if (!isKnown) {
                    sentenceListen = new SentenceListen();
                    sentenceListen.setCourseId(learn.getCourseId());
                    sentenceListen.setFaultTime(1);
                    sentenceListen.setMemoryStrength(0.12);
                    sentenceListen.setStudentId(student.getId());
                    sentenceListen.setUnitId(learn.getUnitId());
                    sentenceListen.setVocabularyId(learn.getExampleId());
                    sentenceListen.setWord(sentence.getCentreExample());
                    sentenceListen.setWordChinese(sentence.getCentreTranslate());

                    Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());
                    sentenceListen.setPush(push);
                    sentenceListenMapper.insert(sentenceListen);
                }

            } else {
                // 认识该例句
                if (isKnown) {
                    // 重新计算黄金记忆点时间
                    double memoryStrength = sentenceListen.getMemoryStrength();
                    Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                    sentenceListen.setPush(push);

                    // 重新计算记忆强度
                    sentenceListen.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, true));
                } else {
                    // 错误次数在原基础上 +1
                    sentenceListen.setFaultTime(sentenceListen.getFaultTime() + 1);
                    // 重新计算黄金记忆点时间
                    double memoryStrength = sentenceListen.getMemoryStrength();
                    Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                    sentenceListen.setPush(push);

                    // 重新计算记忆强度
                    sentenceListen.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, false));
                }
                sentenceListenMapper.updateByPrimaryKeySelective(sentenceListen);

                return sentenceListen;
            }
        } else if ("例句默写".equals(classify)) {
            // 通过学生id，单元id和例句id获取当前例句的记忆追踪信息
            SentenceWrite sentenceWrite = sentenceWriteMapper.selectByStuIdAndUnitIdAndWordId(student.getId(), learn.getUnitId(), sentence.getId());

            if (sentenceWrite == null) {
                if (!isKnown) {
                    sentenceWrite = new SentenceWrite();
                    sentenceWrite.setCourseId(learn.getCourseId());
                    sentenceWrite.setFaultTime(1);
                    sentenceWrite.setMemoryStrength(0.12);
                    sentenceWrite.setStudentId(student.getId());
                    sentenceWrite.setUnitId(learn.getUnitId());
                    sentenceWrite.setVocabularyId(learn.getExampleId());
                    sentenceWrite.setWord(sentence.getCentreExample());
                    sentenceWrite.setWordChinese(sentence.getCentreTranslate());

                    Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());
                    sentenceWrite.setPush(push);
                    sentenceWriteMapper.insert(sentenceWrite);
                }

            } else {
                // 认识该例句
                if (isKnown) {
                    // 重新计算黄金记忆点时间
                    double memoryStrength = sentenceWrite.getMemoryStrength();
                    Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                    sentenceWrite.setPush(push);

                    // 重新计算记忆强度
                    sentenceWrite.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, true));
                } else {
                    // 错误次数在原基础上 +1
                    sentenceWrite.setFaultTime(sentenceWrite.getFaultTime() + 1);
                    // 重新计算黄金记忆点时间
                    double memoryStrength = sentenceWrite.getMemoryStrength();
                    Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                    sentenceWrite.setPush(push);

                    // 重新计算记忆强度
                    sentenceWrite.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, false));
                }
                sentenceWriteMapper.updateByPrimaryKeySelective(sentenceWrite);
                return sentenceWrite;
            }
        }

        return null;
    }

    /**
     * 返回记忆达到黄金记忆点的例句信息
     *
     * @param sentenceTranslate 记忆追踪-例句翻译信息
     * @param plan              进度
     * @param firstStudy        是否是第一次学习
     * @param sentenceCount     当前单元例句总数
     * @param sentenceListen    记忆追踪-例句听力信息
     * @param sentenceWrite     记忆追踪-例句默写信息
     * @param type
     * @return 例句翻译学习页面展示信息
     */
    private ServerResponse<SentenceTranslateVo> returnGoldWord(SentenceTranslate sentenceTranslate, Long plan, boolean firstStudy,
                                                               Long sentenceCount, SentenceListen sentenceListen, SentenceWrite sentenceWrite, Integer type) {
        SentenceTranslateVo sentenceTranslateVo;
        // 例句翻译
        if (sentenceTranslate != null && StringUtils.isNotBlank(sentenceTranslate.getWord())) {
            Sentence sentence = sentenceMapper.selectByPrimaryKey(sentenceTranslate.getVocabularyId());
            // 计算当前例句的记忆强度
            double memoryStrength = sentenceTranslate.getMemoryStrength();
            sentenceTranslateVo = getSentenceTranslateVo(plan, firstStudy, sentenceCount, type, sentence);
            sentenceTranslateVo.setStudyNew(false);
            sentenceTranslateVo.setMemoryStrength(memoryStrength);

        } else if (sentenceListen != null && StringUtils.isNotBlank(sentenceListen.getWord())) {
            // 例句听力
            // 计算当前例句的记忆强度
            Sentence sentence = sentenceMapper.selectByPrimaryKey(sentenceListen.getVocabularyId());
            double memoryStrength = sentenceListen.getMemoryStrength();
            sentenceTranslateVo = this.getSentenceVo(sentence, firstStudy, plan, memoryStrength, sentenceCount, type);
        } else {
            // 例句默写
            // 计算当前例句的记忆强度
            Sentence sentence = sentenceMapper.selectByPrimaryKey(sentenceWrite.getVocabularyId());
            double memoryStrength = sentenceWrite.getMemoryStrength();
            sentenceTranslateVo = this.getSentenceVo(sentence, firstStudy, plan, memoryStrength, sentenceCount, type);
        }
        return ServerResponse.createBySuccess(sentenceTranslateVo);
    }

    private SentenceTranslateVo getSentenceTranslateVo(Long plan, boolean firstStudy, Long sentenceCount, Integer type, Sentence sentence) {
        SentenceTranslateVo sentenceTranslateVo = new SentenceTranslateVo();
        sentenceTranslateVo.setChinese(sentence.getCentreTranslate().replace("*", ""));
        sentenceTranslateVo.setEnglish(sentence.getCentreExample().replace("#", " "));
        sentenceTranslateVo.setFirstStudy(firstStudy);
        sentenceTranslateVo.setId(sentence.getId());
        sentenceTranslateVo.setPlan(plan);
        sentenceTranslateVo.setReadUrl(baiduSpeak.getLanguagePath(sentence.getCentreExample().replace("#", " ")));
        sentenceTranslateVo.setSentenceCount(sentenceCount);
        sentenceTranslateVo.setMemoryStrength(0.0);

        int nextInt = new Random().nextInt(2);
        if (nextInt % 2 == 0) {
            if (type == 2) {
                sentenceTranslateVo.setOrder(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
            }else {
                sentenceTranslateVo.setOrder(commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
            }
            sentenceTranslateVo.setRateList(commonMethod.getEnglishList(sentence.getCentreExample()));
        } else {
            if (type == 2) {
                sentenceTranslateVo.setOrder(commonMethod.getOrderChineseList(sentence.getCentreTranslate(), sentence.getTranslateDisturb()));
            } else {
                sentenceTranslateVo.setOrder(commonMethod.getOrderChineseList(sentence.getCentreTranslate(), null));
            }
            sentenceTranslateVo.setRateList(commonMethod.getChineseList(sentence.getCentreTranslate()));
        }
        return sentenceTranslateVo;
    }

    private SentenceTranslateVo getSentenceVo(Sentence sentence, boolean firstStudy, Long plan, double memoryStrength, Long sentenceCount, Integer type) {
        SentenceTranslateVo sentenceTranslateVo = new SentenceTranslateVo();
        sentenceTranslateVo.setChinese(sentence.getCentreTranslate().replace("*", ""));
        sentenceTranslateVo.setEnglish(sentence.getCentreExample().replace("#", " "));
        sentenceTranslateVo.setFirstStudy(firstStudy);
        sentenceTranslateVo.setId(sentence.getId());
        sentenceTranslateVo.setPlan(plan);
        sentenceTranslateVo.setMemoryStrength(memoryStrength);
        sentenceTranslateVo.setReadUrl(baiduSpeak.getLanguagePath(sentence.getCentreExample().replace("#", " ")));
        sentenceTranslateVo.setSentenceCount(sentenceCount);
        sentenceTranslateVo.setStudyNew(false);
        sentenceTranslateVo.setEnglishList(commonMethod.getEnglishList(sentence.getCentreExample()));
        if (type == 2) {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
        } else {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
        }
        return sentenceTranslateVo;
    }

}

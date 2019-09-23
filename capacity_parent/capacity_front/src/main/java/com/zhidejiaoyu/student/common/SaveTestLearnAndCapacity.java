package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.MemoryStrengthUtil;
import com.zhidejiaoyu.student.service.impl.MemoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * 测试结束保存学习记录和慧记忆信息
 *
 * @author wuchenxi
 * @date 2018/6/29 14:25
 */
@Slf4j
@Component
public class SaveTestLearnAndCapacity {

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private SentenceListenMapper sentenceListenMapper;

    @Autowired
    private SentenceTranslateMapper sentenceTranslateMapper;

    @Autowired
    private MemoryStrengthUtil memoryStrengthUtil;

    @Autowired
    private SentenceWriteMapper sentenceWriteMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Autowired
    private CapacityWriteMapper capacityWriteMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CapacityMemoryMapper capacityMemoryMapper;

    @Autowired
    private CapacityPictureMapper capacityPictureMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    /**
     * 分别保存例句或单词学习信息和记忆追踪信息
     *
     * @param correctWord   答对的单词/例句
     * @param errorWord     答错的单词/例句
     * @param correctWordId 答对的单词/例句 id
     * @param errorWordId   答错的单词/例句 id
     * @param session       session信息
     * @param student       当前学生信息
     * @param unitId        如果单元id长度为1，说明当前测试是以单元为单位的测试；如果长度大于1，说明当前测试是以课程为单位的测试
     * @param classify      学习类型 0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写 4=例句听写 5=例句翻译 6=例句默写
     * @return 响应信息
     */
    public void saveLearnAndCapacity(String[] correctWord, String[] errorWord, Long[] correctWordId,
                                     Long[] errorWordId, HttpSession session, Student student,
                                                       Long[] unitId, Integer classify) {
        // 保存正确单词/例句的学习记录和记忆追踪信息
        if (correctWord != null && correctWordId != null && correctWord.length > 0
                && correctWord.length == correctWordId.length) {
            int correctWordLength = correctWord.length;
            for (int i = 0; i < correctWordLength; i++) {
                if (unitId.length == 1) {
                    this.saveLearnAndCapacity(session, student, unitId[0], correctWordId[i], classify, true);
                } else {
                    this.saveLearnAndCapacity(session, student, unitId[i], correctWordId[i], classify, true);
                }
            }
        }

        // 保存错误单词/例句的学习记录和记忆追踪信息
        if (errorWord != null && errorWordId != null && errorWord.length > 0
                && errorWord.length == errorWordId.length) {
            int errorWordLength = errorWord.length;
            for (int i = 0; i < errorWordLength; i++) {
                if (unitId.length == 1) {
                    this.saveLearnAndCapacity(session, student, unitId[0], errorWordId[i], classify, false);
                } else {
                    this.saveLearnAndCapacity(session, student, unitId[i], errorWordId[i], classify, false);
                }
            }
        }
    }

    /**
     * 测试模块 保存学习记录和记忆追踪信息
     *
     * @param correctWord  答对的单词/例句
     * @param errorWord     答错的单词/例句
     * @param correctWordId 答对的单词/例句 id
     * @param errorWordId   答错的单词/例句 id
     * @param session       session信息
     * @param unitId        如果单元id长度为1，说明当前测试是以单元为单位的测试；如果长度大于1，说明当前测试是以课程为单位的测试
     * @param type   1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     * @return 响应信息
     */
    public int saveTestAndCapacity(String[] correctWord, String[] errorWord, Long[] correctWordId,
                                    Long[] errorWordId, HttpSession session,
                                    Long[] unitId, Integer type) {

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 保存正确单词/例句的学习记录和记忆追踪信息
        int count = 0;
        if (correctWord != null && correctWordId != null && correctWord.length > 0
                && correctWord.length == correctWordId.length) {
            int correctWordLength = correctWord.length;
            for (int i = 0; i < correctWordLength; i++) {
                if (unitId.length == 1) {
                    count = this.saveLearnAndCapacity(session, student, unitId[0], correctWordId[i], type, true);
                } else {
                    count = this.saveLearnAndCapacity(session, student, unitId[i], correctWordId[i], type, true);
                }
            }
        }

        // 保存错误单词/例句的学习记录和记忆追踪信息
        if (errorWord != null && errorWordId != null && errorWord.length > 0
                && errorWord.length == errorWordId.length) {
            int errorWordLength = errorWord.length;
            for (int i = 0; i < errorWordLength; i++) {
                if (unitId.length == 1) {
                    count = this.saveLearnAndCapacity(session, student, unitId[0], errorWordId[i], type, false);
                } else {
                    count = this.saveLearnAndCapacity(session, student, unitId[i], errorWordId[i], type, false);
                }
            }
        }
        return count;
    }

    /**
     * 保存学习记录和记忆追踪记录
     *
     * @param session session 信息
     * @param student 学生信息
     * @param unitId  单词/例句所属单元id
     * @param id      单词或句子的id
     * @param isTrue  题目是否答对
     */
    private int saveLearnAndCapacity(HttpSession session, Student student, Long unitId, Long id, Integer classify,
                                     boolean isTrue) {
        // 查询学习记录
        List<Learn> learns;
        if (classify <= 3) {
            // 查询单词的学习记录
            learns = learnMapper.selectLearnByIdAmdModel(student.getId(), unitId, Long.valueOf(id.toString()), null,
                    commonMethod.getTestType(classify));
        } else {
            // 查询句子的学习记录
            learns = learnMapper.selectLearnByIdAmdModel(student.getId(), unitId, null, Long.valueOf(id.toString()),
                    commonMethod.getTestType(classify));
        }

        Learn learn;
        if (learns.size() > 1) {
            learnMapper.deleteById(learns.get(0));
            learn = learns.get(1);
        } else if (learns.size() == 1) {
            learn = learns.get(0);
        } else {
            // 无学习记录
            log.warn("学生[{} - {} - {}]没有当前模块学习记录：单元id[{}],单词id[{}],模块type[{}]", student.getId(), student.getAccount(), student.getStudentName(), unitId, id, classify);
            return 0;
        }
        // 保存记忆追踪信息
        Object capacity = null;
        try {
            capacity = this.saveCapacityMemory(learn, student, isTrue, classify);

            if (classify <= 3) {
                MemoryServiceImpl.packageAboutStudyPlan(learn, student.getId(), capacityStudentUnitMapper, studentStudyPlanMapper);
            }

            // 判断学生今日复习30个生词且记忆强度达到50%
            dailyAwardAsync.todayReview(student);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 计算记忆难度
        int memoryDifficult = 0;
        if (capacity != null) {
            if (classify <= 3) {
                memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(capacity, 1);
            } else {
                memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(capacity, 2);
            }
        }
        // 更新学习记录
        learn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyCount(learn.getStudyCount() + 1);
        if (memoryDifficult == 0) {
            // 熟词
            learn.setStatus(1);
        } else {
            learn.setStatus(0);
        }
        learn.setUpdateTime(new Date());
        return learnMapper.updateById(learn);
    }

    /**
     * 保存记忆追踪数据
     *
     * @param learn    学习信息
     * @param student  学生信息
     * @param isKnown  是否答对该单词/例句 true：答对；false：答错
     * @param classify 测试的模块 0：单词图鉴 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @throws Exception exception
     */
    private Object saveCapacityMemory(Learn learn, Student student, boolean isKnown, Integer classify) throws Exception {
        Vocabulary vocabulary = null;
        Sentence sentence = null;
        if (classify <= 3) {
            vocabulary = vocabularyMapper.selectByPrimaryKey(learn.getVocabularyId());
        } else {
            sentence = sentenceMapper.selectByPrimaryKey(learn.getExampleId());
        }
        // 通过学生id，单元id和单词id获取当前单词的记忆追踪信息
        Object object = this.getCapacity(student.getId(), learn.getVocabularyId() == null ? learn.getExampleId()
                : learn.getVocabularyId(), learn.getUnitId(), classify);
        if (object == null) {
            // 之前学习该单词或例句是没有答错过，而本次答错
            if (!isKnown) {
                if (classify == 0) {
                    // 单词图鉴
                    Object obj = this.saveCapacity(learn, student, vocabulary, null, CapacityPicture.class);
                    CapacityPicture capacityPicture = (CapacityPicture) obj;
                    capacityPictureMapper.insert(capacityPicture);
                    return capacityPicture;
                } else if (classify == 1) {
                    // 慧记忆
                    Object obj = this.saveCapacity(learn, student, vocabulary, null, CapacityMemory.class);
                    CapacityMemory capacityMemory = (CapacityMemory) obj;
                    capacityMemoryMapper.insert(capacityMemory);
                    return capacityMemory;
                } else if (classify == 2) {
                    // 慧听写
                    Object obj = this.saveCapacity(learn, student, vocabulary, null, CapacityListen.class);
                    CapacityListen capacityListen = (CapacityListen) obj;
                    capacityListenMapper.insert(capacityListen);
                    return capacityListen;
                } else if (classify == 3) {
                    // 慧默写
                    Object obj = this.saveCapacity(learn, student, vocabulary, null, CapacityWrite.class);
                    CapacityWrite capacityWrite = (CapacityWrite) obj;
                    capacityWriteMapper.insert(capacityWrite);
                    return capacityWrite;
                } else if (classify == 4) {
                    // 例句听力
                    Object obj = this.saveCapacity(learn, student, null, sentence, SentenceListen.class);
                    SentenceListen sentenceListen = (SentenceListen) obj;
                    sentenceListenMapper.insert(sentenceListen);
                    return sentenceListen;
                } else if (classify == 5) {
                    // 例句翻译
                    Object obj = this.saveCapacity(learn, student, null, sentence, SentenceTranslate.class);
                    SentenceTranslate sentenceTranslate = (SentenceTranslate) obj;
                    sentenceTranslateMapper.insert(sentenceTranslate);
                    return sentenceTranslate;
                } else if (classify == 6) {
                    // 例句默写
                    Object obj = this.saveCapacity(learn, student, null, sentence, SentenceWrite.class);
                    SentenceWrite sentenceWrite = (SentenceWrite) obj;
                    sentenceWriteMapper.insert(sentenceWrite);
                    return sentenceWrite;
                }
            }
        } else {
            // 之前学习当前单词或例句时，有答错记录
            if(classify == 0){
                CapacityPicture capacityPicture = (CapacityPicture) object;
                if (!isKnown) {
                    capacityPicture.setFaultTime(capacityPicture.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = capacityPicture.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacityPicture.setPush(push);

                // 重新计算记忆强度
                capacityPicture.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                capacityPictureMapper.updateByPrimaryKeySelective(capacityPicture);
                return capacityPicture;
            }else if (classify == 1) {
                CapacityMemory capacityMemory = (CapacityMemory) object;
                if (!isKnown) {
                    capacityMemory.setFaultTime(capacityMemory.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = capacityMemory.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacityMemory.setPush(push);

                // 重新计算记忆强度
                capacityMemory.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                capacityMemoryMapper.updateByPrimaryKeySelective(capacityMemory);
                return capacityMemory;
            } else if (classify == 2) {
                CapacityListen capacityListen = (CapacityListen) object;
                if (!isKnown) {
                    capacityListen.setFaultTime(capacityListen.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = capacityListen.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacityListen.setPush(push);

                // 重新计算记忆强度
                capacityListen.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                capacityListenMapper.updateByPrimaryKeySelective(capacityListen);
                return capacityListen;
            } else if (classify == 3) {
                CapacityWrite capacityWrite = (CapacityWrite) object;
                if (!isKnown) {
                    capacityWrite.setFaultTime(capacityWrite.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = capacityWrite.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacityWrite.setPush(push);

                // 重新计算记忆强度
                capacityWrite.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                capacityWriteMapper.updateByPrimaryKeySelective(capacityWrite);
                return capacityWrite;
            } else if (classify == 4) {
                SentenceListen sentenceListen = (SentenceListen) object;
                if (!isKnown) {
                    sentenceListen.setFaultTime(sentenceListen.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = sentenceListen.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                sentenceListen.setPush(push);

                // 重新计算记忆强度
                sentenceListen.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                sentenceListenMapper.updateByPrimaryKeySelective(sentenceListen);
                return sentenceListen;
            } else if (classify == 5) {
                SentenceTranslate sentenceTranslate = (SentenceTranslate) object;
                if (!isKnown) {
                    sentenceTranslate.setFaultTime(sentenceTranslate.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = sentenceTranslate.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                sentenceTranslate.setPush(push);

                // 重新计算记忆强度
                sentenceTranslate.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                sentenceTranslateMapper.updateByPrimaryKeySelective(sentenceTranslate);
                return sentenceTranslate;
            } else if (classify == 6) {
                SentenceWrite sentenceWrite = (SentenceWrite) object;
                if (!isKnown) {
                    sentenceWrite.setFaultTime(sentenceWrite.getFaultTime() + 1);
                }
                // 重新计算黄金记忆点时间
                double memoryStrength = sentenceWrite.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                sentenceWrite.setPush(push);

                // 重新计算记忆强度
                sentenceWrite.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, true));
                sentenceWriteMapper.updateByPrimaryKeySelective(sentenceWrite);
                return sentenceWrite;
            }

        }
        return null;
    }

    /**
     * 封装记忆追踪信息
     *
     * @param learn      学习信息
     * @param student    学生信息
     * @param vocabulary 单词信息
     * @param sentence   例句信息
     * @param clazz      记忆追踪模块class
     */
    private Object saveCapacity(Learn learn, Student student, Vocabulary vocabulary, Sentence sentence, Class<?> clazz) throws Exception {
        Object object = clazz.newInstance();
        String wordChinese;

        Method setWord = clazz.getMethod("setWord", String.class);
        if (vocabulary != null) {
            setWord.invoke(object, vocabulary.getWord());
            Method setSyllable = clazz.getMethod("setSyllable", String.class);
            setSyllable.invoke(object, StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
            wordChinese = vocabulary.getWordChinese();
        } else {
            wordChinese = sentence.getCentreTranslate().replace("*", "");
            setWord.invoke(object, sentence.getCentreExample().replace("#", " ").replace("$", ""));
        }

        Method setCourseId = clazz.getMethod("setCourseId", Long.class);
        setCourseId.invoke(object, learn.getCourseId());

        Method setFaultTime = clazz.getMethod("setFaultTime", Integer.class);
        setFaultTime.invoke(object, 1);

        Method setMemoryStrength = clazz.getMethod("setMemoryStrength", Double.class);
        setMemoryStrength.invoke(object, 0.12);

        Method setStudentId = clazz.getMethod("setStudentId", Long.class);
        setStudentId.invoke(object, student.getId());

        Method setUnitId = clazz.getMethod("setUnitId", Long.class);
        setUnitId.invoke(object, learn.getUnitId());

        Method setVocabularyId = clazz.getMethod("setVocabularyId", Long.class);
        setVocabularyId.invoke(object, learn.getVocabularyId() == null ? learn.getExampleId() : learn.getVocabularyId());

        Method setWordChinese = clazz.getMethod("setWordChinese", String.class);
        setWordChinese.invoke(object, wordChinese);

        Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());

        Method setPush = clazz.getMethod("setPush", Date.class);
        setPush.invoke(object, push);

        return object;
    }

    /**
     * 根据单词id及类别查找对应的记忆追踪信息
     *
     * @param studentId     学生id
     * @param correctWordId 单词id
     * @param unitId        单元id
     * @param classify      类型 0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写
     * @return 与类型相对应的记忆追踪
     */
    private Object getCapacity(Long studentId, Long correctWordId, Long unitId, Integer classify) {
        if (classify == 0) {
            List<CapacityPicture> capacityPictures = capacityPictureMapper.selectByUnitIdAndId(studentId, unitId, correctWordId);
            if (capacityPictures.size() > 1) {
                capacityPictureMapper.deleteById(capacityPictures.get(1).getId());
                return capacityPictures.get(0);
            } else if (capacityPictures.size() > 0) {
                return capacityPictures.get(0);
            }
            return null;
        } else if (classify == 1) {
            // 慧记忆记忆追踪
            List<CapacityMemory> capacityMemories = capacityMemoryMapper.selectByUnitIdAndId(studentId, unitId, correctWordId);
            if (capacityMemories.size() > 1) {
                capacityMemoryMapper.deleteById(capacityMemories.get(1).getId());
                return capacityMemories.get(0);
            } else if (capacityMemories.size() > 0) {
                return capacityMemories.get(0);
            }
            return null;
        } else if (classify == 2) {
            // 慧听写记忆追踪
            List<CapacityListen> capacityListens = capacityListenMapper.selectByUnitIdAndId(studentId, unitId, correctWordId);
            if (capacityListens.size() > 1) {
                capacityListenMapper.deleteById(capacityListens.get(1).getId());
                return capacityListens.get(0);
            } else if (capacityListens.size() > 0) {
                return capacityListens.get(0);
            }
            return null;
        } else if (classify == 3) {
            // 慧默写记忆追踪
            List<CapacityWrite> capacityWrites = capacityWriteMapper.selectByUnitIdAndId(studentId, unitId, correctWordId);
            if (capacityWrites.size() > 1) {
                capacityWriteMapper.deleteById(capacityWrites.get(1).getId());
                return capacityWrites.get(0);
            } else if (capacityWrites.size() > 0) {
                return capacityWrites.get(0);
            }
            return null;
        } else if (classify == 4) {
            // 例句听力记忆追踪
            return sentenceListenMapper.selectByStuIdAndUnitIdAndWordId(studentId, unitId, correctWordId);
        } else if (classify == 5) {
            // 例句翻译记忆追踪
            return sentenceTranslateMapper.selectByStuIdAndUnitIdAndWordId(studentId, unitId, correctWordId);
        } else if (classify == 6) {
            // 例句默写记忆追踪
            return sentenceWriteMapper.selectByStuIdAndUnitIdAndWordId(studentId, unitId, correctWordId);
        }
        return null;
    }
}

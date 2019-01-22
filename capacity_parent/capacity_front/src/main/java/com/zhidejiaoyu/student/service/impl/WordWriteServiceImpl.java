package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.student.common.SaveWordLearnAndCapacity;
import com.zhidejiaoyu.student.service.WordWriteService;
import com.zhidejiaoyu.student.vo.WordWriteStudyVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Service
public class WordWriteServiceImpl implements WordWriteService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final int pushRise = 3;
    
    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private CapacityWriteMapper capacityWriteMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudyCountMapper studyCountMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private SaveWordLearnAndCapacity saveWordLearnAndCapacity;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<WordWriteStudyVo> getWriteWord(HttpSession session, Long unitId) {

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        boolean firstStudy = this.isFirst(student.getId());

        if (firstStudy) {
            // 如果是首次学习慧记忆单词，新增一条学习数据，目的是完成引导页之后进入学习页面
            Learn learn = new Learn();
            learn.setStudentId(student.getId());
            learn.setStudyModel("慧默写");
            learnMapper.insert(learn);
        }

        // 记录学生开始学习该单词的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        Long courseId = unitMapper.selectCourseIdByUnitId(unitId);
        Integer maxCount = studyCountMapper.selectMaxCountByCourseId(student.getId(), courseId);
        // 查询学生当前单元当前模块下已学习单词的个数，即学习进度
        Long plan = learnMapper.countLearnWord(student.getId(), unitId, "慧默写", maxCount == null ? 1 : maxCount);
        // 获取当前单元下的所有单词的总个数
        Long wordCount = unitVocabularyMapper.countByUnitId(unitId);
        if (wordCount == 0) {
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
        }

        if (wordCount.equals(plan)) {
            // 提醒学生进行单元闯关测试
            return ServerResponse.createBySuccess(TestResponseCode.TO_UNIT_TEST.getCode(), TestResponseCode.TO_UNIT_TEST.getMsg());
        }

        // 查看当前单元下记忆追踪中有无达到黄金记忆点的单词
        CapacityWriteExample capacityWriteExample = new CapacityWriteExample();
        capacityWriteExample.createCriteria().andStudentIdEqualTo(student.getId()).andUnitIdEqualTo(unitId)
                .andPushLessThan(new Date()).andMemoryStrengthLessThan(1.0);
        capacityWriteExample.setOrderByClause("push asc");
        List<CapacityWrite> capacityWrites = capacityWriteMapper.selectByExample(capacityWriteExample);

        // 有到达黄金记忆点的单词优先复习
        if (capacityWrites.size() > 0) {
            // 返回达到黄金记忆点的单词信息
            CapacityWrite capacityWrite = capacityWrites.get(0);
            return this.returnGoldWord(capacityWrite, plan, firstStudy, wordCount);
        }

        // 如果没有到达黄金记忆点的单词
        // 获取当前学习进度的下一个单词
        if (wordCount - 1 >= plan) {
            // 查询学习记录本模块学习过的所有单词id
            List<Long> wordIds = learnMapper.selectLearnedWordIdByUnitId(student, unitId, "慧默写", maxCount == null ? 1 : maxCount);

            WordWriteStudyVo wordWriteStudyVo = new WordWriteStudyVo();
            Vocabulary currentStudyWord = vocabularyMapper.selectOneWordNotInIds(wordIds, unitId);

            String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(unitId, currentStudyWord.getId());
            String soundMark = commonMethod.getSoundMark(currentStudyWord.getWord());
            wordWriteStudyVo.setWordId(currentStudyWord.getId());
            wordWriteStudyVo.setMemoryStrength(0.00);
            wordWriteStudyVo.setSoundmark(soundMark);
            wordWriteStudyVo.setWord(currentStudyWord.getWord());
            wordWriteStudyVo.setSyllable(StringUtils.isEmpty(currentStudyWord.getSyllable()) ? currentStudyWord.getWord() : currentStudyWord.getSyllable());
            wordWriteStudyVo.setWordChinese(wordChinese);
            wordWriteStudyVo.setPlan(plan);
            wordWriteStudyVo.setStudyNew(true);
            wordWriteStudyVo.setFirstStudy(firstStudy);
            wordWriteStudyVo.setWordCount(wordCount);
            wordWriteStudyVo.setReadUrl(baiduSpeak.getLanguagePath(currentStudyWord.getWord()));
            return ServerResponse.createBySuccess(wordWriteStudyVo);
        }
        return null;
    }

    /**
     * 返回达到黄金记忆点的单词信息
     *
     * @param capacityWrite
     * @param plan
     * @param wordCount
     * @param firstStudy
     * @return
     */

    private ServerResponse<WordWriteStudyVo> returnGoldWord(CapacityWrite capacityWrite, Long plan, boolean firstStudy,
                                                            Long wordCount) {
        WordWriteStudyVo wordWriteStudyVo = new WordWriteStudyVo();
        String soundMark = commonMethod.getSoundMark(capacityWrite.getWord());
        // 计算当前单词的记忆强度
        double memoryStrength = capacityWrite.getMemoryStrength();
        wordWriteStudyVo.setWordId(capacityWrite.getVocabularyId());
        wordWriteStudyVo.setMemoryStrength(memoryStrength);
        wordWriteStudyVo.setSoundmark(soundMark);
        wordWriteStudyVo.setWord(capacityWrite.getWord());
        wordWriteStudyVo.setSyllable(capacityWrite.getSyllable());
        wordWriteStudyVo.setWordChinese(capacityWrite.getWordChinese());
        wordWriteStudyVo.setPlan(plan);
        wordWriteStudyVo.setStudyNew(false);
        wordWriteStudyVo.setFirstStudy(firstStudy);
        wordWriteStudyVo.setWordCount(wordCount);
        wordWriteStudyVo.setReadUrl(baiduSpeak.getLanguagePath(capacityWrite.getWord()));
        return ServerResponse.createBySuccess(wordWriteStudyVo);
    }

    /**
     * 判断当前学生是不是第一次进入慧默写学习
     *
     * @param stuId 学生id
     * @return boolean true:是第一次进入慧记忆学习；false：不是第一次进入慧记忆学习
     */
    private boolean isFirst(Long stuId) {
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andStudyModelEqualTo("慧默写");
        List<Learn> learns = learnMapper.selectByExample(learnExample);
        return learns.size() == 0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveWriteWord(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
                                                Integer total, Integer classify) {
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

        // 查询当前单词的学习记录数据
        String studyModel = "";
        if (classify != null && classify == 2) {
            studyModel = "慧听写";
        } else if (classify != null && classify == 3) {
            studyModel = "慧默写";
        } else if(classify != null && classify == 0){
            studyModel = "单词图鉴";
        }
        Integer maxCount = commonMethod.saveStudyCount(session, learn.getCourseId());
        Learn currentLearn = learnMapper.selectLearn(studentId, learn, studyModel, maxCount == null ? 1 : maxCount,1);
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
            if (courseLearnCount == 0) {
                // 首次学习当前课程，记录课程首次学习时间
                learn.setFirstStudyTime(now);
            }
            if (isKnown) {
                // 如果认识该单词，记为熟词
                learn.setStatus(1);
                learn.setFirstIsKnown(1);
            } else {
                learn.setStatus(0);
                learn.setFirstIsKnown(0);
                // 单词不认识将该单词记入记忆追踪中
                if (classify == 3) {
                    saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 3);
                } else if (classify == 2) {
                    saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 2);
                }else if(classify == 0) {
                    saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 0);
                }

            }
            count = learnMapper.insert(learn);
            if (count > 0 && total == (plan + 1)) {
                return ServerResponse.createBySuccess();
            }
            if (count > 0) {
                return ServerResponse.createBySuccess();
            }
        } else {
            // 不是第一次学习
            CapacityWrite capacityWrite = null;
            CapacityListen capacityListen = null;
            CapacityPicture capacityPicture = null;
            if (isKnown) {
                if (classify == 3) {
                    capacityWrite = (CapacityWrite) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 3);
                } else if (classify == 2) {
                    capacityListen = (CapacityListen) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 2);
                } else if(classify == 0) {
                    capacityPicture = (CapacityPicture) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 0);
                }
            } else {
                // 单词不认识将该单词记入记忆追踪中
                if (classify == 3) {
                    capacityWrite = (CapacityWrite) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 3);
                } else if (classify == 2) {
                    capacityListen = (CapacityListen) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 2);
                } else if (classify == 0) {
                    capacityPicture = (CapacityPicture) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 0);
                }
            }
            // 计算记忆难度
            Integer memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(capacityWrite != null ? capacityWrite : (capacityListen != null ? capacityListen : capacityPicture), 1);
            // 更新学习记录
            currentLearn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            session.removeAttribute(TimeConstant.BEGIN_START_TIME);

            currentLearn.setStudyCount(currentLearn.getStudyCount() + 1);
            // 熟词
            currentLearn.setStatus((memoryDifficult == null || memoryDifficult == 0) ? 1 : 0);
            currentLearn.setLearnCount(maxCount);
            currentLearn.setUpdateTime(now);
            int i = learnMapper.updateByPrimaryKeySelective(currentLearn);
            
            // 默写模块错过三次在记忆时间上再加长三小时
            if(classify == 3) {
            	// 查询错误次数>=3 
            	Integer faultTime = capacityWriteMapper.getFaultTime(studentId, learn.getVocabularyId());
            	if(faultTime != null && faultTime >= 3) {
            		// 如果错误次数>=3, 黄金记忆时间推迟3小时
            		capacityWriteMapper.updatePush(studentId, learn.getVocabularyId(), pushRise);
            	}
            }
            
            if (i > 0) {
                return ServerResponse.createBySuccess();
            }
        }
        
        
        
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }


}

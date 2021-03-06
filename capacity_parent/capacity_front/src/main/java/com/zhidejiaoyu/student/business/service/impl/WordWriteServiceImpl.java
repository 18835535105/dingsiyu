package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.SaveWordLearnAndCapacity;
import com.zhidejiaoyu.student.business.service.WordWriteService;
import com.zhidejiaoyu.common.vo.WordWriteStudyVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class WordWriteServiceImpl extends BaseServiceImpl<VocabularyMapper, Vocabulary> implements WordWriteService {

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
    private StudentMapper studentMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private SaveWordLearnAndCapacity saveWordLearnAndCapacity;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private CapacityPictureMapper capacityPictureMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object getWriteWord(HttpSession session, Long unitId, Long[] ignoreWordId) {

        Student student = getStudent(session);
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

        // 查询学生当前单元当前模块下已学习单词的个数，即学习进度
        Long plan = learnMapper.countLearnWord(student.getId(), unitId, "慧默写");
        // 获取当前单元下的所有单词的总个数
        Long wordCount = unitVocabularyNewMapper.countByUnitId(unitId);
        if (wordCount == 0) {
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
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
            List<Long> wordIds = learnMapper.selectLearnedWordIdByUnitId(student, unitId, "慧默写");

            WordWriteStudyVo wordWriteStudyVo = new WordWriteStudyVo();
            Vocabulary currentStudyWord = vocabularyMapper.selectOneWordNotInIds(wordIds, unitId);

            String wordChinese = unitVocabularyNewMapper.selectWordChineseByUnitIdAndWordId(unitId, currentStudyWord.getId());
            String soundMark = StringUtils.isEmpty(currentStudyWord.getSoundMark()) ? "" : currentStudyWord.getSoundMark();
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

        // 如果该单元单词都已经学习完毕并且没有达到黄金记忆点的单词，获取生词
        CapacityWrite capacityWrite = capacityWriteMapper.selectUnknownWordByUnitId(student, unitId, ignoreWordId);
        if (capacityWrite != null) {
            return this.returnGoldWord(capacityWrite, plan, firstStudy, wordCount);
        }

        if (wordCount.equals(plan)) {
            // 提醒学生进行单元闯关测试
            return super.toUnitTest();
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
        Vocabulary vocabulary = vocabularyMapper.selectById(capacityWrite.getVocabularyId());
        // 计算当前单词的记忆强度
        double memoryStrength = capacityWrite.getMemoryStrength();
        wordWriteStudyVo.setWordId(capacityWrite.getVocabularyId());
        wordWriteStudyVo.setMemoryStrength(memoryStrength);
        wordWriteStudyVo.setSoundmark(StringUtils.isEmpty(vocabulary.getSoundMark()) ? "" : vocabulary.getSoundMark());
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
        Student student = getStudent(session);
        Date now = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        Long studentId = student.getId();

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
        } else if (classify != null && classify == 0) {
            studyModel = "单词图鉴";
        }

        List<Long> learnIds = learnMapper.selectLearnIds(studentId, learn, studyModel, 1);
        if (learnIds.size() > 1) {
            List<Long> longs = learnIds.subList(1, learnIds.size());
            learnMapper.deleteBatchIds(longs);
        }
        Learn currentLearn = learnMapper.selectLearn(studentId, learn, studyModel, 1);
        /**
         * 查看慧默写  会听写  单词图鉴是否为上次学习 如果是 删除
         * 开始
         */
        CapacityWrite capacityWrite = null;
        CapacityListen capacityListen = null;
        CapacityPicture capacityPicture = null;
        boolean flag = false;
        if (classify == 3) {
            // 慧默写
            List<CapacityWrite> capacityWrites = capacityWriteMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    learn.getVocabularyId());
            flag = capacityWrites.size() > 0 && capacityWrites.get(0).getPush().getTime() < System.currentTimeMillis();
        } else if (classify == 2) {
            List<CapacityListen> capacityListens = capacityListenMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    learn.getVocabularyId());
            flag = capacityListens.size() > 0 && capacityListens.get(0).getPush().getTime() < System.currentTimeMillis();
        } else if (classify == 0) {
            List<CapacityPicture> capacityPictures = capacityPictureMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    learn.getVocabularyId());
            flag = capacityPictures.size() > 0 && capacityPictures.get(0).getPush().getTime() < System.currentTimeMillis();
        }
        if (currentLearn == null && flag) {
            if (classify == 3) {
                capacityWriteMapper.deleteByStudentIdAndUnitIdAndVocabulary(student.getId(), learn.getUnitId(),
                        learn.getVocabularyId());
            } else if (classify == 2) {
                capacityListenMapper.deleteByStudentIdAndUnitIdAndVocabulary(student.getId(), learn.getUnitId(),
                        learn.getVocabularyId());
            } else {
                capacityPictureMapper.deleteByStudentIdAndUnitIdAndVocabulary(student.getId(), learn.getUnitId(),
                        learn.getVocabularyId());
            }
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
                } else if (classify == 0) {
                    saveWordLearnAndCapacity.saveCapacityMemory(learn, student, false, 0);
                }

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
            if (isKnown) {
                if (classify == 3) {
                    capacityWrite = (CapacityWrite) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 3);
                } else if (classify == 2) {
                    capacityListen = (CapacityListen) saveWordLearnAndCapacity.saveCapacityMemory(learn, student, true, 2);
                } else if (classify == 0) {
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
            int memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(capacityWrite != null ? capacityWrite : (capacityListen != null ? capacityListen : capacityPicture), 1);
            // 更新学习记录
            currentLearn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            session.removeAttribute(TimeConstant.BEGIN_START_TIME);

            currentLearn.setStudyCount(currentLearn.getStudyCount() + 1);
            // 熟词
            currentLearn.setStatus(memoryDifficult == 0 ? 1 : 0);

            currentLearn.setUpdateTime(now);

            MemoryServiceImpl.packageAboutStudyPlan(currentLearn, studentId, capacityStudentUnitMapper, studentStudyPlanMapper);

            int i = learnMapper.updateById(currentLearn);

            // 慧默写、慧听写模块错过三次在记忆时间上再加长三小时
            int pushRise = 3;
            if (classify == 2) {
                Integer faultTime = capacityListenMapper.getFaultTime(studentId, learn.getVocabularyId());
                if (faultTime != null && faultTime >= 5) {
                    capacityListenMapper.updatePush(studentId, learn.getVocabularyId(), pushRise);
                }
            }
            if (classify == 3) {
                // 查询错误次数>=3
                Integer faultTime = capacityWriteMapper.getFaultTime(studentId, learn.getVocabularyId());
                if (faultTime != null && faultTime >= 5) {
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

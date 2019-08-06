package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.bookVo.BookVo;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.BookService;
import com.zhidejiaoyu.common.Vo.bookVo.BookInfoVo;
import com.zhidejiaoyu.common.Vo.bookVo.PlayerVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class BookServiceImpl extends BaseServiceImpl<VocabularyMapper, Vocabulary> implements BookService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private StudyCountMapper studyCountMapper;

    @Autowired
    private UnitSentenceMapper unitSentenceMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private CapacityReviewMapper capacityReviewMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private SentenceUnitMapper sentenceUnitMapper;

    @Autowired
    private RedisOpt redisOpt;

    private final String WORD_MEMORY = "慧记忆";
    private final String WORD_LISTEN = "慧听写";
    private final String WORD_WRITE = "慧默写";
    private final String WORD_PICTURE = "单词图鉴";

    @Override
    public ServerResponse<PageInfo<BookVo>> getWordBookList(HttpSession session, Long courseId, Long unitId, String studyModel,
                                                            Integer condition, Integer pageNum, Integer pageSize) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        int totalWord = 1;
        int unknownWord = 2;
        int knownWord = 3;
        int residue = 4;

        PageInfo pageInfo = null;
        List<BookVo> bookVos = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        // 分条件查询
        if (WORD_MEMORY.equals(studyModel) || WORD_LISTEN.equals(studyModel) || WORD_WRITE.equals(studyModel)) {
            List<Vocabulary> vocabularies = null;
            if (condition == totalWord) {
                // 总单词
                vocabularies = vocabularyMapper.selectByUnitId(unitId);
            } else if (condition == unknownWord || condition == knownWord) {
                // 2:生词，3：熟词
                vocabularies = vocabularyMapper.selectUnknownWordByUnitId(studentId, unitId, studyModel, condition);
            } else if (condition == residue) {
                // 剩余
                vocabularies = vocabularyMapper.selectUnlearnedByUnitId(studentId, unitId, studyModel);
            }

            if (vocabularies != null && vocabularies.size() > 0) {
                pageInfo = new PageInfo<>(vocabularies);
                bookVos = this.getVocabularyBooKVo(vocabularies, unitId);
            }
        } else if (WORD_PICTURE.equals(studyModel)) {
            List<Vocabulary> vocabularies = null;
            if (condition == totalWord) {
                // 总单词
                vocabularies = vocabularyMapper.selectByUnitIdAndStudyModel(unitId, studyModel);
            } else if (condition == unknownWord || condition == knownWord) {
                // 2:生词，3：熟词
                vocabularies = vocabularyMapper.selectUnknownWordByUnitId(studentId, unitId, studyModel, condition);
            } else if (condition == residue) {
                // 剩余
                vocabularies = vocabularyMapper.selectUnlearnedByUnitId(studentId, unitId, studyModel);
            }

            if (vocabularies != null && vocabularies.size() > 0) {
                pageInfo = new PageInfo<>(vocabularies);
                bookVos = this.getVocabularyBooKVo(vocabularies, unitId);
            }
        } else {
            List<Sentence> sentences = null;
            if (condition == totalWord) {
                // 总句子
                sentences = sentenceMapper.selectByUnitId(unitId);
            } else if (condition == unknownWord || condition == knownWord) {
                // 2:生句，3：熟句
                sentences = sentenceMapper.selectUnKnowSentenceByUnitId(studentId, unitId, studyModel, condition);
            } else if (condition == residue) {
                // 剩余
                sentences = sentenceMapper.selectUnLearnedSentenceByUnitId(studentId, unitId, studyModel);
            }
            if (sentences != null && sentences.size() > 0) {
                pageInfo = new PageInfo<>(sentences);
                bookVos = this.getSentenceBookVo(sentences);
            }
        }

        PageInfo<BookVo> info = new PageInfo<>(bookVos);
        if (pageInfo != null) {
            info.setPages(pageInfo.getPages());
        }
        return ServerResponse.createBySuccess(info);
    }

    /**
     * 封装句子本信息
     *
     * @param sentences
     * @return
     */
    private List<BookVo> getSentenceBookVo(List<Sentence> sentences) {
        List<BookVo> list = new ArrayList<>(sentences.size());
        BookVo vo;
        for (Sentence sentence : sentences) {
            vo = new BookVo();
            vo.setId(sentence.getId());
            vo.setChinese(sentence.getCentreTranslate().replace("*", ""));
            vo.setContent(sentence.getCentreExample().replace("#", " ").replace("$", ""));
            vo.setReadUrl(baiduSpeak
                    .getSentencePath(sentence.getCentreExample()));
            list.add(vo);
        }
        return list;
    }

    /**
     * 封装单词本信息
     *
     * @param vocabularies 单词信息
     * @param unitId       单元id
     * @return
     */
    private List<BookVo> getVocabularyBooKVo(List<Vocabulary> vocabularies, Long unitId) {
        List<BookVo> list = new ArrayList<>(vocabularies.size());
        Map<Long, Map<Long, String>> map = unitVocabularyMapper.selectWordChineseMapByUnitId(unitId);
        BookVo vo;
        String wordChinese = "";
        for (Vocabulary vocabulary : vocabularies) {
            vo = new BookVo();
            if (map.containsKey(vocabulary.getId())) {
                wordChinese = map.get(vocabulary.getId()).get("wordChinese");
            }
            vo.setId(vocabulary.getId());
            vo.setChinese(wordChinese);
            vo.setContent(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
            vo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
            vo.setSoundMark(StringUtils.isEmpty(vocabulary.getSoundMark()) ? "" : vocabulary.getSoundMark());
            if (StringUtils.isNotEmpty(vocabulary.getRecordpicurl())) {
                vo.setPictureUrl(GetOssFile.getPublicObjectUrl(vocabulary.getRecordpicurl()));
            } else {
                vo.setPictureUrl(null);
            }
            list.add(vo);
        }
        return list;
    }

    @Override
    public ServerResponse<BookInfoVo> getBookInfo(HttpSession session, Long courseId, Long unitId, String studyModel) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        Long total;
        Long learnedCount;
        Long notKnow;
        // 判断当前类型是不是属于 慧记忆或者慧默写或者慧听写
        boolean flag = WORD_MEMORY.equals(studyModel) || WORD_LISTEN.equals(studyModel) || WORD_WRITE.equals(studyModel);
        if (unitId != 0) {
            // 查看指定单元的单词/例句信息
            Integer learnCount = 1;
            learnedCount = learnMapper.countLearnWord(studentId, unitId, studyModel, learnCount);
            notKnow = learnMapper.countNotKnownWord(studentId, unitId, studyModel, learnCount);
            if (flag) {
                // 查看单词本摘要信息
                total = unitVocabularyMapper.countByUnitId(unitId);
            } else if (WORD_PICTURE.equals(studyModel)) {
                // 单词图鉴只查询有图片的单词总个数
                total = unitVocabularyMapper.countByUnitIdAndStudyModel(unitId, studyModel);
            } else {
                // 查看句子本摘要信息
                total = (long) unitSentenceMapper.countByUnitId(unitId);
            }
        } else {
            // 查看指定课程的单词/例句信息
            Integer learnCount = 1;
            learnedCount = learnMapper.countLearnWordByCourse(studentId, courseId, studyModel, learnCount);
            notKnow = learnMapper.countNotKnownWordByCourse(studentId, courseId, studyModel, learnCount);
            if (flag) {
                // 查看单词本摘要信息
                total = unitVocabularyMapper.countByCourseId(courseId);
            } else if (WORD_PICTURE.equals(studyModel)) {
                // 单词图鉴只查询有图片的单词总个数
                total = unitVocabularyMapper.countByCourseIdAndStudyModel(courseId, studyModel);
            } else {
                // 查看句子本摘要信息
                total = unitSentenceMapper.countByCourseId(courseId);
            }
        }

        // 如果总学习数大于总大次数，将总学习数置为总单词数
        if (learnedCount > total) {
            learnedCount = total;
        }
        // 如果生词数大于总学习数，将生词数置为总学习数
        if (notKnow > learnedCount) {
            notKnow = learnedCount;
        }

        Long know = learnedCount - notKnow;

        BookInfoVo vo = new BookInfoVo();
        vo.setKnow(know);
        vo.setNotKnow(notKnow);
        double plan = BigDecimalUtil.div(learnedCount * 1.0, total, 2);
        vo.setPlan(plan > 1.0 ? 1.0 : plan);
        vo.setResidue(total - learnedCount);
        vo.setTotal(total);
        double scale = BigDecimalUtil.div(notKnow * 1.0, total, 2);
        vo.setScale(scale > 1.0 ? 1.0 : scale);
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    public ServerResponse<PlayerVo> getPlayer(HttpSession session, Long courseId, Long unitId, Integer order) {
        PlayerVo playerVo = new PlayerVo();
        List<BookVo> bookVos = null;
        // 查询当前单元下的所有单词
        List<Vocabulary> vocabularies = redisOpt.getWordInfoInUnit(unitId);
        if (vocabularies.size() > 0) {
            bookVos = this.getVocabularyBooKVo(vocabularies, unitId);
            playerVo.setTotal(vocabularies.size());
        }
        return orderThePlayer(order, playerVo, bookVos);
    }

    @Override
    public ServerResponse<PlayerVo> getBookPlayer(HttpSession session, Long unitId, Integer type, Integer order) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        PlayerVo playerVo = new PlayerVo();
        List<BookVo> bookVos = null;
        if (type == 2) {
            // 如果单元当前所学单词大于当前单元总单词数删除播放机学习记录
            List<Vocabulary> vocabularies = redisOpt.getWordInfoInUnit(unitId);
            int learnedWord = playerMapper.countLearnedWord(studentId, unitId, 2);
            if (vocabularies.size() <= learnedWord) {
                playerMapper.deleteRecord(studentId, unitId, 2);
            } else {
                // 查询当前单元单词播放机还未学习的单词信息
                vocabularies = vocabularyMapper.selectUnlearnInBookPlayer(studentId, unitId);
            }

            if (vocabularies.size() > 0) {
                bookVos = this.getVocabularyBooKVo(vocabularies, unitId);
                playerVo.setTotal(vocabularies.size());
            }
        } else {
            List<Sentence> sentences = redisOpt.getSentenceInfoInUnit(unitId);
            int learnedWord = playerMapper.countLearnedWord(studentId, unitId, 3);
            if (sentences.size() <= learnedWord) {
                playerMapper.deleteRecord(studentId, unitId, 3);
            } else {
                // 查询当前单元句型播放机还未学习的句型信息
                sentences = sentenceMapper.selectUnlearnInBookPlayer(studentId, unitId);
            }

            if (sentences.size() > 0) {
                bookVos = this.getSentenceBookVo(sentences);
                playerVo.setTotal(sentences.size());
            }
        }
        return orderThePlayer(order, playerVo, bookVos);
    }

    /**
     * 播放机内容排序规则
     *
     * @param order
     * @param playerVo
     * @param bookVos
     * @return
     */
    private ServerResponse<PlayerVo> orderThePlayer(Integer order, PlayerVo playerVo, List<BookVo> bookVos) {
        switch (order) {
            case 2:
                // 随机排列
                Collections.shuffle(bookVos);
                break;
            case 3:
                // 倒序排列
                Collections.reverse(bookVos);
                break;
            default:
        }
        playerVo.setPlayerList(bookVos);
        return ServerResponse.createBySuccess(playerVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> studyAgain(HttpSession session, Long courseId) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        // 查询当前学生当前课程的学习记录
        List<Learn> learns = learnMapper.selectByStuIdAndCourseId(studentId, courseId);
        // 初始化学习下一遍的学习记录
        List<Learn> nextLearns = this.initNext(learns);

        try {
            learnMapper.insertList(nextLearns);
        } catch (Exception e) {
            log.error("再学一遍初始化下一遍学习记录时失败！", e);
            super.saveRunLog(student, 2, "再学一遍初始化下一遍学习记录时失败！");
            return ServerResponse.createByErrorMessage("初始化学习记录失败！");
        }

        Integer maxCount = studyCountMapper.selectMaxCountByCourseId(studentId, courseId);
        // 再学一遍时，在 study_count 中新增一条记录，用于记录下一遍的课程学习次数
        StudyCount studyCount = new StudyCount();
        studyCount.setStudyCount(0);
        studyCount.setCount(maxCount + 1);
        studyCount.setCourseId(courseId);
        studyCount.setStudentId(studentId);
        try {
            studyCountMapper.insert(studyCount);
        } catch (Exception e) {
            log.error("新增再学一遍记录失败！", e);
            super.saveRunLog(student, 2, "新增再学一遍记录失败！");
            return ServerResponse.createByErrorMessage("新增再学一遍记录失败！");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> restudy(HttpSession session, Long courseId, Long unitId, Long[] wordIds, Integer studyModel) {
        Student student = getStudent(session);
        // 查询当前学生当前课程的学习遍数
        int maxStudyCount = 1;
        // 更新学习记录
        learnMapper.updateUnknownWords(student, unitId, courseId, wordIds, studyModel, maxStudyCount);
        // 更新记忆追踪记录
        // 根据条件查询单词是否已经在记忆追踪中，如果在更新记忆追踪；如果不在新增记忆追踪
        List<Long> updateIds = capacityReviewMapper.selectByWordIdsAndStudyModel(student, courseId, unitId, wordIds, studyModel);
        Double memoryStrength = 0.12;
        Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
        if (updateIds.size() < wordIds.length) {
            // 说明有些单词在记忆追踪中还不存在
            Map<Long, Long> map = new HashMap<>(16);
            List<Long> insertIds = new ArrayList<>();
            updateIds.forEach(id -> map.put(id, id));
            Arrays.stream(wordIds).forEach(id -> {
                if (!map.containsKey(id)) {
                    insertIds.add(id);
                }
            });

            List<CapacityReview> capacityReviews = new ArrayList<>();
            CapacityReview capacityReview;

            if (studyModel <= 3) {
                List<Vocabulary> vocabularies = vocabularyMapper.selectByWordIds(insertIds);
                Set<Long> idSet = new HashSet<>();
                vocabularies.forEach(vocabulary -> idSet.add(vocabulary.getId()));
                Map<Long, Map<Long, String>> wordChineseMap = null;
                Map<Long, Map<Long, Object>> unitMap = null;
                if (unitId != 0) {
                    wordChineseMap = unitVocabularyMapper.selectWordChineseMapByUnitIdAndWordIds(unitId, idSet);
                } else {
                    unitMap = unitVocabularyMapper.selectWordChineseMapByCourseIdAndWordIds(courseId, idSet);
                }

                for (Vocabulary vocabulary : vocabularies) {
                    capacityReview = new CapacityReview();
                    capacityReview.setClassify(studyModel.toString());
                    capacityReview.setPush(DateUtil.formatYYYYMMDDHHMMSS(push));
                    capacityReview.setStudent_id(student.getId());
                    capacityReview.setUnit_id(unitId != 0L ? unitId : Integer.valueOf(unitMap.get(vocabulary.getId()).get("unitId").toString()));
                    capacityReview.setCourse_id(courseId);
                    capacityReview.setFault_time(0);
                    capacityReview.setMemory_strength(memoryStrength);
                    capacityReview.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
                    capacityReview.setVocabulary_id(vocabulary.getId());
                    capacityReview.setWord(vocabulary.getWord());
                    capacityReview.setWord_chinese(unitId != 0 ? wordChineseMap.get(vocabulary.getId()).get("wordChinese")
                            : unitMap.get(vocabulary.getId()).get("wordChinese").toString());
                    capacityReviews.add(capacityReview);
                }
            } else {
                List<Sentence> sentences = sentenceMapper.selectByIds(insertIds);
                for (Sentence sentence : sentences) {
                    capacityReview = new CapacityReview();
                    capacityReview.setClassify(studyModel.toString());
                    capacityReview.setPush(DateUtil.formatYYYYMMDDHHMMSS(push));
                    capacityReview.setStudent_id(student.getId());
                    capacityReview.setUnit_id(unitId);
                    capacityReview.setCourse_id(courseId);
                    capacityReview.setFault_time(0);
                    capacityReview.setMemory_strength(memoryStrength);
                    capacityReview.setVocabulary_id(sentence.getId());
                    capacityReview.setWord(sentence.getCentreExample().replace("#", " ").replace("$", ""));
                    capacityReview.setWord_chinese(sentence.getCentreTranslate().replace("*", ""));
                    capacityReviews.add(capacityReview);
                }
            }
            capacityReviewMapper.insertByBatch(capacityReviews, studyModel);
        }
        if (updateIds.size() > 0) {
            capacityReviewMapper.updatePushAndMemoryStrengthByPrimaryKeys(updateIds, push, memoryStrength, studyModel);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse savePlayer(HttpSession session, Player player) {
        Student student = getStudent(session);
        int count = playerMapper.selectByType(student.getId(), player.getUnitId(), player.getType(), player.getWordId());
        if (count == 0) {
            player.setStudentId(student.getId());
            player.setUpdateTime(new Date());
            playerMapper.insert(player);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 初始化下一遍学习记录
     *
     * @param learns
     * @return
     */
    private List<Learn> initNext(List<Learn> learns) {
        List<Learn> nextLearns = new ArrayList<>();
        Learn learn;
        for (Learn l : learns) {
            learn = new Learn();
            learn.setLearnCount(l.getLearnCount() + 1);
            learn.setStatus(0);
            learn.setStudyCount(0);
            learn.setStudentId(l.getStudentId());
            learn.setStudyModel(l.getStudyModel());
            learn.setVocabularyId(l.getVocabularyId());
            learn.setExampleId(l.getExampleId());
            learn.setUnitId(l.getUnitId());
            learn.setCourseId(l.getCourseId());
            nextLearns.add(learn);
        }
        return nextLearns;
    }
}

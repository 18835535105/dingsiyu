package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.bookVo.BookVo;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.common.utils.simple.language.SimpleBaiduSpeak;
import com.zhidejiaoyu.student.service.simple.SimpleBookServiceSimple;
import com.zhidejiaoyu.common.Vo.bookVo.BookInfoVo;
import com.zhidejiaoyu.common.Vo.bookVo.PlayerVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleBookServiceImplSimple extends SimpleBaseServiceImpl<SimpleVocabularyMapper, Vocabulary> implements SimpleBookServiceSimple {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleBaiduSpeak simpleBaiduSpeak;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleSentenceMapper simpleSentenceMapper;

    @Autowired
    private SimpleStudyCountMapper simpleStudyCountMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleCapacityReviewMapper simpleCapacityReviewMapper;

    @Autowired
    private SimpleUnitVocabularyMapper simpleUnitVocabularyMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public ServerResponse<PageInfo<BookVo>> getWordBookList(HttpSession session, Long courseId,
                                                            Integer type, Integer condition, Integer pageNum, Integer pageSize) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String typeStr = simpleCommonMethod.getTestType(type);

        String key = "simple_student_book_list";
        String field = courseId + ":" + type + ":" + condition + ":" + pageNum + ":" + pageSize;
        Object object = redisTemplate.opsForHash().get(key, field);

        List<BookVo> vos = new ArrayList<>();
        if (courseId != 0) {

            switch (condition) {
                case 1:
                    if (object != null) {
                        try {
                            return ServerResponse.createBySuccess((PageInfo<BookVo>) object);
                        } catch (Exception e) {
                            log.error("获取单词本内容类型转换错误, field=[{}], error=[{}]", field, e);
                        }
                    }
                    PageHelper.startPage(pageNum, pageSize);
                    vos = vocabularyMapper.selectBookVoByCourseId(courseId);
                    break;
                case 2:
                case 3:
                    PageHelper.startPage(pageNum, pageSize);
                    vos = vocabularyMapper.selectUnknownWordBookVoByCourseId(student.getId(), courseId, condition);
                    break;
                case 4:
                    PageHelper.startPage(pageNum, pageSize);
                    vos = vocabularyMapper.selectUnlearnedBookVoByCourseId(student.getId(), courseId);
                    break;
                default:
            }
        } else {
            // 学生当前模块下学生可学习的所有课程id
            List<Long> courseIds = getAllCourseIdInTypeStr(typeStr, student.getId());
            switch (condition) {
                case 1:
                    if (object != null) {
                        try {
                            return ServerResponse.createBySuccess((PageInfo<BookVo>) object);
                        } catch (Exception e) {
                            log.error("获取单词本内容类型转换错误, field=[{}], error=[{}]", field, e);
                        }
                    }
                    PageHelper.startPage(pageNum, pageSize);
                    vos = vocabularyMapper.selectBookVoByCourseIds(courseIds);
                    break;
                case 2:
                case 3:
                    PageHelper.startPage(pageNum, pageSize);
                    vos = vocabularyMapper.selectUnknownWordBookVoByCourseIds(student.getId(), courseIds, condition);
                    break;
                case 4:
                    PageHelper.startPage(pageNum, pageSize);
                    vos = vocabularyMapper.selectUnlearnedBookVoByCourseIds(student.getId(), courseIds);
                    break;
                default:
            }
        }
        // 词组部分不需要显示音标
        if (type == 4 || type == 9 || type == 2) {
            vos.parallelStream().forEach(vo -> {
                vo.setSoundMark(null);
                vo.setReadUrl(simpleBaiduSpeak.getLanguagePath(vo.getReadUrl()));
            });
        } else {
            vos.parallelStream().forEach(vo -> vo.setReadUrl(simpleBaiduSpeak.getLanguagePath(vo.getReadUrl())));
        }

        PageInfo<BookVo> pageInfo = new PageInfo<>(vos);
        if (condition == 1 && object == null) {
            redisTemplate.opsForHash().put(key, field, pageInfo);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<BookInfoVo> getBookInfo(HttpSession session, Long courseId, Integer type) {
    	// 该模块没有课程
    	if(courseId == null) {
    		return ServerResponse.createByError(300, "暂无数据");
    	}

        Student student = getStudent(session);
        String typeStr = simpleCommonMethod.getTestType(type);
        Long studentId = student.getId();
        Long total;
        Long learnedCount;
        Long notKnow;
        if (courseId != 0) {
            // 当前课程学习总个数
            List<Long> courseIds =  new ArrayList<>();
            courseIds.add(courseId);
            learnedCount = learnMapper.countLearnWordByCourse(studentId, courseIds, typeStr);

            // 当前课程生词总个数
            notKnow = learnMapper.countUnknownWord(studentId, courseIds, typeStr);

            // 当前课程单词总个数
            total = getWordCountInCourse(courseId);
        } else {
            List<Long> courseIds = getAllCourseIdInTypeStr(typeStr, studentId);

            // 学生当前模块学习的总单词数
            learnedCount = learnMapper.countLearnWordByCourse(studentId, courseIds, typeStr);

            // 学生当前模块总单词数
            List<Unit> units = unitMapper.selectList(new EntityWrapper<Unit>().in("course_id", courseIds));
            List<Long> unitIds = getUnitIdsFromUnits(units);
            total = simpleUnitVocabularyMapper.countWordByUnitIds(unitIds);

            // 学生当前模块学习的所有生词数
            notKnow = learnMapper.countUnknownWord(studentId, courseIds, typeStr);
        }

        // 如果总学习数大于总大次数，将总学习数置为总单词数
        if (learnedCount > total) {
            learnedCount = total;
        }
        // 如果生词数大于总学习数，将生词数置为总学习数
        if (notKnow > learnedCount) {
            notKnow = learnedCount;
        }

        long know = learnedCount - notKnow;

        BookInfoVo vo = new BookInfoVo();
        vo.setKnow(know < 0 ? 0 : know);
        vo.setNotKnow(notKnow);
        double plan = BigDecimalUtil.div(learnedCount * 1.0, total, 2);
        vo.setPlan(plan > 1.0 ? 1.0 : plan);
        vo.setResidue(total - learnedCount);
        vo.setTotal(total);
        double scale = BigDecimalUtil.div(notKnow * 1.0, total, 2);
        vo.setScale(scale > 1.0 ? 1.0 : scale);
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 学生当前模块下所有课程id
     *
     * @param typeStr
     * @param studentId
     * @return
     */
    private List<Long> getAllCourseIdInTypeStr(String typeStr, Long studentId) {
        List<Map<String, Object>> maps = simpleCourseMapper.selectAllCourseByStuIdAndType(studentId, typeStr);
        List<Long> courseIds = new ArrayList<>(maps.size());
        maps.parallelStream().forEach(map -> courseIds.add(Long.valueOf(map.get("id").toString())));
        return courseIds;
    }

    /**
     * 统计当前课程下的所有单词个数
     *
     * @param courseId
     * @return
     */
    private Long getWordCountInCourse(Long courseId) {
        List<Unit> units = unitMapper.selectList(new EntityWrapper<Unit>().eq("course_id", courseId));
        List<Long> unitIds = getUnitIdsFromUnits(units);
        return simpleUnitVocabularyMapper.countWordByUnitIds(unitIds);
    }

    private List<Long> getUnitIdsFromUnits(List<Unit> units) {
        List<Long> unitIds = new ArrayList<>(units.size());
        units.parallelStream().forEach(unit -> unitIds.add(unit.getId()));
        return unitIds;
    }

    @Override
    public ServerResponse<PlayerVo> getPlayer(HttpSession session, Long unitId, Integer type, Integer order) {
        // 查询当前学生当前模块下所有单词
        PageHelper.startPage(1, 100);
        List<BookVo> bookVos = vocabularyMapper.selectBookVoByUnitId(unitId);
        bookVos.forEach(bookVo -> bookVo.setReadUrl(simpleBaiduSpeak.getLanguagePath(bookVo.getReadUrl())));
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
        PlayerVo playerVo = new PlayerVo();
        playerVo.setPlayerList(bookVos);
        return ServerResponse.createBySuccess(playerVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> studyAgain(HttpSession session, Long courseId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // 查询当前学生当前课程的学习记录
        List<Learn> learns = learnMapper.selectByStuIdAndCourseId(studentId, courseId);
        // 初始化学习下一遍的学习记录
        List<Learn> nextLearns = this.initNext(learns);

        try {
            learnMapper.insertList(nextLearns);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("再学一遍初始化下一遍学习记录时失败！");
            RunLog runLog = new RunLog(2, "再学一遍初始化下一遍学习记录时失败！", new Date());
            runLogMapper.insert(runLog);
            return ServerResponse.createByErrorMessage("初始化学习记录失败！");
        }

        Integer maxCount = simpleStudyCountMapper.selectMaxCountByCourseId(studentId, courseId);
        // 再学一遍时，在 study_count 中新增一条记录，用于记录下一遍的课程学习次数
        StudyCount studyCount = new StudyCount();
        studyCount.setStudyCount(0);
        studyCount.setCount(maxCount + 1);
        studyCount.setCourseId(courseId);
        studyCount.setStudentId(studentId);
        try {
            simpleStudyCountMapper.insert(studyCount);
        } catch (Exception e) {
            log.error("新增再学一遍记录失败！", e);
            RunLog runLog = new RunLog(2, "新增再学一遍记录失败！", new Date());
            runLogMapper.insert(runLog);
            return ServerResponse.createByErrorMessage("新增再学一遍记录失败！");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> restudy(HttpSession session, Long courseId, Long unitId, Long[] wordIds, Integer studyModel) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 查询当前学生当前课程的学习遍数
        Integer maxStudyCount = simpleStudyCountMapper.selectMaxCountByCourseId(student.getId(), courseId);
        // 更新学习记录
        learnMapper.updateUnknownWords(student, unitId, courseId, wordIds, studyModel, maxStudyCount == null ? 1 : maxStudyCount);
        // 更新记忆追踪记录
        // 根据条件查询单词是否已经在记忆追踪中，如果在更新记忆追踪；如果不在新增记忆追踪
        List<Long> updateIds = simpleCapacityReviewMapper.selectByWordIdsAndStudyModel(student, courseId, unitId, wordIds, studyModel);
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
                Map<Long, Map<Long, String>> wordChineseMap = simpleUnitVocabularyMapper.selectWordChineseMapByUnitIdAndWordIds(unitId, idSet);

                for (Vocabulary vocabulary : vocabularies) {
                    capacityReview = new CapacityReview();
                    capacityReview.setClassify(studyModel.toString());
                    capacityReview.setPush(SimpleDateUtil.formatYYYYMMDDHHMMSS(push));
                    capacityReview.setStudent_id(student.getId());
                    capacityReview.setUnit_id(unitId);
                    capacityReview.setCourse_id(courseId);
                    capacityReview.setFault_time(0);
                    capacityReview.setMemory_strength(memoryStrength);
                    capacityReview.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
                    capacityReview.setVocabulary_id(vocabulary.getId());
                    capacityReview.setWord(vocabulary.getWord());
                    capacityReview.setWord_chinese(wordChineseMap.get(vocabulary.getId()).get("wordChinese"));
                    capacityReviews.add(capacityReview);
                }
            } else {
                List<Sentence> sentences = simpleSentenceMapper.selectByIds(insertIds);
                for (Sentence sentence : sentences) {
                    capacityReview = new CapacityReview();
                    capacityReview.setClassify(studyModel.toString());
                    capacityReview.setPush(SimpleDateUtil.formatYYYYMMDDHHMMSS(push));
                    capacityReview.setStudent_id(student.getId());
                    capacityReview.setUnit_id(unitId);
                    capacityReview.setCourse_id(courseId);
                    capacityReview.setFault_time(0);
                    capacityReview.setMemory_strength(memoryStrength);
                    capacityReview.setVocabulary_id(sentence.getId());
                    capacityReview.setWord(sentence.getCentreExample().replace("#", " "));
                    capacityReview.setWord_chinese(sentence.getCentreTranslate().replace("*", ""));
                    capacityReviews.add(capacityReview);
                }
            }
            simpleCapacityReviewMapper.insertByBatch(capacityReviews, studyModel);
        }
        if (updateIds.size() > 0) {
            simpleCapacityReviewMapper.updatePushAndMemoryStrengthByPrimaryKeys(updateIds, push, memoryStrength, studyModel);
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

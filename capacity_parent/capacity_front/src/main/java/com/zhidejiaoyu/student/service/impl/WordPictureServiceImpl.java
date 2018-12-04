package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.WordPictureUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.language.YouDaoTranslate;
import com.zhidejiaoyu.common.utils.server.GoldResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.WordPictureService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class WordPictureServiceImpl implements WordPictureService {

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
    private YouDaoTranslate youDaoTranslate;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudyCountMapper studyCountMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private WordPictureUtil wordPictureUtil;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;
    
    @Value("${ftp.prefix}")
    private String ftpPrefix;

    /**
     * 获取单词图鉴学习数据
     *
     * @param session
     * @param courseId
     * @param unitId
     * @return
     */
    @Override
    public ServerResponse<Object> getWordPicture(HttpSession session, Long courseId, Long unitId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        judgeIsFirstStudy(session, student);

        // 判断学生是否需要进行单元闯关测试
        ServerResponse<Object> x = judgeToUnitTest(unitId, student, courseId);
        if (x != null) {
            return x;
        }

        // 用于判断哪个题型
        Random random = new Random();
        int i = random.nextInt(3)+1;

        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct = capacityPictureMapper.selectNeedReviewWord(unitId, studentId, DateUtil.DateTime());

        // 没有需要复习的
        if (correct == null) {
            // 获取新词
            correct = vocabularyMapper.getNotNeedReviewWord(unitId, studentId);
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
        // 图片前缀
        correct.put("ftpPrefix", ftpPrefix);

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

        try {
            Map<String, String> resultMap = youDaoTranslate.getResultMap(correct.get("word").toString());
            // 音标
            String phonetic = resultMap.get("phonetic");
            if(StringUtils.isNotBlank(phonetic) && !"null".equals(phonetic)) {
                correct.put("soundmark", "[" + phonetic + "]");
            }else {
                correct.put("soundmark", null);
            }
            // 读音url
            correct.put("readUrl", baiduSpeak.getLanguagePath(correct.get("word").toString()));
        } catch (Exception e) {
            log.error("WordPictureServiceImpl.ServerResponse(有道翻译)");
        }

        // 2. 从本课程非本单元下随机获取三个题, 三个作为错题, 并且id不等于正确题id
        List<Map<String, Object>> mapErrorVocabulary = vocabularyMapper.getWordIdByCourse(new Long(correct.get("id").toString()), courseId, unitId);
        mapErrorVocabulary.add(correct); // 四道题
        Collections.shuffle(mapErrorVocabulary); // 随机打乱顺序

        // 封装选项正确答案
        Map subject = new HashMap();
        for(Map m : mapErrorVocabulary){

            boolean b = false;
            if(m.get("word").equals(correct.get("word"))){
                b = true;
            }

//            if(i == 1){
//                correct.put("type", 1);
//                subject.put(m.get("recordpicurl"), b);
//            }else if(i == 2){
                correct.put("type", 2);
                subject.put(m.get("word"), b);
//            }else {
//                correct.put("type", 3);
//                subject.put(m.get("recordpicurl"), b);
//            }
        }
        // 把四个选项添加到correct正确答案数据中
        correct.put("subject", subject);

        // 3. count单元表单词有多少个查询存在图片的    /.
        Integer count = unitMapper.countWordByUnitidByPic(unitId);
        correct.put("wordCount", count);

        // 4. 该单元已学单词  ./
        //Integer count_ = capacityListenMapper.alreadyStudyWord(unit_id, id);
        Long count_ = learnMapper.learnCountWord(studentId, Integer.parseInt(unitId.toString()), "单词图鉴");
        correct.put("plan", count_);

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
     * 判断学生是否需要进行单元闯关测试
     *
     * @param unitId
     * @param student
     * @param courseId
     * @return
     */
    private ServerResponse<Object> judgeToUnitTest(Long unitId, Student student, Long courseId) {
        // 查询当前课程的学习遍数
        Integer maxCount = studyCountMapper.selectMaxCountByCourseId(student.getId(), courseId);
        Long plan = learnMapper.countLearnWord(student.getId(), unitId, "单词图鉴", maxCount == null ? 1 : maxCount);
        // 获取当前单元下的所有单词的总个数
        int wordCount = unitVocabularyMapper.countWordPictureByUnitId(unitId);
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词图鉴信息！", unitId);
            return ServerResponse.createByErrorMessage("The unit no pictures");
        } else if (wordCount == plan) {
            return ServerResponse.createBySuccess(600, "TO_UNIT_TEST");
        }
        return null;
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
        Long studentId = getStudentId(session);
        // 根据学生id实时查询学生信息
        Student student = studentMapper.selectByPrimaryKey(studentId);

        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 判断学生当前单元有无进行单元闯关测试记录，如果已参加过单元闯关测试，提示其需要花费金币购买测试机会，如果还没有测试记录可以免费进行测试
        int flag = this.isFirstTest(student, unitId, "单词图鉴", isTrue);
        if (flag == 1) {
            return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该单元闯关测试，再次参加需扣除1金币。");
        } else if (flag == 2) {
            return ServerResponse.createByError(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
        }

        // 获取单元下所有有图片的单词
        List<Vocabulary> list = vocabularyMapper.getWordPicAll(unitId);

        // 获取课程下带图片的单词用于四个选择题
        // List<Vocabulary> listSelect = vocabularyMapper.getWordIdByCourseAll(courseId);
        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(list.size() * 4);

        // 分配题型‘看词选图’、‘听音选图’、‘看图选词’ 3:3:4
        //return ServerResponse.createBySuccess(allocationWord(list, listSelect));
        // 分题工具类
        Map<String, Object> map = wordPictureUtil.allocationWord(list, listSelect, null);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断学生当前单元有无进行单元闯关测试记录，如果已参加过单元闯关测试，提示其需要花费金币购买测试机会，如果还没有测试记录可以免费进行测试
     *
     * @param student
     * @param unitId
     * @param studyModel
     * @param isTrue
     * @return 1:提示用户需要支付金币购买测试机会；2：金币不足
     */
    private int isFirstTest(Student student, Long unitId, String studyModel, Boolean isTrue) {
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitId(student.getId(), unitId, "单元闯关测试",
                studyModel);
        if (testRecord != null) {
            if (!isTrue) {
                return 1;
            } else {
                // 金币不足
                if (student.getSystemGold() == 0) {
                    return 2;
                }
                student.setSystemGold(BigDecimalUtil.sub(student.getSystemGold(), 1));
                student.setOfflineGold(BigDecimalUtil.add(student.getOfflineGold(), 1));
                studentMapper.updateByPrimaryKeySelective(student);
                String msg = "id为：" + student.getId() + " 的学生花费 1 金币进行" + studyModel + " 模块下的单元闯关测试。";
                RunLog runLog = new RunLog(student.getId(), 5, msg, new Date());
                runLogMapper.insert(runLog);
                log.info(msg);
            }
        }
        return 0;
    }

    /**
     * 获取当前学生id
     *
     * @param session
     * @return id
     */
    private Long getStudentId(HttpSession session){
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生id
        return student.getId();
    }
}

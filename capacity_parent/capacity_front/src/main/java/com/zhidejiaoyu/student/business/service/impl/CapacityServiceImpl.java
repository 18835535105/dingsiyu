package com.zhidejiaoyu.student.business.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.ExcelUtil;
import com.zhidejiaoyu.common.utils.excelUtil.ExportUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.CapacityService;
import com.zhidejiaoyu.common.utils.CapacityFontUtil;
import com.zhidejiaoyu.common.vo.CapacityContentVo;
import com.zhidejiaoyu.common.vo.CapacityDigestVo;
import com.zhidejiaoyu.common.vo.CapacityListVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CapacityServiceImpl extends BaseServiceImpl<CapacityWriteMapper, CapacityWrite> implements CapacityService {

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;


    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private CapacityReviewMapper capacityReviewMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Resource
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Resource
    private SentenceCourseMapper sentenceCourseMapper;

    @Resource
    private SentenceUnitMapper sentenceUnitMapper;

    @Override
    public ServerResponse<CapacityDigestVo> getCapacityDigestVo(HttpSession session, Long courseId, String unitIdStr, String studyModel) {
        Student student = this.getStudent(session);
        CapacityDigestVo vo = new CapacityDigestVo();
        vo.setShowCapacity(student.getShowCapacity() == null || student.getShowCapacity() == 1);

        // 如果传入的unitId 错误，获取当前学生正在学习的单元
        CourseAndUnit courseAndUnit = this.checkUnitId(courseId, unitIdStr, studyModel, student);
        if (Objects.isNull(courseAndUnit.getUnitId())) {
            return ServerResponse.createBySuccess(this.getDefaultCapacityDigestVo(vo));
        }

        return getCapacityDigestVo(student, courseAndUnit, vo, studyModel);
    }

    private CourseAndUnit checkUnitId(Long courseId, String unitIdStr, String studyModel, Student student) {
        if ("NaN".equalsIgnoreCase(unitIdStr) || StringUtils.isEmpty(unitIdStr)) {
            log.warn("获取追词记/追句记是，unitId=NaN.");
            Map<String, String> wordModelMap = new HashMap<>(16);
            Map<String, String> sentenceModelMap = new HashMap<>(16);

            wordModelMap.put("慧记忆", "慧记忆");
            wordModelMap.put("慧听写", "慧听写");
            wordModelMap.put("慧默写", "慧默写");
            wordModelMap.put("单词图鉴", "单词图鉴");

            sentenceModelMap.put("例句翻译", "例句翻译");
            sentenceModelMap.put("例句听力", "例句听力");
            sentenceModelMap.put("例句默写", "例句默写");

            if (wordModelMap.containsKey(studyModel)) {
                // 查询学生当前学习的单元
                CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectByStudentIdAndType(student.getId(), 1);
                if (capacityStudentUnit == null) {
                    log.error("学生[{} - {} - {}]还没有初始化智慧单词课程！", student.getId(), student.getAccount(), student.getStudentName());
                    return new CourseAndUnit();
                }
                return CourseAndUnit.builder().courseId(capacityStudentUnit.getCourseId()).unitId(capacityStudentUnit.getUnitId()).build();
            } else if (sentenceModelMap.containsKey(studyModel)) {
                // 查询学生当前学习的句型单元
                CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectByStudentIdAndType(student.getId(), 2);
                if (capacityStudentUnit == null) {
                    log.error("学生[{} - {} - {}]还没有初始化抢分句型课程！", student.getId(), student.getAccount(), student.getStudentName());
                    return new CourseAndUnit();
                }
                return CourseAndUnit.builder().courseId(capacityStudentUnit.getCourseId()).unitId(capacityStudentUnit.getUnitId()).build();
            }
            return new CourseAndUnit();

        }
        return CourseAndUnit.builder().courseId(courseId).unitId(Long.parseLong(unitIdStr)).build();

    }

    /**
     * 追词纪、追句记返回空的默认数据
     *
     * @param vo
     * @return
     */
    private CapacityDigestVo getDefaultCapacityDigestVo(CapacityDigestVo vo) {
        vo.setStrangenessCount(0);
        vo.setNeedReview(0);
        vo.setWordInfos(new ArrayList<>(0));
        return vo;
    }

    private ServerResponse<CapacityDigestVo> getCapacityDigestVo(Student student, CourseAndUnit courseAndUnit,
                                                                 CapacityDigestVo vo, String studyModel) {
        Long courseId = courseAndUnit.getCourseId();
        Long unitId = courseAndUnit.getUnitId();

        List<CapacityReview> capacityReviews = capacityReviewMapper.selectNewWordsByCourseIdOrUnitId(student, courseId, unitId, studyModel);
        int needReview = capacityReviewMapper.countNeedReviewByCourseIdOrUnitId(student, courseId, unitId, studyModel);
        vo.setNeedReview(needReview);
        vo.setStrangenessCount(capacityReviews.size());
        List<CapacityDigestVo.WordInfo> wordInfos = new ArrayList<>();
        List<CapacityMemory> capacityMemories = getCapacityMemories(capacityReviews);
        for (CapacityMemory capacityMemory : capacityMemories) {
            packageWordInfo(wordInfos, capacityMemory);
        }
        vo.setWordInfos(wordInfos);
        return ServerResponse.createBySuccess(vo);
    }

    private List<CapacityMemory> getCapacityMemories(List<CapacityReview> capacityReviews) {
        List<CapacityMemory> capacityMemories = new ArrayList<>(capacityReviews.size());
        capacityReviews.forEach(capacityReview -> {
            CapacityMemory capacityMemory = new CapacityMemory();
            capacityMemory.setPush(DateUtil.parseYYYYMMDDHHMMSS(capacityReview.getPush()));
            capacityMemory.setMemoryStrength(capacityReview.getMemory_strength());
            capacityMemory.setFaultTime(capacityReview.getFault_time());
            capacityMemory.setStudentId(capacityReview.getStudent_id());
            capacityMemory.setUnitId(capacityReview.getUnit_id());
            capacityMemory.setVocabularyId(capacityReview.getVocabulary_id());
            capacityMemory.setCourseId(capacityReview.getCourse_id());
            capacityMemory.setSyllable(capacityReview.getSyllable());
            capacityMemory.setWord(capacityReview.getWord().contains("#") ?
                    capacityReview.getWord().replace("#", " ").replace("$", "") : capacityReview.getWord());
            capacityMemory.setWordChinese(capacityReview.getWord_chinese().contains("\\*") ?
                    capacityReview.getWord_chinese().replace("*", " ") : capacityReview.getWord_chinese());
            capacityMemory.setId(capacityReview.getId());
            capacityMemories.add(capacityMemory);
        });
        return capacityMemories;
    }


    private void packageWordInfo(List<CapacityDigestVo.WordInfo> wordInfos, CapacityMemory capacityMemory) {
        CapacityDigestVo.WordInfo wordInfo;
        int fontNum;
        CapacityFontUtil capacityFontUtil;
        wordInfo = new CapacityDigestVo.WordInfo();
        fontNum = commonMethod.getFontSize(capacityMemory);
        capacityFontUtil = new CapacityFontUtil(fontNum);
        wordInfo.setContent(capacityMemory.getWord());
        wordInfo.setUnitId(capacityMemory.getUnitId());
        wordInfo.setId(capacityMemory.getVocabularyId());
        wordInfo.setFontSize(capacityFontUtil.getFontSize());
        wordInfo.setFontColor(capacityFontUtil.getFontColor());
        wordInfo.setFontWeight(capacityFontUtil.getFontWeight());
        wordInfo.setUnitId(capacityMemory.getUnitId());
        wordInfo.setShowInfo(false);
        wordInfo.setReadUrl(baiduSpeak.getLanguagePath(capacityMemory.getWord()));
        wordInfos.add(wordInfo);
    }

    @Override
    public ServerResponse<CapacityContentVo> getCapacityContent(HttpSession session, String studyModel, Long courseId,
                                                                Long unitId, Long id) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        String chinese;
        Integer studyCount;

        // 获取单词/例句 中文信息
        String wordMemory = "慧记忆";
        String wordListen = "慧听写";
        String wordWrite = "慧默写";
        String wordPicture = "单词图鉴";
        if (wordMemory.equals(studyModel) || wordListen.equals(studyModel) || wordWrite.equals(studyModel)
                || wordPicture.equals(studyModel)) {
            chinese = vocabularyMapper.selectWordChineseById(id);
            studyCount = learnMapper.selectStudyCountByCourseId(studentId, courseId, unitId, id, studyModel);
        } else {
            chinese = sentenceMapper.selectChineseByIdAndPhase(id);
            if (StringUtils.isNotEmpty(chinese)) {
                chinese = chinese.replace("*", "");
            }
            studyCount = learnMapper.selectStudyCountByCourseId(studentId, courseId, unitId, id, studyModel);
        }

        if (studyCount == null) {
            return ServerResponse.createByErrorMessage("当前单词无学习记录");
        }

        CapacityReview capacityReview = capacityReviewMapper.selectByCourseIdOrUnitId(student, courseId, unitId, id, studyModel);
        int faultTime = capacityReview.getFault_time();
        // 如果学习次数小于错误次数，将错误次数置为学习次数
        if (studyCount < faultTime) {
            faultTime = studyCount;
        }

        CapacityContentVo capacityContentVo = new CapacityContentVo();
        capacityContentVo.setFaultCount(faultTime);
        capacityContentVo.setMemoryStrength(capacityReview.getMemory_strength());
        capacityContentVo.setPush(this.getPushTime(DateUtil.parseYYYYMMDDHHMMSS(capacityReview.getPush())));
        if (studyModel.equals("例句听力") || studyModel.equals("例句翻译") || studyModel.equals("例句默写")) {
            capacityContentVo.setReadUrl(baiduSpeak.getLanguagePath(sentenceMapper.selectByPrimaryKey(capacityReview.getVocabulary_id()).getCentreExample()));
        }
        capacityContentVo.setChinese(chinese);
        capacityContentVo.setStudyCount(studyCount);
        return ServerResponse.createBySuccess(capacityContentVo);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ServerResponse<PageInfo> getCapacityList(HttpSession session, String studyModel, Long courseId, Long unitId,
                                                    Integer page, Integer size) {
        Student student = getStudent(session);

        courseId = this.getCourseId(courseId, unitId, studyModel);

        PageHelper.startPage(page, size);
        List<CapacityReview> capacityReviews = capacityReviewMapper.selectNewWordsByCourseIdOrUnitId(student, courseId,
                unitId, studyModel);
        PageInfo<CapacityReview> info = new PageInfo<>(capacityReviews);
        List<CapacityMemory> capacityMemories = getCapacityMemories(capacityReviews);

        List<CapacityListVo> capacityListVos = new ArrayList<>();
        for (CapacityMemory capacityMemory : capacityMemories) {
            packageWordCapacityListVO(capacityListVos, capacityMemory);
        }
        PageInfo<CapacityListVo> pageInfo = new PageInfo<>(capacityListVos);
        pageInfo.setPages(info.getPages());
        pageInfo.setTotal(info.getTotal());
        return ServerResponse.createBySuccess(pageInfo);

    }

    /**
     * 防止courseId为null时出现错误
     *
     * @param courseId
     * @param unitId
     * @param studyModel
     * @return
     */
    private Long getCourseId(Long courseId, Long unitId, String studyModel) {
        if (!Objects.isNull(courseId)) {
            return courseId;
        }
        Long returnCourseId;
        if (studyModel.contains("例句")) {
            returnCourseId = sentenceUnitMapper.selectCourseIdByUnitId(unitId);
        } else {
            returnCourseId = unitMapper.selectCourseIdByUnitId(unitId);
        }
        Assert.notNull(returnCourseId, "未查询到单元id为 " + unitId + " 的课程！");
        return returnCourseId;
    }

    @Override
    public void downloadCapacity(HttpSession session, HttpServletResponse response, String studyModel, Long courseId,
                                 Long unitId, Integer pageNum, Integer pageSize) {
        Student student = getStudent(session);
        final String[] modelArr = {"慧记忆", "慧听写", "慧默写", "单词图鉴", "例句听力", "例句翻译", "例句默写"};
        Long studentId = student.getId();

        // excel标题
        String[] title = {"序号", "英文", "中文解释", "记忆强度", "距离复习"};
        PageHelper.startPage(pageNum, pageSize);
        List<CapacityReview> capacityReviews = capacityReviewMapper.selectNewWordsByCourseIdOrUnitId(student, courseId,
                unitId, studyModel);

        // excel文件名
        String fileName = this.getFileName(studyModel, courseId);
        StringBuilder fileNameSb = new StringBuilder(fileName);
        String unitName = this.getUnitName(studyModel, unitId);
        if (unitName != null) {
            fileNameSb.append("-").append(unitName);
        }
        // 内容列表 行、列
        String[][] content = new String[capacityReviews.size()][title.length];
        String sheetName = null;
        for (int i = 0; i < modelArr.length; i++) {
            if (modelArr[i].equals(studyModel)) {
                fileNameSb.append(studyModel);
                if (i <= 3) {
                    fileNameSb.append("生词汇总");
                    downloadWordCapacity(content, capacityReviews);
                } else {
                    fileNameSb.append("生句汇总");
                    downloadSentenceCapacity(content, capacityReviews);
                }
                // sheet名
                sheetName = modelArr[i] + " - 记忆追踪";
                fileNameSb.append(System.currentTimeMillis()).append(".xls");
                break;
            }
        }
        fileName = fileNameSb.toString();

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        // 响应到客户端
        try {
            ExportUtil.exportExcel(response, fileName, wb);
        } catch (Exception e) {
            log.error("学生 {} 导出 {} 模块下记忆追踪信息失败！", studentId, studyModel, e);
        }
    }

    private String getFileName(String studyModel, Long courseId) {
        String fileName;
        String str = "例句";
        if (StringUtils.isNotEmpty(studyModel) && studyModel.contains(str)) {
            SentenceCourse sentenceCourse = sentenceCourseMapper.selectById(courseId);
            fileName = sentenceCourse.getCourseName();
        } else {
            fileName = courseMapper.selectCourseNameById(courseId);
        }
        return fileName;
    }

    private String getUnitName(String studyModel, Long unitId) {
        String str = "例句";
        String unitName = null;
        if (unitId != 0) {
            if (StringUtils.isNotEmpty(studyModel) && studyModel.contains(str)) {
                SentenceUnit sentenceUnit = sentenceUnitMapper.selectById(unitId);
                unitName = sentenceUnit.getUnitName();
            } else {
                unitName = unitMapper.getUnitNameByUnitId(unitId);
            }
        }
        return unitName;
    }

    @Override
    public void cancelTip(HttpSession session) {
        Student student = super.getStudent(session);
        if (!Objects.equals(student.getShowCapacity(), 2)) {
            student.setShowCapacity(2);
            studentMapper.updateById(student);
        }
    }

    private void downloadSentenceCapacity(String[][] content, List<CapacityReview> capacityReviews) {
        List<SentenceListen> sentenceListens = getSentenceListenList(capacityReviews);
        SentenceListen sentenceListen;
        String pushTime;
        if (sentenceListens.size() > 0) {
            for (int i = 0; i < sentenceListens.size(); i++) {
                sentenceListen = sentenceListens.get(i);
                pushTime = this.getPushTime(sentenceListen.getPush());
                content[i][0] = (i + 1) + "";
                content[i][1] = sentenceListen.getWord();
                content[i][2] = sentenceListen.getWordChinese();
                content[i][3] = sentenceListen.getMemoryStrength() * 100 + "%";
                content[i][4] = "0".equals(pushTime) ? "请立刻复习" : pushTime;
            }
        }
    }

    private List<SentenceListen> getSentenceListenList(List<CapacityReview> capacityReviews) {
        List<SentenceListen> sentenceListens = new ArrayList<>(capacityReviews.size());
        capacityReviews.parallelStream().forEach(capacityReview -> {
            SentenceListen sentenceListen = new SentenceListen();
            sentenceListen.setMemoryStrength(capacityReview.getMemory_strength());
            sentenceListen.setPush(DateUtil.parseYYYYMMDDHHMMSS(capacityReview.getPush()));
            sentenceListen.setFaultTime(capacityReview.getFault_time());
            sentenceListen.setCourseId(capacityReview.getCourse_id());
            sentenceListen.setStudentId(capacityReview.getStudent_id());
            sentenceListen.setUnitId(capacityReview.getUnit_id());
            sentenceListen.setVocabularyId(capacityReview.getVocabulary_id());
            sentenceListen.setWord(capacityReview.getWord());
            sentenceListen.setWordChinese(capacityReview.getWord_chinese());
            sentenceListen.setId(capacityReview.getId());
            sentenceListens.add(sentenceListen);
        });
        return sentenceListens;
    }

    @SuppressWarnings("all")
    private void downloadWordCapacity(String[][] content, List<CapacityReview> capacityReviews) {
        List<CapacityMemory> capacityMemories = getCapacityMemories(capacityReviews);
        Map<Long, Map<Long, String>> map;
        CapacityMemory capacityMemory;
        String syllable;
        String pushTime;
        if (capacityMemories.size() > 0) {
            List<Long> ids = capacityMemories.stream().map(CapacityMemory::getVocabularyId).collect(Collectors.toList());
            // 查询单词的音节
            map = vocabularyMapper.selectSyllableByWordId(ids);
            for (int i = 0; i < capacityMemories.size(); i++) {
                capacityMemory = capacityMemories.get(i);
                syllable = map.get(capacityMemory.getVocabularyId()).get("syllable");
                pushTime = this.getPushTime(capacityMemory.getPush());
                content[i][0] = (i + 1) + "";
                content[i][1] = StringUtils.isEmpty(syllable) ? capacityMemory.getWord() : syllable;
                content[i][2] = capacityMemory.getWordChinese();
                content[i][3] = capacityMemory.getMemoryStrength() * 100 + "%";
                content[i][4] = "0".equals(pushTime) ? "请立刻复习" : pushTime;
            }
        }
    }

    private void packageWordCapacityListVO(List<CapacityListVo> capacityListVos, CapacityMemory capacityMemory) {
        boolean flag = capacityMemory.getWordChinese().contains("*");
        Vocabulary vocabulary = vocabularyMapper.selectById(capacityMemory.getVocabularyId());
        CapacityListVo capacityListVo = new CapacityListVo();
        capacityListVo.setChinese(flag ?
                capacityMemory.getWordChinese().replace("*", "") : capacityMemory.getWordChinese());
        capacityListVo.setContent(flag ? capacityMemory.getWord() : StringUtils.isEmpty(capacityMemory.getSyllable()) ? capacityMemory.getWord() : capacityMemory.getSyllable());
        capacityListVo.setMemeoryStrength(capacityMemory.getMemoryStrength());
        capacityListVo.setPush(this.getPushTime(capacityMemory.getPush()));
        capacityListVo.setReadUrl(baiduSpeak.getLanguagePath(capacityMemory.getWord()));
        capacityListVo.setSoundMark((vocabulary == null || StringUtils.isEmpty(vocabulary.getSoundMark())) ? "" : vocabulary.getSoundMark());
        capacityListVos.add(capacityListVo);
    }

    /**
     * 获取黄金记忆时间距当前时间的时长
     *
     * @param push
     * @return
     */
    private String getPushTime(Date push) {
        if (push == null) {
            return null;
        }

        Long now = System.currentTimeMillis();
        Long pushTime = push.getTime();
        long sub = (pushTime - now) / 1000;

        // 已到达黄金记忆点
        if (sub <= 0) {
            return "0";
        }

        long day = sub / (24 * 3600);

        long hour = sub % (24 * 3600) / 3600;

        long minute = sub % 3600 / 60;

        long second = sub % 60;

        String time = "";
        if (day != 0) {
            time += day + "天";
            time += hour + "小时";
            time += minute + "分钟";
            time += second + "秒";
        } else if (hour != 0) {
            time += hour + "小时";
            time += minute + "分钟";
            time += second + "秒";
        } else if (minute != 0) {
            time += minute + "分钟";
            time += second + "秒";
        } else if (second != 0) {
            time += second + "秒";
        } else {
            time += "0";
        }

        return time;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static
    class CourseAndUnit {
        private Long courseId;

        private Long unitId;
    }

}

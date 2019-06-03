package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.ExcelUtil;
import com.zhidejiaoyu.common.utils.excelUtil.ExportUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.CapacityService;
import com.zhidejiaoyu.student.utils.CapacityFontUtil;
import com.zhidejiaoyu.student.vo.CapacityContentVo;
import com.zhidejiaoyu.student.vo.CapacityDigestVo;
import com.zhidejiaoyu.student.vo.CapacityListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CapacityServiceImpl extends BaseServiceImpl<CapacityWriteMapper, CapacityWrite> implements CapacityService {

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private CapacityWriteMapper capacityWriteMapper;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Autowired
    private SentenceListenMapper sentenceListenMapper;

    @Autowired
    private StudyCountMapper studyCountMapper;

    @Autowired
    private SentenceWriteMapper sentenceWriteMapper;

    @Autowired
    private SentenceTranslateMapper sentenceTranslateMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CapacityReviewMapper capacityReviewMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Override
    public ServerResponse<CapacityDigestVo> getCapacityDigestVo(HttpSession session, Long courseId, Long unitId, String studyModel) {
        Student student = getStudent(session);
        CapacityDigestVo vo = new CapacityDigestVo();
        if (student.getShowCapacity() == null || student.getShowCapacity() == 1) {
            vo.setShowCapacity(true);
        } else {
            vo.setShowCapacity(false);
        }

        return getCapacityDigestVo(student, courseId, unitId, vo, studyModel);
    }

    private ServerResponse<CapacityDigestVo> getCapacityDigestVo(Student student, Long courseId, Long unitId,
                                                                 CapacityDigestVo vo, String studyModel) {
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

        Integer maxCount = 1;

        // 获取单词/例句 中文信息
        String wordMemory = "慧记忆";
        String wordListen = "慧听写";
        String wordWrite = "慧默写";
        String wordPicture = "单词图鉴";
        if (wordMemory.equals(studyModel) || wordListen.equals(studyModel) || wordWrite.equals(studyModel)
                || wordPicture.equals(studyModel)) {
            chinese = vocabularyMapper.selectWordChineseById(id);
            studyCount = learnMapper.selectStudyCountByCourseId(studentId, courseId, unitId, id, studyModel,
                    maxCount);
        } else {
            chinese = sentenceMapper.selectChineseByIdAndPhase(id);
            if (StringUtils.isNotEmpty(chinese)) {
                chinese = chinese.replace("*", "");
            }
            studyCount = learnMapper.selectStudyCountByCourseId(studentId, courseId, unitId, id, studyModel,
                    maxCount);
        }

        if (studyCount == null) {
            return ServerResponse.createByErrorMessage("当前单词无学习记录");
        }

        CapacityReview capacityReview = capacityReviewMapper.selectByCourseIdOrUnitId(student, courseId, unitId, id, studyModel);
        CapacityContentVo capacityContentVo = new CapacityContentVo();
        capacityContentVo.setFaultCount(capacityReview.getFault_time());
        capacityContentVo.setMemoryStrength(capacityReview.getMemory_strength());
        capacityContentVo.setPush(this.getPushTime(DateUtil.parseYYYYMMDDHHMMSS(capacityReview.getPush())));
        if(studyModel.equals("例句听力")||studyModel.equals("例句翻译")||studyModel.equals("例句默写")){
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
        return packageCapacityList(student, courseId, unitId, studyModel, page, size);
    }

    @SuppressWarnings("all")
    private ServerResponse<PageInfo> packageCapacityList(Student student, Long courseId, Long unitId, String studyModel,
                                                         Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<CapacityReview> capacityReviews = capacityReviewMapper.selectNewWordsByCourseIdOrUnitId(student, courseId,
                unitId, studyModel);
        PageInfo<CapacityMemory> info = new PageInfo(capacityReviews);
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

    @Override
    public void downloadCapacity(HttpSession session, HttpServletResponse response, String studyModel, Long courseId,
                                 Long unitId, Integer pageNum, Integer pageSize) {
        Student student = getStudent(session);
        final String[] modelArr = {"慧记忆", "慧听写", "慧默写", "单词图鉴", "例句听力", "例句翻译", "例句默写"};
        Long studentId = student.getId();

        String fileName = courseMapper.selectCourseName(Integer.parseInt(courseId.toString()));
        String unitName = null;
        if (unitId != 0) {
            unitName = unitMapper.getUnitNameByUnitId(unitId);
        }

        List<Long> ids = new ArrayList<>();
        // excel标题
        String[] title = {"序号", "英文", "中文解释", "记忆强度", "距离复习"};
        PageHelper.startPage(pageNum, pageSize);
        List<CapacityReview> capacityReviews = capacityReviewMapper.selectNewWordsByCourseIdOrUnitId(student, courseId,
                unitId, studyModel);

        // excel文件名
        StringBuilder fileNameSb = new StringBuilder(fileName);
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
                    downloadWordCapacity(ids, content, capacityReviews);
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
            log.error("学生 {} 导出 {} 模块下记忆追踪信息失败！", studentId, studyModel, e.getMessage(), e);
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
    private void downloadWordCapacity(List<Long> ids, String[][] content, List<CapacityReview> capacityReviews) {
        List<CapacityMemory> capacityMemories = getCapacityMemories(capacityReviews);
        Map<Long, Map<Long, String>> map;
        CapacityMemory capacityMemory;
        String syllable;
        String pushTime;
        if (capacityMemories.size() > 0) {
            capacityMemories.forEach(capacityMemory1 -> ids.add(capacityMemory1.getVocabularyId()));
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

    /**
     * 封装例句默写记忆追踪列表页内容
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param page      当前页
     * @param size      每页数据量
     * @return
     */
    @SuppressWarnings("all")
    private ServerResponse<PageInfo> getSentenceWriteCapacityListVo(Long studentId, Long courseId, Long unitId, Integer page,
                                                                    Integer size) {
        PageHelper.startPage(page, size);
        List<SentenceWrite> sentenceWrites = unitId != 0 ? sentenceWriteMapper.selectByUnitIdAndStudentId(unitId, studentId)
                : sentenceWriteMapper.selectByCourseIdAndStudentId(courseId, studentId);
        PageInfo info = new PageInfo(sentenceWrites);

        List<CapacityListVo> capacityListVos = new ArrayList<>();
        for (SentenceWrite sentenceWrite : sentenceWrites) {
            packageSentenceCapacityListVO(capacityListVos, sentenceWrite);
        }
        PageInfo<CapacityListVo> pageInfo = new PageInfo<>(capacityListVos);
        pageInfo.setPages(info.getPages());
        pageInfo.setTotal(info.getTotal());

        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 封装例句翻译记忆追踪列表页内容
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param page      当前页
     * @param size      每页数据量
     * @return
     */
    @SuppressWarnings("all")
    private ServerResponse<PageInfo> getSentenceTranslateCapacityListVo(Long studentId, Long courseId, Long unitId, Integer page,
                                                                        Integer size) {
        PageHelper.startPage(page, size);
        List<SentenceTranslate> sentenceListens = unitId != 0 ? sentenceTranslateMapper.selectByUnitIdAndStudentId(unitId, studentId)
                : sentenceTranslateMapper.selectByCourseIdAndStudentId(courseId, studentId);
        PageInfo info = new PageInfo(sentenceListens);

        List<CapacityListVo> capacityListVos = new ArrayList<>();
        for (SentenceTranslate sentenceTranslate : sentenceListens) {
            packageSentenceCapacityListVO(capacityListVos, sentenceTranslate);
        }
        PageInfo<CapacityListVo> pageInfo = new PageInfo<>(capacityListVos);
        pageInfo.setTotal(info.getTotal());
        pageInfo.setPages(info.getPages());

        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 封装例句听力模块记忆追踪列表页内容
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param page      当前页
     * @param size      每页数据量
     * @return
     */
    @SuppressWarnings("all")
    private ServerResponse<PageInfo> getSentenceListenCapacityListVo(Long studentId, Long courseId, Long unitId, Integer page,
                                                                     Integer size) {
        PageHelper.startPage(page, size);
        List<SentenceListen> sentenceListens = unitId != 0 ? sentenceListenMapper.selectByUnitIdAndStudentId(unitId, studentId)
                : sentenceListenMapper.selectByCourseIdAndStudentId(courseId, studentId);
        PageInfo info = new PageInfo(sentenceListens);

        List<CapacityListVo> capacityListVos = new ArrayList<>();
        for (SentenceListen sentenceListen : sentenceListens) {
            packageSentenceCapacityListVO(capacityListVos, sentenceListen);
        }
        PageInfo<CapacityListVo> pageInfo = new PageInfo<>(capacityListVos);
        pageInfo.setTotal(info.getTotal());
        pageInfo.setPages(info.getPages());

        return ServerResponse.createBySuccess(pageInfo);
    }

    private void packageSentenceCapacityListVO(List<CapacityListVo> capacityListVos, SentenceListen sentenceListen) {
        CapacityListVo capacityListVo = new CapacityListVo();
        capacityListVo.setChinese(sentenceListen.getWordChinese());
        capacityListVo.setContent(sentenceListen.getWord().replace("*", " "));
        capacityListVo.setMemeoryStrength(sentenceListen.getMemoryStrength());
        capacityListVo.setPush(this.getPushTime(sentenceListen.getPush()));
        capacityListVo.setReadUrl(baiduSpeak.getLanguagePath(sentenceListen.getWord()));
        capacityListVo.setSoundMark(commonMethod.getSoundMark(sentenceListen.getWord()));
        capacityListVos.add(capacityListVo);
    }

    /**
     * 封装慧默写模块记忆追踪列表页内容
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param page      当前页
     * @param size      每页数据量
     * @return
     */
    @SuppressWarnings("all")
    private ServerResponse<PageInfo> getCapacityWriteCapacityListVo(Long studentId, Long courseId, Long unitId, Integer page,
                                                                    Integer size) {
        PageHelper.startPage(page, size);
        List<CapacityWrite> capacityWrites = unitId != 0 ? capacityWriteMapper.selectByUnitIdAndStudentId(unitId, studentId)
                : capacityWriteMapper.selectByCourseIdAndStudentId(courseId, studentId);
        PageInfo info = new PageInfo(capacityWrites);

        List<CapacityListVo> capacityListVos = new ArrayList<>();
        for (CapacityWrite capacityWrite : capacityWrites) {
            packageWordCapacityListVO(capacityListVos, capacityWrite);
        }
        PageInfo<CapacityListVo> pageInfo = new PageInfo<>(capacityListVos);
        pageInfo.setTotal(info.getTotal());
        pageInfo.setPages(info.getPages());

        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 封装慧听写模块记忆追踪列表页内容
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param page      当前页
     * @param size      每页数据量
     * @return
     */
    @SuppressWarnings("all")
    private ServerResponse<PageInfo> getCapacityListenCapacityListVo(Long studentId, Long courseId, Long unitId, Integer page,
                                                                     Integer size) {
        PageHelper.startPage(page, size);
        List<CapacityListen> capacityListens = unitId != 0 ? capacityListenMapper.selectByUnitIdAndStudentId(unitId, studentId)
                : capacityListenMapper.selectByCourseIdAndStudentId(courseId, studentId);
        PageInfo info = new PageInfo(capacityListens);

        List<CapacityListVo> capacityListVos = new ArrayList<>();
        for (CapacityListen capacityListen : capacityListens) {
            packageWordCapacityListVO(capacityListVos, capacityListen);
        }
        PageInfo<CapacityListVo> pageInfo = new PageInfo<>(capacityListVos);
        pageInfo.setPages(info.getPages());
        pageInfo.setTotal(info.getTotal());

        return ServerResponse.createBySuccess(pageInfo);
    }

    private void packageWordCapacityListVO(List<CapacityListVo> capacityListVos, CapacityMemory capacityMemory) {
        CapacityListVo capacityListVo;
        boolean flag = capacityMemory.getWordChinese().contains("*");
        capacityListVo = new CapacityListVo();
        capacityListVo.setChinese(flag ?
                capacityMemory.getWordChinese().replace("*", "") : capacityMemory.getWordChinese());
        capacityListVo.setContent(flag ? capacityMemory.getWord() : StringUtils.isEmpty(capacityMemory.getSyllable()) ? capacityMemory.getWord() : capacityMemory.getSyllable());
        capacityListVo.setMemeoryStrength(capacityMemory.getMemoryStrength());
        capacityListVo.setPush(this.getPushTime(capacityMemory.getPush()));
        capacityListVo.setReadUrl(baiduSpeak.getLanguagePath(capacityMemory.getWord()));
        capacityListVo.setSoundMark(commonMethod.getSoundMark(capacityMemory.getWord()));
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
        Long sub = (pushTime - now) / 1000;

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

}

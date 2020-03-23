package com.zhidejiaoyu.student.business.service.simple.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.CapacityFontUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.ExcelUtil;
import com.zhidejiaoyu.common.utils.excelUtil.ExportUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.CapacityContentVo;
import com.zhidejiaoyu.common.vo.CapacityDigestVo;
import com.zhidejiaoyu.common.vo.simple.capacityVo.CapacityListVo;
import com.zhidejiaoyu.student.business.service.simple.SimpleCapacityServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimpleCourseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@Service
public class SimpleCapacityServiceImplSimple extends SimpleBaseServiceImpl<SimpleSimpleCapacityMapper, SimpleCapacity> implements SimpleCapacityServiceSimple {

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleSimpleCapacityMapper simpleSimpleCapacityMapper;

    @Autowired
    private SimpleCourseService courseService;

    @Override
    public ServerResponse<CapacityDigestVo> getCapacityDigestVo(HttpSession session, Long courseId, Integer type) {
        Student student = getStudent(session);
        CapacityDigestVo vo = new CapacityDigestVo();

        if (student.getShowCapacity() == null || student.getShowCapacity() == 1) {
            vo.setShowCapacity(true);
        } else {
            vo.setShowCapacity(false);
        }

        return getCapacityDigestVo(student, courseId, vo, type);
    }

    private ServerResponse<CapacityDigestVo> getCapacityDigestVo(Student student, Long courseId, CapacityDigestVo vo, Integer type) {

        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());

        List<SimpleCapacity> simpleCapacities = simpleSimpleCapacityMapper.selectList(new QueryWrapper<SimpleCapacity>()
                .eq("student_id", student.getId()).
                eq("course_id", courseId).eq("type", type).lt("memory_strength", 1)
                .orderBy(true, true, "push"));

        PageInfo<SimpleCapacity> pageInfo = new PageInfo<>(simpleCapacities);
        vo.setTotalPages(pageInfo.getPages());

        Integer needReview = simpleSimpleCapacityMapper.countNeedReview(student, courseId, type);

        vo.setNeedReview(needReview);
        vo.setStrangenessCount(simpleCapacities.size());
        List<CapacityDigestVo.WordInfo> wordInfos = new ArrayList<>();
        for (SimpleCapacity simpleCapacity : simpleCapacities) {
            packageWordInfo(wordInfos, simpleCapacity);
        }
        vo.setWordInfos(wordInfos);
        return ServerResponse.createBySuccess(vo);
    }

    private void packageWordInfo(List<CapacityDigestVo.WordInfo> wordInfos, SimpleCapacity simpleCapacity) {
        CapacityDigestVo.WordInfo wordInfo = new CapacityDigestVo.WordInfo();
        int fontNum = commonMethod.getFontSize(simpleCapacity);
        CapacityFontUtil capacityFontUtil = new CapacityFontUtil(fontNum);
        wordInfo.setContent(simpleCapacity.getWord().replace("$","").replace("#"," "));
        wordInfo.setUnitId(simpleCapacity.getUnitId());
        wordInfo.setId(simpleCapacity.getVocabularyId());
        wordInfo.setFontSize(capacityFontUtil.getFontSize());
        wordInfo.setFontColor(capacityFontUtil.getFontColor());
        wordInfo.setFontWeight(capacityFontUtil.getFontWeight());
        wordInfo.setUnitId(simpleCapacity.getUnitId());
        wordInfo.setShowInfo(false);
        wordInfo.setReadUrl(baiduSpeak.getLanguagePath(simpleCapacity.getWord()));
        wordInfos.add(wordInfo);
    }

    @Override
    public ServerResponse<CapacityContentVo> getCapacityContent(HttpSession session, Integer type, Long courseId, Long id) {
        Student student = getStudent(session);

        List<Learn> learns = learnMapper.selectList(new QueryWrapper<Learn>().eq("student_id", student.getId())
                .eq("course_id", courseId).eq("vocabulary_id", id).eq("type", 1));
        if (learns.isEmpty()) {
            return ServerResponse.createByErrorMessage("当前单词无学习记录");
        }

        Vocabulary vocabulary = vocabularyMapper.selectById(id);
        if (vocabulary == null) {
            return ServerResponse.createByErrorMessage("未查询到当前单词信息！");
        }

        List<SimpleCapacity> simpleCapacities = simpleSimpleCapacityMapper.selectList(new QueryWrapper<SimpleCapacity>()
                .eq("student_id", student.getId()).eq("course_id", courseId).eq("type", type).eq("vocabulary_id", id));
        SimpleCapacity simpleCapacity;
        if (simpleCapacities != null && simpleCapacities.size() > 0) {
            simpleCapacity = simpleCapacities.get(0);
        } else  {
            simpleCapacity = new SimpleCapacity();
            simpleCapacity.setMemoryStrength(0.12);
            simpleCapacity.setPush(new Date());
        }

        Integer studyCount = learns.get(0).getStudyCount();
        Integer faultTime = simpleCapacity.getFaultTime();
        // 如果学习次数小于错误次数，将错误次数置为学习次数
        if (faultTime != null && studyCount != null && studyCount < faultTime) {
            faultTime = studyCount;
        }

        CapacityContentVo capacityContentVo = new CapacityContentVo();
        capacityContentVo.setFaultCount(faultTime);
        capacityContentVo.setMemoryStrength(simpleCapacity.getMemoryStrength());
        capacityContentVo.setPush(this.getPushTime(DateUtil.parseYYYYMMDDHHMMSS(simpleCapacity.getPush())));

        capacityContentVo.setChinese(vocabulary.getWordChinese());
        capacityContentVo.setStudyCount(studyCount);
        return ServerResponse.createBySuccess(capacityContentVo);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ServerResponse<PageInfo> getCapacityList(HttpSession session, Integer type, Long courseId,
                                                    Integer page, Integer size) {
        Student student = getStudent(session);
        return packageCapacityList(student, courseId, type, page, size);
    }

    @SuppressWarnings("all")
    private ServerResponse<PageInfo> packageCapacityList(Student student, Long courseId, Integer type, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<CapacityListVo> vos = simpleSimpleCapacityMapper.selectByCourseId(student.getId(), courseId, type);
        for (CapacityListVo vo : vos) {
            vo.setReadUrl(baiduSpeak.getLanguagePath(vo.getReadUrl()));
            vo.setPush(this.getPushTime(vo.getPushTime()));
        }
        PageInfo<CapacityMemory> info = new PageInfo(vos);
        return ServerResponse.createBySuccess(info);
    }

    @Override
    public void downloadCapacity(HttpSession session, HttpServletResponse response, Integer type, Long courseId,
                                 Integer pageNum, Integer pageSize) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String typeStr = commonMethod.getTestType(type);

        String fileName = simpleCourseMapper.selectCourseName(Integer.parseInt(courseId.toString()));

        List<Long> ids = new ArrayList<>();
        // excel标题
        String[] title = {"序号", "英文", "中文解释", "记忆强度", "距离复习"};
        List<SimpleCapacity> simpleCapacities = simpleSimpleCapacityMapper.selectList(new QueryWrapper<SimpleCapacity>().eq("student_id", student.getId())
                .eq("course_id", courseId).eq("type", type));

        // excel文件名
        StringBuilder fileNameSb = new StringBuilder(fileName);
        // 内容列表 行、列
        String[][] content = new String[simpleCapacities.size()][title.length];
        downloadWordCapacity(ids, content, simpleCapacities);
        // sheet名
        String sheetName = typeStr + " - 记忆追踪";
        fileNameSb.append(System.currentTimeMillis()).append(".xls");
        fileName = fileNameSb.toString();

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        // 响应到客户端
        try {
            ExportUtil.exportExcel(response, fileName, wb);
        } catch (Exception e) {
            log.error("学生 {} 导出 {} 模块下记忆追踪信息失败！", student.getId(), typeStr, e);
        }
    }

    @SuppressWarnings("all")
    private void downloadWordCapacity(List<Long> ids, String[][] content, List<SimpleCapacity> simpleCapacities) {
        Map<Long, Map<Long, String>> map;
        SimpleCapacity simpleCapacity;
        String syllable;
        String pushTime;

        int size = simpleCapacities.size();
        if (size > 0) {
            simpleCapacities.forEach(capacityMemory1 -> ids.add(capacityMemory1.getVocabularyId()));
            // 查询单词的音节
            map = vocabularyMapper.selectSyllableByWordId(ids);
            for (int i = 0; i < size; i++) {
                simpleCapacity = simpleCapacities.get(i);
                if (map.get(simpleCapacity.getVocabularyId()) != null) {
                    syllable = map.get(simpleCapacity.getVocabularyId()).get("syllable");
                } else {
                    syllable = null;
                }
                pushTime = this.getPushTime(simpleCapacity.getPush());
                content[i][0] = (i + 1) + "";
                content[i][1] = StringUtils.isEmpty(syllable) ? simpleCapacity.getWord() : syllable;
                content[i][2] = simpleCapacity.getWordChinese();
                content[i][3] = simpleCapacity.getMemoryStrength() * 100 + "%";
                content[i][4] = "0".equals(pushTime) ? "请立刻复习" : pushTime;
            }
        }
    }

    @Override
    public ServerResponse<String> cancelTip(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student.setShowCapacity(2);
        simpleStudentMapper.updateByPrimaryKeySelective(student);
        student = simpleStudentMapper.selectById(student.getId());
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess();
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

    @Override
    public ServerResponse<Map<Object, Object>> getNeedRebiewVo(HttpSession session) {
        Map<Object, Object> courseMap = new HashMap<>();
        for (int type = 1; type <= 9; type++) {
            ServerResponse<List<Map<String, Object>>> allCourse = courseService.getAllCourse(session, type, false);
            List<Map<String, Object>> data = allCourse.getData();
            if (data != null && data.size() >= 1) {
                Map<String, Object> map = data.get(0);
                if (map != null) {
                    Long courseId = (Long) map.get("id");
                    Student student = getStudent(session);
                    Integer countNeedReview = simpleSimpleCapacityMapper.countNeedReview(student, courseId, type);
                    courseMap.put(type, countNeedReview);
                }
            } else {
                courseMap.put(type, 0);
            }
        }
        return ServerResponse.createBySuccess(courseMap);
    }

}

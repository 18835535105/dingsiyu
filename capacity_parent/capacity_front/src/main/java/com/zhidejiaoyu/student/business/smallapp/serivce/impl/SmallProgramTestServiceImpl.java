package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Service("smallProgramTestService")
public class SmallProgramTestServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements SmallProgramTestService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private ErrorLearnLogMapper errorLearnLogMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private ShareConfigMapper shareConfigMapper;
    @Resource
    private TestRecordMapper testRecordMapper;


    @Override
    public Object getTest(HttpSession session) {
        if (session.getAttribute(TimeConstant.BEGIN_START_TIME) == null) {
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        }
        Student student = getStudent(session);
        List<Map<String, Object>> maps = errorLearnLogMapper.selectVocabularyByStudentId(student.getId());
        Map<String, Object> returnMap = new HashMap<>();
        if (maps.size() == 0) {
            //获取优先级最大的单元
            StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(student.getId());
            //获取当前单元的单词
            List<Vocabulary> vocabularies = vocabularyMapper.selectByUnitId(studentStudyPlanNew.getUnitId());
            vocabularies.forEach(vocabulary -> {
                Map<String, Object> listMap = new HashMap<>();
                listMap.put("wordId", vocabulary.getId());
                listMap.put("word", vocabulary.getWord());
                listMap.put("wordChinese", vocabulary.getWordChinese());
                maps.add(listMap);
            });
        }
        returnMap.put("gold", student.getSystemGold().intValue());
        //获取单词id
        List<Long> vocabularyIds = new ArrayList<>();
        maps.forEach(map -> {
            vocabularyIds.add(Long.parseLong(map.get("wordId").toString()));
        });
        List<Map<String, Object>> getMaps = new ArrayList<>();
        getMaps.addAll(maps);
        if (getMaps.size() < 15) {
            while (getMaps.size() < 15) {
                for (Map<String, Object> map : maps) {
                    if (getMaps.size() < 15) {
                        getMaps.add(map);
                    } else {
                        break;
                    }
                }
            }
        }
        returnMap.put("optionList", getOptionList(getMaps, vocabularyIds));
        returnMap.put("writeList", getWriteList(getMaps));
        //更新获取单词复习数量
        updateErrorLearnLog(vocabularyIds, student.getId());
        return returnMap;
    }

    @Override
    public Object saveTest(Integer point, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        Date startDate = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        Date date = new Date();
        if (point > 80) {
            TestRecord testRecord = new TestRecord();
            testRecord.setGenre(GenreConstant.SMALLAPP_GENRE);
            testRecord.setStudyModel(StudyModelConstant.SMALLAPP_STUDY_MODEL);
            testRecord.setStudentId(student.getId());
            testRecord.setTestEndTime(date);
            testRecord.setTestStartTime(startDate);
            returnMap.put("point", point);
            testRecordMapper.insert(testRecord);
        } else {
            returnMap.put("point", 0);
        }
        student.setSystemGold(student.getSystemGold() + 50);
        String msg = "id为：" + student.getId() + "的学生在[" + GenreConstant.SMALLAPP_GENRE
                + "]模块下的单元闯关测试中闯关成功，获得#" + 50 + "#枚金币";
        super.saveRunLog(student, 4, null, null, msg);
        studentMapper.updateById(student);
        Integer adminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        ShareConfig shareConfig = shareConfigMapper.selectByAdminId(adminId);
        if (shareConfig == null) {
            returnMap.put("img", null);
            returnMap.put("word", null);
        } else {
            returnMap.put("img", shareConfig.getImgUrl());
            returnMap.put("word", shareConfig.getImgWord());
        }
        returnMap.put("gold", student.getSystemGold().intValue());
        returnMap.put("studentId", student.getId());
        return returnMap;
    }

    private void updateErrorLearnLog(List<Long> vocabularyIds, Long studentId) {
        List<ErrorLearnLog> errorLearnLogs = errorLearnLogMapper.selectVocabularyByStudentIdAndVocabularyIds(studentId, vocabularyIds);
        errorLearnLogs.forEach(log -> {
            Integer reviewCount = log.getReviewCount();
            if (reviewCount == null) {
                log.setReviewCount(1);
            } else {
                log.setReviewCount(reviewCount + 1);
            }
            errorLearnLogMapper.updateById(log);
        });
    }

    private List<Map<String, Object>> getWriteList(List<Map<String, Object>> getMaps) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        getMaps.forEach(map -> {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("subject", map.get("wordChinese"));
            returnMap.put("answer", map.get("word"));
            returnList.add(returnMap);
        });

        return returnList;
    }


    private List<Map<String, Object>> getOptionList(List<Map<String, Object>> getMaps, List<Long> vocabularyIds) {
        //获取干扰项
        List<String> strings = vocabularyMapper.selectChineseByNotVocabularyIds(vocabularyIds);
        List<Map<String, Object>> returnList = new ArrayList<>();
        getMaps.forEach(map -> {
            Collections.shuffle(strings);
            List<String> chineses = strings.subList(0, 3);
            String wordChinese = map.get("wordChinese").toString();
            chineses.add(wordChinese);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("subject", map.get("word").toString());
            List<Map<String, Boolean>> optionList = new ArrayList<>();
            for (String chinese : chineses) {
                Map<String, Boolean> booleanMap = new HashMap<>();
                if (wordChinese.equals(chinese)) {
                    booleanMap.put(chinese, true);
                } else {
                    booleanMap.put(chinese, false);
                }
                optionList.add(booleanMap);
            }
            returnMap.put("option", optionList);
            returnList.add(returnMap);
        });
        return returnList;
    }


}

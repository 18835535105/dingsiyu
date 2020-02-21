package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.dto.GetUnlimitedQRCodeDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import com.zhidejiaoyu.student.business.smallapp.util.CreateWxQrCodeUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public Object getTest(HttpSession session, String openId) {
        if (session.getAttribute(TimeConstant.BEGIN_START_TIME) == null) {
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        }
        Student student = studentMapper.selectByOpenId(openId);
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
       /* if (getMaps.size() < 15) {
            while (getMaps.size() < 15) {
                for (Map<String, Object> map : maps) {
                    if (getMaps.size() < 15) {
                        getMaps.add(map);
                    } else {
                        break;
                    }
                }
            }
        }else {
            getMaps = getMaps.subList(0, 15);
        }*/
        if (getMaps.size() < 2) {
            while (getMaps.size() < 2) {
                for (Map<String, Object> map : maps) {
                    if (getMaps.size() < 2) {
                        getMaps.add(map);
                    } else {
                        break;
                    }
                }
            }
        } else {
            getMaps = getMaps.subList(0, 2);
        }
        returnMap.put("optionList", getOptionList(getMaps, vocabularyIds));
        returnMap.put("writeList", getWriteList(getMaps));
        //更新获取单词复习数量
        updateErrorLearnLog(vocabularyIds, student.getId());
        return returnMap;
    }

    @Override
    public Object saveTest(Integer point, HttpSession session, String openId) {
        Student student = studentMapper.selectByOpenId(openId);
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
            returnMap.put("word", "sfdfssdfsfsdfsdddsdfsdfsdf");
        } else {
            returnMap.put("img", shareConfig.getImgUrl());
            returnMap.put("word", shareConfig.getImgWord());
        }
        String unlimited = CreateWxQrCodeUtil.getUnlimited(GetUnlimitedQRCodeDTO.builder()
                .scene("?code=" + openId)
                .build());
        returnMap.put("QRCode", unlimited);
        returnMap.put("gold", student.getSystemGold().intValue());
        returnMap.put("studentId", student.getId());
        returnMap.put("studentName", student.getNickname());
        returnMap.put("headPortrait", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        return returnMap;
    }

    @Override
    public ResponseEntity<byte[]> getQRCode(String openId) {
        String unlimited = CreateWxQrCodeUtil.getUnlimited(GetUnlimitedQRCodeDTO.builder()
                .scene("?code=" + openId)
                .build());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(unlimited.getBytes(), headers, HttpStatus.OK);
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

            String wordChinese = map.get("wordChinese").toString();
            List<String> chineses = getAnswer(strings, wordChinese);
            chineses.add(wordChinese);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("subject", map.get("word").toString());
            List<Map<String, Object>> optionList = new ArrayList<>();
            for (String chinese : chineses) {
                Map<String, Object> booleanMap = new HashMap<>();
                if (wordChinese.equals(chinese)) {
                    booleanMap.put("title", chinese);
                    booleanMap.put("result", true);
                } else {
                    booleanMap.put("title", chinese);
                    booleanMap.put("result", false);
                }
                optionList.add(booleanMap);
            }
            returnMap.put("option", optionList);
            returnList.add(returnMap);
        });
        return returnList;
    }

    private List<String> getAnswer(List<String> strings, String wordChinese) {
        List<String> strings1 = strings.subList(0, 3);
        List<String> stringAll = new ArrayList<>();
        stringAll.addAll(strings);
        List<String> removeList = new ArrayList<>();
        for (int i = 0; i < strings1.size(); i++) {
            if (strings1.get(i).equals(wordChinese)) {
                removeList.add(strings1.get(i));
            }
        }
        if (removeList.size() > 0) {
            stringAll.remove(strings1);
            strings1.removeAll(removeList);
            Collections.shuffle(stringAll);
            int size = strings1.size();
            int addSize = 4 - size;
            strings1.addAll(stringAll.subList(0, addSize));

        }
        return strings1;
    }


}

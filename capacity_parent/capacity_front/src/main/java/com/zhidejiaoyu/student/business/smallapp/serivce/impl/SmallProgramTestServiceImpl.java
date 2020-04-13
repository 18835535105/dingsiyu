package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.dto.GetLimitQRCodeDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import com.zhidejiaoyu.student.business.smallapp.util.CreateWxQrCodeUtil;
import com.zhidejiaoyu.student.common.SaveGoldLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Slf4j
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
    @Resource
    private BaiduSpeak baiduSpeak;

    @Resource
    private ClockInMapper clockInMapper;

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
                listMap.put("listenUtrl", baiduSpeak.getLanguagePath(vocabulary.getReadUrl()));
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
    @Transactional(rollbackFor = Exception.class)
    public Object saveTest(Integer point, HttpSession session, String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        Map<String, Object> returnMap = new HashMap<>();
        Date startDate = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        Date date = new Date();
        Long studentId = student.getId();
        if (point > 80) {
            TestRecord testRecord = new TestRecord();
            testRecord.setGenre(GenreConstant.SMALLAPP_GENRE);
            testRecord.setStudyModel(StudyModelConstant.SMALLAPP_STUDY_MODEL);
            testRecord.setStudentId(studentId);
            testRecord.setTestEndTime(date);
            testRecord.setTestStartTime(startDate);
            returnMap.put("point", point);
            testRecordMapper.insert(testRecord);
        } else {
            returnMap.put("point", 0);
        }

        int awardGold = 50;
        student.setSystemGold(student.getSystemGold() + awardGold);
        studentMapper.updateById(student);

        SaveGoldLog.saveStudyGoldLog(studentId, GenreConstant.SMALLAPP_GENRE, awardGold);

        this.saveClockIn(studentId);

        Integer adminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        ShareConfig shareConfig = shareConfigMapper.selectByAdminId(adminId);
        if (shareConfig == null) {
            returnMap.put("img", null);
            returnMap.put("word", "sfdfssdfsfsdfsdddsdfsdfsdf");
        } else {
            returnMap.put("img", GetOssFile.getPublicObjectUrl(shareConfig.getImgUrl()));
            returnMap.put("word", shareConfig.getImgWord());
        }
        returnMap.put("gold", student.getSystemGold().intValue());
        returnMap.put("studentId", studentId);
        returnMap.put("studentName", student.getNickname());
        returnMap.put("headPortrait", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        return returnMap;
    }

    /**
     * 保存打卡记录
     *
     * @param studentId
     */
    public void saveClockIn(Long studentId) {
        int count = clockInMapper.countTodayInfoByStudentId(studentId);

        if (count > 0) {
            return;
        }

        Integer maxCardDays = clockInMapper.selectLastCardDaysByStudentId(studentId);

        clockInMapper.insert(ClockIn.builder()
                .type(1)
                .studentId(studentId)
                .createTime(new Date())
                .cardTime(new Date())
                .cardDays(maxCardDays == null ? 1 : (maxCardDays + 1))
                .build());
    }

    @Override
    public Object getQRCode(String openId, String weChatName, String weChatImgUrl) {

        byte[] qrCode = CreateWxQrCodeUtil.createQRCode(GetLimitQRCodeDTO.builder()
                .path("openid=" + openId + "&weChatName=" + weChatName + "&weChatImgUrl=" + weChatImgUrl)
                .build());

        String fileName = System.currentTimeMillis() + ".png";
        String pathname = FileConstant.QR_CODE + fileName;
        File file = new File(pathname);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(qrCode);
            outputStream.flush();
        } catch (Exception e) {
            log.error("生成小程序码出错！", e);
            throw new ServiceException("生成小程序码出错！");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            boolean b = OssUpload.uploadWithInputStream(inputStream, FileConstant.QR_CODE_OSS, fileName);
            if (b) {
                log.info("小程序码生成成功！");
                file.delete();
                return ServerResponse.createBySuccess(GetOssFile.getPublicObjectUrl(FileConstant.QR_CODE_OSS + fileName));
            }
        } catch (Exception e) {
            log.error("小程序码上传OSS失败！", e);
            throw new ServiceException("小程序码上传OSS失败");
        }

        return ServerResponse.createByError(500, "生成小程序码失败！");
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
            returnMap.put("listenUtrl", map.get("listenUtrl").toString());
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
            returnMap.put("listenUtrl", map.get("listenUtrl").toString());
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
            Collections.shuffle(optionList);
            returnMap.put("option", optionList);
            returnList.add(returnMap);
        });

        return returnList;
    }

    private List<String> getAnswer(List<String> strings, String wordChinese) {
        List<String> stringAll = new ArrayList<>(strings);
        List<String> strings1 = stringAll.subList(0, 3);
        if (stringAll.size() > 0) {
            stringAll.remove(wordChinese);
        }
        /*List<String> removeList = new ArrayList<>();
        for (String str : strings1) {
            if (str.equals(wordChinese)) {
                removeList.add(str);
            }
        }
        if (removeList.size() > 0) {
            if(strings1.size()>0){
                stringAll.remove(strings1);
            }
            strings1.removeAll(removeList);
            Collections.shuffle(stringAll);
            int size = strings1.size();
            int addSize = 4 - size;
            strings1.addAll(stringAll.subList(0, addSize));
        }*/
        return strings1;
    }


}

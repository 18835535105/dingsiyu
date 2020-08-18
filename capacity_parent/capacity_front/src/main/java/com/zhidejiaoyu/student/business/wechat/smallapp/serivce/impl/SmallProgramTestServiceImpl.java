package com.zhidejiaoyu.student.business.wechat.smallapp.serivce.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.center.WeChatMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.pojo.center.WeChat;
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.goldUtil.GoldUtil;
import com.zhidejiaoyu.common.utils.goldUtil.StudentGoldAdditionUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.VocabularyFeignClient;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.smallapp.dto.GetLimitQRCodeDTO;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.SmallProgramTestService;
import com.zhidejiaoyu.student.business.wechat.smallapp.util.CreateWxQrCodeUtil;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
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
    @Resource
    private WeChatMapper weChatMapper;

    @Resource
    private WeekActivityRankOpt weekActivityRankOpt;

    private final CourseFeignClient courseFeignClient;
    @Resource
    private VocabularyFeignClient vocabularyFeignClient;

    public SmallProgramTestServiceImpl(CourseFeignClient courseFeignClient) {
        this.courseFeignClient = courseFeignClient;
    }

    @Override
    public Object getTest(HttpSession session, String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        if (session.getAttribute(TimeConstant.BEGIN_START_TIME) == null) {
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        }
        List<Long> voIds = errorLearnLogMapper.selectVocabularyIdByStudentId(student.getId());
        List<Map<String, Object>> maps = new ArrayList<>();
        if (voIds != null && voIds.size() > 0) {
            List<Vocabulary> vos = vocabularyFeignClient.getVocabularyMapByVocabularys(voIds);
            vos.forEach(vo -> {
                Map<String, Object> map = new HashMap<>();
                map.put("word", vo.getWord());
                map.put("wordChinese", vo.getWordChinese());
                map.put("wordId", vo.getId());
                maps.add(map);
            });
        }
        Map<String, Object> returnMap = new HashMap<>();
        if (maps.size() == 0) {
            //获取优先级最大的单元
            StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(student.getId());
            //获取当前单元的单词
            List<Vocabulary> vocabularies = vocabularyFeignClient.getVocabularyByUnitId(studentStudyPlanNew.getUnitId());
            vocabularies.forEach(vocabulary -> {
                Map<String, Object> listMap = new HashMap<>();
                listMap.put("wordId", vocabulary.getId());
                listMap.put("word", vocabulary.getWord());
                listMap.put("wordList", getWordList(vocabulary.getWord()));
                listMap.put("wordChinese", vocabulary.getWordChinese());
                listMap.put("listenUtrl", baiduSpeak.getLanguagePath(vocabulary.getWord()));
                maps.add(listMap);
            });
        } else {
            maps.forEach(map -> {
                map.put("listenUtrl", baiduSpeak.getLanguagePath(map.get("word").toString()));
                map.put("wordList", getWordList(map.get("word").toString()));
            });
        }
        returnMap.put("gold", student.getSystemGold().intValue());
        //获取单词id
        List<Long> vocabularyIds = new ArrayList<>();
        maps.forEach(map -> vocabularyIds.add(Long.parseLong(map.get("wordId").toString())));
        List<Map<String, Object>> getMaps = new ArrayList<>(maps);
        if (getMaps.size() > 15) {
            getMaps = getMaps.subList(0, 15);
        }

        returnMap.put("optionList", getOptionList(getMaps, vocabularyIds));
        returnMap.put("writeList", getWriteList(getMaps));
        //更新获取单词复习数量
        updateErrorLearnLog(vocabularyIds, student.getId());
        return returnMap;
    }

    private List<String> getWordList(String word) {
        List<String> wordList = new ArrayList<>();
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            wordList.add(chars[i] + "");
        }
        Collections.shuffle(wordList);
        return wordList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @GoldChangeAnnotation
    public Object saveTest(Integer point, HttpSession session, String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        Map<String, Object> returnMap = new HashMap<>();
        Date startDate = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        Date date = new Date();
        Long studentId = student.getId();
        if (point != 0) {
            TestRecord testRecord = new TestRecord();
            testRecord.setGenre(GenreConstant.SMALLAPP_GENRE);
            testRecord.setStudyModel(StudyModelConstant.SMALLAPP_STUDY_MODEL);
            testRecord.setStudentId(studentId);
            testRecord.setTestEndTime(date);
            testRecord.setPoint(point);
            testRecord.setTestStartTime(startDate);
            returnMap.put("point", point);
            testRecordMapper.insert(testRecord);
        }
        int count = clockInMapper.countTodayInfoByStudentId(studentId);
        if (count == 0) {
            this.saveClockIn(studentId);
            this.awardGold(student);
        }

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
        StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(studentId);
        CourseNew course = courseFeignClient.getById(studentStudyPlanNew.getCourseId());
        returnMap.put("courseName", course.getCourseName());
        // 打卡天数（非连续打卡天数）
        int cardDays = clockInMapper.countByStudentId(student.getId());
        returnMap.put("cardDays", cardDays);

        // 更新每周活动连续打卡奖励进度
        weekActivityRankOpt.updateWeekActivitySchoolRank(student);

        return returnMap;
    }

    /**
     * 奖励金币
     *
     * @param student
     */
    public void awardGold(Student student) {

        Long studentId = student.getId();

        Integer cardDays1 = clockInMapper.selectLaseCardDays(studentId);

        int awardGold = 50;
        if (cardDays1 != null && cardDays1 >= 3) {
            // 连续打卡大于等于3天，额外奖励20金币
            awardGold += 20;
        }
        // 金币加成
        Double goldAddition = StudentGoldAdditionUtil.getGoldAddition(student, awardGold);
        int canAddGold = GoldUtil.addStudentGold(student, goldAddition);

        GoldLogUtil.saveStudyGoldLog(studentId, GenreConstant.SMALLAPP_GENRE, canAddGold);
    }

    /**
     * 保存打卡记录
     *
     * @param studentId
     */
    public void saveClockIn(Long studentId) {

        Date yesterday = DateUtil.getBeforeDaysDate(new Date(), 1);
        Integer cardDays = clockInMapper.selectCardDaysByStudentIdAndCardTime(studentId, yesterday);

        clockInMapper.insert(ClockIn.builder()
                .type(1)
                .studentId(studentId)
                .createTime(new Date())
                .cardTime(new Date())
                .cardDays(cardDays == null ? 1 : (cardDays + 1))
                .build());
    }

    @Override
    public Object getQRCode(String openId, String weChatName, String weChatImgUrl) {
        // .path("pages/support2/support?openid=" + openId + "&weChatName=" + weChatName + "&weChatImgUrl=" + weChatImgUrl)
        WeChat weChat = weChatMapper.selectByOpenId(openId);
        if (weChat == null) {
            weChat = new WeChat();
            weChat.setOpenId(openId);
            weChat.setWeChatImgUrl(weChatImgUrl);
            weChat.setWeChatName(weChatName);
            weChatMapper.insert(weChat);
        } else {
            weChat.setWeChatName(weChatName);
            weChat.setWeChatImgUrl(weChatImgUrl);
            weChatMapper.updateById(weChat);
        }

        byte[] qrCode = CreateWxQrCodeUtil.createQRCode(GetLimitQRCodeDTO.builder()
                .path("pages/support2/support?scene=" + URLEncoder.encode("openid=" + openId))
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
            returnMap.put("wordList", getWordList(map.get("word").toString()));
            returnMap.put("listenUtrl", map.get("listenUtrl"));
            returnList.add(returnMap);
        });

        return returnList;
    }


    private List<Map<String, Object>> getOptionList(List<Map<String, Object>> getMaps, List<Long> vocabularyIds) {
        //获取干扰项
        List<String> strings = vocabularyFeignClient.selectChineseByNotVocabularyIds(vocabularyIds);
        List<Map<String, Object>> returnList = new ArrayList<>();
        getMaps.forEach(map -> {
            Collections.shuffle(strings);
            String wordChinese = map.get("wordChinese").toString();
            List<String> chineses = getAnswer(strings, wordChinese);
            chineses.add(wordChinese);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("subject", map.get("word").toString());
            returnMap.put("listenUtrl", map.get("listenUtrl"));
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

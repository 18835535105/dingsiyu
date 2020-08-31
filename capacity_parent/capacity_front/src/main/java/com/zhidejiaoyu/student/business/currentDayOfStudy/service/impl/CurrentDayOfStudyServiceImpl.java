package com.zhidejiaoyu.student.business.currentDayOfStudy.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.TeksNew;
import com.zhidejiaoyu.common.utils.ArrayUtil;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.currentdayofstudy.CurrentDayOfStudyVo;
import com.zhidejiaoyu.common.vo.currentdayofstudy.StudyTimeAndMileageVO;
import com.zhidejiaoyu.common.vo.currentdayofstudy.TodayCurrentDayOfStudyVo;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
import com.zhidejiaoyu.student.business.feignclient.course.CenterTeksFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.SentenceFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.VocabularyFeignClient;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CurrentDayOfStudyServiceImpl extends BaseServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements CurrentDayOfStudyService {

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Resource
    private VocabularyFeignClient vocabularyFeignClient;

    @Resource
    private SentenceFeignClient sentenceFeignClient;

    @Resource
    private CenterTeksFeignClient centerTeksFeignClient;

    @Override
    public ServerResponse<Object> getCurrentDayOfStudy() {
        return this.getTodayStudy(super.getStudentId());
    }


    public ServerResponse<Object> getTodayStudy(Long studentId) {

        TodayCurrentDayOfStudyVo vo = new TodayCurrentDayOfStudyVo();
        vo.setTime(DateUtil.formatYYYYMMDD(new Date()) + "日 飞行记录");
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS(new Date());
        Integer gold = goldLogMapper.selectGoldByStudentIdAndDate(studentId, DateUtil.parseYYYYMMDDHHMMSS(dateStr), 1);
        if (gold != null && gold > 0) {
            vo.setGold(gold);
        } else {
            vo.setGold(0);
        }
        Long validTime = durationMapper.selectValidTimeByStudentIdAndDate(studentId, dateStr);
        if (validTime != null && validTime > 0) {
            vo.setValidTime(validTime.intValue() / 60);
        } else {
            vo.setValidTime(0);
        }
        Long onlineTime = durationMapper.selectByStudentIdAndDate(studentId, dateStr);
        if (onlineTime != null && onlineTime > 0) {
            vo.setOnlineTime(onlineTime.intValue() / 60);
        } else {
            vo.setOnlineTime(0);
        }
        String errorTest = currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.ERROR_TEST, studentId);
        vo.setTest(getTestList(errorTest));
        String errorStudyModel = currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.STUDY_MODEL, studentId);
        vo.setStudyModel(getReturnList(errorStudyModel));
        vo.setWord(currentDayOfStudyRedisOpt.getWordSentenceTest(RedisKeysConst.ERROR_WORD, studentId, 1));
        vo.setSentence(currentDayOfStudyRedisOpt.getWordSentenceTest(RedisKeysConst.ERROR_SENTENCE, studentId, 2));
        String errorSyntax = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SYNTAX, studentId, 3);
        vo.setSyntax(getReturnList(errorSyntax));
        vo.setText(new ArrayList<>());
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    public ServerResponse<Object> getCurrentDayOfStudy(Long studentId) {

        CurrentDayOfStudyVo vo = new CurrentDayOfStudyVo();
        vo.setTime(DateUtil.formatYYYYMMDD(new Date()) + "日 飞行记录");
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS(new Date());
        Integer gold = goldLogMapper.selectGoldByStudentIdAndDate(studentId, DateUtil.parseYYYYMMDDHHMMSS(dateStr), 1);
        if (gold != null && gold > 0) {
            vo.setGold(gold);
        } else {
            vo.setGold(0);
        }
        Long validTime = durationMapper.selectValidTimeByStudentIdAndDate(studentId, dateStr);
        if (validTime != null && validTime > 0) {
            vo.setValidTime(validTime.intValue() / 60);
        } else {
            vo.setValidTime(0);
        }
        Long onlineTime = durationMapper.selectByStudentIdAndDate(studentId, dateStr);
        if (onlineTime != null && onlineTime > 0) {
            vo.setOnlineTime(onlineTime.intValue() / 60);
        } else {
            vo.setOnlineTime(0);
        }
        String errorTest = currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.ERROR_TEST, studentId);
        vo.setTest(getReturnList(errorTest));
        String errorStudyModel = currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.STUDY_MODEL, studentId);
        vo.setStudyModel(getReturnList(errorStudyModel));
        String word = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_WORD, studentId, 1);
        vo.setWord(getReturnList(word));
        String sentence = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SENTENCE, studentId, 2);
        vo.setSentence(getReturnList(sentence));
        String errorSyntax = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SYNTAX, studentId, 3);
        vo.setSyntax(getErrorSyntaxList(errorSyntax));
        vo.setText(new ArrayList<>());

        CurrentDayOfStudy currentDayOfStudy = currentDayOfStudyMapper.selectByStudentIdAndCreateTime(studentId, DateUtil.formatDate(new Date(), DateUtil.YYYYMMDD));
        if (currentDayOfStudy != null) {
            vo.setEvaluate(currentDayOfStudy.getEvaluate());
            vo.setComment(currentDayOfStudy.getComment());
            vo.setShow(currentDayOfStudy.getShow());
            vo.setSiteNo(currentDayOfStudy.getSiteNo());
            vo.setImgUrl(GetOssFile.getPublicObjectUrl(currentDayOfStudy.getImgUrl()));
        }

        return ServerResponse.createBySuccess(vo);
    }

    private List<Object> getErrorSyntaxList(String errorSyntax) {
        if (StringUtil.isEmpty(errorSyntax)) {
            return new ArrayList<>();
        }
        String[] split = errorSyntax.split("##");
        return ArrayUtil.removeSyntaxConvertToList(split);
    }

    @Override
    public StudyTimeAndMileageVO getTodayInfo() {
        Long studentId = super.getStudentId();
        Long onlineTime = durationMapper.countTodayOnlineTimeByStudentId(studentId);

        int groupCount = learnHistoryMapper.countByStudentIdToDay(studentId);
        int testCount = testRecordMapper.countGoldTestByStudentIdToday(studentId);

        int mileage = groupCount + testCount * 3;

        StudyTimeAndMileageVO studyTimeAndMileageVO = new StudyTimeAndMileageVO();
        studyTimeAndMileageVO.setMileage(mileage);
        studyTimeAndMileageVO.setTime(onlineTime);
        return studyTimeAndMileageVO;
    }

    @Override
    public ServerResponse<Object> getCurrentDayOfStudyWithDate(Long studentId, String date) {
        CurrentDayOfStudy currentDayOfStudy = currentDayOfStudyMapper.selectByStudentIdAndCreateTime(studentId, date);

        if (currentDayOfStudy == null) {
            return ServerResponse.createByError(400, "未查询到指定日期的飞行记录！");
        }

        return ServerResponse.createBySuccess(CurrentDayOfStudyVo.builder()
                .studyModel(currentDayOfStudy.getStudyModel() == null ? Collections.emptyList() : ArrayUtil.removeBlankStringConvertToList(currentDayOfStudy.getStudyModel().split("##")))
                .time(currentDayOfStudy.getCreateTime() == null ? "" : DateUtil.formatYYYYMMDD(currentDayOfStudy.getCreateTime()))
                .sentence(currentDayOfStudy.getSentence() == null ? Collections.emptyList() : ArrayUtil.removeBlankStringConvertToList((currentDayOfStudy.getSentence().split("##"))))
                .syntax(currentDayOfStudy.getSyntax() == null ? Collections.emptyList() : ArrayUtil.removeSyntaxConvertToList((currentDayOfStudy.getSyntax().split("##"))))
                .test(currentDayOfStudy.getTest() == null ? Collections.emptyList() : ArrayUtil.removeBlankStringConvertToList((currentDayOfStudy.getTest().split("##"))))
                .text(currentDayOfStudy.getText() == null ? Collections.emptyList() : ArrayUtil.removeBlankStringConvertToList((currentDayOfStudy.getText().split("##"))))
                .word(currentDayOfStudy.getWord() == null ? Collections.emptyList() : ArrayUtil.removeBlankStringConvertToList((currentDayOfStudy.getWord().split("##"))))
                .gold(currentDayOfStudy.getGold())
                .onlineTime(currentDayOfStudy.getOnlineTime())
                .validTime(currentDayOfStudy.getValidTime())
                .comment(currentDayOfStudy.getComment())
                .evaluate(currentDayOfStudy.getEvaluate())
                .imgUrl(GetOssFile.getPublicObjectUrl(currentDayOfStudy.getImgUrl()))
                .show(currentDayOfStudy.getShow())
                .siteNo(currentDayOfStudy.getSiteNo())
                .build());
    }

    @Override
    public Boolean saveOrUpdate1(CurrentDayOfStudy currentDayOfStudy) {
        CurrentDayOfStudy currentDayOfStudy1 = currentDayOfStudyMapper.selectByStudentIdAndQrCodeNum(currentDayOfStudy.getStudentId(), currentDayOfStudy.getQrCodeNum());
        if (currentDayOfStudy1 == null) {
            return this.save(currentDayOfStudy);
        }
        currentDayOfStudy.setId(currentDayOfStudy1.getId());
        return this.updateById(currentDayOfStudy);
    }

    @Override
    public List<Map<String, String>> getTestList(String errorInfo) {
        List<Map<String, String>> returnList = new ArrayList<>();
        if (StringUtil.isEmpty(errorInfo)) {
            return returnList;
        }
        String[] split = errorInfo.split("##");
        if (split.length > 0) {
            List<String> strings = Arrays.asList(split);
            strings.forEach(str -> {
                Map<String, String> map = new HashMap<>();
                String voc = vocabularyFeignClient.getVocabularyChinsesByWordId(str);
                if (voc != null) {
                    map.put("english", str);
                    map.put("chinese", voc);
                } else {
                    TeksNew teks = centerTeksFeignClient.replaceTeks(str);
                    if (teks != null) {
                        map.put("english", teks.getSentence().replace("#", " ").replace("$", ""));
                        map.put("chinese", teks.getParaphrase().replace("*", ""));
                    } else {
                        Sentence sentence = sentenceFeignClient.getReplaceSentece(str);
                        if (sentence != null) {
                            map.put("english", sentence.getCentreExample().replace("#", " ").replace("$", ""));
                            map.put("chinese", sentence.getCentreTranslate().replace("*", ""));
                        } else {
                            map.put("english", str);
                            map.put("chinese", null);
                        }
                    }
                }
                returnList.add(map);
            });
        }
        return returnList;
    }

    @Override
    public List<String> getReturnList(String errorInfo) {
        if (StringUtil.isEmpty(errorInfo)) {
            return new ArrayList<>();
        }
        String[] split = errorInfo.split("##");
        if (split.length > 0) {
            List<String> strings = Arrays.asList(split);
            Set<String> set = new HashSet<>(strings);
            return new ArrayList<>(set);
        }
        return new ArrayList<>();
    }
}

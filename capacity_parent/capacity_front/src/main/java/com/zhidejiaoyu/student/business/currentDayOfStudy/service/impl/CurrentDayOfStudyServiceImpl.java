package com.zhidejiaoyu.student.business.currentDayOfStudy.service.impl;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.CurrentDayOfStudyVo;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class CurrentDayOfStudyServiceImpl extends BaseServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements CurrentDayOfStudyService {

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;

    @Override
    public Object getCurrentDayOfStudy(HttpSession session) {
        CurrentDayOfStudyVo vo = new CurrentDayOfStudyVo();
        Student student = getStudent(session);
        String dateStr = DateUtil.formatYYYYMMDDHHMMSS(new Date());
        vo.setTime(DateUtil.formatYYYYMMDD(new Date()) + "日 飞行记录");
        Integer gold = goldLogMapper.selectGoldByStudentIdAndDate(student.getId(), DateUtil.parseYYYYMMDDHHMMSS(dateStr), 1);
        if (gold != null && gold > 0) {
            vo.setGold(gold);
        } else {
            vo.setGold(0);
        }
        Long vaildTime = durationMapper.selectValidTimeByStudentIdAndDate(student.getId(), dateStr);
        if (vaildTime != null && vaildTime > 0) {
            vo.setValidTime(vaildTime.intValue());
        } else {
            vo.setValidTime(0);
        }
        Long onlineTime = durationMapper.selectByStudentIdAndDate(student.getId(), dateStr);
        if (onlineTime != null && onlineTime > 0) {
            vo.setOnlineTime(onlineTime.intValue());
        } else {
            vo.setOnlineTime(0);
        }
        String errorTest = currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.ERROR_TEST, student.getId());
        vo.setTest(getReturnList(errorTest));
        String errorStudyModel = currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.STUDY_MODEL, student.getId());
        vo.setStudyModel(getReturnList(errorStudyModel));
        String errorWord = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_WORD, student.getId(), 1);
        vo.setWord(getReturnList(errorWord));
        String errorSenten = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SENTENCE, student.getId(), 2);
        vo.setSentence(getReturnList(errorSenten));
        String errorSyntax = currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SYNTAX, student.getId(), 3);
        vo.setSyntax(getReturnList(errorSyntax));
        return ServerResponse.createBySuccess(vo);
    }

    private List<String> getReturnList(String errorTest) {
        String[] split = errorTest.split("##");
        if (split != null && split.length > 0) {
            return Arrays.asList(split);
        }
        return null;
    }
}

package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QueryCurrentDayOfStudyService;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author: liumaoyu
 * @date: 2020/6/3 14:17:03
 */
@Slf4j
@Service
public class QueryCurrentDayOfStudyServiceImpl implements BaseQuartzService, QueryCurrentDayOfStudyService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;

    @Override
    public void saveCurrentDayOfStudy() {
        if (checkPort(port)) {
            return;
        }
        //1查询保存昨日智慧飞信信息记录
        String dateStr = DateUtil.beforeHoursTime(4);
        List<CurrentDayOfStudy> currentDayOfStudies = currentDayOfStudyMapper.selectByDate(dateStr);
        currentDayOfStudies.forEach(study -> {
            Integer gold = goldLogMapper.selectGoldByStudentIdAndDate(study.getStudentId(), DateUtil.parseYYYYMMDDHHMMSS(dateStr), 1);
            study.setGold(gold);
            Long vaildTime = durationMapper.selectValidTimeByStudentIdAndDate(study.getStudentId(), dateStr);
            study.setValidTime(vaildTime.intValue());
            Long onlineTime = durationMapper.selectByStudentIdAndDate(study.getStudentId(), dateStr);
            study.setOnlineTime(onlineTime.intValue());
            study.setTest(currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.ERROR_TEST, study.getStudentId()));
            study.setStudyModel(currentDayOfStudyRedisOpt.getStudyModelAndTestStudyCurrent(RedisKeysConst.STUDY_MODEL, study.getStudentId()));
            study.setWord(currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_WORD, study.getStudentId(), 1));
            study.setSentence(currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SENTENCE, study.getStudentId(), 2));
            study.setSyntax(currentDayOfStudyRedisOpt.getTestStudyCurrent(RedisKeysConst.ERROR_SYNTAX, study.getStudentId(), 3));
            currentDayOfStudyRedisOpt.deleteStudy(study.getStudentId());
            currentDayOfStudyMapper.updateById(study);
        });

    }


}

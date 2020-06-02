package com.zhidejiaoyu.student.business.wechat.smallapp.serivce.impl;

import com.alibaba.excel.util.StringUtils;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.FlyOfStudyService;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.fly.TotalStudyInfoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/2 15:23:23
 */
@Service
public class FlyOfStudyServiceImpl extends BaseServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements FlyOfStudyService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private RunLogMapper runLogMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Override
    public ServerResponse<Object> getTotalStudyInfo(String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        if (student == null) {
            return ServerResponse.createByError(400, "您还未绑定队长账号，不能使用该功能！");
        }

        Long studentId = student.getId();
        Long totalValidTime = durationMapper.countTotalValidTime(studentId);
        Long totalOnlineTime = durationMapper.countTotalOnlineTime(student);
        Date firstLoginTime = runLogMapper.selectFirstLoginTimeByStudentId(studentId);

        int wordCount = learnNewMapper.countLearnedWordCount(studentId);
        int sentenceCount = learnNewMapper.countLearnedSentenceCount(studentId);
        int syntaxCount = learnNewMapper.countLearnedSyntaxCount(studentId);
        int textCount = learnNewMapper.countLearnedTextCount(studentId);
        int testCount = testRecordMapper.countTotalSubjects(studentId);

        return ServerResponse.createBySuccess(TotalStudyInfoVO.builder()
                .studentName(StringUtils.isEmpty(student.getStudentName()) ? "默认姓名" : student.getStudentName())
                .totalGold((int) BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold()))
                .firstLoginTime(firstLoginTime == null ? "无" : DateUtil.formatDate(firstLoginTime, DateUtil.YYYYMMDDHHMMSS))
                .totalOnlineTime(totalOnlineTime == null ? 0 : totalOnlineTime)
                .totalValidTime(totalValidTime == null ? 0 : totalValidTime)
                .wordCount(wordCount)
                .sentenceCount(sentenceCount)
                .syntaxCount(syntaxCount)
                .textCount(textCount)
                .testCount(testCount)
                .build());
    }

    @Override
    public ServerResponse<Object> getStudyInfo(String openId, Integer num) {

        return null;
    }
}

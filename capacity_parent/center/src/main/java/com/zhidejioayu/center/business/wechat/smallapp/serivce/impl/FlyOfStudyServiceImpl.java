package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.FlyOfStudyService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.fly.TotalStudyInfoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/2 15:23:23
 */
@Service
public class FlyOfStudyServiceImpl extends ServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements FlyOfStudyService {

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

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

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
        Student student = studentMapper.selectByOpenId(openId);
        if (student == null) {
            return ServerResponse.createByError(ResponseCode.NO_BIND);
        }

        CurrentDayOfStudy currentDayOfStudy = currentDayOfStudyMapper.selectByStudentIdAndQrCodeNum(student.getId(), num);

        if (currentDayOfStudy == null) {
            return ServerResponse.createByError(400, "未查询到当前二维码的学习信息！");
        }

        return ServerResponse.createBySuccess(StudyInfoVO.builder()
                .contents(currentDayOfStudy.getStudyModel() == null ? new String[0] : currentDayOfStudy.getStudyModel().split("##"))
                .date(currentDayOfStudy.getCreateTime() == null ? "" : DateUtil.formatYYYYMMDD(currentDayOfStudy.getCreateTime()))
                .errorSentence(currentDayOfStudy.getSentence() == null ? new String[0] : currentDayOfStudy.getSentence().split("##"))
                .errorSyntax(currentDayOfStudy.getSyntax() == null ? new String[0] : currentDayOfStudy.getSyntax().split("##"))
                .errorTest(currentDayOfStudy.getTest() == null ? new String[0] : currentDayOfStudy.getTest().split("##"))
                .errorText(currentDayOfStudy.getText() == null ? new String[0] : currentDayOfStudy.getText().split("##"))
                .errorWord(currentDayOfStudy.getWord() == null ? new String[0] : currentDayOfStudy.getWord().split("##"))
                .totalGold(currentDayOfStudy.getGold())
                .totalOnlineTime(currentDayOfStudy.getOnlineTime())
                .totalValidTime(currentDayOfStudy.getValidTime())
                .imgUrl(GetOssFile.getPublicObjectUrl(currentDayOfStudy.getImgUrl()))
                .build());
    }

    @Override
    public ServerResponse<Object> getStudentInfo(String openId, Integer num) {
        Student student = studentMapper.selectByOpenId(openId);
        if (student == null) {
            return ServerResponse.createByError(ResponseCode.NO_BIND);
        }
        CurrentDayOfStudy currentDayOfStudy = currentDayOfStudyMapper.selectByStudentIdAndQrCodeNum(student.getId(), num);
        if (currentDayOfStudy != null) {
            Map<String, String> urlMap = new HashMap<>(16);
            urlMap.put("url", GetOssFile.getPublicObjectUrl(currentDayOfStudy.getImgUrl()));
            return ServerResponse.createBySuccess(urlMap);
        }

        return ServerResponse.createByError(301, "未查询到图片！");
    }
}

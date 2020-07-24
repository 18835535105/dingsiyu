package com.zhidejiaoyu.student.business.wechat.smallapp.serivce.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.currentdayofstudy.CurrentDayOfStudyVo;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.FlyOfStudyService;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.fly.StudyInfoVO;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.fly.TotalStudyInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Resource
    private CurrentDayOfStudyService currentDayOfStudyService;

    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private RunLogMapper runLogMapper;

    @Override
    public ServerResponse<Object> getTotalStudyInfo(Student student) {
        if (student == null) {
            return ServerResponse.createByError(400, "您还未绑定队长账号，不能使用该功能！");
        }

        Long studentId = student.getId();
        Long totalValidTime = durationMapper.countTotalValidTime(studentId);
        Long totalOnlineTime = durationMapper.countTotalOnlineTime(student);

        int wordCount = learnNewMapper.countLearnedWordCount(studentId);
        int sentenceCount = learnNewMapper.countLearnedSentenceCount(studentId);
        int syntaxCount = learnNewMapper.countLearnedSyntaxCount(studentId);
        int textCount = learnNewMapper.countLearnedTextCount(studentId);
        int testCount = testRecordMapper.countTotalSubjects(studentId);

        Date firstPay = payLogMapper.selectFirstPayTimeByStudentId(student.getId());
        Integer loginCount = runLogMapper.countLoginCountByStudentId(studentId);

        return ServerResponse.createBySuccess(TotalStudyInfoVO.builder()
                .studentName(StringUtils.isEmpty(student.getStudentName()) ? "默认姓名" : student.getStudentName())
                .systemGold((int) Math.floor(student.getSystemGold()))
                .firstLoginTime(firstPay == null ? "无" : DateUtil.formatYYYYMMDD(firstPay))
                .totalOnlineTime(totalOnlineTime == null ? 0 : totalOnlineTime)
                .totalValidTime(totalValidTime == null ? 0 : totalValidTime)
                .wordCount(wordCount)
                .sentenceCount(sentenceCount)
                .syntaxCount(syntaxCount)
                .textCount(textCount)
                .testCount(testCount)
                .loginCount(loginCount)
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

        String date = DateUtil.formatYYYYMMDD(currentDayOfStudy.getCreateTime());
        if (Objects.equals(date, DateUtil.formatYYYYMMDD(new Date()))) {
            // 查询当天的学习记录
            ServerResponse<Object> response = currentDayOfStudyService.getCurrentDayOfStudy(student.getId());
            if (response.getStatus() != ResponseCode.SUCCESS.getCode()) {
                return response;
            }
            CurrentDayOfStudyVo vo = (CurrentDayOfStudyVo) response.getData();
            return ServerResponse.createBySuccess(StudyInfoVO.builder()
                    .contents(currentDayOfStudyService.getReturnList(this.joinStr(vo.getStudyModel())))
                    .date(currentDayOfStudy.getCreateTime() == null ? "" : date)
                    .errorSentence(currentDayOfStudyService.getTestList(this.joinStr(vo.getSentence())))
                    .errorSyntax(currentDayOfStudyService.getReturnList(this.joinStr(vo.getSyntax())))
                    .errorTest(currentDayOfStudyService.getTestList(this.joinStr(vo.getTest())))
                    .errorText(currentDayOfStudyService.getReturnList(this.joinStr(vo.getText())))
                    .errorWord(currentDayOfStudyService.getTestList(this.joinStr(vo.getWord())))
                    .comment(currentDayOfStudy.getComment())
                    .evaluate(currentDayOfStudy.getEvaluate())
                    .siteNo(currentDayOfStudy.getSiteNo())
                    .show(currentDayOfStudy.getShow())
                    .studentName(student.getStudentName())
                    .build());
        }

        // 非当天，指定日期的学习记录
        return ServerResponse.createBySuccess(StudyInfoVO.builder()
                .contents(currentDayOfStudyService.getReturnList(currentDayOfStudy.getStudyModel()))
                .date(currentDayOfStudy.getCreateTime() == null ? "" : date)
                .errorSentence(currentDayOfStudyService.getTestList(currentDayOfStudy.getSentence()))
                .errorSyntax(currentDayOfStudyService.getReturnList(currentDayOfStudy.getSyntax()))
                .errorTest(currentDayOfStudyService.getTestList(currentDayOfStudy.getTest()))
                .errorText(currentDayOfStudyService.getReturnList(currentDayOfStudy.getText()))
                .errorWord(currentDayOfStudyService.getTestList(currentDayOfStudy.getWord()))
                .comment(currentDayOfStudy.getComment())
                .evaluate(currentDayOfStudy.getEvaluate())
                .siteNo(currentDayOfStudy.getSiteNo())
                .show(currentDayOfStudy.getShow())
                .studentName(student.getStudentName())
                .build());
    }

    private String joinStr(List<String> list) {
        return list == null ? null : String.join("##", list);
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

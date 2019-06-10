package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.mapper.simple.MessageBoardMapper;
import com.zhidejiaoyu.common.pojo.MessageBoard;
import com.zhidejiaoyu.common.pojo.MessageBoardExample;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.simple.http.FtpUtil;
import com.zhidejiaoyu.common.utils.simple.server.ResponseCode;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.FeedBackService;
import com.zhidejiaoyu.student.utils.sensitiveword.SensitiveWordFilter;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoList;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2018/8/13
 */
@Service
@Slf4j
public class SimpleFeedBackServiceImplSimple extends SimpleBaseServiceImpl<MessageBoardMapper, MessageBoard> implements FeedBackService {

    @Value("${ftp.prefix}")
    private String ftpPrefix;

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Autowired
    private FtpUtil ftpUtil;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Override
    public ServerResponse<FeedBackInfoVO> getFeedBacks(HttpSession session) {
        Student student = getStudent(session);

        // 获取当前学生所有的反馈及被回复信息
        List<MessageBoard> messageBoards = getMessageBoards(student.getId());
        if (messageBoards.size() > 0) {
            return ServerResponse.createBySuccess(packageFeedBackInfoVO(messageBoards, student.getHeadUrl()));
        }
        return ServerResponse.createBySuccess(new FeedBackInfoVO());
    }

    @Override
    public ServerResponse cancelHint(HttpSession session) {
        Student student = getStudent(session);
        messageBoardMapper.updateHintFlag(student.getId(), 2);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse saveFeedBack(HttpSession session, String content) {

        Student student = getStudent(session);

        // 保存反馈内容
        try {
            saveFeedBackContent(content, student);
        } catch (Exception e) {
            log.error("保存学生 {}->{} 意见反馈失败！content=[{}]", student.getId(), student.getStudentName(), content, e);
            return ServerResponse.createByErrorMessage("提交失败！");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse checkFeedBack(HttpSession session, String content, MultipartFile[] files) {
        Student student = getStudent(session);
        // 如果当前学生在禁言期，无法发起反馈
        List<MessageBoard> messageBoards = messageBoardMapper.selectStopSpeakTime(student.getId());
        if (messageBoards.size() > 0) {
            return ServerResponse.createByErrorMessage("您已被禁言至 " + DateUtil.formatYYYYMMDDHHMMSS(messageBoards.get(0).getStopSpeakEndTime())
                    + " ，在此期间无法反馈！");
        }

        // 验证是否含有敏感词
        sensitiveWordFilter.init();
        boolean containsSensitiveWord = sensitiveWordFilter.isContainsSensitiveWord(content, SensitiveWordFilter.maxMatchType);
        if (containsSensitiveWord) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.SENSITIVE_WORD.getCode(), ResponseCode.SENSITIVE_WORD.getMsg());
        }

        if (files != null && files.length > 0) {
            // 校验图片大小
            boolean flag = checkImgSize(files);
            if (!flag) {
                return ServerResponse.createByErrorMessage("图片存储占用超过15MB！");
            }
            List<String> fileNames = ftpUtil.uploadFile(FileConstant.FEEDBACK_IMG, files);
            if (fileNames.size() > 0) {
                // $$img$$ 为域名占位符，防止更换域名后图片不显示
                List<String> returnFileNames = fileNames.stream().map(fileName -> "$$img$$" + FileConstant.FEEDBACK_IMG + fileName).collect(Collectors.toList());
                return ServerResponse.createBySuccess(returnFileNames);
            }
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> cancelRedPoint(HttpSession httpSession) {
        Long studentId = getStudentId(httpSession);
        messageBoardMapper.updateReadFlag(studentId, 4);
        return ServerResponse.createBySuccess();
    }

    private void saveFeedBackContent(String content, Student student) {
        MessageBoard messageBoard = new MessageBoard();
        messageBoard.setRole(0);
        messageBoard.setHintFlag(2);
        messageBoard.setReadFlag(2);
        messageBoard.setTime(new Date());
        messageBoard.setSchoolName(student.getSchoolName());
        messageBoard.setStudentAccount(student.getAccount());
        messageBoard.setStudentName(student.getStudentName());
        messageBoard.setAcceptFlag(2);
        messageBoard.setAwardGold(0);
        messageBoard.setContent(content);
        messageBoard.setStudentId(student.getId());

        messageBoardMapper.insert(messageBoard);
    }

    /**
     * 校验图片大小不能超过15MB
     *
     * @param files
     * @return <code>true</code>大小符合，可以上传 <code>false</code>图片过大
     */
    private boolean checkImgSize(MultipartFile[] files) {
        long size = 0;
        for (MultipartFile file : files) {
            size += file.getSize();
        }
        return size <= 15728640;
    }


    private FeedBackInfoVO packageFeedBackInfoVO(List<MessageBoard> messageBoards, String headUrl) {
        FeedBackInfoVO feedBackInfoVO = new FeedBackInfoVO();
        List<FeedBackInfoList> feedBackInfoLists = new ArrayList<>(messageBoards.size());
        FeedBackInfoList feedBackInfoList;
        MessageBoard messageBoard;
        int size = messageBoards.size();
        for (int i = 0; i < size; i++) {
            messageBoard = messageBoards.get(i);
            feedBackInfoList = new FeedBackInfoList();
            feedBackInfoList.setContent(messageBoard.getContent());
            feedBackInfoList.setHeadUrl(headUrl);
            feedBackInfoList.setRole(messageBoard.getRole());
            feedBackInfoList.setTime(DateUtil.formatYYYYMMDDHHMMSS(messageBoard.getTime()));

            // 如果最后一条记录奖励金币数为0，说明当前学生不需要显示奖励金币提示
            if (i == size - 1) {

                if (messageBoard.getHintFlag() != null && messageBoard.getHintFlag() == 1){
                    feedBackInfoVO.setHint(true);
                    feedBackInfoVO.setAwardGold(messageBoard.getAwardGold());
                } else {
                    feedBackInfoVO.setHint(false);
                }
            }
            feedBackInfoLists.add(feedBackInfoList);
        }
        feedBackInfoVO.setFeedBackInfoLists(feedBackInfoLists);
        return feedBackInfoVO;
    }

    private List<MessageBoard> getMessageBoards(Long studentId) {
        MessageBoardExample messageBoardExample = new MessageBoardExample();
        messageBoardExample.setOrderByClause("id asc");
        messageBoardExample.createCriteria().andStudentIdEqualTo(studentId);
        return messageBoardMapper.selectByExample(messageBoardExample);
    }
}

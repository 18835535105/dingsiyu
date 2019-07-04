package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.MessageBoardMapper;
import com.zhidejiaoyu.common.pojo.MessageBoard;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.FeedBackService;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoList;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wuchenxi
 * @date 2018/8/13
 */
@Service
@Slf4j
public class FeedBackServiceImpl extends BaseServiceImpl<MessageBoardMapper, MessageBoard> implements FeedBackService {

    @Autowired
    private MessageBoardMapper messageBoardMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${adminDomin}")
    private String adminDomin;



    @Override
    public ServerResponse<FeedBackInfoVO> getFeedBacks(HttpSession session) {
        Student student = getStudent(session);

        // 获取当前学生所有的反馈及被回复信息
        List<MessageBoard> messageBoards = messageBoardMapper.selectByStudentId(student.getId());
        if (messageBoards.size() > 0) {
            return ServerResponse.createBySuccess(packageFeedBackInfoVO(messageBoards, AliyunInfoConst.host + student.getHeadUrl()));
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
    public ServerResponse saveFeedBack(HttpSession session, String content, MultipartFile[] files) {

        Student student = getStudent(session);
        // 如果当前学生在禁言期，无法发起反馈
        List<MessageBoard> messageBoards = messageBoardMapper.selectStopSpeakTime(student.getId());
        if (messageBoards.size() > 0) {
            return ServerResponse.createByErrorMessage("您已被禁言至 " + DateUtil.formatYYYYMMDDHHMMSS(messageBoards.get(0).getStopSpeakEndTime())
                    + " ，在此期间无法反馈！");
        }

        String fileName = null;
        if (files != null && files.length > 0) {
            // 校验图片大小
            boolean flag = checkImgSize(files);
            if (!flag) {
                return ServerResponse.createByErrorMessage("图片存储占用超过15MB！");
            }
            List<String> fileNames = OssUpload.uploadFiles(files, FileConstant.FEEDBACK_IMG, null);
            if (fileNames == null) {
                return ServerResponse.createByError();
            }
            fileName = String.join(",", fileNames);
        }
        // 保存反馈内容
        try {
            saveFeedBackContent(content, student, fileName);
        } catch (Exception e) {
            log.error("保存学生 {}->{} 意见反馈失败！", student.getId(), student.getStudentName(), e);
            return ServerResponse.createByErrorMessage("提交失败！");
        }

        try{
            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
            String url = adminDomin + "/socket/getMessage";
            restTemplate.getForEntity(url, Map.class, paramMap);
        }catch (Exception e){
            log.error("[{} - {} -{}]提交反馈信息请求后台 socket 失败！", student.getId(), student.getAccount(), student.getStudentName(), e);
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> cancelRedPoint(HttpSession httpSession) {
        Long studentId = getStudentId(httpSession);
        messageBoardMapper.updateReadFlag(studentId, 4);
        return ServerResponse.createBySuccess();
    }

    private void saveFeedBackContent(String content, Student student, String url) {
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
        messageBoard.setUrl(url);
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            messageBoard = messageBoards.get(i);
            feedBackInfoList = new FeedBackInfoList();
            feedBackInfoList.setHeadUrl(headUrl);
            feedBackInfoList.setRole(messageBoard.getRole());
            feedBackInfoList.setTime(DateUtil.formatYYYYMMDDHHMMSS(messageBoard.getTime()));

            // 如果反馈内容中有图片，拼接图片信息
            if (messageBoard.getContent().contains("<img>") && StringUtils.isNotEmpty(messageBoard.getUrl())) {
                getContent(messageBoard, sb);
                feedBackInfoList.setContent(sb.toString());
            } else {
                feedBackInfoList.setContent(messageBoard.getContent());
            }

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

    /**
     * 拼接图片信息
     *
     * @param messageBoard
     * @param sb
     */
    private void getContent(MessageBoard messageBoard, StringBuilder sb) {
        String[] urlArr = messageBoard.getUrl().split(",");
        String[] contentArr = messageBoard.getContent().split("<img>");
        int urlArrLength = urlArr.length;
        int contentArrLength = contentArr.length;
        sb.setLength(0);
        for (int i1 = 0; i1 < urlArrLength; i1++) {
            if (contentArr.length > 0) {
                sb.append(i1 < contentArrLength ? contentArr[i1] : "").append("<img src='").append(GetOssFile.getPublicObjectUrl(urlArr[i1])).append("' class='infoImg'>");
            } else {
                sb.append("<img src='").append(GetOssFile.getPublicObjectUrl(urlArr[i1])).append("' class='infoImg'>");
            }
        }
        if (contentArrLength > urlArrLength) {
            sb.append(contentArr[contentArrLength - 1]);
        }
    }
}

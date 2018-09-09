package com.zhidejiaoyu.student.common.personal;

import com.zhidejiaoyu.common.mapper.AwardMapper;
import com.zhidejiaoyu.common.mapper.CcieMapper;
import com.zhidejiaoyu.common.mapper.MessageBoardMapper;
import com.zhidejiaoyu.common.mapper.NewsMapper;
import com.zhidejiaoyu.common.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 判断个人中心是否显示红点
 *
 * @author wuchenxi
 * @date 2018/9/1
 */
@Component
public class InitRedPointThread {

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private AwardMapper awardMapper;

    /**
     * 判断个人中心是否需要显示红点
     *
     * @param student
     * @param result
     */
    @Async
    public void redPoint(Student student, Map<String, Object> result) {
        // 查看学生是否有未读的留言反馈内容
        int count = countMessageBoard(student);
        if (count > 0) {
            result.put("redPoint", true);
        }

        // 查看学生消息中心是否未读的消息
        count = countNews(student);
        if (count > 0) {
            result.put("redPoint", true);
        }

        // 查看是否有未查看的新证书
        count = countCcie(student);
        if (count > 0) {
            result.put("redPoint", true);
        }

        // 查看日奖励是否有需要领取的奖励
        count = countAward(student);
        if (count > 0) {
            result.put("redPoint", true);
        } else {
            result.put("readPoint", false);
        }
    }

    /**
     * 查看奖励信息中是否含有需要领取的奖励
     *
     * @param student
     * @return
     */
    private int countAward(Student student) {
        AwardExample awardExample = new AwardExample();
        AwardExample.Criteria criteria = awardExample.createCriteria();
        criteria.andStudentIdEqualTo(student.getId()).andCanGetEqualTo(1).andGetFlagEqualTo(2);
        return awardMapper.countByExample(awardExample);
    }

    private int countCcie(Student student) {
        CcieExample ccieExample = new CcieExample();
        CcieExample.Criteria criteria = ccieExample.createCriteria();
        criteria.andStudentIdEqualTo(student.getId()).andReadFlagEqualTo(0);
        return ccieMapper.countByExample(ccieExample);
    }

    /**
     * 查看学生消息中心是否未读的消息
     *
     * @param student
     * @return
     */
    private int countNews(Student student) {
        NewsExample newsExample = new NewsExample();
        NewsExample.Criteria criteria = newsExample.createCriteria();
        criteria.andStudentidEqualTo(student.getId()).andReadEqualTo(2);
        return newsMapper.countByExample(newsExample);
    }

    /**
     * 查看学生是否有未读的留言反馈内容（管理人员新回复的）
     *
     * @param student
     * @return
     */
    private int countMessageBoard(Student student) {
        MessageBoardExample messageBoardExample = new MessageBoardExample();
        MessageBoardExample.Criteria criteria = messageBoardExample.createCriteria();
        criteria.andReadFlagEqualTo(3).andStudentIdEqualTo(student.getId());
        return messageBoardMapper.countByExample(messageBoardExample);
    }
}

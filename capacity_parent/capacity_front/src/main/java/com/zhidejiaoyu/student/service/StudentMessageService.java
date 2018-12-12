package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.Vo.studentMessage.StudentMessageListVo;
import com.zhidejiaoyu.common.pojo.StudentMessage;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 个人中心—》消息中心—》消息通知表 服务类
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
public interface StudentMessageService extends BaseService<StudentMessage> {

    /**
     * 获取消息通知列表内容
     *
     * @param session
     * @param pageSize
     * @param pageNum
     * @return
     */
    ServerResponse<List<StudentMessageListVo>> getMessageList(HttpSession session, Integer pageSize, Integer pageNum);

    /**
     * 删除消息
     *
     * @param session
     * @param messageId
     * @return
     */
    ServerResponse<Object> deleteMessage(HttpSession session, Integer messageId);

    /**
     * 保存消息
     *
     * @param session
     * @param studentMessage
     * @return
     */
    ServerResponse<String> saveStudentMessage(HttpSession session, StudentMessage studentMessage);

    /**
     * 撤回消息，智能撤回两分钟内的消息
     *
     * @param session
     * @param messageId
     * @return
     */
    ServerResponse<Object> cancelMessage(HttpSession session, Integer messageId);
}

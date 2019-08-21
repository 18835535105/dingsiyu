package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.Vo.studentMessage.StudentMessageListVo;
import com.zhidejiaoyu.common.mapper.StudentMessageMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentMessage;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.StudentMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 个人中心—》消息中心—》消息通知表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
@Service
public class StudentMessageServiceImpl extends BaseServiceImpl<StudentMessageMapper, StudentMessage> implements StudentMessageService {

    @Autowired
    private StudentMessageMapper studentMessageMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public ServerResponse<List<StudentMessageListVo>> getMessageList(HttpSession session, Integer pageSize, Integer pageNum) {
        Student student = getStudent(session);
        PageHelper.startPage(pageNum, pageSize);
        List<StudentMessageListVo> studentMessageListVos = studentMessageMapper.selectMessageList(student.getId());
        if (studentMessageListVos.size() > 0) {
            String headUrl = GetOssFile.getPublicObjectUrl(student.getHeadUrl());
            studentMessageListVos.forEach(studentMessageListVo -> {
                if (studentMessageListVo.getStudentId() != 0) {
                    studentMessageListVo.setHeadUrl(headUrl);
                    studentMessageListVo.setTimestamp(studentMessageListVo.getCreateTime().getTime());
                }
            });
            Collections.reverse(studentMessageListVos);
        }
        return ServerResponse.createBySuccess(studentMessageListVos);
    }

    @Override
    public ServerResponse<Object> deleteMessage(HttpSession session, Integer messageId) {
        Student student = getStudent(session);
        StudentMessage studentMessage = studentMessageMapper.selectByStudentIdAndMessageId(student.getId(), messageId);
        if (studentMessage != null) {
            studentMessageMapper.updateMessageState(messageId, 2);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> saveStudentMessage(HttpSession session, StudentMessage studentMessage) {
        Student student = getStudent(session);
        SysUser sysUser = sysUserMapper.selectById(student.getTeacherId());

        studentMessage.setStudentId(student.getId());
        studentMessage.setTeacherId(0L);
        studentMessage.setState(1);
        studentMessage.setCreateTime(new Date());
        studentMessage.setUpdateTime(new Date());
        if (sysUser != null) {
            studentMessage.setReceiverAccount(sysUser.getAccount());
            studentMessage.setReceiverId(Integer.parseInt(student.getTeacherId().toString()));
            studentMessage.setReceiverName(sysUser.getName());
        }
        studentMessageMapper.insert(studentMessage);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> cancelMessage(HttpSession session, Integer messageId) {
        Student student = getStudent(session);
        StudentMessage studentMessage = studentMessageMapper.selectByStudentIdAndMessageId(student.getId(), messageId);
        if (studentMessage != null) {
            studentMessageMapper.updateMessageState(messageId, 3);
        }
        return ServerResponse.createBySuccess();
    }
}

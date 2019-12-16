package com.zhidejiaoyu.student.business.service.simple.impl;

import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.SimpleConsumeMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleRunLogMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentMapper;
import com.zhidejiaoyu.common.pojo.Consume;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.simple.SimpleConsumeServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-21
 */
@Service
public class SimpleConsumeServiceImplSimple extends SimpleBaseServiceImpl<SimpleConsumeMapper, Consume> implements SimpleConsumeServiceSimple {

    @Autowired
    private SimpleConsumeMapper simpleConsumeMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public int reduceConsume(int type, int number, HttpSession session) {
        Student student = super.getStudent(session);
        if (type == 1) {
            Consume consume = getConsume("金币消耗", type, number, student.getId().intValue(), 2);
            Integer result = simpleConsumeMapper.insert(consume);
            if (result > 0) {
                student.setSystemGold(student.getSystemGold() - number);
                student.setOfflineGold(student.getOfflineGold() + number);
                int i = simpleStudentMapper.updateByPrimaryKey(student);
                if (i > 0) {
                    session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                    return 1;
                }

            }
        } else if (type == 2) {
            Consume consume = getConsume("钻石消耗", type, number, student.getId().intValue(), 2);
            Integer result = simpleConsumeMapper.insert(consume);
            if (result > 0) {
                Integer diamond = student.getDiamond();
                student.setDiamond(diamond - number);
                int i = simpleStudentMapper.updateByPrimaryKey(student);
                if (i > 0) {
                    session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public int addConsume(int type, int number, HttpSession session) {
        Student student = super.getStudent(session);
        if (type == 1) {
            Consume consume = getConsume("金币增加", type, number, student.getId().intValue(), 1);
            Integer result = simpleConsumeMapper.insert(consume);
            if (result > 0) {
                student.setSystemGold(student.getSystemGold() + number);
                int i = simpleStudentMapper.updateByPrimaryKey(student);
                if (i > 0) {
                    session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                    getLevel(session);
                    return 1;
                }
            }
        } else if (type == 2) {
            Consume consume = getConsume("钻石增加", type, number, student.getId().intValue(), 1);
            Integer result = simpleConsumeMapper.insert(consume);
            if (result > 0) {
                Integer diamond = student.getDiamond();
                if (diamond != null) {
                    student.setDiamond(diamond + number);
                } else {
                    student.setDiamond(number);
                }
                int i = simpleStudentMapper.updateByPrimaryKey(student);
                if (i > 0) {
                    session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                    return 1;
                }
            }
        }
        return 0;

    }

    private Consume getConsume(String name, int type, Integer number, Integer studentId, int state) {
        Consume consume = new Consume();
        consume.setName(name);
        consume.setType(type);
        consume.setNumber(number);
        consume.setState(state);
        consume.setStudentId(studentId);
        RunLog runLog = new RunLog();
        runLog.setOperateUserId(Long.valueOf(studentId));
        runLog.setCreateTime(new Date());
        runLog.setType(4);
        runLog.setLogContent("抽奖获得金币#" + number + "#");
        runLogMapper.insert(runLog);
        return consume;
    }


}

package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.ConsumeMapper;
import com.zhidejiaoyu.common.mapper.simple.RunLogMapper;
import com.zhidejiaoyu.common.mapper.simple.StudentMapper;
import com.zhidejiaoyu.common.pojo.Consume;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.simple.SimpleConsumeServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-21
 */
@Service
public class SimpleConsumeServiceImplSimple extends SimpleBaseServiceImpl<ConsumeMapper, Consume> implements SimpleConsumeServiceSimple {

    @Autowired
    private ConsumeMapper consumeMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public int reduceConsume(int type, int number, HttpSession session) {
        Student student = super.getStudent(session);
        if(type==1){
            Consume consume = getConsume("金币消耗", type, number, student.getId().intValue(),2);
            Integer result = consumeMapper.insert(consume);
            if(result>0){
               student.setSystemGold(student.getSystemGold()-number);
               student.setOfflineGold(student.getOfflineGold()+number);
                int i = studentMapper.updateByPrimaryKey(student);
                if(i>0){
                    session.setAttribute(UserConstant.CURRENT_STUDENT,student);
                    return 1;
                }

            }
        }else if(type==2){
            Consume consume = getConsume("钻石消耗", type, number, student.getId().intValue(),2);
            Integer result = consumeMapper.insert(consume);
            if(result>0){
                Integer diamond = student.getDiamond();
                student.setDiamond(diamond - number);
                int i = studentMapper.updateByPrimaryKey(student);
                if(i>0){
                    session.setAttribute(UserConstant.CURRENT_STUDENT,student);
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
        if(type==1){
            Consume consume = getConsume("金币增加", type, number, student.getId().intValue(),1);
            Integer result = consumeMapper.insert(consume);
            if(result>0){
                student.setSystemGold(student.getSystemGold()+number);
                int i = studentMapper.updateByPrimaryKey(student);
                if(i>0){
                    session.setAttribute(UserConstant.CURRENT_STUDENT,student);
                    getLevel(session);
                    return 1;
                }
            }
        }else if(type==2){
            Consume consume = getConsume("钻石增加", type, number, student.getId().intValue(),1);
            Integer result = consumeMapper.insert(consume);
            if(result>0){
                Integer diamond = student.getDiamond();
                if(diamond!=null){
                    student.setDiamond(diamond + number);
                }else{
                    student.setDiamond(number);
                }
                int i = studentMapper.updateByPrimaryKey(student);
                if(i>0){
                    session.setAttribute(UserConstant.CURRENT_STUDENT,student);
                    return 1;
                }
            }
        }
        return 0;

    }

    private Consume getConsume(String name, int type, Integer number, Integer studentId, int state){
        Consume consume=new Consume();
        consume.setName(name);
        consume.setType(type);
        consume.setNumber(number);
        consume.setState(state);
        consume.setStudentId(studentId);
        RunLog runLog=new RunLog();
        runLog.setCreateTime(new Date());
        runLog.setType(4);
        runLog.setLogContent("id为:"+studentId+"抽奖获得金币#"+number+"#");
        runLogMapper.insert(runLog);
        return consume;
    }



}

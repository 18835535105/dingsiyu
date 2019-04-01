package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Consume;
import com.zhidejiaoyu.common.mapper.ConsumeMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.ConsumeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-21
 */
@Service
public class ConsumeServiceImpl extends BaseServiceImpl<ConsumeMapper, Consume> implements ConsumeService {

    @Autowired
    private ConsumeMapper consumeMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public int reduceConsume(int type, int number, HttpSession session) {
        Student student = getStudent(session);
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
        Student student = getStudent(session);
        if(type==1){
            Consume consume = getConsume("金币增加", type, number, student.getId().intValue(),1);
            Integer result = consumeMapper.insert(consume);
            if(result>0){
                student.setSystemGold(student.getSystemGold()+number);
                int i = studentMapper.updateByPrimaryKey(student);
                if(i>0){
                    session.setAttribute(UserConstant.CURRENT_STUDENT,student);
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
        return consume;
    }



}

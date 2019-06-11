package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.simple.SimpleBaseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2018/8/29
 */
public class SimpleBaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements SimpleBaseService<T> {

    @Autowired
    private SimpleDurationMapper simpleDurationMapper;

    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;


    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Autowired
    private SimpleLevelMapper simpleLevelMapper;


    @Override
    public Student getStudent(HttpSession session) {
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (student != null) {
            return simpleStudentMapper.selectById(student.getId());
        }
        throw new RuntimeException("学生未登录");
    }

    @Override
    public Long getStudentId(HttpSession session) {
        return getStudent(session).getId();
    }

    @Override
    public Integer getValidTime(Long studentId, String beginTime, String endTime) {
        Integer time = simpleDurationMapper.selectValidTime(studentId, beginTime, endTime);
        return time == null ? 0 : time;
    }

    @Override
    public Integer getOnLineTime(HttpSession session, String beginTime, String endTime) {
        Integer onlineTime = simpleDurationMapper.selectOnlineTime(getStudentId(session), beginTime, endTime);
        int loginTime = (int) (System.currentTimeMillis() - ((Date) session.getAttribute(TimeConstant.LOGIN_TIME)).getTime()) / 1000;
        if (onlineTime == null) {
            onlineTime = loginTime;
        } else {
            onlineTime += loginTime;
        }
        return onlineTime;
    }

    @Override
    public void getLevel(HttpSession session) {
        Student student = getStudent(session);
        Double gold = BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        int level = getLevels(gold.intValue(), levels);
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(student.getId());
        if(studentExpansion != null && studentExpansion.getLevel()<level){
            Integer oldStudy = simpleLevelMapper.getStudyById(studentExpansion.getLevel());
            Integer newStudy = simpleLevelMapper.getStudyById(level);
            Integer addStudy=0;
            if(oldStudy==null || newStudy==null){
                if(newStudy==null){
                    addStudy=oldStudy;
                }
                if(oldStudy==null){
                    addStudy=newStudy;
                }
            }else{
               addStudy =newStudy-oldStudy;
            }

            studentExpansion.setLevel(level);
            studentExpansion.setStudyPower(studentExpansion.getStudyPower()+addStudy);
            simpleStudentExpansionMapper.updateById(studentExpansion);
        }
    }

    /**
     * 计算今天的在线时长
     *
     * @param session
     * @return
     */
    Integer getTodayOnlineTime(HttpSession session) {
        String formatYYYYMMDD = SimpleDateUtil.formatYYYYMMDD(new Date());
        return this.getOnLineTime(session, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 23:59:59");
    }

    /**
     * 计算今天的有效时长
     *
     * @param studentId
     * @return
     */
    Integer getTodayValidTime(Long studentId) {
        String formatYYYYMMDD = SimpleDateUtil.formatYYYYMMDD(new Date());
        return this.getValidTime(studentId, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 23:59:59");
    }

    /**
     * 学生需要单元测试提示信息
     *
     * @return
     */
    Map<String, Object> toUnitTest(Integer code, String msg) {
        long token = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>(16);
        map.put("status", code);
        map.put("msg", msg);
        map.put("token", token);
        request.getSession().setAttribute("token", token);
        return map;
    }

    /**
     * 获取学生的校管 id
     *
     * @param student
     * @return  校管 id，可能为 null
     */
    Integer getSchoolAdminId(Student student) {
        return student.getTeacherId() == null ? null : simpleTeacherMapper.selectSchoolIdAdminByTeacherId(student.getTeacherId());
    }

    public  int getLevels(Integer myGold, List<Map<String, Object>> levels) {
        int level = 0;
        if (myGold >= 50) {
            int myrecord = 0;
            int myauto = 1;
            for (int i = 0; i < levels.size(); i++) {
                // 循环的当前等级分数
                int levelGold = (int) levels.get(i).get("gold");
                // 下一等级分数
                int xlevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");

                if (myGold >= myrecord && myGold < xlevelGold) {
                    level = i + 1;
                    break;
                    // 等级循环完还没有确定等级 = 最高等级
                } else if (myauto == levels.size()) {
                    level = i + 1;
                    break;
                }
                myrecord = levelGold;
                myauto++;
            }
            myrecord = 0;
            myauto = 0;
        }else{
            level=1;
        }
        return level;
    }
}

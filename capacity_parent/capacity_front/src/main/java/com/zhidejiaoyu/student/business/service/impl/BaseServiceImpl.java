package com.zhidejiaoyu.student.business.service.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.TokenUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import com.zhidejiaoyu.student.common.SaveRunLog;
import com.zhidejiaoyu.student.business.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
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
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private StudyFlowMapper studyFlowMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private StudentExpansionMapper studentExpansionMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private SaveRunLog saveRunLog;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private LearnNewMapper learnNewMapper;


    @Override
    public Student getStudent(HttpSession session) {
        return studentMapper.selectById(this.getStudentId(session));
    }

    @Override
    public Long getStudentId(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        return student.getId();
    }

    /**
     * 获取测试或者学习开始时间
     *
     * @return
     */
    public Date getStartTime() {
        return (Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME);
    }

    @Override
    public void getLevel(HttpSession session) {
        Student student = getStudent(session);
        double gold = student.getSystemGold() + student.getOfflineGold();
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        int level = getLevel((int) gold, levels);
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
        if (studentExpansion != null && studentExpansion.getLevel() < level) {
            Integer oldStudy = 0;
            if (studentExpansion.getLevel() != 0) {
                oldStudy = levelMapper.getStudyById(studentExpansion.getLevel());
            }
            Integer newStudy = levelMapper.getStudyById(level);
            Integer addStudy = newStudy - oldStudy;
            studentExpansion.setLevel(level);
            studentExpansion.setStudyPower(studentExpansion.getStudyPower() + addStudy);
            studentExpansionMapper.updateById(studentExpansion);
        }
    }

    @Override
    public StudyFlow getCurrentStudyFlow(Long studentId) {
        return studyFlowMapper.selectCurrentFlowByStudentId(studentId);
    }

    @Override
    public void saveRunLog(Student student, Integer type, Long courseId, Long unitId, String msg) {
        saveRunLog.saveRunLog(student, type, courseId, unitId, msg);
    }

    @Override
    public void saveRunLog(Student student, Integer type, String msg) throws RuntimeException {
        saveRunLog.saveRunLog(student, type, msg);
    }

    /**
     * 保存测试时间 保证测试时间不为空
     *
     * @param testRecord
     * @param session
     * @param date
     */
    public void saveTestRecordTime(TestRecord testRecord, HttpSession session, Date date) {
        if (date == null) {
            new Date();
        }
        testRecord.setTestEndTime(date);
        Object startTime = session.getAttribute(TimeConstant.BEGIN_START_TIME);
        if (startTime != null) {
            testRecord.setTestStartTime((Date) startTime);
        } else {
            testRecord.setTestStartTime(date);
        }

    }

    /**
     * 学生需要单元测试提示信息
     *
     * @return
     */
    public Map<String, Object> toUnitTest() {
        String token = TokenUtil.getToken();
        Map<String, Object> map = new HashMap<>(16);
        map.put("status", TestResponseCode.TO_UNIT_TEST.getCode());
        map.put("msg", TestResponseCode.TO_UNIT_TEST.getMsg());
        map.put("token", token);
        request.getSession().setAttribute("token", token);
        return map;
    }

    /**
     * 获取需要奖励的能量值
     *
     * @param student
     * @param point
     * @return
     */
    public int getEnergy(Student student, Integer point, Integer number) {
        int addEnergy = 0;
        if (number == null || number == 0) {
            if (student.getEnergy() == null) {
                if (point >= PointConstant.SIXTY && point < PointConstant.HUNDRED) {
                    student.setEnergy(4);
                    addEnergy = 4;
                } else if (point == PointConstant.HUNDRED) {
                    student.setEnergy(5);
                    addEnergy = 5;
                }
            } else {
                if (point >= PointConstant.SIXTY && point < PointConstant.HUNDRED) {
                    student.setEnergy(student.getEnergy() + 4);
                    addEnergy = 4;
                } else if (point == PointConstant.HUNDRED) {
                    student.setEnergy(student.getEnergy() + 5);
                    addEnergy = 5;
                }
            }
        }
        return addEnergy;
    }

    public int getLevel(Integer myGold, List<Map<String, Object>> levels) {
        int level = 0;
        if (myGold >= 50) {
            int myRecord = 0;
            int myAuto = 1;
            for (int i = 0; i < levels.size(); i++) {
                // 循环的当前等级分数
                int levelGold = (int) levels.get(i).get("gold");
                // 下一等级分数
                int nextLevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");

                if (myGold >= myRecord && myGold < nextLevelGold) {
                    level = i + 1;
                    break;
                    // 等级循环完还没有确定等级 = 最高等级
                } else if (myAuto == levels.size()) {
                    level = i + 1;
                    break;
                }
                myRecord = levelGold;
                myAuto++;
            }
        } else {
            level = 1;
        }
        return level;
    }

    /**
     * 获取未学过的单元信息
     *
     * @param unitId
     * @param studentId
     * @param type
     * @param model
     * @return
     */
    public Map<String, Object> getSudyWords(Long unitId, Long studentId, Integer type, Integer model, Integer group, String studyModel) {
        Map<String, Object> correct;
        //获取当前单词模块已经学习过的wordId
        List<Long> longs = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, studentId, studyModel, 1);
        // 获取新词
        correct = learnNewMapper.selectStudyMap(studentId, unitId, longs, type, model, group);
        return correct;
    }


}

package com.zhidejiaoyu.student.business.service.simple.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleLevelMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleTeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2018/8/29
 */
public class SimpleBaseServiceImpl<M extends BaseMapper<T>, T> extends BaseServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Autowired
    private SimpleLevelMapper simpleLevelMapper;

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

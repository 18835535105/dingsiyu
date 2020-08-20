package com.dfdz.teacher.util;

import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wuchenxi
 * @date 2019-06-06
 */
@Slf4j
@Component
public class RedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RunLogMapper runLogMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private TeacherMapper teacherMapper;

    public String getStudentFirstLoginTime(Student student) {
        Object obj = redisTemplate.opsForHash().get(RedisKeysConst.STUDENT_FIRST_LOGIN_TIME, student.getId());
        if (obj != null) {
            return obj.toString();
        } else {
            Map<String, Object> stringObjectMap = runLogMapper.selCreateTimeByTypeAndStudentId(1, student.getId());
            if (stringObjectMap != null) {
                String time = DateUtil.formatYYYYMMDDHHMMSS((Date) stringObjectMap.get("time"));
                redisTemplate.opsForHash().put(RedisKeysConst.STUDENT_FIRST_LOGIN_TIME, student.getId(), time);
                redisTemplate.expire(RedisKeysConst.STUDENT_FIRST_LOGIN_TIME, 30, TimeUnit.DAYS);
                return time;
            }
            return null;
        }
    }

    //判断该教师是否进行添加学习计划操作
    public boolean getTeacherAddStudentStudyPlan(Long teacherId) {
        Object obj = redisTemplate.opsForHash().get(RedisKeysConst.ADD_STUDENT_STUDY_PLAN, teacherId);
        if (obj != null) {
            return true;
        }
        return false;
    }

    //添加教师分配学习计划状态
    public void addRedisTeacherStudentPlan(Long teacherId) {
        redisTemplate.opsForHash().put(RedisKeysConst.ADD_STUDENT_STUDY_PLAN, teacherId, true);
        redisTemplate.expire(RedisKeysConst.PERMISSION_OF_ROLE, 20, TimeUnit.SECONDS);
    }

    //去除教师分配学生计划状态
    public void delRedisTeacherStudentPlan(Long teacherId) {
        redisTemplate.opsForHash().delete(RedisKeysConst.ADD_STUDENT_STUDY_PLAN, teacherId);
    }

    /**
     * 获取学生账号范围
     *
     * @param count 生成的个数
     * @return
     */
    public AccountRange getAccountRange(Integer count) {
        Object o = redisTemplate.opsForValue().get(RedisKeysConst.MAX_ACCOUNT);
        AccountRange accountRange = new AccountRange();
        if (o != null) {
            try {
                Long current = Long.valueOf(o.toString());
                accountRange.setCurrent(current);
                accountRange.setMax(current + count);
                redisTemplate.opsForValue().increment(RedisKeysConst.MAX_ACCOUNT, count);
                return accountRange;
            } catch (Exception e) {
                log.warn("类型转换错误！", e);
            }
        }
        Student student = studentMapper.selectStudentByMaxId();
        String account = student.getAccount();
        // 账号的数字
        String numStr = account.substring(2);
        long num = Long.parseLong(numStr);

        accountRange.setCurrent(num);
        accountRange.setMax(count + num);

        redisTemplate.opsForValue().set(RedisKeysConst.MAX_ACCOUNT, accountRange.getMax());
        return accountRange;
    }

    /**
     * 获取学管账号范围
     *
     * @return
     */
    public AccountRange getTeacherAccountRange(Integer count, String prefix) {
        Object o = redisTemplate.opsForValue().get(RedisKeysConst.MAX_TEACHER_ACCOUNT + prefix + ":");
        AccountRange accountRange = new AccountRange();
        if (o != null) {
            try {
                Long current = Long.valueOf(o.toString());
                accountRange.setCurrent(current);
                accountRange.setMax(current + count);
                redisTemplate.opsForValue().increment(RedisKeysConst.MAX_TEACHER_ACCOUNT + prefix + ":", count);
                return accountRange;
            } catch (Exception e) {
                log.warn("类型转换错误！", e);
            }
        }
        SysUser user = userMapper.selectByMaxAccount(prefix);
        String account = user.getAccount();
        // 账号的数字
        String numStr = account.substring(2);
        long num = Long.parseLong(numStr);
        accountRange.setCurrent(num + 1);
        accountRange.setMax(count + accountRange.getCurrent());
        redisTemplate.opsForValue().set(RedisKeysConst.MAX_TEACHER_ACCOUNT + prefix + ":", accountRange.getMax());
        return accountRange;
    }

    @Data
    public
    class AccountRange {
        /**
         * 账号当前最大数字
         */
        Long current;

        /**
         * 需要生成到最大的数字
         */
        Long max;
    }

    /**
     * 删除缓存中的排行榜数据
     *
     * @param studentIds
     */
    public void deleteCaches(List<Long> studentIds) {
        try {
            List<Student> students = studentMapper.selectByIds(studentIds);

            students.forEach(student -> {
                Long studentId = student.getId();
                Long teacherId = student.getTeacherId();
                Long classId = student.getClassId();

                this.deleteMember(RankKeysConst.CLASS_CCIE_RANK + teacherId + ":" + classId, studentId);
                this.deleteMember(RankKeysConst.CLASS_GOLD_RANK + teacherId + ":" + classId, studentId);
                this.deleteMember(RankKeysConst.CLASS_MEDAL_RANK + teacherId + ":" + classId, studentId);
                this.deleteMember(RankKeysConst.CLASS_WORSHIP_RANK + teacherId + ":" + classId, studentId);

                Integer schoolAdminId = getSchoolAdminId(teacherId.intValue());
                this.deleteMember(RankKeysConst.SCHOOL_CCIE_RANK + schoolAdminId, studentId);
                this.deleteMember(RankKeysConst.SCHOOL_GOLD_RANK + schoolAdminId, studentId);
                this.deleteMember(RankKeysConst.SCHOOL_MEDAL_RANK + schoolAdminId, studentId);
                this.deleteMember(RankKeysConst.SCHOOL_WORSHIP_RANK + schoolAdminId, studentId);

                this.deleteMember(RankKeysConst.COUNTRY_CCIE_RANK, studentId);
                this.deleteMember(RankKeysConst.COUNTRY_GOLD_RANK, studentId);
                this.deleteMember(RankKeysConst.COUNTRY_MEDAL_RANK, studentId);
                this.deleteMember(RankKeysConst.COUNTRY_WORSHIP_RANK, studentId);
            });
        } catch (Exception e) {
            log.warn("删除 redis 中排行数据失败！", e);
        }
    }

    private Integer getSchoolAdminId(Integer teacherId) {
        Integer schoolAdminId = teacherMapper.getSchoolAdminById(teacherId);
        schoolAdminId = schoolAdminId == null ? teacherId : schoolAdminId;
        return schoolAdminId;
    }


    /**
     * 删除多余的排行信息
     *
     * @param key
     * @param studentId
     */
    public void deleteMember(String key, Long studentId) {
        redisTemplate.opsForZSet().remove(key, studentId);
    }
}

package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleTeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.BaseService;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

}

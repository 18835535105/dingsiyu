package com.zhidejiaoyu.common.aop;

import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 单元闯关测试变化时相关操作
 *
 * @author wuchenxi
 * @date 2019-03-28
 */
@Slf4j
@Aspect
@Component
public class UnitTestChangeAop {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Pointcut("execution(public * com.zhidejiaoyu.student.service.impl.TestServiceImpl(..))")
    public void testPoint() {}

    @After("testPoint()")
    public void afterTest() {
        HttpSession session = request.getSession();
        Object object = session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (object != null) {
            Student student = (Student) object;
            student = studentMapper.selectById(student.getId());
            String point = request.getParameter("point");

            // 验证学生今日是否完成一个单元
            dailyAwardAsync.todayLearnOneUnit(student);
            // 验证学生今日是否完成10个单元闯关测试
            dailyAwardAsync.todayCompleteTenUnitTest(student);
            // 验证学生单元闯关成功个数
            goldAwardAsync.completeUnitTest(student);

            // 最有潜力勋章
            medalAwardAsync.potentialMan(student);
            if (Objects.equals(point, "100")) {
                // 学霸崛起勋章计算
                medalAwardAsync.superStudent(student);
            }
        }
    }
}

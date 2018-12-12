package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.StudentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取学生信息相关controller
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
@Slf4j
@RestController
@RequestMapping("/student")
@Validated
public class StudentInfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentInfoController.class);

    @Autowired
    private StudentInfoService studentInfoService;

    /**
     * 完善学生信息、修改学生信息时获取学生信息
     *
     * @return
     */
    @GetMapping("/getStudentInfo")
    public ServerResponse<Student> getStudentInfo(HttpSession session, @RequestParam(value = "studentId", required = false) Long studentId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (StringUtils.isNotEmpty(student.getPetName()) && studentId == null) {
            session.invalidate();
            // 学生已经完善过信息，不可重复完善信息
            return ServerResponse.createByErrorMessage("您已经完善过个人信息，不可再次执行该操作！");
        }
        student.setPassword(null);
        return ServerResponse.createBySuccess(student);
    }

    /**
     * 完善学生信息时保存学生信息
     *
     * @param session
     * @param student
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return
     */
    @PostMapping("/saveStudentInfo")
    public ServerResponse<String> saveStudentInfo(HttpSession session, Student student, String oldPassword,
                                                  String newPassword) {
        ServerResponse<String> x = validStudentInfo(session, student, oldPassword, newPassword);
        if (x != null) {
            return x;
        }
        return studentInfoService.saveStudentInfo(session, student, oldPassword, newPassword);
    }

    /**
     * 学生在个人中心修改个人信息
     *
     * @param session
     * @param student
     * @return
     */
    @PostMapping("/updateStudentInfo")
    public ServerResponse<String> updateStudentInfo(HttpSession session, Student student) {
        ServerResponse<String> stringServerResponse = checkStudentCommon(student);
        if (stringServerResponse != null) {
            return stringServerResponse;
        }
        return studentInfoService.updateStudentInfo(session, student);
    }

    /**
     * 验证原密码是否正确
     *
     * @param session
     * @param oldPassword   原密码
     * @return
     */
    @PostMapping("/judgeOldPassword")
    public ServerResponse<String> judgeOldPassword(HttpSession session, String oldPassword) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String password = student.getPassword();
        int minPasswordLength = 6;
        int maxPasswordLength = 10;
        ServerResponse<String> stringServerResponse = validOldPassword(oldPassword, password, minPasswordLength, maxPasswordLength);
        if (stringServerResponse != null) {
            return stringServerResponse;
        }
        return studentInfoService.judgeOldPassword(password, oldPassword);
    }

    /**
     * 校验学生信息
     *
     * @param session
     * @param student
     * @param oldPassword
     * @param newPassword
     * @return
     */
    private ServerResponse<String> validStudentInfo(HttpSession session, Student student, String oldPassword, String newPassword) {
        Student studentInfo = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        int minPasswordLength = 6;
        int maxPasswordLength = 10;

        if (StringUtils.isNotEmpty(studentInfo.getPetName())) {
            return ServerResponse.createByErrorMessage("您已经完善过个人信息，不可再次执行该操作！");
        }

        ServerResponse<String> x = validOldPassword(oldPassword, studentInfo.getPassword(), minPasswordLength, maxPasswordLength);
        if (x != null) {
            return x;
        }

        boolean validNewPassword = StringUtils.isNotEmpty(newPassword)
                && (newPassword.length() < minPasswordLength || newPassword.length() > maxPasswordLength);
        if (validNewPassword) {
            return ServerResponse.createByErrorMessage("新密码长度必须是6~10个字符！");
        }

        ServerResponse<String> x1 = checkStudentCommon(student);
        if (x1 != null) {
            return x1;
        }
        return null;
    }

    /**
     * 学生信息公共验证部分
     *
     * @param student
     * @return
     */
    private ServerResponse<String> checkStudentCommon(Student student) {
        int maxNameLength = 20;
        int minNameLength = 2;

        if (student.getStudentName().trim().length() > maxNameLength || student.getStudentName().trim().length() < minNameLength) {
            return ServerResponse.createByErrorMessage("姓名长度限制在 2-20 之间!请重新输入!");
        }

        // 验证出生日期在 1970至今的日期
        String birthDate = student.getBirthDate();
        if (StringUtils.isNotEmpty(birthDate)) {
            try {
                Date birth = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
                Date beginDate = new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01 00:00:00");
                if (birth.getTime() < beginDate.getTime() || birth.getTime() > System.currentTimeMillis()) {
                    return ServerResponse.createByErrorMessage("出生日期不合法！");
                }
            } catch (Exception e) {
                LOGGER.error("保存学生 {} -> {} 信息时验证出生日期合法性出错！", student.getId(), student.getStudentName(), e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 验证旧密码是否合法
     *
     * @param oldPassword
     * @param nowPassword
     * @param minPasswordLength
     * @param maxPasswordLength
     * @return
     */
    private ServerResponse<String> validOldPassword(String oldPassword, String nowPassword, int minPasswordLength, int maxPasswordLength) {
        boolean validOldPassword = StringUtils.isNotEmpty(oldPassword)
                && (oldPassword.length() < minPasswordLength || oldPassword.length() > maxPasswordLength);
        if (validOldPassword) {
            return ServerResponse.createByErrorMessage("旧密码长度必须是6~10个字符！");
        }
        // 验证原密码是否正确
        if (StringUtils.isNotBlank(oldPassword) && !oldPassword.equals(nowPassword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.PASSWORD_ERROR.getCode(), ResponseCode.PASSWORD_ERROR.getMsg());
        }
        return null;
    }

    /**
     * 膜拜
     *
     * @param session
     * @param userId  被膜拜人的id
     * @return
     */
    @PostMapping("/worship")
    public ServerResponse<String> worship(HttpSession session, Long userId) {
        if (userId == null) {
            return ServerResponse.createByErrorMessage("userId can't be null!");
        }
        return studentInfoService.worship(session, userId);
    }

    /**
     * 学生进入学习页面，开始记录学生学习有效时长
     *
     * @param session
     */
    @PostMapping("/startValidTime")
    public ServerResponse<String> startValidTime(HttpSession session) {
        session.setAttribute(TimeConstant.BEGIN_VALID_TIME, new Date());
        return ServerResponse.createBySuccess();
    }

    /**
     * 学生退出学习页面，记录本次在学习页面学习时长
     *
     * @param session
     * @param classify
     *                  学习模块(有效时长)，区分各个学习模块的时长，7：单元闯关测试；8：复习测试；9：已学测试；10：熟词测试；11：生词测试；
     *                  12：五维测试；13：任务课程；'14:单词辨音; 15:词组辨音; 16:单词认读; 17:词组认读; 18:词汇考点; 19:句型认读;
     *                  20:语法辨析; 21单词拼写; 22:词组拼写;
     * @param courseId
     * @param unitId
     * @return
     */
    @PostMapping("/endValidTime")
    public ServerResponse<String> endValidTime(HttpSession session, Integer classify,
                                               Long courseId, Long unitId, String validTime) {
        if (classify == null || courseId == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        }
        // TODO：validTime 修改为string，查找NAN问题
        // 有效时长数字
        long valid = 0L;
        if (!StringUtils.isEmpty(validTime)) {
            try {
                valid = Long.valueOf(validTime);
            } catch (Exception e) {
                log.error("有效时长入参类型错误：学习模块[{}]，validTime[{}]，error=[{}]", classify, validTime, e.getMessage());
            }
        }
        return studentInfoService.calculateValidTime(session, classify, courseId, unitId, valid);
    }
}

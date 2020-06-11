package com.zhidejiaoyu.student.business.controller.simple;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.dto.EndValidTimeDto;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.simple.studentInfoVo.ChildMedalVo;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.controller.StudentInfoController;
import com.zhidejiaoyu.student.business.service.StudentInfoService;
import com.zhidejiaoyu.student.business.service.simple.SimpleStudentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

import static com.zhidejiaoyu.student.business.controller.StudentInfoController.getStringServerResponse;

/**
 * 获取学生信息相关controller
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
@Slf4j
@RestController
@RequestMapping("/api/student")
public class SimpleStudentInfoController extends BaseController {

    @Autowired
    private SimpleStudentInfoService simpleStudentInfoService;

    @Autowired
    private StudentInfoService studentInfoService;

    /**
     * 完善学生信息、修改学生信息时获取学生信息
     *
     * @return
     */
    @GetMapping("/getStudentInfo")
    public ServerResponse<Student> getStudentInfo(HttpSession session, @RequestParam(value = "studentId", required = false) Long studentId) {
        Student student = super.getStudent(session);
        if (StringUtils.isNotEmpty(student.getPetName()) && studentId == null) {
            // 学生已经完善过信息，不可重复完善信息
            return ServerResponse.createByError(443,"您已经完善过个人信息，不可再次执行该操作！");
        }
        student.setPassword(null);
        student.setHeadUrl(GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        student.setPartUrl(GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        return ServerResponse.createBySuccess(student);
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
        ServerResponse<String> stringServerResponse = StudentInfoController.checkStudentCommon(student);
        if (stringServerResponse != null) {
            return stringServerResponse;
        }
        if (StringUtils.isNotEmpty(student.getQq()) && student.getQq().length() > 13) {
            student.setQq("");
        }
        return simpleStudentInfoService.updateStudentInfo(session, student);
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
        Student student = super.getStudent(session);
        String password = student.getPassword();
        int minPasswordLength = 6;
        int maxPasswordLength = 10;
        ServerResponse<String> stringServerResponse = StudentInfoController.validOldPassword(oldPassword, password, minPasswordLength, maxPasswordLength);
        if (stringServerResponse != null) {
            return stringServerResponse;
        }
        return simpleStudentInfoService.judgeOldPassword(password, oldPassword);
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
     * @return
     */
    @PostMapping("/endValidTime")
    public ServerResponse<String> endValidTime(HttpSession session, EndValidTimeDto dto) {
        return getStringServerResponse(session, dto, log, studentInfoService);
    }

    /**
     * 获取学生膜拜数据
     *
     * @param session
     * @param type     1：我被膜拜的数据；2：我膜拜别人的数据
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getWorship")
    public ServerResponse<Map<String, Object>> getWorship(HttpSession session, @RequestParam(required = false, defaultValue = "1") Integer type,
                                                          @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                          @RequestParam(required = false, defaultValue = "18") Integer pageSize) {
        return simpleStudentInfoService.getWorship(session, type, pageNum, pageSize);
    }

    /**
     * 点击父勋章获取子勋章信息
     *
     * @param session
     * @param stuId 为空时查看当前学生子勋章信息；否则查看指定学生的子勋章信息
     * @param medalId   勋章id
     * @return
     */
    @GetMapping("/getChildMedal")
    public ServerResponse<ChildMedalVo> getChildMedal(HttpSession session, @RequestParam(required = false) Long stuId, Long medalId) {
        if (medalId == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return simpleStudentInfoService.getChildMedal(session, stuId, medalId);
    }

    /**
     * 分页获取所有勋章信息
     *
     * @param session
     * @param stuId 为空时查看当前学生所有勋章信息；否则查看指定学生勋章信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getAllMedal")
    public ServerResponse<Map<String, Object>> getAllMedal(HttpSession session, @RequestParam(required = false) Long stuId,
                                                           @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return simpleStudentInfoService.getAllMedal(session, stuId, pageNum, pageSize);
    }

    /**
     * 打开/关闭背景音乐
     *
     * @param session
     * @param status  1:打开音乐；2：关闭音乐
     * @return
     */
//    @PostMapping("/optBackMusic")
    public ServerResponse optBackMusic(HttpSession session, Integer status) {
        if (status == null || status < 1 || status > 2) {
            status = 1;
        }
        return simpleStudentInfoService.optBackMusic(session, status);
    }

    /**
     * 获取学生背景音乐开关状态
     *
     * @param session
     * @return
     */
//    @GetMapping("/getBackMusicStatus")
    public ServerResponse<Map<String, Integer>> getBackMusicStatus(HttpSession session) {
        return simpleStudentInfoService.getBackMusicStatus(session);
    }

    /**
     * 删除 runlog 表中重复的退出日志记录，该接口仅使用一次
     *
     * @return
     */
//    @PostMapping("/deleteRepeatLogoutLogs")
    public Object deleteRepeatLogoutLogs() {
        return simpleStudentInfoService.deleteRepeatLogoutLogs();
    }
}

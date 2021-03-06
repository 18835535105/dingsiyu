package com.zhidejiaoyu.student.business.service.simple;

import com.zhidejiaoyu.common.vo.simple.studentInfoVo.ChildMedalVo;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 学生信息相关service
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
public interface SimpleStudentInfoService extends SimpleBaseService<Student> {

    /**
     * 验证原密码是否正确
     *
     * @param nowPassword   当前密码
     * @param oldPassword   原密码
     * @return
     */
    ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword);

    ServerResponse<String> updateStudentInfo(HttpSession session, Student student);

    /**
     * 获取学生膜拜数据
     *
     * @param session
     * @param type     1：我被膜拜的数据；2：我膜拜别人的数据
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<Map<String, Object>> getWorship(HttpSession session, Integer type, Integer pageNum, Integer pageSize);

    /**
     * 点击父勋章获取子勋章信息
     * @param session
     * @param stuId
     * @param medalId
     * @return
     */
    ServerResponse<ChildMedalVo> getChildMedal(HttpSession session, Long stuId, Long medalId);

    /**
     * 分页获取所有勋章信息
     *
     * @param session
     * @param stuId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<Map<String, Object>> getAllMedal(HttpSession session, Long stuId, Integer pageNum, Integer pageSize);

    /**
     * 打开/关闭背景音乐
     *
     * @param session
     * @param status    1:打开音乐；2：关闭音乐
     * @return
     */
    ServerResponse optBackMusic(HttpSession session, Integer status);

    /**
     * 获取学生背景音乐开关状态
     *
     * @param session
     * @return
     */
    ServerResponse<Map<String, Integer>> getBackMusicStatus(HttpSession session);

    Object deleteRepeatLogoutLogs();
}

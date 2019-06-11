package com.zhidejiaoyu.student.service.simple;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.simple.studentInfoVo.ChildMedalVo;
import com.zhidejiaoyu.common.Vo.simple.studentInfoVo.LevelVo;
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
public interface SimpleStudentInfoServiceSimple extends SimpleBaseService<Student> {

    /**
     * 学生完善个人信息，保存学生信息
     *
     * @param student
     * @param newPassword
     * @param oldPassword
     * @return
     */
    ServerResponse<String> saveStudentInfo(HttpSession session, Student student, String oldPassword, String newPassword);

    /**
     * 膜拜
     *
     * @param session
     * @param userId  被膜拜人的id
     * @return
     */
    ServerResponse<String> worship(HttpSession session, Long userId);

    /**
     *  学生退出学习页面，记录本次在学习页面学习时长
     * @param session
     * @param classify 学习模块(有效时长)，区分各个学习模块的时长，7：单元闯关测试；8：复习测试；9：已学测试；10：熟词测试；11：生词测试；
     *                       12：五维测试；13：任务课程；'14:单词辨音; 15:词组辨音; 16:单词认读; 17:词组认读; 18:词汇考点; 19:句型认读;
     *                       20:语法辨析; 21单词拼写; 22:词组拼写;
     * @param courseId
     * @param unitId
     * @param validTime 学生学习的有效时长，单位：秒
     */
    ServerResponse<String> calculateValidTime(HttpSession session, Integer classify, Long courseId, Long unitId, Long validTime);

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
     * 获取学生的等级信息
     *
     * @param session
     * @param stuId 为空时查看当前学生的等级信息；不为空时查看选中的学生的等级信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<LevelVo> getLevel(HttpSession session, Long stuId, Integer pageNum, Integer pageSize);

    /**
     * 分页获取学生已领取的勋章信息
     *
     * @param session
     * @param stuId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<String>> getMedalByPage(HttpSession session, Long stuId, Integer pageNum, Integer pageSize);

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

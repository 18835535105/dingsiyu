package com.zhidejiaoyu.student.business.service;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.vo.student.level.ChildMedalVo;
import com.zhidejiaoyu.common.vo.student.level.LevelVo;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.dto.EndValidTimeDto;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 学生信息相关service
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
public interface StudentInfoService extends BaseService<Student> {

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
     * 学生退出学习页面，记录本次在学习页面学习时长
     *
     * @param session
     */
    ServerResponse<String> calculateValidTime(HttpSession session, EndValidTimeDto dto);

    /**
     * 验证原密码是否正确
     *
     * @param nowPassword 当前密码
     * @param oldPassword 原密码
     * @return
     */
    ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword);

    /**
     * 获取学生的等级信息
     *
     * @param session
     * @param stuId    为空时查看当前学生的等级信息；不为空时查看选中的学生的等级信息
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
     * 点击父勋章获取子勋章信息
     *
     * @param session
     * @param stuId
     * @param medalId
     * @return
     */
    ServerResponse<ChildMedalVo> getChildMedal(HttpSession session, Long stuId, Long medalId);

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
     * 根据uuid查询学生信息
     *
     * @param uuid
     * @return
     */
    Student getByUuid(String uuid);

    /**
     * 根据openId查询学生信息
     *
     * @param openId
     * @return
     */
    Student getByOpenId(String openId);
}

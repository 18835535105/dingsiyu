package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.StudentSkin;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * <p>
 * 学生皮肤表 服务类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface StudentSkinService extends BaseService<StudentSkin> {

    /**
     * 添加皮肤
     * @param name
     * @param studentId
     * @param date
     * @return
     */
    int addStudentSkin(String name, Integer studentId, Date date, String imgUrl);


    /**
     * 获取皮肤以及皮肤碎片
     * @param session
     * @return
     */
    ServerResponse<Object> selSkinAndExhumation(HttpSession session);

    /**
     * 获取皮肤
     * @param session
     * @return
     */
    ServerResponse<Object> selSkin(HttpSession session);

    /**
     * 使用皮肤
     * @param session
     * @param skinInteger
     * @param dateInteger
     * @return
     */
    ServerResponse<Object> useSkin(HttpSession session, Integer skinInteger, Integer dateInteger, Integer type, String imgUrl);

    /**
     * 查找正在使用的皮肤
     * @param session
     * @return
     */
    ServerResponse<Object> selUseSkinById(HttpSession session);

    ServerResponse<Object> addStudentSkinByDiamond(HttpSession session, int number, int skinInteger, String imgUrl);

}

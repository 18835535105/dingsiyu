package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentSkin;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生皮肤表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface StudentSkinMapper extends BaseMapper<StudentSkin> {

    /**
     * 根据学生id获取学生皮肤
     * @param studentId
     * @return
     */
    List<StudentSkin> selSkinByStudentId(Long studentId);

    /**
     * 获取学生已拥有皮肤
     * @param studentId
     * @return
     */
    List<StudentSkin> selSkinByStudentIdIsHave(Long studentId);

    List<StudentSkin> selSkinByStudentIdAndEndTime(Long studentId);

    /**
     *  查询
     */
    StudentSkin selUseSkinByStudentId(Long studentId);

    /**
     * 根据studentid和skinname查找学生皮肤
     * @param skin
     * @return
     */
    StudentSkin selSkinBystudentIdAndName(StudentSkin skin);

    /**
     * 根据id修改使用状态和时间
     * @param studentSkin
     * @return
     */
    Integer updUseSkin(StudentSkin studentSkin);


    /**
     * 根据id查询正在使用的皮肤
     * @param studentId
     * @return
     */
    StudentSkin selUseSkinByStudentIdAndName(Long studentId);


    List<Map<String,Object>> selTrySkinAndHaveSkin(Long id);
}

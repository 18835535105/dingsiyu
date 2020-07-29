package com.dfdz.teacher.common;

/**
 * 系统常量
 *
 * @author zdjy
 * @date 2017年2月12日 下午9:42:53
 */
public interface Const {

    /**
     * 系统默认的管理员密码
     */
    String DEFAULT_PWD = "111111";

    /**
     * 管理员角色的名字
     */
    String ADMIN_NAME = "administrator";

    /**
     * 管理员id
     */
    Integer ADMIN_ID = 1;

    /**
     * 超级管理员角色id
     */
    Integer ADMIN_ROLE_ID = 1;

    /**
     * 接口文档的菜单名
     */
    String API_MENU_NAME = "接口文档";

    /**
     * 学校部门名称
     */
    String SCHOOL_DEPT_NAME = "学校";

    /**
     * 学校部门id
     */
    Integer SCHOOL_DEPT_ID = 28;

    /**
     * 教师角色名字
     */
    String TEACHER_NAME = "teacher";

    /**
     * 教师角色id
     */
    Integer TEACHER_ROLE_ID = 6;

    /**
     * 校长角色名
     */
    String SCHOOL_ADMIN = "school_admin";
    /**
     * 校长角色id
     */
    Integer SCHOOL_ADMIN_ROLE_ID = 9;

    /**
     * 学生角色名
     */
    String STUDENT = "student";
}

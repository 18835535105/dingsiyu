package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.ExerciseSheetBeforeClass;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 课前测试习题表 服务类
 * </p>
 *
 * @author zdjy
 * @since 2019-09-09
 */
public interface ExerciseSheetBeforeClassService extends BaseService<ExerciseSheetBeforeClass> {

    /**
     * 根据学段获取所有课程信息
     *
     * @param studyParagraph
     * @return
     */
    Object getAllCourse(String studyParagraph);

    Object getUnitByCourseId(Long courseId);

    /**
     * 获取小学单元评测
     *
     * @param session
     * @param jointNames 单元名称
     * @return
     */
    Object getSmallBeforeStudyUnitTest(HttpSession session, String[] jointNames);
}

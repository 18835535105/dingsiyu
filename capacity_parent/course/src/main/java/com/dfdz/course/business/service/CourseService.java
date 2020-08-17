package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.CourseNew;

import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:09:09
 */
public interface CourseService extends IService<CourseNew> {
    CourseNew getCourseById(String id);

    /**
     * 统计各个课程下单元个数
     *
     * @param courseIds
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    Map<Long, Integer> countUnitByIds(List<Long> courseIds, int type);

    /**
     * 当前版本中小于或等于当前年级的所有课程id
     *
     * @param version   版本
     * @param gradeList 年级
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    List<Long> getByGradeListAndVersionAndGrade(String version, List<String> gradeList, Integer type);

    /**
     * 获取当前课程的语法数据
     *
     * @param courseNews
     * @return
     */
    Map<Long, Map<String, Object>> getByCourseNews(List<CourseNew> courseNews);

    /**
     * 根据课程id批量获取课程信息
     *
     * @param courseIds
     * @return
     */
    List<CourseNew> getByIdsGroupByVersion(List<Long> courseIds);

    /**
     * 获取所有版本
     *
     * @param studyParagraph 学段
     *                       <ul>
     *                       <li>小学</li>
     *                       <li>初中</li>
     *                       <li>高中</li>
     *                       </ul>
     * @return
     */
    List<String> getAllVersion(String studyParagraph);

    /**
     * 使用版本，年级，标签 查看courseId
     * @param version
     * @param grade
     * @param label
     * @return
     */
    List<Integer> selectCourseIdByVersionAndGradeAndLabel(String version, String grade, String label);

    List<CourseNew> selectExperienceCourses();


    /**
     * 通过单元id查询当前课程所属学段
     *
     * @param unitId
     * @return
     */
    String getPhaseByUnitId(Long unitId);

    /**
     * 根据单元id查询单元与年级的关系
     *
     * @param unitIds
     * @return
     */
    List<GradeAndUnitIdDTO> getGradeAndLabelByUnitIds(List<Long> unitIds);

    String selectGradeByCourseId(Long courseId);

    CourseNew getByUnitId(Long unitId);

    List<Map<String, Object>> selectIdAndVersionByStudentIdByPhase(Long studentId, String phase);

    Map<Long, Map<String, Object>> selectUnitsWordSum(long courseId);

    List<Long> getIdsByVersion(String version);

    List<Long> getIdsByPhaseAndIds(List<String> phase, List<Long> courseIds);
}

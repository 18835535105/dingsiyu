package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
}

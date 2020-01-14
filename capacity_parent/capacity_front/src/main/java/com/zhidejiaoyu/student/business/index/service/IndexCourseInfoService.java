package com.zhidejiaoyu.student.business.index.service;

import com.zhidejiaoyu.common.pojo.CourseConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.dto.UnitInfoDTO;
import com.zhidejiaoyu.student.business.index.vo.course.CourseInfoVO;
import com.zhidejiaoyu.student.business.service.BaseService;

/**
 * @author: wuchenxi
 * @date: 2019/12/27 13:41:41
 */
public interface IndexCourseInfoService extends BaseService<CourseConfig> {

    /**
     * 获取各个年级课程数据
     *
     * @param type 1：单词；2：句型；3：语法；4：课文
     * @return
     */
    ServerResponse<CourseInfoVO> getStudyCourse(Integer type);

    /**
     * 获取当前单元下所有单元信息
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> getUnitInfo(UnitInfoDTO dto);
}

package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.mapper.CourseNewMapper;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.TokenUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 封装节点响应信息
 *
 * @author: wuchenxi
 * @date: 2020/1/3 10:34:34
 */
@Component
@SuppressWarnings("all")
public class PackageFlowVO {

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    /**
     * 封装响应信息
     *
     * @param studyFlowNew
     * @param student
     * @param unitId
     * @return
     */
    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        UnitNew unitNew = unitNewMapper.selectById(unitId);
        CourseNew courseNew = courseNewMapper.selectById(unitNew.getCourseId());
        String token = TokenUtil.getToken();
        HttpUtil.getHttpSession().setAttribute("token", token);

        return FlowVO.builder()
                .courseId(courseNew.getId())
                .courseName(courseNew.getCourseName())
                .id(studyFlowNew.getId())
                .modelName(studyFlowNew.getModelName())
                .unitId(unitNew.getId())
                .unitName(unitNew.getUnitName())
                .token(token)
                .petName(StringUtils.isEmpty(student.getPetName()) ? "大明白" : student.getPetName())
                .build();
    }
}

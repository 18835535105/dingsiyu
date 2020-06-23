package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.CourseNewMapper;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.TokenUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.index.service.impl.IndexCourseInfoServiceImpl;
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
     * 封装非语法响应信息
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

        String englishGrade = IndexCourseInfoServiceImpl.getGradeAndLabelEnglishName(courseNew.getGrade(), courseNew.getLabel());
        return FlowVO.builder()
                .courseId(courseNew.getId())
                .courseName(courseNew.getCourseName())
                .id(studyFlowNew.getId())
                .modelName(studyFlowNew.getModelName())
                .unitId(unitNew.getId())
                .unitName(unitNew.getUnitName())
                .token(token)
                .lastUnit(false)
                .englishGrade(englishGrade)
                .petName(StringUtils.isEmpty(student.getPetName()) ? "大明白" : student.getPetName())
                .build();
    }

    /**
     * 封装语法响应信息
     *
     * @param dto 参数需要：unitId，student，studyFlowNew,lastUnit
     * @return
     */
    public FlowVO packageSyntaxFlowVO(NodeDto dto) {

        Long unitId = dto.getUnitId();
        Student student = dto.getStudent();
        Boolean lastUnit = dto.getLastUnit();
        StudyFlowNew studyFlowNew = dto.getStudyFlowNew();
        UnitNew unitNew = unitNewMapper.selectById(unitId);
        CourseNew courseNew = courseNewMapper.selectById(unitNew.getCourseId());
        String token = TokenUtil.getToken();
        HttpUtil.getHttpSession().setAttribute("token", token);

        String englishGrade = IndexCourseInfoServiceImpl.getGradeAndLabelEnglishName(courseNew.getGrade(), courseNew.getLabel());
        return FlowVO.builder()
                .courseId(courseNew.getId())
                .courseName(courseNew.getCourseName())
                .id(studyFlowNew.getId())
                .modelName(studyFlowNew.getModelName())
                .unitId(unitNew.getId())
                .unitName(unitNew.getUnitName())
                .token(token)
                .lastUnit(lastUnit)
                .englishGrade(englishGrade)
                .unitIndex(unitNew.getUnitIndex())
                .petName(StringUtils.isEmpty(student.getPetName()) ? "大明白" : student.getPetName())
                .build();
    }
}

package com.zhidejiaoyu.student.syntax.learnmodel;

import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.StudentStudySyntaxMapper;
import com.zhidejiaoyu.common.mapper.SyntaxUnitMapper;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import com.zhidejiaoyu.common.pojo.SyntaxUnit;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 语法学习模块相关
 *
 * @author: wuchenxi
 * @Date: 2019/11/7 11:31
 */
@Component
public class LearnModelInfo {

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private LearnMapper learnMapper;

    /**
     * 保存语法下个需要学习的模块信息
     *
     * @param unitId
     * @param student
     * @param nextModelName 下一个模块名称
     * @return 语法学习模块信息
     */
    public StudentStudySyntax packageStudentStudySyntax(Long unitId, Student student, String nextModelName) {
        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (!Objects.isNull(studentStudySyntax)) {
            studentStudySyntax.setModel(nextModelName);
            studentStudySyntax.setUpdateTime(new Date());
            studentStudySyntaxMapper.updateById(studentStudySyntax);
        } else {
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            studentStudySyntax = StudentStudySyntax.builder()
                    .updateTime(new Date())
                    .model(nextModelName)
                    .unitId(unitId)
                    .studentId(student.getId())
                    .courseId(!Objects.isNull(syntaxUnit) ? syntaxUnit.getCourseId() : null)
                    .build();
            studentStudySyntaxMapper.insert(studentStudySyntax);
        }
        return studentStudySyntax;
    }

    /**
     * 将指定模块的学习记录置为已学习状态
     *
     * @param studentStudySyntax
     */
    public void updateLearnType(StudentStudySyntax studentStudySyntax) {
        List<Learn> learns = learnMapper.selectSyntaxByUnitIdAndStudyModel(studentStudySyntax);
        if (!CollectionUtils.isEmpty(learns)) {
            learns.parallelStream().forEach(learn -> {
                learn.setType(2);
                learnMapper.updateById(learn);
            });
        }
    }
}

package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentFlow;
import com.zhidejiaoyu.common.pojo.StudentFlowExample;
import java.util.List;

import com.zhidejiaoyu.common.pojo.StudyFlow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface StudentFlowMapper {
    int countByExample(StudentFlowExample example);

    int deleteByExample(StudentFlowExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudentFlow record);

    int insertSelective(StudentFlow record);

    List<StudentFlow> selectByExample(StudentFlowExample example);

    StudentFlow selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudentFlow record, @Param("example") StudentFlowExample example);

    int updateByExample(@Param("record") StudentFlow record, @Param("example") StudentFlowExample example);

    int updateByPrimaryKeySelective(StudentFlow record);

    int updateByPrimaryKey(StudentFlow record);

    @Update("update student_flow set current_flow_id = #{node} where student_id = #{studentId}")
    int updateFlowByStudentId(@Param("studentId") long studentId, @Param("node") long node);

    /**
     * 获取学生当前所学节点模块名
     *
     * @param id 学生id
     * @return
     */
    @Select("select b.model_name from student_flow a join study_flow b ON a.current_flow_id = b.id AND a.student_id = #{id}")
	String getStudentFlow(Long id);

    /**
     *
     * @param studentId
     * @param timeMachine   是否是时光机流程。0：普通流程；1：时光机流程
     * @param presentFlow   是否是当前流程 1：是；2：不是
     * @return
     */
    StudentFlow selectByStudentId(@Param("studentId") Long studentId, @Param("timeMachine") int timeMachine, @Param("presentFlow") int presentFlow);
}
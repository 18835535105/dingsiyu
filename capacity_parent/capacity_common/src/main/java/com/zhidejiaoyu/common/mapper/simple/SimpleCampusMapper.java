package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Campus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 校区信息 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-12
 */
public interface SimpleCampusMapper extends BaseMapper<Campus> {



    /**
     * 根据教师 id 查询其所管辖的校区
     *
     * @param teacherId
     * @return
     */
    Campus selectByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 查询校区个数
     *
     * @param name
     * @return
     */
    @Select("select count(id) from campus where name = #{name}")
    Integer countByName(@Param("name") String name);


    List<String> getSchoolName(@Param("longs") List<Long> longs) ;

    @Select("select name from campus where teacher_id =#{teacherId}")
    String selSchoolName(Long teacherId);


    @Select("select teacher_id from campus where name=#{schoolName}")
    Integer getTeacherIdBySchoolName(String schoolName);
}

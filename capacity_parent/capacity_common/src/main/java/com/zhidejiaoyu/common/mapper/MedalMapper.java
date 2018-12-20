package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.MedalExample;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface MedalMapper {
    int countByExample(MedalExample example);

    int deleteByExample(MedalExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Medal record);

    int insertSelective(Medal record);

    List<Medal> selectByExample(MedalExample example);

    Medal selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Medal record, @Param("example") MedalExample example);

    int updateByExample(@Param("record") Medal record, @Param("example") MedalExample example);

    int updateByPrimaryKeySelective(Medal record);

    int updateByPrimaryKey(Medal record);

    /**
     * 查询每个勋章大类含有的子勋章个数
     * @param ids
     * @return map key:父勋章名， value map key:父勋章名，value：子勋章个数
     */
    @MapKey("parentName")
    Map<String, Map<String, Long>> countChildByIds(@Param("ids") List<Long> ids);

    /**
     * 查询学生获取的所有勋章个数
     * @param studentId
     * @return map key:父勋章名， value map key:父勋章名，value：获取的子勋章个数
     */
    @MapKey("parentName")
    Map<String, Map<String, Long>> countGetCountByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据父勋章id查询其子勋章id的集合
     * @param parentId 父勋章id
     * @return
     */
    List<Medal> selectChildrenIdByParentId(@Param("parentId") int parentId);

    /**
     * 获取学生新领取的勋章图片路径
     *
     * @param student
     * @return
     */
    @Select("select child_img_url url from medal m, award a where m.id = a.medal_type and a.student_id = #{student.id} and get_flag = 1 order by a.id desc")
    List<String> selectLastMedalUrl(@Param("student") Student student);

    /**
     * 查询父id下的所有子id
     *
     * @param parentIds
     * @return
     */
    List<Long> selectAllIdsByParentIds(@Param("parentIds") List<Long> parentIds);

    /**
     * 获取已获取的勋章的大图片路径
     *
     * @param student
     * @return
     */
    List<String> selectHadMedalImgUrl(@Param("student") Student student);

    /**
     * 获取子勋章图片
     * 已经获取的勋章获取金色图片
     * 未获取的勋章获取灰色图片
     *
     * @param student
     * @param medalId
     * @return key： imgUrl，content(勋章说明文字)
     */
    List<Map<String, String>> selectChildrenInfo(@Param("student") Student student, @Param("medalId") long medalId);

    /**
     * 获取已获取的勋章的大图片路径
     *
     * @param student
     * @return
     */
    List<String> selectHadBigMedalImgUrl(@Param("student") Student student);

    /**
     * 获取勋章图片
     * 已经获取的勋章获取金色图片
     * 未获取的勋章获取灰色图片
     *
     * @param student
     * @return key： imgUrl，id(勋章id)
     */
    List<Map<String, Object>> selectMedalImgUrl(@Param("student") Student student);
}
package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SimpleMedalMapper extends BaseMapper<Medal> {
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
     * 获取勋章图片
     * 已经获取的勋章获取金色图片
     * 未获取的勋章获取灰色图片
     *
     * @param student
     * @return key： imgUrl，id(勋章id)
     */
    List<Map<String, Object>> selectMedalImgUrl(@Param("student") Student student);

    /**
     * 获取已获取的勋章的图片路径
     *
     * @param student
     * @return
     */
    List<String> selectHadMedalImgUrl(@Param("student") Student student);

    /**
     * 获取已获取的勋章的大图片路径
     *
     * @param student
     * @return
     */
    List<String> selectHadBigMedalImgUrl(@Param("student") Student student);

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
     * 查询父id下的所有子id
     *
     * @param parentIds
     * @return
     */
    List<Long> selectAllIdsByParentIds(@Param("parentIds") List<Long> parentIds);

    /**
     * 学生已经获取的勋章信息
     *
     * @param studentId
     * @return
     */
    List<Medal> selectHadByStudentId(@Param("studentId") Long studentId);
    List<Medal> selectByParentIds(@Param("parentIds") List<Long> parentIds);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.vo.ship.MedalStatusVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MedalMapper extends BaseMapper<Medal> {

    /**
     * 查询每个勋章大类含有的子勋章个数
     *
     * @param ids
     * @return map key:父勋章名， value map key:父勋章名，value：子勋章个数
     */
    @MapKey("parentName")
    Map<String, Map<String, Long>> countChildByIds(@Param("ids") List<Long> ids);

    /**
     * 查询学生获取的所有勋章个数
     *
     * @param studentId
     * @return map key:父勋章名， value map key:父勋章名，value：获取的子勋章个数
     */
    @MapKey("parentName")
    Map<String, Map<String, Long>> countGetCountByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据父勋章id查询其子勋章id的集合
     *
     * @param parentId 父勋章id
     * @return
     */
    List<Medal> selectChildrenIdByParentId(@Param("parentId") int parentId);

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

    /**
     * 通过父节点id查询父节点下所有勋章内容
     *
     * @param parentIds
     * @return
     */
    List<Medal> selectByParentIds(@Param("parentIds") List<Long> parentIds);

    /**
     * 获取学生勋章状态
     *
     * @param studentId
     * @return
     */
    List<MedalStatusVO> selectMedalStatusByStudentId(@Param("studentId") Long studentId);
}

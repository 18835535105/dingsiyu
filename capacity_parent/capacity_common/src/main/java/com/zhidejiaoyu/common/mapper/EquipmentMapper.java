package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Equipment;
import com.zhidejiaoyu.common.vo.ship.EquipmentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-27
 */
@Repository
public interface EquipmentMapper extends BaseMapper<Equipment> {

    /**
     * 查询学生已装备的飞船及装备信息
     *
     * @param studentId
     * @return <ul>
     * <li>key:type 类型</li>
     * <li>key:imgUrl 图片地址</li>
     * <li>key:durability 耐久度</li>
     * <li>key:commonAttack 普通攻击</li>
     * <li>key:sourceForceAttack 源力攻击</li>
     * <li>key:sourceForce 源力</li>
     * <li>key:hitRate 命中率</li>
     * <li>key:mobility 机动力</li>
     * </ul>
     */
    List<Map<String, Object>> selectUsedByStudentId(@Param("studentId") Long studentId);

    List<Map<String, Object>> selectByStudentId(@Param("studentId") Long studentId);

    List<Equipment> selectAll();


    List<Equipment> selectIdByTypeAndLevel(@Param("type") int type, @Param("level") int level);

    List<Equipment> selectByType(@Param("type") Integer type);

    /**
     * 获取正在使用的装备名称和级别
     *
     * @param studentId 学生id
     * @param type      1，飞船 2，武器 3，导弹 4，装甲
     * @return
     */
    EquipmentVo selectNameAndGradeByStudentId(@Param("studentId") Long studentId, @Param("type") int type);

    Equipment selectByName(@Param("name") String name);
}

package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Exhumation;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽取的碎片记录表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface SimpleExhumationMapper extends BaseMapper<Exhumation> {

    /**
     * 获取学生全部未使用 手套/花朵 碎片
     * @return
     */
    List<Exhumation> selExhumation(Integer studentId);

    /**
     * 获取皮肤碎片
     */
    List<Exhumation> selExhumationByStudentIdTOSkin(Long studentId);

    /**
     * 获取每个皮肤使用的碎片数量
     * @param studentId
     * @return
     */
    List<Map<String,Object>> selExhumationByStudentIdTOSkinState(Long studentId);
    /**
     * 查询使用的手套花朵碎片数量
     * @param studnetId
     * @return
     */
    @Select("select final_name finalName, count(id) count , name name from exhumation where student_id=#{studentId} and type!=3 and state = 1")
    List<Map<String,Object>> selExhumationByStudentId(Long studnetId);

    /**
     * 查询皮肤已使用碎片数量
     * @param studnetId
     * @return
     */
    @Select("select final_name finalName, count(id) count , name name from exhumation where student_id=#{studentId} and type=3 and state = 1")
    List<Map<String,Object>> selExhumationSkinByStudentId(Long studnetId);

    /**
     *  查询未使用的碎片
     * @return
     */
    @Select("select id from exhumation where  (type=1 or type=2) and state = 0 and student_id =#{studentId}")
    List<Integer> selExhumationId(Map<String, Object> map);

    /**
     *  查询未使用的碎片
     * @return
     */
    @Select("select id from exhumation where type=3 and state = 0 and student_id =#{studentId}")
    List<Integer> selSkinExhumationId(Map<String, Object> map);

    /**
     *根据id修改数据
     * @return
     */
    @Update("update exhumation set final_name=#{finalName},state=1 where id=#{id}")
    Integer updExhumationFinalNameById(Map<String, Object> map);

    @Update("update exhumation set state =2 where student_id=#{studentId} and final_name=#{name} and state = 1")
    Integer updExhumationFinalNameByStudentId(Map<String, Object> map);


    Integer selExhumationCountByNameAndFinalName(Map<String, Object> selMap);
}















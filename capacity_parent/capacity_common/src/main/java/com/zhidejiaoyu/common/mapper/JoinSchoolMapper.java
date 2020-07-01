package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.JoinSchool;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2018-11-07
 */
public interface JoinSchoolMapper extends BaseMapper<JoinSchool> {

    /**
     * 查询加盟校信息
     *
     * @param schoolAdminId
     * @return
     */
    JoinSchool selectByUserId(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 根据传来的地址进行学校搜索 因为是前台显示功能,只显示已成功加盟与预约加盟的学校.审核未通过、加盟成功后删除的学校不显示
     *
     * @param address
     * @return
     */
    List<JoinSchool> selectByLikeAddress(String address);

    @Select("select count(id) from join_school where reporting=2 and audit_status=2")
    Integer selCountByReporting();

    /**
     * 查询加盟学校的最大值
     *
     * @return
     */
    @Select("select max(reservation_number) from join_school")
    Integer selectMaxReservation();
}






















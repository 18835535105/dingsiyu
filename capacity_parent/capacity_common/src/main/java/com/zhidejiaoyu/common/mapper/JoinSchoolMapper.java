package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.JoinSchool;
import com.zhidejiaoyu.common.vo.joinSchool.JoinSchoolDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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


    List<JoinSchool> selBySchool(@Param("school") JoinSchoolDto joinSchoolDto, @Param("start") Integer startIndex, @Param("size") Integer pageSize);


    /**
     * 生成管理员账号成功后,添加user_id和加盟数量
     *
     * @param JoinSchoolDto
     * @return
     */
    @Update("update join_school set user_id = #{uuid} , joining_number =#{joiningNumber} ,reporting=#{reporting} where id=#{id}")
    Integer updSchoolStatusByUserId(JoinSchoolDto JoinSchoolDto);

    /**
     * 根据id修改审核状态与审核日期  未使用,使用时按需求修改
     * <p>
     * joinSchoolDto 传值对象
     *
     * @return
     */
    @Update("update join_school set audit_status=#{auditStatus} , date_of_audit=#{dateOfaudit}  where id=#{id}")
    Integer updSchoolStatus(JoinSchoolDto joinSchoolDto);


    /**
     * 查询加盟数量最大值
     *
     * @return
     */
    @Select("select Max(joining_number) from join_school")
    Integer selMaxjoiningNumber();
}






















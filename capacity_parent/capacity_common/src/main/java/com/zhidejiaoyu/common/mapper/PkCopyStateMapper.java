package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PkCopyState;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-03-17
 */
@Repository
public interface PkCopyStateMapper extends BaseMapper<PkCopyState> {

    /**
     * 查询当前校区当前副本的挑战状态
     *
     * @param schoolAdminId 校管id
     * @param copyId        副本id
     * @return
     */
    PkCopyState selectBySchoolAdminIdAndPkCopyBaseId(@Param("schoolAdminId") Integer schoolAdminId, @Param("copyId") Long copyId);

    PkCopyState selectByStudentIdAndBossId(@Param("studentId") Long id, @Param("bossId") Long bossId);

    /**
     * 删除校区副本挑战状态
     */
    void deleteSchoolCopy();

    /**
     * 查询本周学生校区副本挑战信息
     *
     * @param studentId
     * @param bossId
     * @return
     */
    @Select("select count(id)" +
            " from pk_copy_state" +
            " where student_id = #{studentId}" +
            "  and pk_copy_base_id =#{bossId}" +
            "  and to_days(update_time) = to_days(now())" +
            " group by pk_copy_base_id")
    Integer countThisWeekSchoolCopyByStudentIdAndPkCopyBaseId(@Param("studentId") Long studentId, @Param("bossId") Long bossId);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.RecycleBin;
import com.zhidejiaoyu.common.pojo.SchoolGoldFactory;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wuchenxi
 * @date 2018/7/16
 */
public interface RecycleBinMapper extends BaseMapper<RecycleBin> {

    /**
     * 批量新增记录
     *
     * @param recycleBins
     */
    void insertByList(@Param("recycleBins") List<RecycleBin> recycleBins);

    /**
     * 根据条件查询
     *
     * @param adminId
     * @param createTimeBegin
     * @param createTimeEnd
     * @param account
     * @param schoolName
     * @param squad
     * @param grade
     * @return
     */
    List<Map<String, Object>> selectRecycle(@Param("adminId") Long adminId, @Param("createTimeBegin") String createTimeBegin, @Param("createTimeEnd") String createTimeEnd, @Param("account") String account,
                                            @Param("schoolName") String schoolName, @Param("squad") String squad, @Param("grade") String grade);

    /**
     * 根据id查找回收站信息
     *
     * @param ids 回收站id集合
     * @return
     */
    List<RecycleBin> selectByIds(@Param("ids") Long[] ids);

    List<Long> selectDeleteStudentIdByDate(@Param("date") Date date);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIds);

    RecycleBin selectByStudentId(@Param("studentId") Long studentId);
}

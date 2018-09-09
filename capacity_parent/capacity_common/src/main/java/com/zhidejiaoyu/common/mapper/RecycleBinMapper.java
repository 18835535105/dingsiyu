package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Admin;
import com.zhidejiaoyu.common.pojo.RecycleBin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wuchenxi
 * @date 2018/7/16
 */
public interface RecycleBinMapper {

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

    /**
     * 批量更新删除状态为恢复状态
     *
     * @param ids   回收站id集合
     * @param admin
     */
    void updateDelStatusByIds(@Param("ids") Long[] ids, @Param("admin") Admin admin);
}

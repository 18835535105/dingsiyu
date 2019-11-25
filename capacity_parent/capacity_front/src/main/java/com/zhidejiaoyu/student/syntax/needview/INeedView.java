package com.zhidejiaoyu.student.syntax.needview;

import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * 获取需要复习的内容
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 10:07
 */
public interface INeedView {

    /**
     * 获取需要复习的内容响应数据
     *
     * @param dto
     * @return
     */
    ServerResponse getNeedView(NeedViewDTO dto);

    /**
     * 获取没有达到黄金记忆点的数据
     *
     * @param dto
     * @return
     */
    ServerResponse getNextNotGoldTime(NeedViewDTO dto);

    /**
     * 获取记忆强度，整数（不是百分比）
     *
     * @param studyCapacity
     * @return
     */
    default int getMemoryStrength(StudyCapacity studyCapacity) {
        return (int) Math.round(studyCapacity.getMemoryStrength() * 100);
    }
}

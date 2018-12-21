package com.zhidejiaoyu.student.service;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.pojo.CapacityWrite;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.vo.CapacityContentVo;
import com.zhidejiaoyu.student.vo.CapacityDigestVo;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 记忆追踪（慧追踪）模块
 *
 * @author wuchenxi
 * @date 2018年5月18日 上午9:18:52
 */
public interface CapacityService extends BaseService<CapacityWrite> {

    /**
     * 获取记忆追踪中摘要内容 只有单词或例句显示的页面，通过字体大小来确定复习紧迫程度的页面
     *
     * @param session
     * @param courseId
     * @param unitId
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @return
     */
    ServerResponse<CapacityDigestVo> getCapacityDigestVo(HttpSession session, Long courseId, Long unitId, String studyModel);

    /**
     * 记忆追踪中鼠标悬浮到指定单词或例句上时，页面展示的该单词或者例句的详细学习状况
     *
     * @param session
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @param courseId
     * @param unitId
     * @param id         单词/例句id
     * @return
     */
    ServerResponse<CapacityContentVo> getCapacityContent(HttpSession session, String studyModel, Long courseId,
                                                         Long unitId, Long id);

    /**
     * 获取记忆追踪列表页内容
     *
     * @param session
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @param courseId   课程id
     * @param unitId    单元id，当单元id=0 的时候根据课程id查询，否则根据单元id查询
     * @param pageNum    当前页码
     * @param pageSize   每页数据量
     * @return
     */
    @SuppressWarnings("rawtypes")
    ServerResponse<PageInfo> getCapacityList(HttpSession session, String studyModel, Long courseId, Long unitId, Integer pageNum, Integer pageSize);

    /**
     * 下载记忆追踪单词/例句内容
     * @param session
     * @param response
     * @param studyModel
     * @param courseId
     * @param unitId
     * @param pageNum
     * @param pageSize
     */
    void downloadCapacity(HttpSession session, HttpServletResponse response, String studyModel, Long courseId, Long unitId, Integer pageNum, Integer pageSize);

    /**
     * 取消进入慧追踪的提示框
     *
     * @param session
     * @return
     */
    ServerResponse<String> cancelTip(HttpSession session);
}

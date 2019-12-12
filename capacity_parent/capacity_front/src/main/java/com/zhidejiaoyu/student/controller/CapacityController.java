package com.zhidejiaoyu.student.controller;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.CapacityService;
import com.zhidejiaoyu.student.vo.CapacityContentVo;
import com.zhidejiaoyu.student.vo.CapacityDigestVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 记忆追踪（慧追踪）模块
 *
 * @author wuchenxi
 * @date 2018年5月18日 上午9:18:52
 */
@Controller
@RequestMapping("/capacity")
public class CapacityController extends BaseController {

    @Autowired
    private CapacityService capacityService;

    /**
     * 获取记忆追踪中摘要内容 只有单词或例句显示的页面，通过字体大小来确定复习紧迫程度的页面
     *
     * @param session
     * @param courseId
     * @param unitId
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @return
     */
    @ResponseBody
    @GetMapping("/getCapacityDigestVo")
    public ServerResponse<CapacityDigestVo> getCapacityDigestVo(HttpSession session, Long courseId, String unitId, String studyModel) {
        if (courseId == null || StringUtils.isBlank(studyModel) || unitId == null) {
            return ServerResponse.createByErrorMessage("参数非法");
        }
        return capacityService.getCapacityDigestVo(session, courseId, unitId, studyModel);
    }

    /**
     * 记忆追踪中鼠标悬浮到指定单词或例句上时，页面展示的该单词或者例句的详细学习状况
     *
     * @param session
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @param courseId   课程id
     * @param unitId     单元id
     * @param id         单词/例句id
     * @return
     */
    @ResponseBody
    @GetMapping("/getCapacityContent")
    public ServerResponse<CapacityContentVo> getCapacityContent(HttpSession session, String studyModel, Long courseId,
                                                                Long unitId, Long id) {
        if (StringUtils.isBlank(studyModel) || courseId == null || id == null) {
            return ServerResponse.createByErrorMessage("请求错误！");
        }
        return capacityService.getCapacityContent(session, studyModel, courseId, unitId, id);
    }


    /**
     * 下载记忆追踪单词/例句内容
     *
     * @param session
     * @param response
     * @param studyModel
     * @param courseId
     * @param pageNum
     * @param pageSize
     */
    @PostMapping("/downloadCapacity")
    public void downloadCapacity(HttpSession session, HttpServletResponse response, String studyModel, Long courseId,
                                 Long unitId,
                                 @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        if (checkDownloadParams(studyModel, courseId, unitId)) {
            capacityService.downloadCapacity(session, response, studyModel, courseId, unitId, pageNum, pageSize);
        }
    }

    private boolean checkDownloadParams(String studyModel, Long courseId, Long unitId) {
        return !StringUtils.isEmpty(studyModel) && courseId != null && unitId != null;
    }

    /**
     * 获取记忆追踪列表页内容
     *
     * @param session
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @param courseId   课程id
     * @param unitId     单元id，当单元id=0 的时候根据课程id查询，否则根据单元id查询
     * @param pageNum    当前页码
     * @param pageSize   每页数据量
     * @return
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @GetMapping("/getCapacityList")
    public ServerResponse<PageInfo> getCapacityList(HttpSession session, String studyModel, Long courseId, Long unitId,
                                                    @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Assert.notNull(unitId, "unitId can't be null!");
        if (StringUtils.isBlank(studyModel)) {
            return ServerResponse.createByErrorMessage("非法参数！");
        }
        return capacityService.getCapacityList(session, studyModel, courseId, unitId, pageNum, pageSize);
    }

    /**
     * 取消进入慧追踪的提示框
     *
     * @param session
     * @return
     */
    @ResponseBody
    @PostMapping("/cancelTip")
    public ServerResponse<Object> cancelTip(HttpSession session) {
        capacityService.cancelTip(session);
        return ServerResponse.createBySuccess();
    }
}

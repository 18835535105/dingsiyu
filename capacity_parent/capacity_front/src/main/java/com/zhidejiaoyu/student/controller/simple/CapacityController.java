package com.zhidejiaoyu.student.controller.simple;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.CapacityService;
import com.zhidejiaoyu.student.vo.CapacityContentVo;
import com.zhidejiaoyu.student.vo.CapacityDigestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 记忆追踪（慧追踪）模块
 *
 * @author wuchenxi
 * @date 2018年5月18日 上午9:18:52
 */
@Controller
@RequestMapping("/api/capacity")
public class CapacityController {

    @Autowired
    private CapacityService capacityService;


    /**
     * 获取慧追踪所有的单元需要复习的数量
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("getAllNeedReview")
    public ServerResponse<Map<Object,Object>> getAllNeedReview(HttpSession session) {

    	return capacityService.getNeedRebiewVo(session);
    }

    /**
     * 获取记忆追踪中摘要内容 只有单词或例句显示的页面，通过字体大小来确定复习紧迫程度的页面
     *
     * @param session
     * @param courseId
     * @param type
     * @return
     */
    @ResponseBody
    @GetMapping("/getCapacityDigestVo")
    public ServerResponse<CapacityDigestVo> getCapacityDigestVo(HttpSession session, Long courseId, Integer type) {
        if (courseId == null || type == null) {
            return ServerResponse.createByErrorMessage("请求错误！");
        }

        return capacityService.getCapacityDigestVo(session, courseId, type);
    }

    /**
     * 记忆追踪中鼠标悬浮到指定单词或例句上时，页面展示的该单词或者例句的详细学习状况
     *
     * @param session
     * @param type  1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @param courseId   课程id
     * @param id         单词/例句id
     * @return
     */
    @ResponseBody
    @GetMapping("/getCapacityContent")
    public ServerResponse<CapacityContentVo> getCapacityContent(HttpSession session, Integer type, Long courseId, Long id) {
        if (type == null || courseId == null || id == null) {
            return ServerResponse.createByErrorMessage("请求错误！");
        }
        return capacityService.getCapacityContent(session, type, courseId, id);
    }


    /**
     * 下载记忆追踪单词/例句内容
     *
     * @param session
     * @param response
     * @param type
     * @param courseId
     * @param pageNum
     * @param pageSize
     */
    @RequestMapping("/downloadCapacity")
    public void downloadCapacity(HttpSession session, HttpServletResponse response, Integer type, Long courseId,
                                 @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        if (courseId == null || type == null) {
            throw new RuntimeException("请求错误");
        }
        capacityService.downloadCapacity(session, response, type, courseId, pageNum, pageSize);
    }

    /**
     * 获取记忆追踪列表页内容
     *
     * @param session
     * @param type
     * @param courseId   课程id
     * @param pageNum    当前页码
     * @param pageSize   每页数据量
     * @return
     */
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @GetMapping("/getCapacityList")
    public ServerResponse<PageInfo> getCapacityList(HttpSession session, Integer type, Long courseId,
                                                    @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        if (courseId == null || type == null) {
            throw new RuntimeException("请求错误");
        }
        return capacityService.getCapacityList(session, type, courseId, pageNum, pageSize);
    }

    /**
     * 取消进入慧追踪的提示框
     *
     * @param session
     * @return
     */
    @ResponseBody
    @PostMapping("/cancelTip")
    public ServerResponse<String> cancelTip(HttpSession session) {
        return capacityService.cancelTip(session);
    }
}

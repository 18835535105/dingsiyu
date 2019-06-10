package com.zhidejiaoyu.student.controller.simple;


import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.DrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 抽奖记录表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Controller
@RequestMapping("/api/drawRecord")
public class DrawRecordController {

    @Autowired
    private DrawRecordService drawRecordService;

    /**
     * 抽奖添加
     *
     * @param session
     * @param type    编号
     * @param explain 抽奖说明
     * @param imgUrl  图片类地址
     * @return
     */
    @PostMapping("/AddAward")
    @ResponseBody
    public ServerResponse<Object> addAward(HttpSession session, int type, String explain, String imgUrl) {
        int[] i = drawRecordService.addAward(session, type, explain, imgUrl);
        int index = i[0];
        ServerResponse<Object> result;
        if (index == 3) {
            result = ServerResponse.createByError(300, "无能量");
        } else if (index > 0) {
            String str = "0";
            if (i[1] != 0) {
                str = i[1] + "";
            }
            result = ServerResponse.createBySuccess(str);

        } else {
            result = ServerResponse.createBySuccess(601, "失败");
        }
        return result;
    }

    /**
     * 分页查询抽奖记录
     *
     * @param session
     * @param page    页数
     * @param rows    每页显示的个数
     * @return
     */
    @PostMapping("/selDrawRecord")
    @ResponseBody
    public ServerResponse<Object> selDrawRecord(HttpSession session, Integer page, Integer rows) {
        return drawRecordService.selDrawRecordByStudentId(session, page, rows);
    }

    /**
     * 查询所有的抽奖一级抽奖消耗记录
     */
    @PostMapping("/selAllRecord")
    @ResponseBody
    public ServerResponse<Object> selAllRecord(HttpSession session) {
        return drawRecordService.selAllRecord(session);
    }

    /**
     * 查看是否是第一次抽取,并返回能量
     *
     * @param session
     * @return
     */
    @PostMapping("/getRecord")
    @ResponseBody
    public ServerResponse<Object> selAwardNow(HttpSession session) {
        return drawRecordService.selAwardNow(session);
    }

}

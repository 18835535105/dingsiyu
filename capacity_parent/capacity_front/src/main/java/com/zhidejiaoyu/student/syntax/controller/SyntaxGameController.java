package com.zhidejiaoyu.student.syntax.controller;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.controller.BaseController;
import com.zhidejiaoyu.student.syntax.service.SyntaxGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 语法游戏
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 17:16
 */
@Slf4j
@RestController
@RequestMapping("/syntaxGame")
public class SyntaxGameController extends BaseController {

    @Resource
    private SyntaxGameService syntaxGameService;

    /**
     * 获取超级语法小游戏数据
     *
     * @param unitId    超级语法单元id
     * @return
     */
    @GetMapping("/getSyntaxGame")
    public ServerResponse<Object> getSyntaxGame(Long unitId) {
        if (Objects.isNull(unitId)) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]获取超级语法小游戏参数错误，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            return ServerResponse.createByError(400, "unitId不能为空！");
        }

        return syntaxGameService.getSyntaxGame(unitId);
    }

    /**
     * 保存小游戏数据
     *
     * @return
     */
    @PostMapping("/saveSyntaxGame")
    public ServerResponse<Object> saveSyntaxGame(TestRecord testRecord) {
        if (this.checkSaveSyntaxGameParam(testRecord)) {
            return ServerResponse.createByError(400, "参数错误");
        }
        return syntaxGameService.saveSyntaxGame(testRecord);
    }

    private boolean checkSaveSyntaxGameParam(TestRecord testRecord) {
        if (Objects.isNull(testRecord.getCourseId())) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]保存超级语法小游戏参数错误，courseId=null", student.getId(), student.getAccount(), student.getStudentName());
            return true;
        }

        if (Objects.isNull(testRecord.getUnitId())) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]保存超级语法小游戏参数错误，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            return true;
        }

        if (Objects.isNull(testRecord.getPoint())) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]保存超级语法小游戏参数错误，point=null", student.getId(), student.getAccount(), student.getStudentName());
            return true;
        }
        return false;
    }
}

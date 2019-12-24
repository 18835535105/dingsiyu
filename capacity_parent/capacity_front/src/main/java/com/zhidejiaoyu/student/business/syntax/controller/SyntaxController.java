package com.zhidejiaoyu.student.business.syntax.controller;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.syntax.service.LearnSyntaxService;
import com.zhidejiaoyu.student.business.syntax.service.SyntaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 超级语法
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 11:34
 */
@Slf4j
@RestController
@RequestMapping("/syntax")
public class SyntaxController extends BaseController {

    @Resource
    private SyntaxService syntaxService;

    @Resource(name = "learnSyntaxService")
    private LearnSyntaxService learnSyntaxService;

    @Resource(name = "selectSyntaxService")
    private LearnSyntaxService selectSyntaxService;

    @Resource(name = "writeSyntaxService")
    private LearnSyntaxService writeSyntaxService;

    /**
     * 获取学生学习课程
     *
     * @param session
     * @return
     */
    @RequestMapping("/getStudyCourse")
    public Object getStudyCourse(HttpSession session) {
        return syntaxService.getStudyCourse(session);
    }


    /**
     * 获取学生当前单元需要学习的模块名称
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getSyntaxNode")
    public ServerResponse<Object> getSyntaxNode(Long unitId) {
        if (unitId == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在请求获取语法学习模块的接口时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return syntaxService.getSyntaxNode(unitId);
    }

    /**
     * 获取当前单元的学语法数据
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getLearnSyntax")
    public ServerResponse<Object> getLearnSyntax(Long unitId) {
        if (unitId == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在获取学语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return learnSyntaxService.getLearnSyntax(unitId);
    }

    /**
     * 保存学语法数据
     *
     * @param known 是否知道 true：知道；false：不知道
     * @return
     */
    @PostMapping("/saveLearnSyntax")
    public ServerResponse<Object> saveLearnSyntax(Learn learn, Boolean known) {
        if (learn.getUnitId() == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在保存学语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return learnSyntaxService.saveLearnSyntax(learn, known);
    }

    /**
     * 获取选语法数据
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getSelectSyntax")
    public ServerResponse<Object> getSelectSyntax(Long unitId) {
        if (unitId == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在获取选语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return selectSyntaxService.getLearnSyntax(unitId);
    }

    /**
     * 保存选语法数据
     *
     * @param learn
     * @param known
     * @return
     */
    @PostMapping("/saveSelectSyntax")
    public ServerResponse<Object> saveSelectSyntax(Learn learn, Boolean known) {
        if (learn.getUnitId() == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在保存选语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return selectSyntaxService.saveLearnSyntax(learn, known);
    }

    /**
     * 获取选语法数据
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getWriteSyntax")
    public ServerResponse<Object> getWriteSyntax(Long unitId) {
        if (unitId == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在获取写语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return writeSyntaxService.getLearnSyntax(unitId);
    }

    /**
     * 保存写语法数据
     *
     * @param learn
     * @param known
     * @return
     */
    @PostMapping("/saveWriteSyntax")
    public ServerResponse<Object> saveWriteSyntax(Learn learn, Boolean known) {
        if (learn.getUnitId() == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在保存写语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return writeSyntaxService.saveLearnSyntax(learn, known);
    }
}
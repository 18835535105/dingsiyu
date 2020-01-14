package com.zhidejiaoyu.student.business.syntax.controller;

import com.zhidejiaoyu.common.dto.syntax.SaveSyntaxDTO;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.syntax.service.LearnSyntaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @Resource(name = "learnSyntaxService")
    private LearnSyntaxService learnSyntaxService;

    @Resource(name = "selectSyntaxService")
    private LearnSyntaxService selectSyntaxService;

    @Resource(name = "writeSyntaxService")
    private LearnSyntaxService writeSyntaxService;

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
     * @param dto
     * @return
     */
    @PostMapping("/saveLearnSyntax")
    public ServerResponse<Object> saveLearnSyntax(SaveSyntaxDTO dto) {
        if (dto.getUnitId() == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在保存学语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return learnSyntaxService.saveLearnSyntax(dto);
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
     * @param dto
     * @return
     */
    @PostMapping("/saveSelectSyntax")
    public ServerResponse<Object> saveSelectSyntax(SaveSyntaxDTO dto) {
        if (dto.getUnitId() == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在保存选语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return selectSyntaxService.saveLearnSyntax(dto);
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
     * @param dto
     * @return
     */
    @PostMapping("/saveWriteSyntax")
    public ServerResponse<Object> saveWriteSyntax(SaveSyntaxDTO dto) {
        if (dto.getUnitId() == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在保存写语法数据时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return writeSyntaxService.saveLearnSyntax(dto);
    }
}

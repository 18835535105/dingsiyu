package com.zhidejiaoyu.student.business.index.service.impl;

import com.zhidejiaoyu.common.mapper.CapacityStudentUnitMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentUnitMapper;
import com.zhidejiaoyu.common.pojo.CapacityStudentUnit;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudyPlan;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.ModelService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2019/12/21 14:03:03
 */
@Service
public class ModelServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ModelService {

    @Resource
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Resource
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

    @Resource
    private RedisOpt redisOpt;

    @Override
    public ServerResponse<Map<String, Boolean>> getModelStatus(HttpSession session, Integer type) {
        Long studentId = super.getStudentId(session);
        switch (type) {
            case 1:
                // 智慧单词
                return this.judgeMemoryWord(studentId);
            case 2:
            case 3:
                // 句型或课文
                return this.judgeSentenceOrText(studentId, type);
            case 4:
                // 字母或音标
                return this.judgeLetterOrSymbol(studentId);
            case 5:
                // 绝招好课
                return this.judgeGoodClasses(studentId);
            case 6:
                // 阅读
                return this.judgeRead(studentId);
            case 7:
                // 语法
                return this.judgeSyntax(studentId);
            case 8:
                //智能流程
                return this.judgeTestBeforeStudy(studentId);
            default:
                Map<String, Boolean> map = new HashMap<>(16);
                map.put("isHave", false);
                return ServerResponse.createBySuccess(map);
        }
    }

    /**
     * 查看智能流程
     *
     * @param studentId
     * @return
     */
    private ServerResponse<Map<String, Boolean>> judgeTestBeforeStudy(Long studentId) {
        Map<String, Boolean> map = new HashMap<>(16);
        map.put("isHave", redisOpt.getTestBeforeStudy(studentId));
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断是否有语法课程
     *
     * @param studentId
     * @return
     */
    private ServerResponse<Map<String, Boolean>> judgeSyntax(Long studentId) {
        Map<String, Boolean> map = new HashMap<>(16);
        int i = studentStudyPlanMapper.countByStudentId(studentId, 7);
        map.put("isHave", i > 0);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断是否有阅读课程
     *
     * @param studentId
     * @return
     */
    private ServerResponse<Map<String, Boolean>> judgeRead(Long studentId) {
        Map<String, Boolean> map = new HashMap<>(16);
        StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selReadByStudentId(studentId);
        map.put("isHave", studentStudyPlan != null);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断是否有绝招好课课程
     *
     * @param studentId
     * @return
     */
    public ServerResponse<Map<String, Boolean>> judgeGoodClasses(Long studentId) {
        Map<String, Boolean> map = new HashMap<>(16);
        int count = simpleStudentUnitMapper.countAllUnlockByStudentId(studentId);
        map.put("isHave", count > 0);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断是否有字母或者音标课程
     *
     * @param studentId
     * @return
     */
    private ServerResponse<Map<String, Boolean>> judgeLetterOrSymbol(Long studentId) {
        Map<String, Boolean> map = new HashMap<>(16);
        StudentStudyPlan letterPlan = studentStudyPlanMapper.selSymbolByStudentId(studentId);
        if (letterPlan != null) {
            map.put("isHave", true);
            return ServerResponse.createBySuccess(map);
        }

        StudentStudyPlan symbolPlan = studentStudyPlanMapper.selLetterByStudentId(studentId);
        if (symbolPlan != null) {
            map.put("isHave", true);
            return ServerResponse.createBySuccess(map);
        }

        map.put("isHave", false);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断是否有句型或者课文课程
     *
     * @param studentId
     * @param type
     * @return
     */
    private ServerResponse<Map<String, Boolean>> judgeSentenceOrText(Long studentId, Integer type) {
        Map<String, Boolean> map = new HashMap<>(16);
        int count = studentStudyPlanMapper.countByStudentId(studentId, type);
        map.put("isHave", count > 0);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断是否有智慧单词课程
     *
     * @param studentId
     * @return
     */
    public ServerResponse<Map<String, Boolean>> judgeMemoryWord(Long studentId) {
        Map<String, Boolean> map = new HashMap<>(16);
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 1);
        map.put("isHave", capacityStudentUnit != null);
        return ServerResponse.createBySuccess(map);
    }
}

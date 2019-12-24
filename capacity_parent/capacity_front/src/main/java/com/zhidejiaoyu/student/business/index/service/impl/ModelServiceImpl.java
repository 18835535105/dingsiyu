package com.zhidejiaoyu.student.business.index.service.impl;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentUnitMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.ModelService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
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
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

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
     * @param studentId
     * @return
     */
    private ServerResponse<Map<String, Boolean>> judgeTestBeforeStudy(Long studentId) {
        //1判断数据是否放入缓存中
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(studentId);
        boolean falg = redisOpt.getTestBeforeStudy(studentId, expansion.getPhase());
        boolean isHave=false;
        if (falg) {
            isHave = true;
        } else {
            List<TestRecord> testRecords =
                    testRecordMapper.selectListByGenre(studentId, GenreConstant.TEST_BEFORE_STUDY);
            boolean isStudy = false;
            for (TestRecord testRecord : testRecords) {
                String explain = testRecord.getExplain();
                if (explain.equals(expansion.getPhase())) {
                    isStudy = true;
                }
            }
            if (isStudy) {
                redisOpt.addTestBeforeStudy(studentId, expansion.getPhase());
                isHave = true;
            }
        }
        Map<String, Boolean> map = new HashMap<>(16);
        map.put("isHave", isHave);
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

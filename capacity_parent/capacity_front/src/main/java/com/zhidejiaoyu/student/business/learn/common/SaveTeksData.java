package com.zhidejiaoyu.student.business.learn.common;

import com.zhidejiaoyu.common.config.RedisConfig;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feignclient.course.CenterTeksFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.service.TeksService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.service.impl.TeksServiceImpl;
import com.zhidejiaoyu.student.common.CurrentDayOfStudyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Component
@Slf4j
public class SaveTeksData extends BaseServiceImpl<LearnNewMapper, LearnNew> {

    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private TeksService teksService;
    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private SaveData saveData;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private CourseFeignClient courseFeignClient;
    @Resource
    private CenterTeksFeignClient centerTeksFeignClient;

    private static final String TEKS = "TEKS";

    public Object getStudyModel(Long unitId, Long studentId, Integer easyOrHard, Integer type) {
        //查看课文数据是否保存过
      /*  List<Long> longs = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, studentId, studyModel);

        if (longs.size() > 0) {
            return super.toUnitTest();
        }*/
        //获取单元下需要学习的group
        //获取当前单元下的learnId
        int modelType = 3;
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, unitId, easyOrHard, modelType);
        if (type.equals(11)) {
            return getTeksAuditionData(unitId, learnNews.getGroup());
        } else if (type.equals(12)) {
            return getTextTraining(unitId, learnNews.getGroup(), studentId);
        } else if (type.equals(13)) {
            return breakThroughTheText(unitId, learnNews.getGroup());
        }
        return null;
    }

    private Object breakThroughTheText(Long unitId, Integer group) {
        List<TeksNew> teks = centerTeksFeignClient.selTeksByUnitIdAndGroup(unitId, group);
        return teksService.getTeks(teks);
    }

    private Object getTextTraining(Long unitId, Integer group, Long studentId) {
        Map<String, Object> map = new HashMap<>();
        //获取当前单元当前group数据
        List<TeksNew> teks = centerTeksFeignClient.selTeksByUnitIdAndGroup(unitId, group);
        map.put("chooseTeks", getChooseTeks(teks, unitId, studentId));
        map.put("writeTeks", getWriteTeks(teks));
        return ServerResponse.createBySuccess(map);
    }

    private Object getWriteTeks(List<TeksNew> listTeks) {
        List<Object> resultTeks = new ArrayList<>();
        for (TeksNew teksNew : listTeks) {
            teksService.getListTeks(resultTeks, teksNew);
        }
        return resultTeks;
    }

    private Object getChooseTeks(List<TeksNew> teks, Long unitId, Long studentId) {
        if (teks.size() > 0) {
            //返回的集合
            List<Map<String, Object>> resultList = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            //遍历数据
            for (TeksNew teks1 : teks) {
                //将遍历的数据放入到
                Map<String, Object> map = new HashMap<>();
                map.put("chinese", teks1.getParaphrase());
                map.put("pronunciation", baiduSpeak.getSentencePath(teks1.getSentence()));
                map.put("id", teks1.getId());
                String[] sentenceList = teks1.getSentence().trim().split(" ");
                teksService.getList(sentenceList, map);
                resultList.add(map);
            }
            Integer integer = getStudyMaxPoint(unitId, studentId);
            if (integer == null) {
                resultMap.put("maxScore", 0);
            } else {
                resultMap.put("maxScore", integer);
            }
            resultMap.put("list", resultList);
            resultMap.put("number", teks.size());
            return ServerResponse.createBySuccess(resultMap);
        }
        return null;
    }

    private Integer getStudyMaxPoint(Long unitId, Long studentId) {
        Map<String, Object> selMap = new HashMap<>();
        selMap.put("unitId", unitId);
        selMap.put("studentId", studentId);
        selMap.put("model", "课文默写测试");
        selMap.put("genre", null);
        return testRecordMapper.selectMaxPointByUnitStudentModel(selMap);
    }

    private Object getTeksAuditionData(Long unitId, Integer group) {
        List<TeksNew> teks = centerTeksFeignClient.selTeksByUnitIdAndGroup(unitId, group);
        if (teks.size() > 0) {
            List<TeksNew> resultTeks = new ArrayList<>();
            for (TeksNew teks1 : teks) {
                teks1.setPronunciation(baiduSpeak.getSentencePath(teks1.getSentence()));
                teks1.setSentence(TeksServiceImpl.replace(teks1.getSentence()));
                resultTeks.add(teks1);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }
        return null;
    }

    public void insertLearnExtend(Long flowId, Long unitId, Student student, String studyModel, Integer easyOrHard, Integer model) {
        StudyFlowNew flow = saveData.getCurrentStudyFlowById(flowId);
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, easyOrHard, model);
        LearnExtend learnExtend = new LearnExtend();
        learnExtend.setStudyModel(studyModel);
        learnExtend.setLearnId(learnNew.getId());
        learnExtend.setStudyCount(0);
        learnExtend.setLearnTime(new Date());
        learnExtend.setUpdateTime(new Date());
        learnExtend.setFlowName(flow.getFlowName());
        if (student.getTeacherId() != null) {
            Integer teacherId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
            if (teacherId != null) {
                learnExtend.setSchoolAdminId(teacherId.longValue());
            }
        }
        learnExtend.setFirstIsKnow(1);
        learnExtendMapper.insert(learnExtend);
    }

    public void saveStudy(HttpSession session, Long unitId, Long flowId, String studyModel, Integer easyOrHard) {
        Student student = getStudent(session);
        insertLearnExtend(flowId, unitId, student, studyModel, easyOrHard, 3);
    }
}

package com.zhidejiaoyu.student.business.learn.service.impl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.UnitSentenceNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 语法游戏界面
 */
@Service(value = "sentencePatternGameService")
@Slf4j
public class SentencePatternGameServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {

    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;
    @Resource
    private SentenceMapper sentenceMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    private Integer modelType = 2;
    private Integer easyOrHard = 1;

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Integer group;
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<Map<String, Object>> getMaps;
        Map<String, Object> returnMap = new HashMap<>();
        List<Map<String, Object>> returnList = new ArrayList<>();
        //获取当前单元下的group
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, unitId, easyOrHard, modelType);
        if (learnNews == null) {
            group = 1;
        } else {
            group = learnNews.getGroup();
        }
        //获取题目
        List<Map<String, Object>> lookMaps = unitSentenceNewMapper.selectSentenceAndChineseByUnitIdAndGroup(unitId, group);
        if (lookMaps.size() > 0) {
            if (lookMaps.size() < 5) {
                getMaps = lookMaps;
                while (getMaps.size() < 5) {
                    for (Map<String, Object> map : lookMaps) {
                        if (getMaps.size() < 5) {
                            getMaps.add(map);
                        } else {
                            break;
                        }
                    }
                }
            } else {
                getMaps = lookMaps.subList(0, 5);
            }
            //获取干扰项
            List<Long> sentenIds = new ArrayList<>();
            getMaps.forEach(map -> {
                sentenIds.add(Long.parseLong(map.get("id").toString()));
            });
            //获取不在sentenIds中的干扰项
            List<String> strings = sentenceMapper.selectNotIds(sentenIds);
            StudentExpansion expansion = studentExpansionMapper.selectByStudentId(studentId);
            if (expansion.getPhase() == null) {
                returnMap.put("phase", "小学");
            } else {
                returnMap.put("phase", expansion.getPhase());
            }
            Collections.shuffle(getMaps);
            for (Map<String, Object> map : getMaps) {
                String chinese = map.get("chinese").toString();
                String replace = chinese.replace("*", "");
                Map<String, Object> reMap = new HashMap<>();
                reMap.put("subject", replace);
                Collections.shuffle(strings);
                List<String> answers = strings.subList(0, 3);
                List<String> answersList = new ArrayList<>();
                String sentence = map.get("sentence").toString().replace("#", " ").replace("$", "");
                answersList.add(sentence);
                answers.forEach(answer -> answersList.add(answer.replace("#", " ").replace("$", "")));
                Map<String, Boolean> answerMap = new HashMap<>();
                answersList.forEach(answer->{
                    if(answer.equals(sentence)){
                        answerMap.put(answer,true);
                    }else{
                        answerMap.put(answer,false);
                    }
                });
                reMap.put("answer", answerMap);
                returnList.add(reMap);
            }
            returnMap.put("list", returnList);
            return returnMap;
        }

        return null;
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId) {
        return null;
    }
}

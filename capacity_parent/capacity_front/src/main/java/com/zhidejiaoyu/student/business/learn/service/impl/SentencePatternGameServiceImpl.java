package com.zhidejiaoyu.student.business.learn.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.common.constant.PetMP3Constant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.study.TestPointUtil;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 句型游戏界面
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
    private StudentMapper studentMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Autowired
    private PetSayUtil petSayUtil;
    private Integer modelType = 2;
    private Integer easyOrHard = 1;

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Integer group;
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<Map<String, Object>> getMaps =new ArrayList<>();
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
                getMaps.addAll(lookMaps);
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
                answersList.forEach(answer -> {
                    if (answer.equals(sentence)) {
                        answerMap.put(answer, true);
                    } else {
                        answerMap.put(answer, false);
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
        Student student = getStudent(session);
        Integer gold = 0;
        Integer enger = 0;
        if (total == 100) {
            gold = 5;
            enger = 3;
        }
        if (total >= 60) {
            gold = 3;
            enger = 2;
        }
        student.setSystemGold(student.getSystemGold() + gold);
        student.setEnergy(student.getEnergy() + enger);
        studentMapper.updateById(student);
        super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在游戏《" + "句型游戏"
                + "》中奖励#" + gold + "#枚金币" + "单元id:" + unitId);
        Map<String, Object> resultMap = new HashMap<>();
        if (total < PointConstant.EIGHTY) {
            resultMap.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
            resultMap.put("text", "很遗憾，闯关失败，再接再厉。");
            resultMap.put("backMsg", new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(total), "的同学，继续努力吧！"});
        } else if (total < PointConstant.NINETY) {
            resultMap.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
            resultMap.put("text", "闯关成功，独孤求败！");
            resultMap.put("backMsg", new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(total), "的同学，再接再励！"});
        } else {
            resultMap.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
            resultMap.put("text", "恭喜你刷新了纪录！");
            resultMap.put("backMsg", new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(total), "的同学，再接再励！"});
        }
        resultMap.put("energy", enger);
        resultMap.put("gold", gold);
        resultMap.put("point", total);
        resultMap.put("imgUrl", AliyunInfoConst.host + student.getPartUrl());
        return resultMap;
    }
}

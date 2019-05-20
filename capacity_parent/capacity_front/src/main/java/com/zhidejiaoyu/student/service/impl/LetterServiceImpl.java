package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Service
public class LetterServiceImpl extends BaseServiceImpl<LetterMapper, Letter> implements LetterService {

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;
    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;
    @Autowired
    private LetterUnitMapper letterUnitMapper;
    @Autowired
    private LearnMapper learnMapper;
    @Autowired
    private LetterPairMapper letterPairMapper;
    @Autowired
    private LetterMapper letterMapper;
    @Autowired
    private LetterWriteMapper letterWriteMapper;
    @Autowired
    private TestRecordMapper testRecordMapper;
    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private LetterVocabularyMapper letterVocabularyMapper;

    /**
     * 获取字母单元
     *
     * @param session
     * @return
     */
    @Override
    public Object getLetterUnit(HttpSession session) {
        Long studentId = getStudentId(session);
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selLetterByStudentId(studentId);
        Map<String, Object> map = new HashMap<>();
        if (capacityStudentUnit != null) {
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterUnit(capacityStudentUnit.getStartunit(), capacityStudentUnit.getEndunit());
            map.put("study", capacityStudentUnit.getUnitId());
            map.put("list", letterUnits);
        } else {
            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selLetterByStudentId(studentId);
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterUnit(studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
            map.put("study", studentStudyPlan.getStartUnitId());
            map.put("list", letterUnits);
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Object getLetterUnitStatus(HttpSession session, Long unitId) {
        Long studentId = getStudentId(session);
        Map<String, Object> map = new HashMap<>();
        if (unitId == null) {
            CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selLetterByStudentId(studentId);
            if (capacityStudentUnit != null) {
                unitId = capacityStudentUnit.getStartunit();
            } else {
                StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selLetterByStudentId(studentId);
                if (studentStudyPlan != null) {
                    unitId = studentStudyPlan.getStartUnitId();
                }
            }
        }
        if (unitId != null) {
            //查看单词播放器是否学习
            Integer letterCount = letterMapper.selLetterCountById(unitId);
            Integer letterListen = learnMapper.selLetterLearn(unitId, studentId, "字母播放器");
            map.put("letterListen", true);
            if (letterListen != null && letterListen >= letterCount) {
                map.put("letterPair", true);
                //查看字母配对是否学习
                Integer letterListenCount = learnMapper.selLetterLearn(unitId, studentId, "字母配对");
                if (letterListenCount != null && letterCount <= letterListenCount) {
                    map.put("letterWrite", true);
                    //查看字母听写是否完成
                    Integer letterWriteCount = learnMapper.selLetterLearn(unitId, studentId, "字母听写");
                    if (letterWriteCount != null && letterCount <= letterWriteCount) {
                        map.put("LettersBreakThrough", true);
                        Integer testPoint = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 10);
                        if (testPoint == 100) {
                            map.put("LetterPosttest", true);
                        } else {
                            map.put("LetterPosttest", false);
                        }
                    } else {
                        map.put("LettersBreakThrough", false);
                        map.put("LetterPosttest", false);
                    }
                } else {
                    map.put("letterWrite", false);
                    map.put("LettersBreakThrough", false);
                    map.put("LetterPosttest", false);
                }
            } else {
                map.put("letterPair", false);
                map.put("letterWrite", false);
                map.put("LettersBreakThrough", false);
                map.put("LetterPosttest", false);
            }
            CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selLetterByStudentId(studentId);
            if (capacityStudentUnit == null || unitId.equals(capacityStudentUnit.getUnitId())) {
                StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selLetterSudyByStudentAndUnitId(studentId, unitId);
                if (capacityStudentUnit == null) {
                    capacityStudentUnit = new CapacityStudentUnit();
                }
                capacityStudentUnit.setEndunit(studentStudyPlan.getEndUnitId());
                capacityStudentUnit.setStartunit(studentStudyPlan.getStartUnitId());
                capacityStudentUnit.setUnitId(unitId);
                if (capacityStudentUnit.getId() == null || capacityStudentUnit.getId().equals("")) {
                    capacityStudentUnitMapper.insert(capacityStudentUnit);
                } else {
                    capacityStudentUnitMapper.updateById(capacityStudentUnit);
                }
            }
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createBySuccess(new HashMap<>());
    }

    @Override
    public Object getLetterListen(Long unitId, HttpSession session) {
        Long studentId = getStudentId(session);
        StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selLetterSudyByStudentAndUnitId(studentId, unitId);
        if (studentStudyPlan == null) {
            return ServerResponse.createByError(400, "您没有当前课程请按正确路径进入");
        }
        List<Letter> byUnitId = letterMapper.getByUnitId(unitId);
        Map<String, Object> map = new HashMap<>();
        map.put("list", byUnitId);
        map.put("total", byUnitId.size());
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Object saveLetterListen(Player player, HttpSession session) {
        Long studentId = getStudentId(session);
        int i = playerMapper.selectByType(studentId, player.getUnitId(), 4, player.getWordId());
        if (i == 0) {
            player.setStudentId(studentId);
            player.setType(4);
            player.setUpdateTime(new Date());
            player.setLearnCount(1);
            playerMapper.insert(player);
        } else {
            player.setStudentId(studentId);
            player.setType(4);
            player.setUpdateTime(new Date());
            player.setLearnCount(player.getLearnCount() + 1);
            playerMapper.updateById(player);
        }
        Learn learn = learnMapper.selLetter(studentId, player.getWordId(), player.getUnitId());
        Date date = new Date();
        if (learn == null) {
            learn = new Learn();
            learn.setStudentId(studentId);
            learn.setStudyModel("字母播放器");
            learn.setStatus(1);
            learn.setLearnTime(date);
            learn.setUpdateTime(date);
            learn.setStudyCount(1);
            learn.setType(4);
            learnMapper.insert(learn);
        } else {
            learn.setUpdateTime(date);
            learn.setStudyCount(learn.getLearnCount() + 1);
            learnMapper.updateById(learn);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public Object getLetterPair(Long unitId, HttpSession session) {
        Long studentId = getStudentId(session);
        //查看是否已经学习完当前的模块
        Integer letterPairCount = letterPairMapper.selCountStudyLetter(unitId, studentId);
        Integer countByUnitId = letterMapper.selLetterCountById(unitId);
        if (countByUnitId.equals(letterPairCount)) {
            letterPairMapper.deleteByUnitAndStudent(unitId, studentId);
            learnMapper.updLetterPair(studentId, unitId, "字母配对");
        }
        Map<String, Object> map = new HashMap<>();
        //查看当前单元已经学过的单词
        List<Long> longs = letterPairMapper.selAllStudyLetter(unitId, studentId);
        Letter studyLetter = letterMapper.getStudyLetter(unitId, longs);
        //随机获取字母
        List<Letter> threeLetter = letterMapper.getThreeLetter(studyLetter.getId());
        Integer integer = letterPairMapper.selCountStudyLetter(unitId, studentId);
        map.put("total", countByUnitId);
        if (integer != null) {
            integer += 1;
        } else {
            integer = 1;
        }
        map.put("plan", integer);
        //获取题目
        Random random = new Random();
        int ranId = random.nextInt(1);
        List<String> options = new ArrayList<>();
        if (ranId > 0) {
            //大写字母题目
            map.put("title", studyLetter.getBigLetter());
            options.add(studyLetter.getLowercaseLetters());
            for (Letter letter : threeLetter) {
                options.add(letter.getLowercaseLetters());
            }
        } else {
            //小写字母题目
            map.put("title", studyLetter.getLowercaseLetters());
            options.add(studyLetter.getBigLetter());
            for (Letter letter : threeLetter) {
                options.add(letter.getBigLetter());
            }
        }
        Collections.shuffle(options);
        map.put("options", options);
        if (ranId > 0) {
            for (int i = 0; i < options.size(); i++) {
                if (studyLetter.getLowercaseLetters().equals(options.get(i))) {
                    map.put("answer", i);
                }
            }
        } else {
            for (int i = 0; i < options.size(); i++) {
                if (studyLetter.getBigLetter().equals(options.get(i))) {
                    map.put("answer", i);
                }
            }
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Object saveLetterPair(LetterPair letterPair, HttpSession session) {
        Long studentId = getStudentId(session);
        Learn learn = new Learn();
        learn.setStudentId(studentId);
        learn.setStudyCount(1);
        learn.setUpdateTime(new Date());
        learn.setType(4);
        learn.setStudyModel("字母配对");
        learn.setType(1);
        learn.setUnitId(letterPair.getUnitId().longValue());
        learn.setVocabularyId(letterPair.getLetterId().longValue());
        learnMapper.insert(learn);
        letterPair.setStudentId(studentId.intValue());
        letterPairMapper.insert(letterPair);
        return ServerResponse.createBySuccess();
    }

    @Override
    public Object getLetterTreasure(String major, String subordinate) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (major.equals("认字母")) {
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterAllUnit();
            for (LetterUnit letterUnit : letterUnits) {
                List<Letter> allLetter = letterMapper.getAllLetterByUnitId(letterUnit.getId());
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("title", letterUnit.getUnitName());
                returnMap.put("list", allLetter);
                returnList.add(returnMap);
            }
        } else {
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterTreasure(major, subordinate);
            for (LetterUnit unit : letterUnits) {
                List<LetterVocabulary> letterVocabulary = letterVocabularyMapper.selByUnitIds(major, subordinate, unit.getId());
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("title", unit.getUnitName());
                returnMap.put("list", letterVocabulary);
                returnList.add(returnMap);
            }
        }

        return ServerResponse.createBySuccess(returnList);
    }


}

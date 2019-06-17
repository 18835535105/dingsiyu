package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.MemoryStrengthUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.LetterService;
import com.zhidejiaoyu.student.service.StudentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Service
@Slf4j
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
    @Autowired
    private BaiduSpeak baiduSpeak;
    @Autowired
    private MemoryStrengthUtil memoryStrengthUtil;
    @Autowired
    private StudentInfoService studentInfoService;


    @Value("${ftp.prefix}")
    private String partUrl;

    /**
     * 获取字母单元
     *
     * @param session
     * @return
     */
    @Override
    public Object getLetterUnit(HttpSession session) {
        Long studentId = getStudentId(session);
        //获取是否有当前学习的单元信息
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selLetterByStudentId(studentId);
        Map<String, Object> map = new HashMap<>();
        if (capacityStudentUnit != null) {
            //查询单元信息
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterUnit(capacityStudentUnit.getStartunit(), capacityStudentUnit.getEndunit());
            map.put("study", capacityStudentUnit.getUnitId());
            map.put("list", getLetterUnites(letterUnits, studentId));
        } else {
            //查询学习计划信息
            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selLetterByStudentId(studentId);
            //如果没有学习计划返回
            if (studentStudyPlan == null) {
                map.put("study", 0);
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("id", 0);
                returnMap.put("unitName", "暂无课程");
                returnMap.put("isOpen", true);
                List<Object> list = new ArrayList<>();
                list.add(returnMap);
                map.put("list", list);
                return ServerResponse.createBySuccess(map);
            }
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterUnit(studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
            map.put("study", studentStudyPlan.getStartUnitId());
            map.put("list", getLetterUnites(letterUnits, studentId));
        }
        return ServerResponse.createBySuccess(map);
    }

    private List<Map<String, Object>> getLetterUnites(List<LetterUnit> list, Long studentId) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        Boolean isTrue = true;
        //根据单元测试判断单元是否开启
        for (LetterUnit unit : list) {
            Map<String, Object> map = new HashMap<>();
            if (isTrue) {
                Integer point = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unit.getId().longValue(), 10);
                map.put("isOpen", isTrue);
                if (point == null || point < 100) {
                    isTrue = false;
                }
            } else {
                map.put("isOpen", isTrue);
            }
            map.put("id", unit.getId());
            map.put("unitName", unit.getUnitName());
            returnList.add(map);
        }
        return returnList;
    }

    /**
     * 获取单元模块开启详情
     * @param session
     * @param unitId
     * @return
     */
    @Override
    public Object getLetterUnitStatus(HttpSession session, Long unitId) {
        Long studentId = getStudentId(session);
        Map<String, Object> map = new HashMap<>();
        //在unitId未传入时查看信息
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
        //根据unitId查询单元信息
        if (unitId != null) {
            //查看单词播放器是否学习
            Integer letterCount = letterMapper.selLetterCountById(unitId);
            Integer letterListen = learnMapper.selLetterLearn(studentId, unitId, "字母播放器");
            map.put("letterListen", true);
            //判断是否开启单元配对
            if (letterListen != null && letterListen >= letterCount) {
                map.put("letterPair", true);
                //查看字母配对是否学习
                Integer letterListenCount = learnMapper.selLetterLearn(studentId, unitId, "字母配对");
                //判断是否开起单元默写
                if (letterListenCount != null && letterCount <= letterListenCount) {
                    map.put("letterWrite", true);
                    //查看字母听写是否完成
                    Integer letterWriteCount = learnMapper.selLetterLearn(studentId, unitId, "字母听写");
                    //判断是否开启单元闯关
                    if (letterWriteCount != null && letterCount <= letterWriteCount) {
                        map.put("LettersBreakThrough", true);
                    } else {
                        map.put("LettersBreakThrough", false);
                    }
                } else {
                    map.put("letterWrite", false);
                    map.put("LettersBreakThrough", false);
                }
            } else {
                map.put("letterPair", false);
                map.put("letterWrite", false);
                map.put("LettersBreakThrough", false);
            }
            //查看说有单元闯关是否完成
            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selLetterByStudentId(studentId);
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterUnit(studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
            if (letterUnits != null && letterUnits.size() > 0) {
                List<Integer> letterUnitIds = new ArrayList<>();
                for (LetterUnit letterUnit : letterUnits) {
                    letterUnitIds.add(letterUnit.getId());
                }
                //查看学后测试是否开启
                boolean falg = true;
                for (Integer letterUnitId : letterUnitIds) {
                    Integer max=testRecordMapper.selectUnitTestMaxPointByStudyModels(studentId, letterUnitId, 10);
                    if (max != null) {
                        if (max < 100) {
                            falg = false;
                        }
                    }else{
                        falg = false;
                    }
                }
                if(falg){
                    map.put("LetterPosttest",true);
                }else{
                    map.put("LetterPosttest",false);
                }
            }

            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createBySuccess(new HashMap<>());
    }


    @Override
    public Object getLetterListen(Long unitId, HttpSession session) {
        /**
         * 查看字母播放器全部数据
         */
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
        //保存字母播放器数据
        Long studentId = getStudentId(session);
        try {
            int i = playerMapper.selectByType(studentId, player.getUnitId(), 4, player.getWordId());
            if (i == 0) {
                player.setStudentId(studentId);
                player.setType(4);
                player.setUpdateTime(new Date());
                player.setLearnCount(1);
                playerMapper.insert(player);
            } else {
                Player player1 = playerMapper.selectPlayerByType(studentId, player.getUnitId(), 4, player.getWordId());
                player1.setUpdateTime(new Date());
                player1.setLearnCount(player1.getLearnCount() + 1);
                playerMapper.updateById(player1);
            }
            Learn learn = learnMapper.selLetter(studentId, player.getWordId(), player.getUnitId(), "字母播放器");
            Date date = new Date();
            if (learn == null) {
                learn = new Learn();
                learn.setStudentId(studentId);
                learn.setStudyModel("字母播放器");
                learn.setStatus(1);
                learn.setUnitId(player.getUnitId());
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
        } catch (Exception e) {
            log.error("学生: {} 保存字母播放器出错, errMsg=[{}]", studentId, e.getMessage());
            return ServerResponse.createByError(400, "保存失败");
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
        //查看黄金记忆点单词
        LetterPair letterPair = letterPairMapper.selPushLetter(unitId, studentId);
        Letter studyLetter = null;
        if (letterPair == null) {
            //查看当前单元已经学过的单词
            List<Long> longs = letterPairMapper.selAllStudyLetter(unitId, studentId);
            studyLetter = letterMapper.getStudyLetter(unitId, longs);
        } else {
            studyLetter = letterMapper.selectById(letterPair.getLetterId());
        }
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
        int ranId = random.nextInt(10);
        List<String> options = new ArrayList<>();
        if (ranId > 5) {
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
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (ranId > 5) {
            for (int i = 0; i < options.size(); i++) {
                Map<String, Object> returnMap = new HashMap<>();
                if (studyLetter.getLowercaseLetters().equals(options.get(i))) {
                    returnMap.put("letter", options.get(i));
                    returnMap.put("isTurn", true);
                } else {
                    returnMap.put("letter", options.get(i));
                    returnMap.put("isTurn", false);
                }
                returnList.add(returnMap);
            }
        } else {
            for (int i = 0; i < options.size(); i++) {
                Map<String, Object> returnMap = new HashMap<>();
                if (studyLetter.getBigLetter().equals(options.get(i))) {
                    returnMap.put("letter", options.get(i));
                    returnMap.put("isTurn", true);
                } else {

                    returnMap.put("letter", options.get(i));
                    returnMap.put("isTurn", false);
                }
                returnList.add(returnMap);
            }
        }
        map.put("options", returnList);
        map.put("id", studyLetter.getId());
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Object saveLetterWrite(Letter letter, HttpSession session, Boolean falg) {
        Long studentId = getStudentId(session);
        try {
            LetterWrite letterWrite = letterWriteMapper.selByLetterIdAndStudent(letter.getId(), studentId);
            if (letterWrite != null) {
                // 重新计算记忆强度
                Date push = GoldMemoryTime.getGoldMemoryTime(letterWrite.getMemoryStrength(), new Date());
                letterWrite.setPush(push);
                letterWrite.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(letterWrite.getMemoryStrength(), falg));
                letterWriteMapper.updateById(letterWrite);
            } else {
                letterWrite = new LetterWrite();
                letterWrite.setLetterId(letter.getId());
                letterWrite.setUnitId(letter.getUnitId());
                letterWrite.setStudentId(studentId.intValue());
                letterWrite.setState(1);
                if (!falg) {
                    letterWrite.setMemoryStrength(0.12);
                    Date push = GoldMemoryTime.getGoldMemoryTime(letterWrite.getMemoryStrength(), new Date());
                    letterWrite.setPush(push);
                }
                letterWriteMapper.insert(letterWrite);
                Date date = new Date();
                Learn learn = new Learn();
                learn.setStudentId(studentId);
                learn.setStudyModel("字母听写");
                learn.setVocabularyId(letter.getId().longValue());
                learn.setStatus(1);
                learn.setUnitId(letter.getUnitId().longValue());
                learn.setLearnTime(date);
                learn.setUpdateTime(date);
                learn.setStudyCount(1);
                learn.setType(4);
                learnMapper.insert(learn);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ServerResponse.createBySuccess();
    }


    @Override
    public Object saveLetterPair(LetterPair letterPair, HttpSession session, Boolean falg) {
        Long studentId = getStudentId(session);
        try {
            LetterPair pair = letterPairMapper.selByLetterIdAndStudent(letterPair.getLetterId(), studentId);
            if (pair != null) {
                // 重新计算记忆强度
                Date push = GoldMemoryTime.getGoldMemoryTime(pair.getMemoryStrength(), new Date());
                pair.setPush(push);
                pair.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(pair.getMemoryStrength(), falg));
                letterPairMapper.updateById(pair);
            }
            Learn learn = new Learn();
            learn.setStudentId(studentId);
            learn.setStudyCount(1);
            learn.setUpdateTime(new Date());
            learn.setType(4);
            learn.setStudyModel("字母配对");
            learn.setType(1);
            learn.setLearnTime(new Date());
            learn.setUnitId(letterPair.getUnitId().longValue());
            learn.setVocabularyId(letterPair.getLetterId().longValue());
            learnMapper.insert(learn);
            if(pair == null){
                if (!falg) {
                    letterPair.setMemoryStrength(0.12);
                    Date push = GoldMemoryTime.getGoldMemoryTime(letterPair.getMemoryStrength(), new Date());
                    letterPair.setPush(push);
                }
                letterPair.setStudentId(studentId.intValue());
                letterPairMapper.insert(letterPair);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public Object getLetterTreasure(String major, String subordinate) {
        List<Object> returnList = new ArrayList<>();
        if (major.equals("认字母")) {
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterAllUnit();
            Map<String, Object> map = new HashMap<>();
            List<Object> list = new ArrayList<>();
            for (LetterUnit letterUnit : letterUnits) {
                List<Letter> allLetter = letterMapper.getAllLetterByUnitId(letterUnit.getId());
                List<Letter> returnLetter = new ArrayList<>();
                allLetter.forEach(letter -> {
                    letter.setGifUrl(partUrl + letter.getGifUrl());
                    returnLetter.add(letter);
                });
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("title", letterUnit.getUnitName());
                returnMap.put("list", returnLetter);
                list.add(returnMap);
            }
            for (LetterUnit letterUnit : letterUnits) {
                int i = 0;
                List<Letter> allLetter = letterMapper.getAllLetterByUnitId(letterUnit.getId());
                for (Letter letter : allLetter) {
                    Map<String, Object> letterMap = new HashMap<>();
                    letterMap.put("title", letterUnit.getUnitName());
                    letter.setGifUrl(partUrl + letter.getGifUrl());
                    letterMap.put("letter", letter);
                    if (i == 0) {
                        letterMap.put("marge", true);
                    } else {
                        letterMap.put("display", true);
                    }
                    i++;
                    letterMap.put("line", allLetter.size());
                    returnList.add(letterMap);
                }
            }
            map.put("letterMap", list);
            map.put("listen", returnList);
            return ServerResponse.createBySuccess(map);
        } else {
            List<LetterUnit> letterUnits = letterUnitMapper.selLetterTreasure(major, subordinate);
            for (LetterUnit unit : letterUnits) {
                List<LetterVocabulary> letterVocabulary = letterVocabularyMapper.selByUnitIds(major, subordinate, unit.getId());
                if (major.equals("字母拼读")) {
                    //获取当前单元显示的字母
                    List<String> letters = letterVocabularyMapper.selLetterByUnitId(major, subordinate, unit.getId());
                    Map<String, List<LetterVocabulary>> collect = letterVocabulary.stream().collect(Collectors.groupingBy(vo -> vo.getLetter()));
                    Integer i = 0;
                    for (String letter : letters) {
                        Map<String, Object> letterMap = new HashMap<>();
                        if (i == 0) {
                            letterMap.put("marge", true);
                        } else {
                            letterMap.put("display", true);
                        }
                        letterMap.put("letter", letter);
                        letterMap.put("unit", unit.getUnitName());
                        letterMap.put("line", letters.size());
                        letterMap.put("list", collect.get(letter).subList(0, 5));
                        returnList.add(letterMap);
                        i++;
                    }
                } else {

                    int i = 0;
                    int total = 0;
                    List<LetterVocabulary> list = new ArrayList<>();
                    int lineSize = letterVocabulary.size() % 6 > 0 ? letterVocabulary.size() / 6 + 1 : letterVocabulary.size() / 6;

                    for (LetterVocabulary vo : letterVocabulary) {
                        list.add(vo);
                        total++;
                        if (list.size() % 6 == 0 || letterVocabulary.size() == total) {
                            Map<String, Object> map = new HashMap<>();
                            if (i == 0) {
                                map.put("merge", true);
                            } else {
                                map.put("display", true);
                            }
                            map.put("title", unit.getUnitName());
                            map.put("list", list);
                            map.put("line", lineSize);
                            returnList.add(map);
                            list = new ArrayList<>();
                            i++;
                        }
                    }

                }
            }
        }

        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public Object getLetterWrite(Long unitId, HttpSession session) {
        Long studentId = getStudentId(session);
        //获取学习总数量
        Integer countByUnitId = letterMapper.selLetterCountById(unitId);
        //获取以学习字母默写数量
        Integer letterWriteCount = letterWriteMapper.selStudyLetterCountByUnitIdAndStudent(unitId, studentId);
        if (countByUnitId.equals(letterWriteCount)) {
            return ServerResponse.createBySuccess(600, "无学习");
        }
        Integer letterWriteCounts = letterWriteMapper.selStudyLetterCountByUnitIdAndStudent(unitId, studentId);
        //查看是否有到黄金记忆点的字母
        Letter letter = letterMapper.selPushLetterByUnitIdAndStudent(unitId, studentId);
        Map<String, Object> map = new HashMap<>();
        if (letter == null) {
            List<Long> studyWirteIds = letterWriteMapper.selStudyLetterIdByUnitIdAndStudent(unitId, studentId);
            letter = letterMapper.getStudyLetter(unitId, studyWirteIds);
            map.put("memoryStrength", 0);
            map.put("studyNew", true);
        } else {
            Map<String, Object> stringObjectMap = letterWriteMapper.selByLetterMemoryStrengthAndStudent(letter.getId(), unitId, studentId);
            map.put("memoryStrength", stringObjectMap.get("memoryStrength"));
            map.put("studyNew", false);
        }
        map.put("id", letter.getId());
        map.put("unitId", unitId);
        map.put("letter", letter.getLowercaseLetters());
        map.put("bigLetter", letter.getBigLetter());
        map.put("listen", baiduSpeak.getLetterPath(letter.getBigLetter()));
        map.put("total", countByUnitId);
        map.put("plan", letterWriteCounts + 1);
        return ServerResponse.createBySuccess(map);

    }


    @Override
    public Object updLetter(HttpSession session, Long unitId) {
        Long studentId = getStudentId(session);
        try {
            //修改字母听写数据
            letterWriteMapper.delByUnitIdAndStudentId(unitId, studentId);
            learnMapper.updLetterPair(studentId, unitId, "字母听写");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public Object updLetterSymbolStudyModel(Long unitId, Integer type, HttpSession session) {
        Long studentId = getStudentId(session);
        CapacityStudentUnit capacityStudentUnit = null;
        if (type == 4) {
            capacityStudentUnit = capacityStudentUnitMapper.selLetterByStudentId(studentId);
        } else if (type == 5) {
            capacityStudentUnit = capacityStudentUnitMapper.selSymbolByStudentId(studentId);
        }
        if (capacityStudentUnit != null) {
            capacityStudentUnit.setUnitId(unitId);
            capacityStudentUnitMapper.updateById(capacityStudentUnit);
        }
        return ServerResponse.createBySuccess();
    }

}

package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.study.phonetic.PhoneticSymbolListenVo;
import com.zhidejiaoyu.common.Vo.study.phonetic.Topic;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.PhoneticSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PhoneticSymbolServiceImpl extends BaseServiceImpl<PhoneticSymbolMapper, PhoneticSymbol> implements PhoneticSymbolService {

    public static final String STUDY_MODEL = "音标辨音";

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;
    @Autowired
    private LetterUnitMapper letterUnitMapper;
    @Autowired
    private PhoneticSymbolMapper phoneticSymbolMapper;
    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;
    @Autowired
    private LearnMapper learnMapper;
    @Autowired
    private RedisOpt redisOpt;
    @Autowired
    private BaiduSpeak baiduSpeak;
    @Value("${ftp.prefix}")
    private String url;
    @Value("${ftp.prefix}")
    private String prefix;

    @Override
    public Object getSymbolUnit(HttpSession session) {
        Student student = getStudent(session);
        StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selSymbolByStudentId(student.getId());
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        if (studentStudyPlan == null) {
            map.put("study", 0);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("id", 0);
            returnMap.put("unitName", "暂无课程");
            returnMap.put("isOpen",true);
            list.add(returnMap);
            map.put("list", list);
            return ServerResponse.createBySuccess(map);
        }
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(),5);
        if (capacityStudentUnit != null) {
            Long unitId = capacityStudentUnit.getUnitId();
            if (unitId != null) {
                map.put("study", unitId);
            }
        }
        if (studentStudyPlan != null) {
            //获取单元
            List<LetterUnit> letterUnits = letterUnitMapper.selSymbolUnit(studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
            Boolean isTrue = true;
            if (map.get("study") == null) {
                if (letterUnits != null && letterUnits.size() > 0) {
                    map.put("study", letterUnits.get(0).getId());
                }
            }
            for (LetterUnit letterUnit : letterUnits) {
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("id", letterUnit.getId());
                returnMap.put("unitName", letterUnit.getUnitName());
                Integer studyCount = learnMapper.selLetterLearn(student.getId(), letterUnit.getId().longValue(), "音标辨音");
                int count = phoneticSymbolMapper.countByUnitId(letterUnit.getId().longValue());
                if (isTrue) {
                    returnMap.put("isOpen", true);
                    if (count > studyCount) {
                        isTrue = false;
                    }
                } else {
                    returnMap.put("isOpen", false);
                }
                list.add(returnMap);
            }
        }
        map.put("list", list);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 获取单元音标信息
     *
     * @param unitId
     * @return
     */
    @Override
    public Object getSymbol(Integer unitId, HttpSession session) {
        if (unitId == null) {
            Student student = getStudent(session);
            CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selSymbolByStudentId(student.getId());
            if (capacityStudentUnit != null) {
                unitId = capacityStudentUnit.getUnitId().intValue();
            } else {
                StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selSymbolByStudentId(student.getId());
                unitId = studentStudyPlan.getStartUnitId().intValue();
            }
        }
        List<String> symbolsList = phoneticSymbolMapper.selSymbolByUnitId(unitId);
        List<Map<String, Object>> returnList = new ArrayList<>();
        int isD=0;
        for (String symbol : symbolsList) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> list = new ArrayList<>();
            //获取当前单元下所有的音标
            List<PhoneticSymbol> phoneticSymbols = phoneticSymbolMapper.selAllByUnitIdAndSymbol(unitId, symbol);
            for (PhoneticSymbol phonetic : phoneticSymbols) {
                Map<String, Object> symbolMap = new HashMap<>();
                Map<String, Object> symbolMap2 = new HashMap<>();
                if (phonetic.getPhoneticSymbol().equals(symbol)) {
                    if (map.get("method") == null || map.get("method") == "") {
                        String[] split = phonetic.getPronunciationMethod().split("；");
                        map.put("method", split);
                    }
                    if (map.get("listen") == null || map.get("listen") == "") {
                        map.put("listen", url + phonetic.getUrl());
                    }
                    if (map.get("partUrl") == null || map.get("partUrl") == "") {
                        map.put("partUrl", url + phonetic.getPartUrl());
                    }
                }
                symbolMap.put("letter", phonetic.getLetter());
                String[] split = phonetic.getContent().split("；");
                List<String> vocabularies = new ArrayList<>();
                if (split.length > 4) {
                    List<String> lets = new ArrayList<>();
                    int i = 0;
                    for (String let : split) {
                        String[] s = let.split(" ");
                        vocabularies.add(s[1].replace("#", ""));
                        lets.add(let);
                        if (lets.size() == 4 && i == 0) {
                            i++;
                            symbolMap2.put("letter", phonetic.getLetter());
                            symbolMap2.put("merge", true);
                            symbolMap2.put("example", lets);
                            symbolMap2.put("vocabularies", vocabularies);
                            list.add(symbolMap2);
                            vocabularies = new ArrayList<>();
                            lets = new ArrayList<>();
                        }
                    }
                    symbolMap.put("display", true);
                    symbolMap.put("example", lets);
                    symbolMap.put("vocabularies", vocabularies);
                    list.add(symbolMap);
                } else {
                    symbolMap.put("example", split);
                    for (String let : split) {
                        String[] s = let.split(" ");
                        vocabularies.add(s[1].replace("#", ""));
                    }
                    symbolMap.put("vocabularies", vocabularies);
                    list.add(symbolMap);

                }
                if (isD == 0&&phonetic.getStatus()==2) {
                    Map<String, Object> sMap = new HashMap<>();
                    sMap.put("title", ".");
                    returnList.add(sMap);
                    isD++;
                }
            }
            map.put("title", symbol);
            map.put("content", list);
            returnList.add(map);
        }
        Long studentId = getStudentId(session);
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selSymbolByStudentId(studentId);
        if (capacityStudentUnit != null) {
            if (!capacityStudentUnit.getUnitId().equals(unitId)) {
                capacityStudentUnit.setUnitId(unitId.longValue());
                capacityStudentUnitMapper.updateById(capacityStudentUnit);
            }
        } else {
            capacityStudentUnit = new CapacityStudentUnit();
            capacityStudentUnit.setUnitId(unitId.longValue());
            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selSymbolByStudentId(studentId);
            capacityStudentUnit.setStartunit(studentStudyPlan.getStartUnitId());
            capacityStudentUnit.setEndunit(studentStudyPlan.getEndUnitId());
            capacityStudentUnitMapper.insert(capacityStudentUnit);
        }
        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public ServerResponse<Object> getSymbolListen(Long unitId, HttpSession session, Boolean restudy) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        if (restudy) {
            return this.restudy(student, unitId);
        }

        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        int learnCount = learnMapper.countByStudentIdAndStudyModel(studentId, STUDY_MODEL, unitId);

        int total = phoneticSymbolMapper.countByUnitId(unitId);

        PhoneticSymbol phoneticSymbol = this.getUnLearnedPhoneticSymbol(unitId, studentId);
        if (phoneticSymbol == null) {
             return ServerResponse.createBySuccess(600, "当前单元已学习完！");
        }

        PhoneticSymbolListenVo vo = new PhoneticSymbolListenVo();
        vo.setId(phoneticSymbol.getId());
        vo.setPhonetic(phoneticSymbol.getPhoneticSymbol());
        vo.setPlan(learnCount);
        vo.setTotal(total);
        vo.setTopics(this.getTopics(phoneticSymbol));
        vo.setAudioUrl(prefix + phoneticSymbol.getUrl());

        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 重新学习
     *
     * @param student
     * @param unitId
     * @return
     */
    private ServerResponse<Object> restudy(Student student, Long unitId) {
        learnMapper.updateTypeByStudentIdAndUnitId(student.getId(), unitId, STUDY_MODEL, 2);
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取当前单元未学习的音标信息
     *
     * @param unitId
     * @param studentId
     * @return
     */
    private PhoneticSymbol getUnLearnedPhoneticSymbol(Long unitId, Long studentId) {
        List<String> phoneticSymbols = phoneticSymbolMapper.selectLearnedPhoneticSymbolByStudentIdAndUnitId(studentId, STUDY_MODEL, unitId);
        return phoneticSymbolMapper.selectUnLearnPhoneticSymbolByPhoneticSymbols(unitId, phoneticSymbols);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveSymbolListen(HttpSession session, Long unitId, Integer symbolId) {
        Student student = super.getStudent(session);
        this.saveSymbolListenLearn(session, unitId, symbolId, student);

        PhoneticSymbol phoneticSymbol = this.getUnLearnedPhoneticSymbol(unitId, student.getId());
        if (phoneticSymbol == null) {
            // 单元闯关
            return ServerResponse.createBySuccess(super.toUnitTest());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getUnitTest(HttpSession session, Long unitId) {
        List<PhoneticSymbol> allPhoneticSymbol = redisOpt.getPhoneticSymbol();
        // 当前单元的所有音标信息
        List<PhoneticSymbol> phoneticSymbols = allPhoneticSymbol.parallelStream()
                .filter(phoneticSymbol -> Objects.equals(Long.valueOf(phoneticSymbol.getUnitId().toString()), unitId))
                .collect(Collectors.toList());
        Collections.shuffle(phoneticSymbols);

        // 非当前单元的所有音标信息
        List<PhoneticSymbol> otherPhoneticSymbol = allPhoneticSymbol.parallelStream()
                .filter(phoneticSymbol -> !Objects.equals(Long.valueOf(phoneticSymbol.getUnitId().toString()), unitId))
                .collect(Collectors.toList());
        Collections.shuffle(otherPhoneticSymbol);

        // 根据读音去重
        List<String> urlList = getUrlList(otherPhoneticSymbol);

        List<Map<String, Object>> resultList = new ArrayList<>(phoneticSymbols.size() * 3);

        Map<String, List<PhoneticSymbol>> collectMap = phoneticSymbols.parallelStream().collect(Collectors.groupingBy(PhoneticSymbol::getPhoneticSymbol));
        collectMap.forEach((key, val) -> {
            // 看音标选声音（听力理解）
            this.getTypeOne(urlList, resultList, val);

            // 听音标选单词（音标辨音）
            this.getTypeTwo(otherPhoneticSymbol, resultList, val);

            // 看单词选音标（音标配对）
            this.getTypeThree(otherPhoneticSymbol, val, resultList);
        });

        Collections.shuffle(resultList);
        return ServerResponse.createBySuccess(resultList);
    }

    /**
     * 获取所有音标读音
     *
     * @return
     */
    @Override
    public ServerResponse getAllSymbolListen() {
        List<Map<String,Object>> symbols = phoneticSymbolMapper.selAllSymbol();
        Map<String,Object> map=new HashMap<>();
        if (symbols != null && symbols.size() > 0) {
            for(Map<String,Object> symbol:symbols){
                map.put(symbol.get("symbol").toString().replace(" ",""),url+symbol.get("url"));
            }
        }
        return ServerResponse.createBySuccess(map);
    }

    private List<String> getUrlList(List<PhoneticSymbol> otherPhoneticSymbol) {
        Map<String, List<PhoneticSymbol>> collect = otherPhoneticSymbol.stream().collect(Collectors.groupingBy(PhoneticSymbol::getUrl));
        return new ArrayList<>(collect.keySet());
    }

    /**
     * 看音标选单词（音标配对）
     *
     * @param symbols         其他单元的音标信息
     * @param phoneticSymbols 当前单元的音标信息
     * @param resultList
     */
    private void getTypeThree(List<PhoneticSymbol> symbols, List<PhoneticSymbol> phoneticSymbols, List<Map<String, Object>> resultList) {

        Collections.shuffle(symbols);
        List<PhoneticSymbol> collect = symbols.stream().limit(3).collect(Collectors.toList());
        List<Map<String, Object>> answerList = new ArrayList<>(collect.size());
        Map<String, Object> answerMap;
        for (PhoneticSymbol symbol : collect) {
            answerMap = new HashMap<>(16);
            answerMap.put("word", symbol.getPhoneticSymbol());
            answerMap.put("answer", false);
            answerList.add(answerMap);
        }
        answerMap = new HashMap<>(16);
        answerMap.put("word", phoneticSymbols.get(0).getPhoneticSymbol());
        answerMap.put("answer", true);
        answerList.add(answerMap);
        Collections.shuffle(answerList);

        Map<String, Object> map = new HashMap<>(16);
        map.put("type", 3);
        map.put("title", phoneticSymbols.get(0).getLetter());
        map.put("answer", answerList);
        resultList.add(map);
    }

    /**
     * 听音标选单词（音标辨音）
     *
     * @param otherPhoneticSymbol
     * @param resultList
     * @param val
     */
    private void getTypeTwo(List<PhoneticSymbol> otherPhoneticSymbol, List<Map<String, Object>> resultList, List<PhoneticSymbol> val) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("type", 2);
        map.put("title", prefix + val.get(0).getUrl());

        List<Map<String, Object>> list = new ArrayList<>(4);
        Map<String, Object> answerMap = new HashMap<>(16);
        answerMap.put("word", val.get(0).getLetter());
        answerMap.put("answer", true);
        list.add(answerMap);

        for (int i = 0; i < 3; i++) {
            answerMap = new HashMap<>(16);
            answerMap.put("word", otherPhoneticSymbol.get(i).getLetter());
            answerMap.put("answer", false);
            list.add(answerMap);
        }
        Collections.shuffle(list);

        map.put("answer", list);
        resultList.add(map);
    }

    /**
     * 看音标选声音（听力理解）
     *
     * @param phoneticSymbolList
     * @param resultList
     * @param val
     */
    private void getTypeOne(List<String> phoneticSymbolList, List<Map<String, Object>> resultList, List<PhoneticSymbol> val) {
        Collections.shuffle(phoneticSymbolList);
        Map<String, Object> map = new HashMap<>(16);
        map.put("type", 1);
        map.put("title", val.get(0).getPhoneticSymbol());

        List<Map<String, Object>> list = new ArrayList<>(4);
        Map<String, Object> answerMap;
        for (int i = 0; i < 3; i++) {
            answerMap = new HashMap<>(16);
            answerMap.put("url", prefix + phoneticSymbolList.get(i));
            answerMap.put("answer", false);
            list.add(answerMap);
        }
        answerMap = new HashMap<>(16);
        answerMap.put("url", prefix + val.get(0).getUrl());
        answerMap.put("answer", true);
        list.add(answerMap);
        Collections.shuffle(list);

        map.put("answer", list);
        resultList.add(map);
    }

    private void saveSymbolListenLearn(HttpSession session, Long unitId, Integer symbolId, Student student) {
        Object learnTime = session.getAttribute(TimeConstant.BEGIN_START_TIME);
        Learn learn = new Learn();
        learn.setStudyModel(STUDY_MODEL);
        learn.setUnitId(unitId);
        learn.setStudentId(student.getId());
        learn.setStatus(1);
        learn.setLearnTime(learnTime == null ? new Date() : (Date) learnTime);
        learn.setUpdateTime(new Date());
        learn.setStudyCount(1);
        learn.setType(1);
        learn.setVocabularyId(Long.valueOf(symbolId.toString()));
        learn.setLearnCount(1);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        learnMapper.insert(learn);
    }

    /**
     * 封装试题及答案
     *
     * @param phoneticSymbol
     * @return
     */
    private List<Topic> getTopics(PhoneticSymbol phoneticSymbol) {
        List<PhoneticSymbol> phoneticSymbols = redisOpt.getPhoneticSymbol();
        if (phoneticSymbols.size() > 0) {
            Collections.shuffle(phoneticSymbols);
            // 添加正确答案
            List<Topic> topics = phoneticSymbols.stream().filter(phoneticSymbol1 -> Objects.equals(phoneticSymbol1.getPhoneticSymbol(), phoneticSymbol.getPhoneticSymbol())).map(result -> {
                Topic topic = new Topic();
                topic.setAnswer(true);
                topic.setWord(result.getLetter());
                return topic;
            }).collect(Collectors.toList());

            // 添加两个错误答案
            topics.addAll(phoneticSymbols.stream().filter(phoneticSymbol1 -> !Objects.equals(phoneticSymbol1.getPhoneticSymbol(), phoneticSymbol.getPhoneticSymbol())
                    && !Objects.equals(phoneticSymbol1.getLetter(), phoneticSymbol.getLetter()))
                    .limit(2)
                    .map(result -> {
                        Topic topic = new Topic();
                        topic.setAnswer(false);
                        topic.setWord(result.getLetter());
                        return topic;
                    }).collect(Collectors.toList()));

            Collections.shuffle(topics);
            return topics;
        }
        return new ArrayList<>();
    }


}

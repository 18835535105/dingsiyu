package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.study.phonetic.PhoneticSymbolListenVo;
import com.zhidejiaoyu.common.Vo.study.phonetic.Topic;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.PhoneticSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Service
public class PhoneticSymbolServiceImpl extends BaseServiceImpl<PhoneticSymbolMapper, PhoneticSymbol> implements PhoneticSymbolService {

    private final String STUDY_MODEL = "音标辨音";

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

    @Value("${ftp.url}")
    private String url;

    @Override
    public Object getSymbolUnit(HttpSession session) {
        Student student = getStudent(session);
        StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selSymbolByStudentId(student.getId());
        List<Object> list=new ArrayList<>();
        if(studentStudyPlan!=null){
            //获取单元
            List<LetterUnit> letterUnits = letterUnitMapper.selSymbolUnit(studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
            for(LetterUnit letterUnit:letterUnits){
                Map<String,Object> returnMap=new HashMap<>();
                returnMap.put("title",letterUnit.getUnitName());
                returnMap.put("id",letterUnit.getId());
                List<Map<String, Object>> maps = phoneticSymbolMapper.selByUnitId(letterUnit.getId());
                List<Map<String,Object>> symbolList=new ArrayList<>();
                for(Map<String,Object> map:maps){
                    Map<String,Object> unitMap=new HashMap<>();
                    unitMap.put("title",map.get("type").toString()+map.get("phonetic_symbol")+"的发音方法");
                    unitMap.put("type",map.get("type").toString()+map.get("phonetic_symbol"));
                    symbolList.add(unitMap);
                }
                returnMap.put("child",symbolList);
                list.add(returnMap);
            }
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     * 获取单元音标信息
     * @param unitId
     * @return
     */
    @Override
    public Object getSymbol(Integer unitId,HttpSession session) {
        if(unitId==null){
            Student student = getStudent(session);
            CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selSymbolByStudentId(student.getId());
            if(capacityStudentUnit!=null){
                unitId=capacityStudentUnit.getUnitId().intValue();
            }else{
                StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selSymbolByStudentId(student.getId());
                unitId=studentStudyPlan.getStartUnitId().intValue();
            }
        }
        List<String> symbolsList=phoneticSymbolMapper.selSymbolByUnitId(unitId);
        List<Map<String,Object>> returnList=new ArrayList<>();
        for(String symbol:symbolsList){
            Map<String,Object> map=new HashMap<>();
            List<Map<String,Object>> list=new ArrayList<>();
            List<PhoneticSymbol> phoneticSymbols = phoneticSymbolMapper.selAllByUnitIdAndSymbol(unitId,symbol);
            for(PhoneticSymbol phonetic:phoneticSymbols){
                Map<String,Object> symbolMap=new HashMap<>();
                Map<String,Object> symbolMap2=new HashMap<>();
                if(phonetic.getPhoneticSymbol().equals(symbol)){
                    if(map.get("method")==null || map.get("method")==""){
                        String[] split = phonetic.getPronunciationMethod().split("；");
                        map.put("method",split);
                    }
                    if(map.get("listen")==null || map.get("listen")==""){
                        map.put("listen",url+"/"+phonetic.getUrl());
                    }
                }
                symbolMap.put("letter",phonetic.getLetter());
                String[] split = phonetic.getContent().split("；");
                if(split.length>4){
                    List<String> lets=new ArrayList<>();
                    int i=0;
                    for(String let:split){
                        lets.add(let);
                        if(lets.size()==4 && i==0){
                            i++;
                            symbolMap2.put("letter",phonetic.getLetter());
                            symbolMap2.put("merge",true);
                            symbolMap2.put("example",lets);
                            list.add(symbolMap2);
                            lets=new ArrayList<>();
                        }
                    }
                    symbolMap.put("display",true);
                    symbolMap.put("example",lets);
                    list.add(symbolMap);
                }else{
                    symbolMap.put("example",split);
                    list.add(symbolMap);
                }

            }
            map.put("title",symbol);
            map.put("content",list);
            returnList.add(map);
            if(returnList.size()==2){
                Map<String,Object> sMap=new HashMap<>();
                sMap.put("title",".");
                returnList.add(sMap);
            }
        }
        Long studentId = getStudentId(session);
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selSymbolByStudentId(studentId);
        if(capacityStudentUnit!=null){
           if(!capacityStudentUnit.getUnitId().equals(unitId)){
               capacityStudentUnit.setUnitId(unitId.longValue());
               capacityStudentUnitMapper.updateById(capacityStudentUnit);
           }
        }else{
            capacityStudentUnit=new CapacityStudentUnit();
            capacityStudentUnit.setUnitId(unitId.longValue());
            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selSymbolByStudentId(studentId);
            capacityStudentUnit.setStartunit(studentStudyPlan.getStartUnitId());
            capacityStudentUnit.setEndunit(studentStudyPlan.getEndUnitId());
            capacityStudentUnitMapper.insert(capacityStudentUnit);
        }
        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public ServerResponse<Object> getSymbolListen(Long unitId, HttpSession session) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        List<Learn> learns = learnMapper.countByStudentIdAndStudyModel(studentId, STUDY_MODEL, unitId);

        int total = phoneticSymbolMapper.countByUnitId(unitId);

        PhoneticSymbol phoneticSymbol = getUnLearnedPhoneticSymbol(unitId, studentId);
        if (phoneticSymbol == null) {
            return ServerResponse.createBySuccess(600, "当前单元已学习完！");
        }

        PhoneticSymbolListenVo vo = new PhoneticSymbolListenVo();
        vo.setId(phoneticSymbol.getId());
        vo.setPhonetic(phoneticSymbol.getPhoneticSymbol());
        vo.setPlan(learns.size());
        vo.setTotal(total);
        vo.setTopics(this.getTopics(phoneticSymbol));

        return ServerResponse.createBySuccess(vo);
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
        return phoneticSymbolMapper.selectUnLearnPhoneticSymbolByPhoneticSymbols(phoneticSymbols);
    }

    @Override
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
            topics.addAll(phoneticSymbols.stream().filter(phoneticSymbol1 -> !Objects.equals(phoneticSymbol1.getPhoneticSymbol(), phoneticSymbol.getPhoneticSymbol()))
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

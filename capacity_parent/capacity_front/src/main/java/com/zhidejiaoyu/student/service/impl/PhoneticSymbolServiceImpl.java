package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.CapacityStudentUnitMapper;
import com.zhidejiaoyu.common.mapper.LetterUnitMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.mapper.PhoneticSymbolMapper;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.PhoneticSymbolService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;
    @Autowired
    private LetterUnitMapper letterUnitMapper;
    @Autowired
    private PhoneticSymbolMapper phoneticSymbolMapper;
    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;
    @Value("${ftp.prefix}")
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
                        map.put("listen",url+phonetic.getUrl());
                    }
                    if(map.get("partUrl")==null||map.get("partUrl")==""){
                        map.put("partUrl",url+phonetic.getPartUrl());
                    }
                }
                symbolMap.put("letter",phonetic.getLetter());
                String[] split = phonetic.getContent().split("；");
                List<String> vocabularies=new ArrayList<>();
                if(split.length>4){
                    List<String> lets=new ArrayList<>();
                    int i=0;
                    for(String let:split){
                        String[] s = let.split(" ");
                        vocabularies.add(s[1].replace("#",""));
                        lets.add(let);
                        if(lets.size()==4 && i==0){
                            i++;
                            symbolMap2.put("letter",phonetic.getLetter());
                            symbolMap2.put("merge",true);
                            symbolMap2.put("example",lets);
                            symbolMap2.put("vocabularies",vocabularies);
                            list.add(symbolMap2);
                            vocabularies=new ArrayList<>();
                            lets=new ArrayList<>();
                        }
                    }
                    symbolMap.put("display",true);
                    symbolMap.put("example",lets);
                    symbolMap.put("vocabularies",vocabularies);
                    list.add(symbolMap);
                }else{
                    symbolMap.put("example",split);
                    for(String let:split){
                        String[] s = let.split(" ");
                        vocabularies.add(s[1].replace("#",""));
                    }
                    symbolMap.put("vocabularies",vocabularies);
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





}

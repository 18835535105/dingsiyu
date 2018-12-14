package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.TeksMapper;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.TeksService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TeksServiceImpl implements TeksService {

    @Autowired
    private TeksMapper teksMapper;
    @Autowired
    private BaiduSpeak baiduSpeak;
    @Autowired
    private CommonMethod commonMethod;

    @Override
    public ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId) {
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        if(teks.size()>0){
            List<Teks> resultTeks=new ArrayList<>();
            for(Teks teks1:teks){
                teks1.setPronunciation(baiduSpeak.getLanguagePath(teks1.getSentence()));
                resultTeks.add(teks1);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> selChooseTeks(Integer unitId) {
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        if(teks.size()>0){
            List<Map<String,Object>> resultList=new ArrayList<>();
            for(Teks teks1:teks){
                Map<String,Object> map=new HashMap<>();
                map.put("chinese",teks1.getParaphrase());
                map.put("pronunciation",baiduSpeak.getLanguagePath(teks1.getSentence()));
                map.put("sentence",teks1.getSentence());
                map.put("id",teks1.getId());
                int count=0;
                count+=StringUtils.countMatches(teks1.getSentence(),",");
                count+=StringUtils.countMatches(teks1.getSentence(),"!");
                count+=StringUtils.countMatches(teks1.getSentence(),"?");
                count+=StringUtils.countMatches(teks1.getSentence(),".");
                String[] sentenceList = teks1.getSentence().split(" ");
                String[] blankSentenceArray=new String[sentenceList.length+count];
                int index=0;
                for(int i=0;i<sentenceList.length;i++){
                    int s=StringUtils.countMatches(sentenceList[i],",");
                    s+=StringUtils.countMatches(sentenceList[i],"!");
                    s+=StringUtils.countMatches(sentenceList[i],"?");
                    s+=StringUtils.countMatches(sentenceList[i],".");
                    if(s>0){
                        int u=StringUtils.countMatches(sentenceList[i],",");
                        if(u>0){
                            blankSentenceArray[index]=null;
                            index+=1;
                            blankSentenceArray[index]=",";
                            index+=1;
                        }
                        int p=StringUtils.countMatches(sentenceList[i],"!");
                        if(p>0){
                            blankSentenceArray[index]=null;
                            index+=1;
                            blankSentenceArray[index]="!";
                            index+=1;
                        }
                        int q=StringUtils.countMatches(sentenceList[i],"?");
                        if(q>0){
                            blankSentenceArray[index]=null;
                            index+=1;
                            blankSentenceArray[index]="?";
                            index+=1;
                        }
                        int e=StringUtils.countMatches(sentenceList[i],".");
                        if(e>0){
                            blankSentenceArray[index]=null;
                            index+=1;
                            blankSentenceArray[index]=".";
                            index+=1;
                        }
                    }else{

                        blankSentenceArray[index]=null;
                        index+=1;
                    }
                    map.put("vocabularyArray",commonMethod.getOrderEnglishList(teks1.getSentence(),null));
                    map.put("blankSentenceArray",blankSentenceArray);
                }
                resultList.add(map);
            }
            return ServerResponse.createBySuccess(resultList);
        }
        return ServerResponse.createByError();
    }























}

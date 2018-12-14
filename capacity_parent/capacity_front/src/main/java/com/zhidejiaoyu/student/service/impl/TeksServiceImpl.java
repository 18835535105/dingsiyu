package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.mapper.CourseMapper;
import com.zhidejiaoyu.common.mapper.TeksMapper;
import com.zhidejiaoyu.common.mapper.TestRecordMapper;
import com.zhidejiaoyu.common.mapper.UnitSentenceMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.TeksService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;


@Service
public class TeksServiceImpl extends BaseServiceImpl<TeksMapper, Teks> implements TeksService {

    @Autowired
    private TeksMapper teksMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;
    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;


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

    @Override
    @SuppressWarnings("all")
    public ServerResponse<List<CourseUnitVo>> getCourseAndUnit(HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<CourseUnitVo> courseUnitVos = new ArrayList<>();
        CourseUnitVo courseUnitVo;
        List<Map<String, Object>> resultMap;

        // 学生所有课程id及课程名
        List<Map<String, Object>> courses = courseMapper.selectTextCourseIdAndCourseNameByStudentId(studentId);

        // 学生课程下所有例句的单元id及单元名
        if (courses.size() > 0) {
            List<Long> courseIds = new ArrayList<>(courses.size());
            courses.forEach(map -> courseIds.add((Long) map.get("id")));

            // 获取课程下所有课文的单元信息
            List<Map<String, Object>> textUnits = teksMapper.selectUnitIdAndNameByCourseIds(studentId, courseIds);

            // 已经进行过单元闯关的单元
            Map<Long, Map<Long, Long>> testMap = null;
            if (textUnits.size() > 0) {
                List<Long> unitIds = new ArrayList<>(textUnits.size());
                textUnits.forEach(map -> unitIds.add((Long) map.get("id")));
                testMap = testRecordMapper.selectHasUnitTest(studentId, unitIds);
            }

            for (Map<String, Object> courseMap : courses) {
                courseUnitVo = new CourseUnitVo();
                resultMap = new ArrayList<>();
                courseUnitVo.setCourseId((Long) courseMap.get("id"));
                courseUnitVo.setCourseName(courseMap.get("courseName").toString());

                // 存放单元信息
                Map<String, Object> unitInfoMap;
                for (Map<String, Object> unitMap : textUnits) {
                    unitInfoMap = new HashMap<>(16);
                    if (Objects.equals(courseMap.get("id"), unitMap.get("courseId"))) {
                        unitInfoMap.put("unitId", unitMap.get("id"));
                        unitInfoMap.put("unitName", unitMap.get("unitName"));
                        if (testMap != null && testMap.containsKey(unitMap.get("id"))) {
                            // 当前单元已进行过单元闯关，标记为已学习
                            unitInfoMap.put("state", 4);
                        } else {
                            // 当前单元还未学习
                            unitInfoMap.put("state", 1);
                        }
                        resultMap.add(unitInfoMap);
                    }
                }
                courseUnitVo.setUnitVos(resultMap);
                courseUnitVos.add(courseUnitVo);
            }
        }
        return ServerResponse.createBySuccess(courseUnitVos);
    }
}

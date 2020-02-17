package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.common.mapper.ErrorLearnLogMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanNewMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Service("smallProgramTestService")
public class SmallProgramTestServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements SmallProgramTestService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private ErrorLearnLogMapper errorLearnLogMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;


    @Override
    public Object getTest(HttpSession session) {
        Student student = getStudent(session);
        List<Map<String, Object>> maps = errorLearnLogMapper.selectVocabularyByStudentId(student.getId());
        Map<String, Object> returnMap = new HashMap<>();
        if (maps.size() == 0) {
            //获取优先级最大的单元
            StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(student.getId());
            //获取当前单元的单词
            List<Vocabulary> vocabularies = vocabularyMapper.selectByUnitId(studentStudyPlanNew.getUnitId());
            vocabularies.forEach(vocabulary -> {
                Map<String, Object> listMap = new HashMap<>();
                listMap.put("wordId", vocabulary.getId());
                listMap.put("word", vocabulary.getWord());
                listMap.put("wordChinese", vocabulary.getWordChinese());
                maps.add(listMap);
            });
        }
        returnMap.put("gold", student.getSystemGold().intValue());
        //获取单词id
        List<Long> vocabularyIds = new ArrayList<>();
        maps.forEach(map -> {
            vocabularyIds.add(Long.parseLong(map.get("wordId").toString()));
        });
        List<Map<String, Object>> getMaps = new ArrayList<>();
        getMaps.addAll(maps);
        if (getMaps.size() < 15) {
            while (getMaps.size() < 15) {
                for (Map<String, Object> map : maps) {
                    if (getMaps.size() < 15) {
                        getMaps.add(map);
                    } else {
                        break;
                    }
                }
            }
        }
        returnMap.put("optionList", getOptionList(getMaps, vocabularyIds));
        returnMap.put("writeList", getWriteList(getMaps));
        return returnMap;
    }

    private List<Map<String, Object>> getWriteList(List<Map<String, Object>> getMaps) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        getMaps.forEach(map -> {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("subject", map.get("wordChinese"));
            returnMap.put("answer", map.get("word"));
            returnList.add(returnMap);
        });

        return returnList;
    }


    private List<Map<String, Object>> getOptionList(List<Map<String, Object>> getMaps, List<Long> vocabularyIds) {
        //获取干扰项
        List<String> strings = vocabularyMapper.selectChineseByNotVocabularyIds(vocabularyIds);
        List<Map<String, Object>> returnList = new ArrayList<>();
        getMaps.forEach(map -> {
            Collections.shuffle(strings);
            List<String> chineses = strings.subList(0, 3);
            String wordChinese = map.get("wordChinese").toString();
            chineses.add(wordChinese);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("subject", map.get("word").toString());
            List<Map<String, Boolean>> optionList = new ArrayList<>();
            for (String chinese : chineses) {
                Map<String, Boolean> booleanMap = new HashMap<>();
                if (wordChinese.equals(chinese)) {
                    booleanMap.put(chinese, true);
                } else {
                    booleanMap.put(chinese, false);
                }
                optionList.add(booleanMap);
            }
            returnMap.put("option", optionList);
            returnList.add(returnMap);
        });
        return returnList;
    }
}

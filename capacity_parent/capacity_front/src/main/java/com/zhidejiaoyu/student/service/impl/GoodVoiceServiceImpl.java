package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.voice.VoiceRankVo;
import com.zhidejiaoyu.common.Vo.student.voice.VoiceVo;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.http.FtpUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.FileResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GoodVoiceService;
import com.zhidejiaoyu.student.utils.GoodVoiceUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author wuchenxi
 * @date 2018/8/29
 */
@Service
@Log4j
public class GoodVoiceServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements GoodVoiceService {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private VoiceMapper voiceMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Value("${ftp.prefix}")
    private String prefix;

    @Autowired
    private FtpUtil ftpUtil;

    @Autowired
    private GoodVoiceUtil goodVoiceUtil;

    @Override
    public ServerResponse getSubjects(HttpSession session, Long unitId, Integer type) {
        Student student = getStudent(session);
        List<VoiceVo> voiceVos;
        if (type == 1) {
            List<Vocabulary> vocabularies = vocabularyMapper.selectWordVoice(student.getId(), unitId);
            if (vocabularies.size() == 0) {
                return ServerResponse.createBySuccess("当前单元没有待学习的单词。");
            }
            voiceVos = new ArrayList<>(vocabularies.size());
            VoiceVo voiceVo;
            for (Vocabulary vocabulary : vocabularies) {
                voiceVo = new VoiceVo();
                voiceVo.setChinese(vocabulary.getWordChinese());
                voiceVo.setId(vocabulary.getId());
                voiceVo.setSoundMark(commonMethod.getSoundMark(voiceVo.getWord()));
                voiceVo.setSyllable(vocabulary.getSyllable());
                voiceVo.setWord(vocabulary.getWord());
                voiceVo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
                voiceVos.add(voiceVo);
            }
            Collections.shuffle(voiceVos);
            return ServerResponse.createBySuccess(voiceVos);
        } else {
            List<Sentence> sentences = sentenceMapper.selectSentenceVoice(student.getId(), unitId);
            if (sentences.size() == 0) {
                return ServerResponse.createBySuccess("当前单元没有待学习的句型。");
            }
            voiceVos = new ArrayList<>(sentences.size());
            VoiceVo voiceVo;
            for (Sentence sentence : sentences) {
                voiceVo = new VoiceVo();
                voiceVo.setChinese(sentence.getCentreTranslate());
                voiceVo.setId(sentence.getId());
                voiceVo.setWord(sentence.getCentreExample());
                voiceVo.setReadUrl(baiduSpeak.getLanguagePath(sentence.getCentreExample()));
                voiceVos.add(voiceVo);
            }
            Collections.shuffle(voiceVos);
            return ServerResponse.createBySuccess(voiceVos);
        }
    }

    @Override
    public ServerResponse getRank(HttpSession session, Long unitId, Long wordId, Integer type) {
        Student student = getStudent(session);
        List<VoiceRankVo> classRankVos = new ArrayList<>();
        List<VoiceRankVo> countryRankVos = new ArrayList<>(10);

        // 全国排行
        List<Voice> countryVoiceRank = voiceMapper.selectCountryRank(unitId, wordId, type);
        packageVoiceRankVo(countryRankVos, countryVoiceRank);

        // 全班排行
        List<Voice> classVoiceRank = voiceMapper.selectClassRank(student, unitId, wordId, type);
        packageVoiceRankVo(classRankVos, classVoiceRank);

        Map<String, List<VoiceRankVo>> map = new HashMap<>(16);
        map.put("classRankVos", classRankVos);
        map.put("countryRankVos", countryRankVos);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse saveVoice(HttpSession session, Long unitId, Long wordId, String word, Integer type,Integer count, MultipartFile audio) {

        Student student = getStudent(session);

        // 文件大于2M禁止上传，讯飞语音评测接口最大支持2M音频文件
        long maxSize = 2097152L;
        if (audio.getSize() > maxSize) {
            return ServerResponse.createBySuccess(FileResponseCode.TOO_LARGE.getCode(), FileResponseCode.TOO_LARGE.getMsg());
        }

        // 上传录音文件
        String file=  ftpUtil.uploadGoodVoice(audio, FileConstant.GOOD_VOICE);
        String fileName=FileConstant.GOOD_VOICE +file;

        System.out.println("上传数据 wordId :"+wordId+"  word :"+word+"    type :"+type+"   count"+count+"  audio :"+audio);
        String url = prefix + fileName;
        int score=0;
        Map<String, Object> map;
        if (type == 1) {
            if(file!=null){
                map = goodVoiceUtil.getWordEvaluationRecord(word, url);
                score = (int) map.get("score");
                map.put("flow",true);
            }else{
                map=new HashMap<>();
                map.put("score",0);
                map.put("heart",0);
                map.put("flow",false);
            }
        } else {
            if(file!=null){
                System.out.println("fileName  :"+fileName);
                System.out.println("url  :"+url);
                map = goodVoiceUtil.getSentenceEvaluationRecord(word, url);
                score = (int) map.get("totalScore");
                map.put("flow",true);
            }else{
                map=new HashMap<>();
                map.put("flow",false);
                String[] s = word.split(" ");
                List<Map<String,Object>> resList=new ArrayList<>();
                for(String wo : s ){
                    Map<String,Object> resMap=new HashMap<>();
                    Map<String,Object> ressMap = null;
                    if (wo.endsWith(",") || wo.endsWith("!") ||wo.endsWith("?") || wo.endsWith(".")) {
                        resMap.put("word", wo.substring(0, wo.length() - 1));
                        ressMap=new HashMap<>();
                        ressMap.put("word", wo.substring(wo.length() - 1));
                        resMap.put("score",0);
                        resMap.put("color","red");
                        resList.add(resMap);
                    } else {
                        resMap.put("word", wo);
                    }
                    resMap.put("score",0);
                    resMap.put("color","red");
                    resList.add(resMap);
                    if(ressMap!=null){
                        resList.add(ressMap);
                    }
                }
                map.put("word",resList);
            }
        }
        map.put("voiceUrl", url);

        Long courseId = unitMapper.selectCourseIdByUnitId(unitId);
        if(file!=null){
            saveVoice(unitId, wordId, type, student, fileName, score, courseId,count);
        }

        return ServerResponse.createBySuccess(map);
    }

    private void saveVoice(Long unitId, Long wordId, Integer type, Student student, String fileName, int record, Long courseId,Integer count) {
        Voice voice = new Voice();
        voice.setCourseId(courseId);
        voice.setCreateTime(new Date());
        voice.setScore(record * 1.0);
        voice.setStudentId(student.getId());
        voice.setStudentName(student.getStudentName());
        voice.setType(type);
        voice.setUnitId(unitId);
        voice.setVoiceUrl(fileName);
        voice.setWordId(wordId);
        voice.setCount(count);
        voiceMapper.insert(voice);
    }

    private void packageVoiceRankVo(List<VoiceRankVo> rankVos, List<Voice> voiceRank) {
        if (voiceRank.size() > 0) {
            VoiceRankVo vo;
            for (Voice voice : voiceRank) {
                vo = new VoiceRankVo();
                vo.setScore(Integer.valueOf(voice.getScore().toString().split("\\.")[0]));
                vo.setStudentName(voice.getStudentName());
                vo.setVoiceUrl(prefix + voice.getVoiceUrl());
                rankVos.add(vo);
            }
        }
    }
}

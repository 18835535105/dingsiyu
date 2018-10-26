package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.voice.VoiceRankVo;
import com.zhidejiaoyu.common.Vo.student.voice.VoiceVo;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.mapper.UnitMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.mapper.VoiceMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.http.FtpUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GoodVoiceService;
import com.zhidejiaoyu.student.utils.GoodVoiceUtil;
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
public class GoodVoiceServiceImpl extends BaseServiceImpl implements GoodVoiceService {

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
            List<Vocabulary> vocabularies = vocabularyMapper.selectByUnitId(student.getId(), unitId);
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
            List<Sentence> sentences = sentenceMapper.selectByUnitId(student.getId(), unitId);
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
    public ServerResponse saveVoice(HttpSession session, Long unitId, Long wordId, String word, Integer type, MultipartFile audio) {

        Student student = getStudent(session);
        // 上传录音文件
        String fileName = FileConstant.GOOD_VOICE + ftpUtil.uploadGoodVoice(audio, FileConstant.GOOD_VOICE);
        String url = prefix + fileName;
        int score;
        Map<String, Object> map;
        if (type == 1) {
            map = goodVoiceUtil.getWordEvaluationRecord(word, url);
            score = (int) map.get("score");
        } else {
            map = goodVoiceUtil.getSentenceEvaluationRecord(word, url);
            score = (int) map.get("totalScore");
        }
        map.put("voiceUrl", url);

        Long courseId = unitMapper.selectCourseIdByUnitId(unitId);

        saveVoice(unitId, wordId, type, student, fileName, score, courseId);


        return ServerResponse.createBySuccess(map);
    }

    private void saveVoice(Long unitId, Long wordId, Integer type, Student student, String fileName, int record, Long courseId) {
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

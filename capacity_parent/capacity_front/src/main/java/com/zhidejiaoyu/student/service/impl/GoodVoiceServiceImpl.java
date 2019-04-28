package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.voice.VoiceRankVo;
import com.zhidejiaoyu.common.Vo.student.voice.VoiceVo;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.http.FtpUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.FileResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
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
    private TeacherMapper teacherMapper;

    @Autowired
    private VoiceMapper voiceMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StudentMapper studentMapper;

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

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private TeksMapper teksMapper;

    @Override
    public ServerResponse getSubjects(HttpSession session, Long unitId, Integer type, Integer flag) {
        Student student = getStudent(session);
        List<VoiceVo> voiceVos;
        if (type == 1) {
            List<Vocabulary> vocabularies;
            if (flag == 1) {
                vocabularies = vocabularyMapper.selectWordVoice(student.getId(), unitId);
            } else {
                vocabularies = redisOpt.getWordInfoInUnit(unitId);
            }
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
                voiceVo.setVoice(1+"");
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
                voiceVo.setVoice(1+"");
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

        // 全校排行
        List<Voice> schoolVoiceRank = packageSchoolRank(unitId, wordId, type, student);

        packageVoiceRankVo(classRankVos, schoolVoiceRank);

        Map<String, List<VoiceRankVo>> map = new HashMap<>(16);
        map.put("classRankVos", classRankVos);
        map.put("countryRankVos", countryRankVos);

        return ServerResponse.createBySuccess(map);
    }

    private List<Voice> packageSchoolRank(Long unitId, Long wordId, Integer type, Student student) {
        Long teacherId = student.getTeacherId();
        List<Voice> schoolVoiceRank = null;
        if (teacherId == null) {
           schoolVoiceRank = voiceMapper.selectTeacherIdIsNull(unitId, wordId, type);
        } else {
            // 判断教师角色，如果是教师，查找其校管，再查找其所管辖的所有教师；如果是校管查找其所管辖的所有教师
            List<Teacher> teachers;
            SysUser sysUser = sysUserMapper.selectById(teacherId);
            if (sysUser != null && sysUser.getRoleid().equals(UserConstant.SCHOOL_ADMIN_ROLE_ID.toString())) {
                // 校管角色
                teachers = teacherMapper.selectBySchoolAdminId(sysUser.getId());
                if (teachers.size() > 0) {
                    schoolVoiceRank = voiceMapper.selectSchoolRank(teachers, sysUser.getId(), unitId, wordId, type);
                }
            } else {
                // 教师角色
                Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(teacherId);
                teachers = teacherMapper.selectBySchoolAdminId(schoolAdminId);
                if (teachers.size() > 0) {
                    schoolVoiceRank = voiceMapper.selectSchoolRank(teachers, schoolAdminId, unitId, wordId, type);
                }
            }
        }
        return schoolVoiceRank;
    }

    @Override
    public ServerResponse saveVoice(HttpSession session, Voice voice, String word, MultipartFile audio,Integer type) {

        Student student = getStudent(session);
        log.info("学生[" + student.getId() + " -> " + student.getAccount() + "] 上传" + (Objects.equals(voice.getType(), 1) ? "单词" : "句型") + "[" + voice.getWordId() + " -> " + word + "] 录音");

        // 文件大于2M禁止上传，讯飞语音评测接口最大支持2M音频文件
        long maxSize = 2097152L;
        if (audio.getSize() > maxSize) {
            return ServerResponse.createBySuccess(FileResponseCode.TOO_LARGE.getCode(), FileResponseCode.TOO_LARGE.getMsg());
        }

        // 上传录音文件
        String file=  ftpUtil.uploadGoodVoice(audio, FileConstant.GOOD_VOICE);
        String fileName=FileConstant.GOOD_VOICE +file;



        String url = prefix + fileName;
        int score=0;
        Map<String, Object> map;
        if (voice.getType() == 1) {
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

        Long courseId = unitMapper.selectCourseIdByUnitId(voice.getUnitId());
        if(file != null){
            voice.setCourseId(courseId);
            voice.setCreateTime(new Date());
            voice.setScore(score * 1.0);
            voice.setStudentId(student.getId());
            voice.setStudentName(student.getStudentName());
            voice.setVoiceUrl(fileName);
            voiceMapper.insert(voice);
        }
        if(type==2){
            Learn learn =new Learn ();
            learn.setLearnTime(new Date());
            learn.setUpdateTime(new Date());
            learn.setCourseId(voice.getCourseId());
            learn.setStudentId(voice.getStudentId());
            learn.setUnitId(voice.getUnitId());
            learn.setStudyModel("课文好声音");
            learn.setType(1);
            Long aLong = learnMapper.selTeksLearn(learn);
            if(aLong!=null){
                learn.setId(aLong);
                learnMapper.updTeksLearn(learn);
            }else{
                learnMapper.insert(learn);
            }
        }
        return ServerResponse.createBySuccess(map);
    }

    private void packageVoiceRankVo(List<VoiceRankVo> rankVos, List<Voice> voiceRank) {
        if (voiceRank != null && voiceRank.size() > 0) {
            List<Long> studentIds = new ArrayList<>(voiceRank.size());
            voiceRank.forEach(voice -> studentIds.add(voice.getStudentId()));
            Map<Long, Map<Long, String>> hearUrlMap = studentMapper.selectHeadUrlMapByStudentId(studentIds);
            VoiceRankVo vo;
            for (Voice voice : voiceRank) {
                vo = new VoiceRankVo();
                vo.setScore(Integer.valueOf(voice.getScore().toString().split("\\.")[0]));
                vo.setStudentName(voice.getStudentName());
                vo.setVoiceUrl(prefix + voice.getVoiceUrl());
                if (hearUrlMap.get(voice.getStudentId()) != null) {
                    vo.setHeadUrl(hearUrlMap.get(voice.getStudentId()).get("headUrl"));
                } else {
                    vo.setHeadUrl("static/img/portrait/17.png");
                }
                rankVos.add(vo);
            }
        }
    }
}

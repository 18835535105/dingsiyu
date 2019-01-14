package com.zhidejiaoyu.student.service.impl;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.student.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.service.TeksService;
import com.zhidejiaoyu.student.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;


@Service
public class TeksServiceImpl extends BaseServiceImpl<TeksMapper, Teks> implements TeksService

{


    /**
     * 80分
     */
    private static final int PASS = 80;

    /**
     * 100分
     */
    private static final int FULL_MARK = 100;


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

    @Autowired
    private TestService testService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private VoiceMapper voiceMapper;

    @Value("${ftp.prefix}")
    private String prefix;


    @Override
    public ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId) {
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        if(teks.size()>0){
            List<Teks> resultTeks=new ArrayList<>();
            int i=0;
            for(Teks teks1:teks){
                teks1.setPronunciation(baiduSpeak.getSentencePaht(teks1.getSentence()));
                i++;
                resultTeks.add(teks1);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> selSpeakTeksByUnitId(Integer unitId,HttpSession session) {
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        Map<String,Object> map=new HashMap<>();
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        Map<String,Object> getMap=new HashMap<>();
        getMap.put("studentId",student.getId());
        getMap.put("unitId",unitId);
        Integer integer = voiceMapper.selMaxCountByUnitIdAndStudentId(getMap);
        if(teks.size()>0){
            List<Teks> resultTeks=new ArrayList<>();
            int i=0;
            for(Teks teks1:teks){
                teks1.setPronunciation(baiduSpeak.getSentencePaht(teks1.getSentence()));
                i++;
                resultTeks.add(teks1);
            }
            map.put("list",resultTeks);
            if(integer==null){
                map.put("count",0);
            }else{
                map.put("count",integer);
            }

            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> selHistoryByCountAndUnitId(Integer count, Integer unitId, HttpSession session) {
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        Map<String,Object> maps=new HashMap<>();
        maps.put("unitId",unitId);
        maps.put("studentId",student.getId());
        maps.put("count",count);
        List<Map<String,Object>> map=teksMapper.selHistoryByCountAndUnitId(maps);
        Map<String,Object> result=new HashMap<>();
        List<Map<String,Object>> resultMap=new ArrayList<>();
        for(Map<String,Object> getMap:map){
            getMap.put("url",prefix+getMap.get("url"));
            resultMap.add(getMap);
        }
        result.put("list",resultMap);
        result.put("count",count);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> selRankingList(Integer unitId,HttpSession session) {
        Student student =(Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Map<String,Object> getMap=new HashMap<>();
        getMap.put("schoolName",student.getSchoolName());
        getMap.put("unitId",unitId);
        Map<String,Object> result=new HashMap<>();
        //全国排名
        List<Map<String, Object>> maps = voiceMapper.selectTeksRank(getMap);
        result.put("nationalRanking",maps);
        //全校排名
        List<Map<String, Object>> mapss = voiceMapper.selectTeksRankSchool(getMap);
        result.put("shcoolRanking",mapss);
        return ServerResponse.createBySuccess(result);
    }



    @Override
    public ServerResponse<Object> selChooseTeks(Integer unitId,HttpSession session) {
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        session.setAttribute(TimeConstant.BEGIN_START_TIME,new Date());
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        //判断是否取出数据
        if(teks.size()>0){
            //返回的集合
            List<Map<String,Object>> resultList=new ArrayList<>();
            Map<String,Object> resultMap=new HashMap<>();
            //遍历数据
            for(Teks teks1:teks){
                //将遍历的数据放入到
                Map<String,Object> map=new HashMap<>();
                map.put("chinese",teks1.getParaphrase());
                map.put("pronunciation",baiduSpeak.getLanguagePath(teks1.getSentence()));
                map.put("id",teks1.getId());
                String[] sentenceList = teks1.getSentence().split(" ");
                List blankSentenceArray=new ArrayList();
                List sentence=new ArrayList();
                //获取填空位置
                for(int i=0;i<sentenceList.length;i++){
                    if(sentenceList[i].endsWith(",")||sentenceList[i].endsWith(".")||sentenceList[i].endsWith("?")||sentenceList[i].endsWith("!")){
                        blankSentenceArray.add(null);
                        blankSentenceArray.add(sentenceList[i].substring(sentenceList[i].length()-1));
                        sentence.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                        sentence.add(sentenceList[i].substring(sentenceList[i].length()-1));
                    }else{
                        blankSentenceArray.add(null);
                        sentence.add(sentenceList[i]);
                    }
                    //返回的填空单词 以及句子填空位置
                    map.put("vocabularyArray",commonMethod.getOrderEnglishList(teks1.getSentence(),null));
                    map.put("blankSentenceArray",blankSentenceArray);
                    map.put("sentence",sentence);
                }
                resultList.add(map);
                resultMap.put("list",resultList);
                resultMap.put("number",teks.size());
                Map<String,Object> selMap=new HashMap<>();
                selMap.put("unitId",unitId);
                selMap.put("studentId",student.getId());
                selMap.put("model","课文测试");
                Integer integer = testRecordMapper.selectMaxPointByUnitStudentModel(selMap);
                if(integer==null){
                    resultMap.put("maxScore",0);
                }else{
                    resultMap.put("maxScore",integer);
                }
            }

            return ServerResponse.createBySuccess(resultMap);
        }
        return ServerResponse.createByError();
    }

    @Override
    @SuppressWarnings("all")
    public ServerResponse<Map<String,Object>> getCourseAndUnit(HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<CourseUnitVo> courseUnitVos = new ArrayList<>();
        CourseUnitVo courseUnitVo;
        Map<String,Object> studyMap=null;
        List<Map<String, Object>> resultMap;
        Map<String,Object> returnMap=new HashMap<>();
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
                courseUnitVo.setVersion(courseMap.get("version").toString());
                courseUnitVo.setGrad(courseMap.get("grade").toString()+courseMap.get("label").toString());
                // 存放单元信息
                Map<String, Object> unitInfoMap;
                for (Map<String, Object> unitMap : textUnits) {
                    unitInfoMap = new HashMap<>(16);
                    if (Objects.equals(courseMap.get("id"), unitMap.get("courseId"))) {
                        if(studyMap==null){
                            studyMap=new HashMap<>();
                            studyMap.put("unitId", unitMap.get("id"));
                            studyMap.put("unitName", unitMap.get("unitName"));
                            studyMap.put("version",unitMap.get("version"));
                            studyMap.put("grade",unitMap.get("grade").toString()+unitMap.get("label").toString());
                        }
                        unitInfoMap.put("unitId", unitMap.get("id"));
                        unitInfoMap.put("unitName", unitMap.get("unitName"));
                        unitInfoMap.put("version",unitMap.get("version"));
                        unitInfoMap.put("grade",unitMap.get("grade").toString()+unitMap.get("label").toString());
                        if(unitMap.get("number")==null){
                            unitInfoMap.put("number",0);
                        }else{
                            unitInfoMap.put("number",unitMap.get("number"));
                        }
                        unitInfoMap.put("number",unitMap.get("number"));
                        if (unitMap.get("number") != null && (Long)unitMap.get("number")>0) {
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
            List<Map<String,Object>> testList=teksMapper.getStudentAllCourse(studentId, courseIds);
            returnMap.put("present",studyMap);
           returnMap.put("versionList",testList);
           returnMap.put("list",courseUnitVos);
        }


        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public ServerResponse<Object> selWriteTeks(Integer unitId) {
        //获取课文句子
        List<Teks> listTeks = teksMapper.selTeksByUnitId(unitId);
        //返回的数据集合
        List<Object> resultTeks=new ArrayList<>();
        if(listTeks.size()>0){
            for(Teks teks:listTeks){
                //保存返回的数据
                Map<String,Object> map=new HashMap<>();
                map.put("chinese",teks.getParaphrase());
                map.put("pronunciation",baiduSpeak.getSentencePaht(teks.getSentence()));
                map.put("sentence",teks.getSentence());
                map.put("id",teks.getId());
                String[] sentenceList = teks.getSentence().split(" ");
                int[] integers;
                //获取空格出现的位置
               /* if(sentenceList.length>6){
                    integers=wirterBlank(sentenceList.length,3);
                }else*/ if(sentenceList.length>3){
                    integers=wirterBlank(sentenceList.length,2);
                }else{
                    integers=wirterBlank(sentenceList.length,1);
                }
                List<String> blanceSentence=new ArrayList<>();
                List<String> vocabulary=new ArrayList<>();
                if(integers.length==1){
                    //当空格数为一时调用
                    for(int i=0;i<sentenceList.length;i++){
                        if(i==(integers[0]-1)){
                            addList(sentenceList[i],blanceSentence,vocabulary);
                        }else{
                            if(sentenceList[i].endsWith(",")||sentenceList[i].endsWith(".")||sentenceList[i].endsWith("?")||sentenceList[i].endsWith("!")){
                                vocabulary.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                                vocabulary.add(sentenceList[i].substring(sentenceList[i].length()-1));
                                blanceSentence.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                                blanceSentence.add(sentenceList[i].substring(sentenceList[i].length()-1));
                            }else{
                                vocabulary.add(sentenceList[i]);
                                blanceSentence.add(sentenceList[i]);
                            }
                        }
                    }
                    map.put("blanceSentence",blanceSentence);
                    map.put("vocabulary",vocabulary);
                }else if(integers.length==2){
                    //当空格数为二时调用
                    for(int i=0;i<sentenceList.length;i++){
                        if(i==(integers[0]-1)||i==(integers[1]-1)){
                           addList(sentenceList[i],blanceSentence,vocabulary);
                        }else{
                            if(sentenceList[i].endsWith(",")||sentenceList[i].endsWith(".")||sentenceList[i].endsWith("?")||sentenceList[i].endsWith("!")){
                                vocabulary.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                                vocabulary.add(sentenceList[i].substring(sentenceList[i].length()-1));
                                blanceSentence.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                                blanceSentence.add(sentenceList[i].substring(sentenceList[i].length()-1));
                            }else{
                                vocabulary.add(sentenceList[i]);
                                blanceSentence.add(sentenceList[i]);
                            }
                        }
                    }
                    map.put("blanceSentence",blanceSentence);
                    map.put("vocabulary",vocabulary);
                }else if(integers.length==3){
                    //当空格数为二时调用
                    for(int i=0;i<sentenceList.length;i++){
                        if(i==(integers[0]-1)||i==(integers[1]-1)||i==(integers[2]-1)){
                            addList(sentenceList[i],blanceSentence,vocabulary);
                        }else{
                            if(sentenceList[i].endsWith(",")||sentenceList[i].endsWith(".")||sentenceList[i].endsWith("?")||sentenceList[i].endsWith("!")){
                                vocabulary.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                                vocabulary.add(sentenceList[i].substring(sentenceList[i].length()-1));
                                blanceSentence.add(sentenceList[i].substring(0,sentenceList[i].length()-1));
                                blanceSentence.add(sentenceList[i].substring(sentenceList[i].length()-1));
                            }else{
                                vocabulary.add(sentenceList[i]);
                                blanceSentence.add(sentenceList[i]);
                            }
                        }
                    }
                    map.put("blanceSentence",blanceSentence);
                    map.put("vocabulary",vocabulary);
                }

                resultTeks.add(map);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }


        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> addData(TestRecord testRecord, HttpSession session) {
        //学生对象
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        //测试开始时间
        Date startTime = (Date)session.getAttribute(TimeConstant.BEGIN_START_TIME);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        //测试结束时间
        Date endTime=new Date();
        //添加学习模块
        testRecord.setStudyModel("课文测试");
        //获取课程id
        Long aLong = unitMapper.selectCourseIdByUnitId(testRecord.getUnitId());
        //添加金币
        WordUnitTestDTO wordUnitTestDTO=new WordUnitTestDTO();
        wordUnitTestDTO.setClassify(7);
        Integer point = testRecord.getPoint();
        Integer goldCount=0;
        if (point >= PASS) {
            if (point < FULL_MARK) {
                goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_FULL;
                this.saveLog(student, goldCount, wordUnitTestDTO, "课文测试");
            } else if (point == FULL_MARK) {
                goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
                this.saveLog(student, goldCount, wordUnitTestDTO, "课文测试");
            }
        }
        testRecord.setGenre("课文测试");
        testRecord.setAwardGold(goldCount);
        //添加对象
        testRecord.setStudentId(student.getId());
        testRecord.setCourseId(aLong );
        testRecord.setTestStartTime(startTime);
        testRecord.setTestEndTime(endTime);
        testRecordMapper.insert(testRecord);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> selHistoryPronunciation(Integer unitId,HttpSession session) {
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        Map<String,Object> maps=new HashMap<>();
        maps.put("unitId",unitId);
        maps.put("studentId",student.getId());
        List<Map<String,Object>> map=teksMapper.selHistoryPronunciation(maps);
        List<Map<String,Object>> resultMap=new ArrayList<>();
        for(Map<String,Object> getMap:map){
            getMap.put("url",prefix+getMap.get("url"));
            resultMap.add(getMap);
        }
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public ServerResponse<Object> isHistoryPronunciation(Integer unitId, HttpSession session) {
        Student student = (Student)session.getAttribute(UserConstant.CURRENT_STUDENT);
        Map<String,Object> maps=new HashMap<>();
        maps.put("unitId",unitId);
        maps.put("studentId",student.getId());
        Integer map=teksMapper.isHistoryPronunciation(maps);
        if(map!=null && map>0){
            return ServerResponse.createBySuccess(true);
        }else{
            return ServerResponse.createBySuccess(false);
        }
    }



    //判断空格出现位置
    public int[] wirterBlank(Integer number , Integer choose ){
        int[] integers=new int[choose];
        int s=0;
        if(choose==1){
            integers[0]=((int)(Math.random()*number))+1;
        }
        if(choose==2){
            while (integers[choose-1]==0){
                int i=((int)(Math.random()*number))+1;
                if(choose==2&&s==1){
                    if(i!=integers[0]){
                        integers[s]=i;
                    }
                }else{
                    integers[s] = i;
                }
                if(s!=1){
                    s++;
                }
            }
        }
        /*if(choose==3){
            while(integers[choose-1]==0){
                int i=((int)(Math.random()*number))+1;
                if(s==0){
                    integers[0]=i;
                }
                if(s==1){
                    if(integers[0]!=i){
                        integers[1]=i;
                        s++;
                    }
                }
                if(s==2){
                    if(integers[0]!=i&&integers[1]!=i){
                        integers[2]=i;
                        s++;
                    }
                }

            }
        }*/


        return integers;
    }


    //添加数据
    public void addList(String str, List<String> blanceSentence,List<String> vocabulary){
        if(str.endsWith(",")||str.endsWith(".")||str.endsWith("?")||str.endsWith("!")){
            vocabulary.add(str.substring(0,str.length()-1));
            vocabulary.add(str.substring(str.length()-1));
            blanceSentence.add(null);
            blanceSentence.add(str.substring(str.length()-1));
        }else{
            vocabulary.add(str);
            blanceSentence.add(null);
        }
    }

    /**
     * 保存金币变化日志信息
     *
     * @param student
     * @param goldCount       奖励金币数
     * @param wordUnitTestDTO
     * @param model           测试模块
     */
    private void saveLog(Student student, int goldCount, WordUnitTestDTO wordUnitTestDTO, String model) {
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldCount));
        studentMapper.updateByPrimaryKeySelective(student);
        String msg;
        if (wordUnitTestDTO != null) {
            msg = "id为：" + student.getId() + "的学生在" + commonMethod.getTestType(wordUnitTestDTO.getClassify())
                    + " 模块下的单元闯关测试中首次闯关成功，获得#" + goldCount + "#枚金币";
        } else {
            msg = "id为：" + student.getId() + "的学生在" + model + " 模块下，获得#" + goldCount + "#枚金币";
        }
        RunLog runLog = new RunLog(student.getId(), 4, msg, new Date());
        runLog.setCourseId(student.getCourseId());
        runLog.setUnitId(student.getUnitId());
        runLogMapper.insert(runLog);
    }


    /**
     * 获取课文测试
     */
    @Override
    public ServerResponse<Object> getTeksTest(Integer unitId) {
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        List<Teks> addTeks=null;
        if(teks.size()<4){
            addTeks=teksMapper.getTwentyTeks();
        }
        return getReturnTestTeks(teks,addTeks);
    }

    private ServerResponse<Object> getReturnTestTeks(List<Teks> teks,List<Teks> addTeks){
        List<Map<String,Object>> returnList = new ArrayList<>();
        List<Teks> optionList=new ArrayList<>();
        optionList.addAll(teks);
        if(addTeks!=null){
            optionList.addAll(addTeks);
        }
        for(int i=0;i<teks.size();i++){
            Collections.shuffle(optionList);
            List<Teks> list=new ArrayList<>();
            list.addAll(optionList);
            list.remove(teks.get(i));
            Integer ss=(int)Math.ceil((Math.random()*2));
            Map<String,Object> returnMap=new HashMap<>();
            returnMap.put("chinese",teks.get(i).getParaphrase());
            returnMap.put("pronunciation",baiduSpeak.getSentencePaht(teks.get(i).getSentence()));
            returnMap.put("english",teks.get(i).getSentence());
            //英选汉
            if(ss==1){
                returnMap.put("type","BritishElectHan");
                BritishElectHan(teks.get(i),list.subList(0,3),returnMap);
            //汉选英
            }else{
                returnMap.put("type","HanElectBritish");
                HanElectBritish(teks.get(i),list.subList(0,3),returnMap);
            }
            returnList.add(returnMap);
        }
        return ServerResponse.createBySuccess(returnList);
    }

    private void HanElectBritish(Teks teks, List<Teks> optionList, Map<String, Object> returnMap) {
        List<String> option=new ArrayList<>();
        option.add(teks.getSentence());
        for(int i=0;i<3;i++){
            option.add(optionList.get(i).getSentence());
        }
        Collections.shuffle(option);
        for(int i=0;i<option.size();i++){
            if(option.get(i).equals(teks.getSentence())){
                returnMap.put("answer",i);
            }
        }
        returnMap.put("option",option);

    }

    private void BritishElectHan(Teks teks, List<Teks> optionList, Map<String, Object> returnMap) {
       optionList.add(teks);
       Collections.shuffle(optionList);
       List<String> english=new ArrayList<>();
       List<String> option=new ArrayList<>();
       for(int i=0;i<optionList.size();i++){
           if(optionList.get(i).getSentence().equals(teks.getSentence())){
               returnMap.put("answer",i);
           }
            english.add(optionList.get(i).getSentence());
            option.add(baiduSpeak.getSentencePaht(optionList.get(i).getSentence()));
       }
        returnMap.put("option",option);
    }


}

package com.zhidejiaoyu.student.service.simple.impl;

import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.common.Vo.simple.GameTwoVo;
import com.zhidejiaoyu.common.Vo.simple.StrengthGameVo;
import com.zhidejiaoyu.common.Vo.simple.StudentGauntletVo;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.SimpleLevelUtils;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.common.utils.simple.language.SimpleBaiduSpeak;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.simple.SimpleIGauntletServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

/**
 * <p>
 * 挑战书表； 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Service
public class SimpleGauntletServiceImplSimple extends SimpleBaseServiceImpl<SimpleGauntletMapper, Gauntlet> implements SimpleIGauntletServiceSimple {

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;

    @Autowired
    private SimpleGauntletMapper simpleGauntletMapper;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleBaiduSpeak simpleBaiduSpeak;

    @Autowired
    private SimpleStudentStudyPlanMapper simpleStudentStudyPlanMapper;

    @Autowired
    private SimpleSimpleStudentUnitMapper simpleSimpleStudentUnitMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private SimpleLevelMapper simpleLevelMapper;

    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

    @Override
    public ServerResponse<Map<String, Object>> getStudentByType(HttpSession session, Integer type, Integer page, Integer rows, String account) {
        //获取学生
        Student student = getStudent(session);
        //更改过时挑战
        delGauntlets(student);
        Long schoolAdminId = null;
        List<Integer> teachers = null;
        List<StudentGauntletVo> classOrSchoolStudents = null;
        Map<String, Object> returnMap = new HashMap<>();
        //获取教师id
        if (type == 2) {
            if (student.getTeacherId() != null) {
                Integer schoolAdminById = simpleTeacherMapper.getSchoolAdminById(student.getTeacherId().intValue());
                if (schoolAdminById == null) {
                    Integer teacherCountByAdminId = simpleTeacherMapper.getTeacherCountByAdminId(student.getTeacherId());
                    if (teacherCountByAdminId != null && teacherCountByAdminId > 0) {
                        schoolAdminId = student.getTeacherId();
                    }
                } else {
                    schoolAdminId = schoolAdminById.longValue();
                }
            }
            teachers = simpleTeacherMapper.getTeacherIdByAdminId(schoolAdminId.intValue());
        }
        Integer integer = 0;
        //获取学生数据数量
        if (schoolAdminId != null) {
            integer = simpleStudentMapper.selNumberById(student.getClassId(), student.getTeacherId(), type.toString(), schoolAdminId.intValue(), teachers, account, student.getId());
        } else {
            integer = simpleStudentMapper.selNumberById(student.getClassId(), student.getTeacherId(), type.toString(), null, teachers, account, student.getId());
        }

        returnMap.put("total", integer % rows > 0 ? integer / rows + 1 : integer / rows);
        Integer start = (page - 1) * rows;
        returnMap.put("page", page);
        returnMap.put("rows", rows);
        //获取每页显示的学生数据
        if (type == 1) {
            classOrSchoolStudents = simpleStudentMapper.getClassOrSchoolStudents(student.getClassId(), student.getTeacherId(), teachers, schoolAdminId, 1, start, rows, account, student.getId());
        } else if (type == 2) {
            classOrSchoolStudents = simpleStudentMapper.getClassOrSchoolStudents(student.getClassId(), student.getTeacherId(), teachers, schoolAdminId, 2, start, rows, account, student.getId());
        }
        List<StudentGauntletVo> listStudentGauntletVo = new ArrayList<>();
        if (classOrSchoolStudents != null && classOrSchoolStudents.size() > 0) {
            for (StudentGauntletVo studentGauntletVo : classOrSchoolStudents) {
                if (studentGauntletVo.getId().equals(student.getId())) {
                    continue;
                }
                //填装学生数据
                getStudentGauntletVo(studentGauntletVo, 2, student.getId());
                listStudentGauntletVo.add(studentGauntletVo);
            }
        }
        //返回数据
        returnMap.put("data", listStudentGauntletVo);
        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public ServerResponse<StudentGauntletVo> getStudyInteger(HttpSession session) {
        //获取个人的数据
        Student student = getStudent(session);
        List<Gauntlet> gauntlets = simpleGauntletMapper.selByStudentIdAndFormat(student.getId(), new Date());
        StudentGauntletVo studentGauntletVo = new StudentGauntletVo();
        studentGauntletVo.setId(student.getId());
        studentGauntletVo.setName(student.getNickname());
        studentGauntletVo.setHeadUrl(student.getHeadUrl());
        //获取每天的挑战次数
        if (gauntlets != null && gauntlets.size() > 0) {
            boolean isTrue = true;
            for (Gauntlet gauntlet : gauntlets) {
                if (gauntlet.getChallengeStatus() != 3 && gauntlet.getChallengeStatus() != 4) {
                    isTrue = false;
                }
            }
            if (isTrue) {
                int size = gauntlets.size();
                studentGauntletVo.setPkNum(3 - size);
            } else {
                studentGauntletVo.setPkNum(0);
            }
        } else {
            studentGauntletVo.setPkNum(3);
        }
        //整理挑战数据
        getStudentGauntletVo(studentGauntletVo, 1, student.getId());
        return ServerResponse.createBySuccess(studentGauntletVo);
    }

    /**
     * 添加挑战
     *
     * @param session
     * @param gameName
     * @param studentId
     * @param gold
     * @param courseId
     * @param challengerMsg
     * @return
     */
    @Override
    public ServerResponse<Object> addPkRecord(HttpSession session, String gameName, Long studentId, Integer gold, Long courseId, String challengerMsg) {
        Student student = getStudent(session);
        Gauntlet gauntlet = new Gauntlet();
        //获取今日是否有挑战的数据
        List<Gauntlet> gauntlets = simpleGauntletMapper.selByStudentIdAndFormat(student.getId(), new Date());
        //获取挑战人学习力等数据
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(student.getId());
        //获取被挑战人学习力等数据
        StudentExpansion studentExpansion1 = simpleStudentExpansionMapper.selectByStudentId(studentId);
        Integer challengerStudyPower = 0;
        Integer beChallengerStudyPower = 0;
        if (studentExpansion != null) {
            challengerStudyPower = studentExpansion.getStudyPower();
        }
        if (studentExpansion1 != null) {
            beChallengerStudyPower = studentExpansion1.getStudyPower();
        }
        if (gauntlets != null && gauntlets.size() > 0) {
            //在今日不是第一次挑战时获取挑战数据添加数据库
            Gauntlet gauntlet1 = gauntlets.get(0);
            addGauntlet(gauntlet, student.getId(), studentId, courseId, gold, gameName, gauntlet1.getChallengerPoint(),
                    null, challengerMsg, 3, 3, null,
                    null, null, null, new Date(), challengerStudyPower, beChallengerStudyPower);
        } else {
            //在今日是第一次挑战时添加数据库
            addGauntlet(gauntlet, student.getId(), studentId, courseId, gold, gameName, null,
                    null, challengerMsg, 3, 3, null,
                    null, null, null, new Date(), challengerStudyPower, beChallengerStudyPower);
        }
        Integer insert = simpleGauntletMapper.insert(gauntlet);
        if (insert > 0) {
            return ServerResponse.createBySuccess(gauntlet.getId());
        }
        return ServerResponse.createByError();
    }

    /**
     * 获取游戏
     *
     * @param pageNum
     * @param courseId
     * @param gameName
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getGame(Integer pageNum, Long courseId, String gameName, HttpSession session) {
        Student student = getStudent(session);
        if ("找同学".equals(gameName)) {
            List<Map<String, Object>> subjects = new ArrayList<>();
            getGameOne(courseId, subjects);
            Map<String, Object> map = new HashMap<>();
            map.put("testResults", subjects);
            map.put("petUrl", student.getPartUrl());
            return ServerResponse.createBySuccess(map);
        }
        if ("桌牌捕音".equals(gameName)) {
            ServerResponse<Object> gameTwo = getGameTwo(courseId);
            return gameTwo;
        }
        if ("冰火两重天".equals(gameName)) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> subjects = new ArrayList<>();
            getGameThree(subjects, pageNum, courseId);
            map.put("matchKeyValue", subjects);
            return ServerResponse.createBySuccess(map);
        }
        if ("实力初显".equals(gameName)) {
            Map<String, Object> map = new HashMap<>();
            getGameFour(map, courseId, student);
            if (map != null) {
                return ServerResponse.createBySuccess(map);
            }
        }
        return null;
    }

    /**
     * 获取游戏的教材版本
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getCourse(HttpSession session) {
        Student student = getStudent(session);
        List<Long> courseIds = simpleStudentStudyPlanMapper.getCourseId(student.getId());
        courseIds.addAll(simpleStudentUnitMapper.getAllCourseIdByTypeToStudent(student.getId(), 2));
        List<Map<String, Object>> courses = simpleCourseMapper.getCourseByIds(courseIds);
        return ServerResponse.createBySuccess(courses);
    }

    /**
     * 查看挑战被挑战数据
     *
     * @param type
     * @param challengeType
     * @param pageNum
     * @param rows
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getChallenge(Integer type, Integer challengeType, Integer pageNum, Integer rows, HttpSession session) {
        Long studentId = getStudentId(session);
        Map<String, Object> returnMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date time = calendar.getTime();
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simple.format(time);
        int start = (pageNum - 1) * rows;
        //根据type的不同来区分是查询我发出的挑战还是挑战我的数据
        List<Gauntlet> gauntlets = simpleGauntletMapper.selGauntletByTypeAndChallengeType(type, challengeType, start, rows, studentId, format);
        //获取要查询的挑战数量
        Integer count = simpleGauntletMapper.getCount(type, challengeType, studentId, format);
        returnMap.put("page", pageNum);
        returnMap.put("rows", rows);
        List<Map<String, Object>> list = new ArrayList<>();
        returnMap.put("total", count % rows > 0 ? count / rows + 1 : count / rows);
        getGauntlet(list, gauntlets, type);
        returnMap.put("data", list);
        return ServerResponse.createBySuccess(returnMap);
    }

    /**
     * 保存挑战的数据
     *
     * @param gauntletId
     * @param type
     * @param isDelete
     * @param point
     * @param concede
     * @return
     */
    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveResult(Long gauntletId, Integer type, Integer isDelete, Integer point, String concede) {
        //根据挑战id获取挑战数据
        Gauntlet gauntlet = simpleGauntletMapper.selectById(gauntletId);
        if (type == 1) {
            //发起者保存分数
            gauntlet.setChallengerPoint(point);
            simpleGauntletMapper.updateById(gauntlet);
        } else {
            if (isDelete == 2) {
                //挑战者拒绝挑战
                gauntlet.setChallengeStatus(6);
                gauntlet.setBeChallengerStatus(6);
                gauntlet.setConcede(concede);
                simpleGauntletMapper.updateById(gauntlet);
            } else {
                //挑战者接受挑战保存分数
                //获取加成比例
                List<Map<String, Object>> levels = redisOpt.getAllLevel();
                //查询挑战学生信息
                Student challengerStudnet = simpleStudentMapper.selectByPrimaryKey(gauntlet.getChallengerStudentId());
                Double challengeGold = challengerStudnet.getSystemGold() + challengerStudnet.getOfflineGold();
                //获取挑战学生等级信息
                int challengeLevel = getLevels(challengeGold.intValue(), levels);
                //查询被挑战学生信息
                Student beChallengerStudnet = simpleStudentMapper.selectByPrimaryKey(gauntlet.getBeChallengerStudentId());
                Double beChallengeGold = beChallengerStudnet.getSystemGold() + beChallengerStudnet.getOfflineGold();
                //获取被挑战学生等级信息
                int beChallengeLevel = getLevels(beChallengeGold.intValue(), levels);
                Map<String, Double> map = new HashMap<>();
                if (gauntlet.getChallengerPoint() - point > 0) {
                    //获取挑战人勋章数量
                    List<Map<String, Object>> maps = simpleAwardMapper.selAwardCountByStudentId(challengerStudnet.getId());
                    //获得加成
                    map = SimpleLevelUtils.getAddition(challengeLevel, beChallengeLevel, 1, challengerStudnet.getId(), map, maps);
                } else if (gauntlet.getChallengerPoint() - point == 0) {
                    List<Map<String, Object>> maps = simpleAwardMapper.selAwardCountByStudentId(challengerStudnet.getId());
                    map = SimpleLevelUtils.getAddition(challengeLevel, beChallengeLevel, 2, challengerStudnet.getId(), map, maps);
                }
                gauntlet.setBeChallengerPoint(point);
                //根据得分计算加成数量及保存数据
                //被挑战人分数小于挑战人分数
                if (gauntlet.getChallengerPoint() > point) {
                    gauntlet.setBeChallengerPoint(point);
                    gauntlet.setChallengeStatus(1);
                    gauntlet.setBeChallengerStatus(2);
                    int study = gauntlet.getChallengerPoint() - point;
                    gauntlet.setChallengeStudy(study);
                    gauntlet.setBeChallengeStudy(study);
                    Double level = map.get("level");
                    Double levelGold = 0.0;
                    if (level != null) {
                        levelGold = gauntlet.getBetGold() * level;
                    }
                    Double award = map.get("award");
                    Double awardGold = 0.0;
                    if (award != null) {
                        awardGold = gauntlet.getBetGold() * award;
                    }
                    gauntlet.setGrade(levelGold.intValue());
                    gauntlet.setAward(awardGold.intValue());
                    Integer goldChallenge = gauntlet.getBetGold() + levelGold.intValue() + awardGold.intValue();
                    gauntlet.setChallengeGold(goldChallenge);
                    gauntlet.setBeChallengeGold(gauntlet.getBetGold());
                    addGoldAndStudy(gauntlet.getChallengerStudentId(), gauntlet.getBeChallengerStudentId(), study, goldChallenge.intValue(), gauntlet.getBetGold());
                    //分数相等
                } else if (gauntlet.getChallengerPoint().equals(point)) {
                    gauntlet.setBeChallengerPoint(point);
                    gauntlet.setChallengeStatus(5);
                    gauntlet.setBeChallengerStatus(5);
                    gauntlet.setChallengeStudy(0);
                    gauntlet.setBeChallengeStudy(0);
                    Double level = map.get("level");
                    Double levelGold = 0.0;
                    if (level != null) {
                        levelGold = gauntlet.getBetGold() * level;
                    }
                    Double award = map.get("award");
                    Double awardGold = 0.0;
                    if (award != null) {
                        awardGold = gauntlet.getBetGold() * award;
                    }
                    gauntlet.setGrade(levelGold.intValue());
                    gauntlet.setAward(awardGold.intValue());
                    Integer goldChallenge = gauntlet.getBetGold() + levelGold.intValue() + awardGold.intValue();
                    gauntlet.setChallengeGold(goldChallenge);
                    gauntlet.setBeChallengeGold(0);
                    if (goldChallenge > 0) {
                        Student winnerStudent = simpleStudentMapper.selectByPrimaryKey(gauntlet.getChallengerStudentId());
                        winnerStudent.setSystemGold(winnerStudent.getSystemGold() + goldChallenge);
                        simpleStudentMapper.updateByPrimaryKey(winnerStudent);
                        RunLog runLog = new RunLog();
                        runLog.setOperateUserId(gauntlet.getChallengerStudentId());
                        runLog.setType(4);
                        runLog.setCreateTime(new Date());
                        runLog.setLogContent("pk对战获得#" + goldChallenge + "#金币");
                        runLogMapper.insert(runLog);
                    }
                } else {
                    //被挑战人分数大于挑战人分数
                    int study = point - gauntlet.getChallengerPoint();
                    gauntlet.setBeChallengerPoint(point);
                    gauntlet.setChallengeStatus(2);
                    gauntlet.setBeChallengerStatus(1);
                    gauntlet.setChallengeStudy(study);
                    gauntlet.setBeChallengeStudy(study);
                    gauntlet.setChallengeGold(gauntlet.getBetGold());
                    gauntlet.setBeChallengeGold(gauntlet.getBetGold());
                    gauntlet.setGrade(0);
                    gauntlet.setAward(0);
                    addGoldAndStudy(gauntlet.getBeChallengerStudentId(), gauntlet.getChallengerStudentId(), study, gauntlet.getBetGold(), gauntlet.getBetGold());
                }
                simpleGauntletMapper.updateById(gauntlet);
            }
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 查询挑战详情
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getChallengeInformation(HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        //获取学生挑战数据
        List<Gauntlet> gauntlets = simpleGauntletMapper.selByStudentIdAndFormat(student.getId(), new Date());
        //在没有挑战时返回
        if (gauntlets == null) {
            returnMap.put("isPk", true);
            returnMap.put("isFirst", true);
            return ServerResponse.createBySuccess(returnMap);
        }
        //当天未挑战时返回
        if (gauntlets.size() <= 0) {
            returnMap.put("isFirst", true);
            returnMap.put("isPk", true);
            return ServerResponse.createBySuccess(returnMap);
        }
        //在挑战人当天挑战次数大于3次时返回
        if (gauntlets.size() > 3) {
            returnMap.put("isPk", false);
            returnMap.put("isFirst", false);
            return ServerResponse.createBySuccess(returnMap);
        }

        boolean isPk = true;
        for (Gauntlet gauntlet : gauntlets) {
            if (gauntlet.getChallengeStatus() != 3 && gauntlet.getChallengeStatus() != 4) {
                isPk = false;
            }
        }
        //在以挑战时返回当前第一回挑战的数据
        if (isPk) {
            returnMap.put("isPk", true);
            returnMap.put("isFirst", false);
            Gauntlet gauntlet = gauntlets.get(0);
            Long courseId = gauntlet.getCourseId();
            Course course = simpleCourseMapper.selectById(courseId);
            returnMap.put("gold", gauntlet.getBetGold());
            returnMap.put("courseId", courseId);
            returnMap.put("gameName", gauntlet.getChallengeName());
            returnMap.put("courseName", course.getCourseName());
        } else {
            returnMap.put("isPk", false);
        }
        return ServerResponse.createBySuccess(returnMap);
    }

    /**
     * 显示挑战详情
     *
     * @param session
     * @param page
     * @param rows
     * @param type
     * @return
     */
    @Override
    public ServerResponse<Object> getPersonalPkData(HttpSession session, Integer page, Integer rows, Integer type) {
        Student student = getStudent(session);
        //查看挑战次数
        Integer size = simpleGauntletMapper.selCountByStudentId(student.getId());
        Integer total = size % rows > 0 ? size / rows + 1 : size / rows;
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("total", total);
        returnMap.put("page", page);
        returnMap.put("rows", rows);
        Integer start = (page - 1) * rows;
        /**
         * 根据学生id查询每页显示的挑战数据
         */
        List<Gauntlet> gauntlets = simpleGauntletMapper.selByStudentId(student.getId(), start, rows, type);
        List<Map<String, Object>> returnList = new ArrayList<>();
        //填装返回数据格式
        for (Gauntlet gauntlet : gauntlets) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", student.getNickname());
            map.put("headUrl", student.getHeadUrl());
            if (gauntlet.getChallengerStudentId().equals(student.getId())) {
                map.put("status", gauntlet.getChallengeStatus());
                map.put("study", gauntlet.getChallengerStudyNow());
                if (gauntlet.getChallengeStatus() == 1) {
                    map.put("gold", "+" + gauntlet.getChallengeGold());
                    map.put("changeStudy", "+" + gauntlet.getChallengeStudy());
                } else {
                    map.put("gold", "-" + gauntlet.getChallengeGold());
                    map.put("changeStudy", "-" + gauntlet.getChallengeStudy());
                }
                map.put("createTime", gauntlet.getCreateTime());
                returnList.add(map);
            }
            if (gauntlet.getBeChallengerStudentId().equals(student.getId())) {
                map.put("status", gauntlet.getBeChallengerStatus());
                map.put("study", gauntlet.getBeChallengerStudyNow());
                if (gauntlet.getBeChallengerStatus() == 1) {
                    map.put("gold", "+" + gauntlet.getBeChallengeGold());
                    map.put("changeStudy", "+" + gauntlet.getBeChallengeStudy());
                } else {
                    map.put("gold", "-" + gauntlet.getBeChallengeGold());
                    map.put("changeStudy", "-" + gauntlet.getBeChallengeStudy());
                }
                map.put("createTime", gauntlet.getCreateTime());
                returnList.add(map);
            }
        }
        returnMap.put("list", returnList);
        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public ServerResponse<Object> removeGauntlet(Long gauntletId) {
        //在一人接受挑战时清楚其他发出的挑战记录
        Gauntlet gauntlet = simpleGauntletMapper.selectById(gauntletId);
        Date createTime = gauntlet.getCreateTime();
        List<Gauntlet> gauntlets = simpleGauntletMapper.selByStudentIdAndFormat(gauntlet.getChallengerStudentId(), createTime);
        for (Gauntlet gauntlet1 : gauntlets) {
            if (!gauntlet1.getId().equals(gauntletId)) {
                simpleGauntletMapper.updateByStatus(gauntlet1.getId());
            }
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> getReceiveChallenges(HttpSession session) {
        //查看收到的挑战数量
        Student student = getStudent(session);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -72);
        Date time = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleGauntletMapper.updateByTime(format.format(time));
        //查询收到挑战数量
        Integer integer = simpleGauntletMapper.selReceiveChallenges(student.getId());
        if (integer != null) {
            return ServerResponse.createBySuccess(integer);
        } else {
            return ServerResponse.createBySuccess(0);
        }
    }

    @Override
    public ServerResponse<Object> closePkExplain(HttpSession session) {
        //关闭挑战详情查看
        Student student = getStudent(session);
        simpleStudentExpansionMapper.updatePkExplain(student.getId());
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> getHeroList(HttpSession session, Integer type) {
        //查看英雄榜数据
        Student student = getStudent(session);
        delGauntlets(student);
        Long schoolAdminId = null;
        List<Integer> teachers = null;
        if (type == 2) {
            if (student.getTeacherId() != null) {
                Integer schoolAdminById = simpleTeacherMapper.getSchoolAdminById(student.getTeacherId().intValue());
                if (schoolAdminById == null) {
                    Integer teacherCountByAdminId = simpleTeacherMapper.getTeacherCountByAdminId(student.getTeacherId());
                    if (teacherCountByAdminId != null && teacherCountByAdminId > 0) {
                        schoolAdminId = student.getTeacherId();
                    }
                } else {
                    schoolAdminId = schoolAdminById.longValue();
                }
            }
            teachers = simpleTeacherMapper.getTeacherIdByAdminId(schoolAdminId.intValue());
        }

        List<Map<String, Object>> maxStudyTwenty = simpleStudentExpansionMapper.getMaxStudyTwenty(student.getClassId(), student.getTeacherId(), teachers, schoolAdminId, type);
        List<Map<String, Object>> returnList = new ArrayList<>();
        int index = 0;
        for (Map<String, Object> map : maxStudyTwenty) {
            index += 1;
            Integer pkNumber = 0;
            map.put("index", index);
            Object mePkOthers = map.get("mePkOthers");
            if (mePkOthers != null) {
                pkNumber = Integer.parseInt(mePkOthers.toString());
            }
            Object othersPkMe = map.get("othersPkMe");
            if (othersPkMe != null) {
                pkNumber += Integer.parseInt(othersPkMe.toString());
            }
            Object study = map.get("study");
            if (study == null) {
                map.put("study", 0);
            }
            map.put("pkNumber", pkNumber);
            returnList.add(map);
        }
        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public void getStudy() {
        //批量生成学生账号扩展表信息
        List<Student> all = simpleStudentMapper.getAll();
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        for (Student student : all) {
            StudentExpansion isHave = simpleStudentExpansionMapper.isHave(student.getId());
            if (isHave == null) {
                Double gold = student.getSystemGold() + student.getOfflineGold();
                int level = getLevels(gold.intValue(), levels);
                Integer study = simpleLevelMapper.getStudyById(level);
                simpleStudentExpansionMapper.addStudy(student.getId(), study, level, 2);
            }
        }
    }

    @Override
    public ServerResponse<Object> getInformation(Long gauntletId, Integer type) {
        //查看详情
        Gauntlet gauntlet = simpleGauntletMapper.getInformationById(gauntletId);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("courseId", gauntlet.getCourseId());
        //查询课程信息
        Course course = simpleCourseMapper.selectById(gauntlet.getCourseId());
        if (course == null) {
            returnMap.put("courseName", "单词课程");
        } else {
            returnMap.put("courseName", course.getCourseName());
        }
        returnMap.put("gauntletId", gauntletId);
        returnMap.put("pkGold", gauntlet.getBetGold());
        returnMap.put("game", gauntlet.getChallengeName());
        //根据胜败添加挑战信息详情
        if (type == 1) {
            returnMap.put("status", gauntlet.getChallengeStatus());

            if (gauntlet.getChallengeStatus() == 1) {
                returnMap.put("study", "+" + gauntlet.getChallengeStudy());
                returnMap.put("gold", "+" + gauntlet.getChallengeGold());
            } else if (gauntlet.getChallengeStatus() == 2) {
                returnMap.put("study", "-" + gauntlet.getChallengeStudy());
                returnMap.put("gold", "-" + gauntlet.getChallengeGold());
            }
            Student student = simpleStudentMapper.selectByPrimaryKey(gauntlet.getChallengerStudentId());
            returnMap.put("oneself", student.getNickname());
            returnMap.put("oneselfUrl", student.getHeadUrl());
            returnMap.put("oneselfPoint", gauntlet.getChallengerPoint());
            returnMap.put("challengePoint", gauntlet.getBeChallengerPoint());
            Student challengeStudent = simpleStudentMapper.selectByPrimaryKey(gauntlet.getBeChallengerStudentId());
            returnMap.put("challenge", challengeStudent.getNickname());
            returnMap.put("challengeUrl", challengeStudent.getHeadUrl());
            returnMap.put("gradeGold", gauntlet.getGrade());
            returnMap.put("awardGold", gauntlet.getAward());
        } else {
            returnMap.put("status", gauntlet.getBeChallengerStatus());
            if (gauntlet.getBeChallengerStatus() == 1) {
                returnMap.put("study", "+" + gauntlet.getBeChallengeStudy());
                returnMap.put("gold", "+" + gauntlet.getBeChallengeGold());
            } else if (gauntlet.getBeChallengerStatus() == 2) {
                returnMap.put("study", "-" + gauntlet.getBeChallengeStudy());
                returnMap.put("gold", "-" + gauntlet.getBeChallengeGold());
            }
            returnMap.put("oneselfPoint", gauntlet.getBeChallengerPoint());
            returnMap.put("challengePoint", gauntlet.getChallengerPoint());
            Student student = simpleStudentMapper.selectByPrimaryKey(gauntlet.getChallengerStudentId());
            returnMap.put("challenge", student.getNickname());
            returnMap.put("challengeUrl", student.getHeadUrl());
            Student challengeStudent = simpleStudentMapper.selectByPrimaryKey(gauntlet.getBeChallengerStudentId());
            returnMap.put("oneself", challengeStudent.getNickname());
            returnMap.put("oneselfUrl", challengeStudent.getHeadUrl());
            returnMap.put("gradeGold", 0);
            returnMap.put("awardGold", 0);
        }
        String challengerMsg = gauntlet.getChallengerMsg();
        //返回挑战话语
        if (challengerMsg != null) {
            String[] split = challengerMsg.split("，");
            if (split.length > 1) {
                split[0] = split[0] + "，";
            }
            returnMap.put("challengerMsg", split);
        }
        String concede = gauntlet.getConcede();
        if (concede != null) {
            String[] split = concede.split("，");
            if (split.length > 1) {
                split[0] = split[0] + "，";
            }
            returnMap.put("challengerMsg", split);
        }
        return ServerResponse.createBySuccess(returnMap);
    }

    /**
     * 根据胜利人信息添加金币
     * @param winnerStudentId
     * @param failStudentId
     * @param study
     * @param winnerGold
     * @param failGold
     */
    private void addGoldAndStudy(long winnerStudentId, long failStudentId, Integer study, Integer winnerGold, Integer failGold) {
        //胜利人
        StudentExpansion challengeStudentExpansion = simpleStudentExpansionMapper.selectByStudentId(winnerStudentId);
        challengeStudentExpansion.setStudyPower(challengeStudentExpansion.getStudyPower() + study);
        simpleStudentExpansionMapper.updateById(challengeStudentExpansion);
        Student winnerStudent = simpleStudentMapper.selectByPrimaryKey(winnerStudentId);
        if (winnerStudent.getSystemGold() == null) {
            winnerStudent.setSystemGold(winnerGold.doubleValue());
        } else {
            winnerStudent.setSystemGold(winnerStudent.getSystemGold() + winnerGold);
        }
        simpleStudentMapper.updateByPrimaryKey(winnerStudent);
        RunLog runLog = new RunLog();
        runLog.setOperateUserId(winnerStudentId);
        runLog.setType(4);
        runLog.setLogContent("pk对战获得#" + winnerGold + "#金币");
        runLogMapper.insert(runLog);
        //失败人
        StudentExpansion beChallengeStudentExpansion = simpleStudentExpansionMapper.selectByStudentId(failStudentId);
        if (challengeStudentExpansion.getStudyPower() - study < 0) {
            challengeStudentExpansion.setStudyPower(0);
        } else {
            challengeStudentExpansion.setStudyPower(challengeStudentExpansion.getStudyPower() - study);
        }
        simpleStudentExpansionMapper.updateById(beChallengeStudentExpansion);
        Student failStudent = simpleStudentMapper.selectByPrimaryKey(failStudentId);
        if (failStudent.getSystemGold() - failGold < 0) {
            failStudent.setSystemGold(0.0);
        } else {
            failStudent.setSystemGold(failStudent.getSystemGold() - failGold);
        }
        simpleStudentMapper.updateByPrimaryKey(failStudent);
        RunLog runLog1 = new RunLog();
        runLog1.setOperateUserId(winnerStudentId);
        runLog1.setType(5);
        runLog1.setLogContent("pk对战减少#" + failGold + "#金币");
        runLogMapper.insert(runLog1);
    }


    /**
     * 添加挑战信息详情
     * @param returnList
     * @param gauntlets
     * @param type
     */
    private void getGauntlet(List<Map<String, Object>> returnList, List<Gauntlet> gauntlets, Integer type) {

        for (Gauntlet gauntlet : gauntlets) {
            if (gauntlet.getChallengeStatus() == 4) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(gauntlet.getCreateTime());
                calendar.add(Calendar.HOUR_OF_DAY, 24);
                Date time = calendar.getTime();
                if (time.getTime() < System.currentTimeMillis()) {
                    continue;
                }
            }
            Map<String, Object> map = new HashMap<>();
            Student student = simpleStudentMapper.selectByPrimaryKey(gauntlet.getChallengerStudentId());
            Student students = simpleStudentMapper.selectByPrimaryKey(gauntlet.getBeChallengerStudentId());
            map.put("originator", student.getNickname());
            map.put("challenged", students.getNickname());
            map.put("courseId", gauntlet.getCourseId());
            map.put("gauntletId", gauntlet.getId());
            map.put("game", gauntlet.getChallengeName());
            map.put("createTime", gauntlet.getCreateTime());
            String str = gauntlet.getChallengerMsg();
            String[] split = str.split("，");
            map.put("challengerMsg", split);
            if (type == 1) {
                map.put("type", gauntlet.getChallengeStatus());
            } else {
                map.put("type", gauntlet.getBeChallengerStatus());
            }
            returnList.add(map);
        }

    }


    private ServerResponse<Object> getGameTwo(Long courseId) {
        // 从当前课程随机取10个已学的单词
        List<Vocabulary> gameTwoSubject = this.getGameTwoSubject(courseId);
        List<Long> wordIds = new ArrayList<>(10);
        gameTwoSubject.forEach(map -> wordIds.add(Long.valueOf(map.getId().toString())));
        // 从单词中随机取出11个单词
        List<Vocabulary> wordList = vocabularyMapper.getWord(0, 110, wordIds);
        Collections.shuffle(wordList);
        List<GameTwoVo> gameTwoVos = new ArrayList<>();
        List<Object> list;
        // 中文集合
        List<String> chinese;
        // 试题英文集合
        List<String> subjects;
        GameTwoVo gameTwoVo;
        int i = 0;
        for (Vocabulary needReviewWord : gameTwoSubject) {
            int bigBossIndex = -1;
            int minBossIndex = -1;
            if (i < 2) {
                int index = new Random().nextInt(2);
                if (index % 2 == 0) {
                    bigBossIndex = new Random().nextInt(12);
                } else {
                    minBossIndex = new Random().nextInt(12);
                }
                i++;
            }
            gameTwoVo = new GameTwoVo();
            gameTwoVo.setBigBossIndex(bigBossIndex);
            gameTwoVo.setMinBossIndex(minBossIndex);
            gameTwoVo.setReadUrl(simpleBaiduSpeak.getLanguagePath(needReviewWord.getWord()));
            // 封装纸牌的试题集合并打乱顺序；
            list = new ArrayList<>(12);
            list.add(needReviewWord);
            list.addAll(wordList.subList(i * 11, (i + 1) * 11));
            Collections.shuffle(list);
            subjects = new ArrayList<>(list.size());
            chinese = new ArrayList<>(list.size());
            for (Object object : list) {
                Vocabulary objectMap = (Vocabulary) object;
                subjects.add(objectMap.getWord());
                chinese.add(objectMap.getWordChinese());
            }
            gameTwoVo.setChinese(chinese);
            gameTwoVo.setSubjects(subjects);
            // 封装正确答案的索引
            for (int i1 = 0; i1 < subjects.size(); i1++) {
                if (Objects.equals(subjects.get(i1), needReviewWord.getWord())) {
                    gameTwoVo.setRightIndex(i1);
                }
            }
            gameTwoVos.add(gameTwoVo);
        }
        return ServerResponse.createBySuccess(gameTwoVos);
    }

    /**
     * 获取游戏题目
     *
     * @return
     */
    private List<Vocabulary> getGameTwoSubject(Long courseId) {
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, 0, 10);
        Collections.shuffle(vocabularys);
        return vocabularys;
    }

    private void getGameOne(Long courseId, List<Map<String, Object>> subjects) {
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, 0, 80);
        Integer page = vocabularys.size() / 4;
        if (vocabularys.size() % 4 != 0) {
            if (vocabularys.size() > 0 && vocabularys.size() > 4) {
                vocabularys = vocabularys.subList(0, page * 4);
            } else {
                vocabularys = null;
            }
        }
        if (vocabularys != null) {
            for (int i = 0; i < page; i++) {
                Map<String, Object> map = new HashMap<>();
                List<Vocabulary> vocabularies = vocabularys.subList(i * 4, (i + 1) * 4);
                Vocabulary vocabulary = vocabularies.get(0);
                map.put("id", vocabulary.getId());
                map.put("type", "汉译英");
                map.put("title", vocabulary.getWordChinese());
                Map<String, Object> getMap = new HashMap<>();
                for (Vocabulary isVocabulary : vocabularies) {
                    if (isVocabulary.getId().equals(vocabulary.getId())) {
                        getMap.put(isVocabulary.getWord(), true);
                    } else {
                        getMap.put(isVocabulary.getWord(), false);
                    }
                }
                map.put("subject", getMap);
                map.put("readUrl", vocabulary.getReadUrl());
                subjects.add(map);
            }
        }
    }

    private void getGameThree(List<Map<String, Object>> subjects, Integer pageNum, Long courseId) {
        Integer row = 10;
        Integer start = (pageNum - 1) * 10;
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, start, row);
        vocabularys.forEach(vocabulary -> {
            Map<String, Object> subjectMap1 = new HashMap<>(16);
            subjectMap1.put("title", vocabulary.getWordChinese());
            subjectMap1.put("value", vocabulary.getWord());
            Map<String, Object> subjectMap2 = new HashMap<>(16);
            subjectMap2.put("title", vocabulary.getWord());
            subjectMap2.put("value", vocabulary.getWordChinese());
            subjects.add(subjectMap1);
            subjects.add(subjectMap2);
        });
        Collections.shuffle(subjects);
    }

    private void getGameFour(Map<String, Object> map, Long courseId, Student student) {
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, 0, 20);
        Map<String, String> reMap = this.getWordMap(vocabularys);
        int size = vocabularys.size();
        int errorSize = size * 3;
        List<Vocabulary> errorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(courseId, vocabularys);
        List<Vocabulary> ignore = new ArrayList<>(errorVocabularies);
        reMap.putAll(this.getWordMap(ignore));
        if (errorVocabularies.size() < errorSize) {
            PageHelper.startPage(1, errorSize - errorVocabularies.size());
            List<Vocabulary> otherErrorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(courseId + 1, ignore);
            if (otherErrorVocabularies.size() > 0) {
                errorVocabularies.addAll(otherErrorVocabularies);
                ignore.addAll(otherErrorVocabularies);
                reMap.putAll(this.getWordMap(otherErrorVocabularies));
            }
            if (otherErrorVocabularies.size() < errorSize) {
                PageHelper.startPage(1, errorSize - errorVocabularies.size());
                otherErrorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(courseId - 1, ignore);
                if (otherErrorVocabularies.size() > 0) {
                    errorVocabularies.addAll(otherErrorVocabularies);
                    reMap.putAll(this.getWordMap(otherErrorVocabularies));
                }
            }
        }
        map.put("sex", student.getSex());
        packageStrengthVo(map, student, vocabularys, reMap, size,
                errorSize, errorVocabularies);
    }

    private void packageStrengthVo(Map<String, Object> reMap, Student student, List<Vocabulary> rightVocabularies, Map<String, String> map, int size, int errorSize, List<Vocabulary> errorVocabularies) {
        List<StrengthGameVo> strengthGameVos = new ArrayList<>();
        if (size > 0) {
            List<String> wordList;
            List<String> chineseList;
            StrengthGameVo strengthGameVo;

            int k = 0;
            for (Vocabulary rightVocabulary : rightVocabularies) {
                wordList = new ArrayList<>(8);
                chineseList = new ArrayList<>(8);
                strengthGameVo = new StrengthGameVo();
                // 小人图片顺序
                int pictureIndex = 0;
                wordList.add(String.valueOf(pictureIndex++));
                wordList.add(rightVocabulary.getWord());
                for (int j = 0; j < 3; j++) {
                    wordList.add(String.valueOf(pictureIndex++));
                    if (k < errorSize) {
                        wordList.add(errorVocabularies.get(k).getWord());
                    }
                    k++;
                }
                Collections.shuffle(wordList);
                int num = new Random().nextInt(2);
                for (int n = 0; n < 8; n++) {
                    if (Objects.equals(wordList.get(n), rightVocabulary.getWord())) {
                        if (num % 2 == 0) {
                            strengthGameVo.setType("英译汉");
                            strengthGameVo.setTitle(rightVocabulary.getWord());
                        } else {
                            strengthGameVo.setType("汉译英");
                            strengthGameVo.setTitle(rightVocabulary.getWordChinese());
                        }
                        strengthGameVo.setRightIndex(n);
                        break;
                    }
                }
                pictureIndex = 0;
                for (String word : wordList) {
                    if (map.get(word) == null) {
                        chineseList.add(String.valueOf(pictureIndex++));
                    } else {
                        chineseList.add(map.get(word));
                    }

                }
                strengthGameVo.setChineseList(chineseList);
                strengthGameVo.setWordList(wordList);
                strengthGameVos.add(strengthGameVo);
            }
            reMap.put("result", strengthGameVos);
            reMap.put("sex", student.getSex());
        }
    }

    private Map<String, String> getWordMap(List<Vocabulary> rightVocabularies) {
        Map<String, String> map = new HashMap<>(16);
        if (rightVocabularies.size() > 0) {
            rightVocabularies.forEach(vocabulary -> map.put(vocabulary.getWord(), vocabulary.getWordChinese()));
        }
        return map;
    }


    private void getStudentGauntletVo(StudentGauntletVo studentGauntletVo, int type, Long studentId) {
        //查询我发起的总接受pk次数
        Integer pkNumberforHis = simpleGauntletMapper.getInformation(studentGauntletVo.getId(), 1);
        //查询我发起的总胜利pk次数
        Integer winnerNumberForHis = simpleGauntletMapper.getInformation(studentGauntletVo.getId(), 2);
        //查询他人对我发起的总接受pk次数
        Integer pkNumberForMe = simpleGauntletMapper.getInformation(studentGauntletVo.getId(), 3);
        //查询他人对我发起的总胜利pk次数
        Integer winnerNumberForMe = simpleGauntletMapper.getInformation(studentGauntletVo.getId(), 4);
        if (type == 2) {
            Integer pkForMe = simpleGauntletMapper.getCountPkForMe(studentId, studentGauntletVo.getId(), 1);
            pkForMe += simpleGauntletMapper.getCountPkForMe(studentId, studentGauntletVo.getId(), 2);
            studentGauntletVo.setForMe(pkForMe);
            Gauntlet byStudentIdAndBeStudentId = simpleGauntletMapper.getByStudentIdAndBeStudentId(studentId, studentGauntletVo.getId());
            if (byStudentIdAndBeStudentId != null) {
                studentGauntletVo.setStatus(2);
            } else {
                studentGauntletVo.setStatus(1);
            }
        } else {
            StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
            studentGauntletVo.setPkExplain(studentExpansion.getPkExplain());
            if (studentExpansion != null) {
                if (studentExpansion.getIsLook() == 2) {
                    studentGauntletVo.setFirst(true);
                    simpleStudentExpansionMapper.updateByIsLook(studentExpansion.getId());
                } else {
                    studentGauntletVo.setFirst(false);
                }
                studentGauntletVo.setStudy(studentExpansion.getStudyPower());
            } else {
                studentGauntletVo.setFirst(false);
                studentGauntletVo.setStudy(0);
            }

        }
        //计算总挑战次数
        Integer pkNumber = pkNumberforHis + pkNumberForMe;
        //计算总赢次数
        Integer winnerNumber = winnerNumberForHis + winnerNumberForMe;

        if (pkNumber != null && pkNumber != 0) {
            studentGauntletVo.setPkNumber(pkNumber);
            Double pk = 1.0 * winnerNumber / pkNumber * 100;
            studentGauntletVo.setWinner(pk.intValue() + "%");
        } else {
            studentGauntletVo.setPkNumber(0);
            studentGauntletVo.setWinner("0");
        }
    }


    /**
     * 清楚72小时以上的未接受挑战 改为超时
     *
     * @param student
     */
    private void delGauntlets(Student student) {
        String date = SimpleDateUtil.beforHoursTime(72);
        List<Gauntlet> gauntlets = simpleGauntletMapper.selDelGauntlet(student.getId(), date);
        List<Long> gauntletIds = new ArrayList<>();
        for (Gauntlet gauntlet : gauntlets) {
            gauntletIds.add(gauntlet.getId());
        }
        if (gauntletIds.size() > 0) {
            simpleGauntletMapper.updateStatus(4, gauntletIds);
        }
    }

    /**
     * @param gauntlet              对象
     * @param challengerStudentId   挑战学生id
     * @param beChallengerStudentId 被挑战学生id
     * @param courseId              单元id
     * @param betGold               挑战金币
     * @param challengeName         挑战名称
     * @param challengerPoint       挑战学生得分
     * @param beChallengerPoint     被挑战学生得分
     * @param challengerMsg         挑战留言
     * @param challengeStatus       挑战书状态
     * @param beChallengerStatus    被挑战书状态
     * @param challengeStudy        挑战学生学习力变化值
     * @param beChallengeStudy      被挑战学生学习力变化值
     * @param ChallengeGold         学生金币变化值
     * @param beChallengeGold       被挑战学生金币变化值
     */
    private void addGauntlet(Gauntlet gauntlet, Long challengerStudentId, Long beChallengerStudentId, Long courseId, Integer betGold,
                             String challengeName, Integer challengerPoint, Integer beChallengerPoint, String challengerMsg,
                             Integer challengeStatus, Integer beChallengerStatus, Integer challengeStudy, Integer beChallengeStudy,
                             Integer ChallengeGold, Integer beChallengeGold, Date createTime, Integer challengeStudyNow, Integer beChallengeStudyNow) {
        if (challengerStudentId != null) {
            gauntlet.setChallengerStudentId(challengerStudentId);
        }
        if (beChallengerStudentId != null) {
            gauntlet.setBeChallengerStudentId(beChallengerStudentId);
        }
        if (courseId != null) {
            gauntlet.setCourseId(courseId);
        }
        if (betGold != null) {
            gauntlet.setBetGold(betGold);
        }
        if (challengeName != null) {
            gauntlet.setChallengeName(challengeName);
        }
        if (challengerPoint != null) {
            gauntlet.setChallengerPoint(challengerPoint);
        }
        if (beChallengerPoint != null) {
            gauntlet.setBeChallengerPoint(beChallengerPoint);
        }
        if (challengerMsg != null) {
            gauntlet.setChallengerMsg(challengerMsg);
        }
        if (challengeStatus != null) {
            gauntlet.setChallengeStatus(challengeStatus);
        }
        if (beChallengerStatus != null) {
            gauntlet.setBeChallengerStatus(beChallengerStatus);
        }
        if (challengeStudy != null) {
            gauntlet.setChallengeStudy(challengeStudy);
        }
        if (beChallengeStudy != null) {
            gauntlet.setBeChallengerStatus(beChallengeStudy);
        }
        if (createTime != null) {
            gauntlet.setCreateTime(createTime);
        }
        if (ChallengeGold != null) {
            gauntlet.setChallengeGold(ChallengeGold);
        }
        if (beChallengeGold != null) {
            gauntlet.setBeChallengeGold(beChallengeGold);
        }
        if (challengeStudyNow != null) {
            gauntlet.setChallengerStudyNow(challengeStudyNow);
        }
        if (beChallengeStudyNow != null) {
            gauntlet.setBeChallengerStudyNow(beChallengeStudyNow);
        }

    }
}

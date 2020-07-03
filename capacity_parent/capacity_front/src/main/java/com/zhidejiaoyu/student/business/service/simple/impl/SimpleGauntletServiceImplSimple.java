package com.zhidejiaoyu.student.business.service.simple.impl;

import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.LevelUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.grade.GradeUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.GauntletRankVo;
import com.zhidejiaoyu.common.vo.gauntlet.GauntletSortVo;
import com.zhidejiaoyu.common.vo.simple.StrengthGameVo;
import com.zhidejiaoyu.common.vo.simple.StudentGauntletVo;
import com.zhidejiaoyu.student.business.game.service.impl.GameServiceImpl;
import com.zhidejiaoyu.student.business.service.simple.SimpleIGauntletServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimplePersonalCentreServiceSimple;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 挑战书表； 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Service
public class SimpleGauntletServiceImplSimple extends SimpleBaseServiceImpl<GauntletMapper, Gauntlet> implements SimpleIGauntletServiceSimple {

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;

    @Autowired
    private GauntletMapper gauntletMapper;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Resource
    private CourseConfigMapper courseConfigMapper;

    @Autowired
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Autowired
    private RedisOpt redisOpt;
    @Autowired
    private SimpleAwardMapper simpleAwardMapper;
    @Autowired
    private SimpleLevelMapper simpleLevelMapper;
    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;
    @Resource
    private CourseNewMapper courseNewMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private SourcePowerRankOpt sourcePowerRankOpt;
    @Resource
    private EquipmentMapper equipmentMapper;
    @Resource
    private ShipIndexService shipIndexService;
    @Resource
    private SimplePersonalCentreServiceSimple simplePersonalCentreServiceSimple;
    @Resource
    private RankOpt rankOpt;


    @Override
    public ServerResponse<Map<String, Object>> getStudentByType(HttpSession session, Integer type, Integer page, Integer rows, String account, GauntletSortVo vo) {
        //获取学生
        Student student = getStudent(session);
        //更改过时挑战
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        long startIndex = (page - 1) * rows;
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("page", page);
        returnMap.put("rows", rows);
        List<Long> studentIds;
        if (type == 2) {
            // 校区排行（全部学生）
            // 我在校区的排行
            studentIds = simpleStudentMapper.selectStudentIdByAdminIdOrAll(schoolAdminId);
            returnMap.put("total", studentIds.size() % rows > 0 ? studentIds.size() / rows + 1 : studentIds.size() / rows);
            studentIds = getRankStudent(vo, studentIds, startIndex, rows);
        } else {
            // 全国排行（前50名）
            studentIds = simpleStudentMapper.selectStudentIdByAdminIdOrAll(null);
            returnMap.put("total", studentIds.size() % rows > 0 ? studentIds.size() / rows + 1 : studentIds.size() / rows);
            studentIds = getRankStudent(vo, studentIds, startIndex, rows);
        }
        List<StudentGauntletVo> classOrSchoolStudents = new ArrayList<>();
        getListStudentGauntletVo(classOrSchoolStudents, studentIds);

        returnMap.put("data", classOrSchoolStudents);
        return ServerResponse.createBySuccess(returnMap);
    }

    private List<Long> getRankStudent(GauntletSortVo vo, List<Long> studentIds, long startIndex, long endIndex) {
        if (vo.getBattle() == null && vo.getPkNum() == null
                && vo.getSourcePower() == null) {
            vo.setBattle(1);
        }
        if (vo.getBattle() != null) {
            return gauntletMapper.selectSortByStudentId(studentIds, startIndex, endIndex, vo.getBattle());
        }
        if (vo.getSourcePower() != null) {
            return simpleStudentExpansionMapper.selectSourcePowerSortByStudentIds(studentIds, startIndex, endIndex, vo.getSourcePower());
        }
        if (vo.getPkNum() != null) {
            return simpleStudentExpansionMapper.selectPkNumSortByStudentIds(studentIds, startIndex, endIndex, vo.getPkNum());
        }
        return null;

    }

    private void getListStudentGauntletVo(List<StudentGauntletVo> classOrSchoolStudents, List<Long> studentIds) {
        studentIds.forEach(studentId -> {
            StudentGauntletVo vo = new StudentGauntletVo();
            Student student = simpleStudentMapper.selectById(studentId);
            vo.setId(studentId);
            vo.setHeadUrl(student.getHeadUrl());
            vo.setAccount(student.getAccount());
            vo.setName(student.getNickname());
            getStudentGauntletVo(vo, 2, studentId);
            classOrSchoolStudents.add(vo);
        });

    }

    @Override
    public ServerResponse<StudentGauntletVo> getStudyInteger(HttpSession session) {
        //获取个人的数据
        Student student = getStudent(session);
        Integer pkNum = gauntletMapper.countByStudentIdAndStartDateAndEndDate(student.getId(), DateUtil.beforeHoursTime(1), DateUtil.formatYYYYMMDDHHMMSS(new Date()));
        StudentGauntletVo studentGauntletVo = new StudentGauntletVo();
        studentGauntletVo.setId(student.getId());
        studentGauntletVo.setName(student.getNickname());
        studentGauntletVo.setHeadUrl(student.getHeadUrl());
        //获取每天的挑战次数
        if (pkNum == null) {
            studentGauntletVo.setPkNum(5);
        } else {
            studentGauntletVo.setPkNum(5 - pkNum > 0 ? 5 - pkNum : 0);
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
        List<Gauntlet> gauntlets = gauntletMapper.selByStudentIdAndFormat(student.getId(), new Date());
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
        Integer insert = gauntletMapper.insert(gauntlet);
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
    public ServerResponse<Object> getGame(Integer pageNum, Long courseId, String gameName, HttpSession session, int type) {
        Student student = getStudent(session);
        if ("找同学".equals(gameName)) {
            List<Map<String, Object>> subjects = new ArrayList<>();
            getGameOne(courseId, subjects, type);
            Map<String, Object> map = new HashMap<>();
            map.put("testResults", subjects);
            map.put("petUrl", AliyunInfoConst.host + student.getPartUrl());
            return ServerResponse.createBySuccess(map);
        }
        if ("桌牌捕音".equals(gameName)) {
            return getGameTwo(courseId, type);
        }
        if ("冰火两重天".equals(gameName)) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> subjects = new ArrayList<>();
            getGameThree(subjects, pageNum, courseId, type);
            map.put("matchKeyValue", subjects);
            return ServerResponse.createBySuccess(map);
        }
        if ("实力初显".equals(gameName)) {
            Map<String, Object> map = new HashMap<>();
            getGameFour(map, courseId, student, type);
            return ServerResponse.createBySuccess(map);
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
        Long studentId = student.getId();
        //获取学生版本
        List<String> gradeList = GradeUtil.smallThanCurrent(student.getVersion(), student.getGrade());
        List<Long> courseIds = studentStudyPlanNewMapper.getCourseIdAndGradeList(studentId, gradeList);
        List<Map<String, Object>> returnMap = new ArrayList<>();
        //获取course_config数据
        //查看是否学生是否拥有课程
        //判断学生是否有排课的内容
        if (student.getVersion() != null && student.getGrade() != null) {
            int count = courseConfigMapper.countByUserIdAndType(studentId, 2);
            List<Long> longs;
            if (count > 0) {
                longs = courseConfigMapper.selectByUserId(studentId, gradeList);
            } else {
                //如果学生没有排课查看校区是否有排课
                //获取校长id
                Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
                count = courseConfigMapper.countByUserIdAndType(schoolAdminId.longValue(), 1);
                if (count > 0) {
                    longs = courseConfigMapper.selectByUserId(schoolAdminId.longValue(), gradeList);
                } else {
                    //如果学校没有分课，查询总部分课
                    longs = courseConfigMapper.selectByUserId(1L, gradeList);
                }
            }
            if (longs.size() > 0) {
                courseIds.addAll(longs);
            }
        }
        //获取学生正在学习的版本
        GradeUtil.smallThanCurrent(student.getVersion(), student.getGrade());
        List<Map<String, Object>> courses = simpleCourseMapper.getCourseByIds(courseIds);
        courses.forEach(map -> {
            map.put("type", 2);
            returnMap.add(map);
        });
        List<Long> simpleCouseIds = simpleStudentUnitMapper.getAllCourseIdByTypeToStudent(studentId, 2);
        if (simpleCouseIds.size() > 0) {
            List<Map<String, Object>> maps = courseMapper.selectCourseByCourseIds(simpleCouseIds);
            maps.forEach(map -> {
                map.put("type", 1);
                returnMap.add(map);

            });
        }
        return ServerResponse.createBySuccess(returnMap);
    }

    /**
     * countByStudentId
     *
     * @param type
     * @param pageNum
     * @param rows
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getChallenge(Integer type, Integer pageNum, Integer rows, HttpSession session) {
        Long studentId = getStudentId(session);
        Map<String, Object> returnMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        int start = (pageNum - 1) * rows;
        //根据type的不同来区分是查询我发出的挑战还是挑战我的数据
        List<Gauntlet> gauntlets = gauntletMapper.selGauntletByTypeAndChallengeType(type, start, rows, studentId);
        //获取要查询的挑战数量
        Integer count = gauntletMapper.getCount(type, studentId);
        returnMap.put("page", pageNum);
        returnMap.put("rows", rows);
        List<Map<String, Object>> list = new ArrayList<>();
        returnMap.put("total", count % rows > 0 ? count / rows + 1 : count / rows);
        getGauntlet(list, gauntlets, type, studentId);
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
        Gauntlet gauntlet = gauntletMapper.selectById(gauntletId);
        if (type == 1) {
            //发起者保存分数
            gauntlet.setChallengerPoint(point);
            gauntletMapper.updateById(gauntlet);
        } else {
            if (isDelete == 2) {
                //挑战者拒绝挑战
                gauntlet.setChallengeStatus(6);
                gauntlet.setBeChallengerStatus(6);
                gauntlet.setConcede(concede);
                gauntletMapper.updateById(gauntlet);
            } else {
                //挑战者接受挑战保存分数
                //获取加成比例
                List<Map<String, Object>> levels = redisOpt.getAllLevel();
                //查询挑战学生信息
                Student challengerStudent = simpleStudentMapper.selectById(gauntlet.getChallengerStudentId());
                Double challengeGold = challengerStudent.getSystemGold() + challengerStudent.getOfflineGold();
                //获取挑战学生等级信息
                int challengeLevel = getLevels(challengeGold.intValue(), levels);
                //查询被挑战学生信息
                Student beChallengerStudent = simpleStudentMapper.selectById(gauntlet.getBeChallengerStudentId());
                Double beChallengeGold = beChallengerStudent.getSystemGold() + beChallengerStudent.getOfflineGold();
                //获取被挑战学生等级信息
                int beChallengeLevel = getLevels(beChallengeGold.intValue(), levels);
                Map<String, Double> map = new HashMap<>();
                if (gauntlet.getChallengerPoint() - point > 0) {
                    //获取挑战人勋章数量
                    List<Map<String, Object>> maps = simpleAwardMapper.selAwardCountByStudentId(challengerStudent.getId());
                    //获得加成
                    map = LevelUtil.getAddition(challengeLevel, beChallengeLevel, 1, challengerStudent.getId(), map, maps);
                } else if (gauntlet.getChallengerPoint() - point == 0) {
                    List<Map<String, Object>> maps = simpleAwardMapper.selAwardCountByStudentId(challengerStudent.getId());
                    map = LevelUtil.getAddition(challengeLevel, beChallengeLevel, 2, challengerStudent.getId(), map, maps);
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
                        Student winnerStudent = simpleStudentMapper.selectById(gauntlet.getChallengerStudentId());
                        winnerStudent.setSystemGold(winnerStudent.getSystemGold() + goldChallenge);
                        simpleStudentMapper.updateById(winnerStudent);

                        GoldLogUtil.saveStudyGoldLog(gauntlet.getChallengerStudentId(), "pk对战", goldChallenge);
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
                gauntletMapper.updateById(gauntlet);
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
        List<Gauntlet> gauntlets = gauntletMapper.selByStudentIdAndFormat(student.getId(), new Date());
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
            CourseNew course = courseNewMapper.selectById(courseId);
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
        Integer size = gauntletMapper.selCountByStudentId(student.getId());
        Integer total = size % rows > 0 ? size / rows + 1 : size / rows;
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("total", total);
        returnMap.put("page", page);
        returnMap.put("rows", rows);
        Integer start = (page - 1) * rows;
        /**
         * 根据学生id查询每页显示的挑战数据
         */
        List<Gauntlet> gauntlets = gauntletMapper.selByStudentId(student.getId(), start, rows, type);
        List<Map<String, Object>> returnList = new ArrayList<>();
        //填装返回数据格式
        for (Gauntlet gauntlet : gauntlets) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", student.getNickname());
            map.put("headUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
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
        Gauntlet gauntlet = gauntletMapper.selectById(gauntletId);
        Date createTime = gauntlet.getCreateTime();
        List<Gauntlet> gauntlets = gauntletMapper.selByStudentIdAndFormat(gauntlet.getChallengerStudentId(), createTime);
        for (Gauntlet gauntlet1 : gauntlets) {
            if (!gauntlet1.getId().equals(gauntletId)) {
                gauntletMapper.updateByStatus(gauntlet1.getId());
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
        gauntletMapper.updateByTime(format.format(time));
        //查询收到挑战数量
        Integer integer = gauntletMapper.selReceiveChallenges(student.getId());
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
        Integer schoolAdminId = null;
        List<Integer> teachers = null;
        //获取教师id
        if (type == 2) {
            schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
            teachers = simpleTeacherMapper.getTeacherIdByAdminId(schoolAdminId);
        }
        //搜索数据
        List<Map<String, Object>> maxStudyTwenty = simpleStudentExpansionMapper.getMaxStudyTwenty(student.getClassId(), student.getTeacherId(), teachers, schoolAdminId, type);
        List<Map<String, Object>> returnList = new ArrayList<>();
        int index = 0;
        for (Map<String, Object> map : maxStudyTwenty) {
            index += 1;
            int pkNumber = 0;
            map.put("index", index);
            Object mePkOthers = map.get("mePkOthers");
            if (mePkOthers != null) {
                pkNumber = Integer.parseInt(mePkOthers.toString());
            }
            Object othersPkMe = map.get("othersPkMe");
            if (othersPkMe != null) {
                pkNumber += Integer.parseInt(othersPkMe.toString());
            }
            map.putIfAbsent("study", 0);
            map.put("pkNumber", pkNumber);
            returnList.add(map);
        }
        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public ServerResponse<Object> getRank(HttpSession session, Integer type) {
        Student student = getStudent(session);
        List<Long> studentIds = null;
        int pageNum = PageUtil.getPageNum();
        Integer pageSize = PageUtil.getPageSize();
        long startIndex = (pageNum - 1) * pageSize;
        long endIndex = startIndex + pageSize;
        Map<String, Object> map = new HashMap<>();
        map.put("page", pageNum);
        map.put("rows", pageSize);
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        if (type.equals(1)) {
            List<Long> longs = simpleStudentMapper.selectMaxSourceByClassId(student.getClassId(), student.getTeacherId(), null, null);
            studentIds = simpleStudentMapper.selectMaxSourceByClassId(student.getClassId(), student.getTeacherId(), startIndex, pageSize.longValue());
            map.put("total", longs.size() % pageSize > 0 ? longs.size() / pageSize + 1 : longs.size() / pageSize);
        } else if (type.equals(2)) {
            // 校区排行（全部学生）
            // 我在校区的排行
            String key = SourcePowerKeysConst.SCHOOL_RANK + schoolAdminId;
            studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex, null);
            long memberSize = sourcePowerRankOpt.getMemberSize(key);
            map.put("total", memberSize % pageSize > 0 ? memberSize / pageSize + 1 : memberSize / pageSize);
        } else {

            String key = SourcePowerKeysConst.SERVER_RANK;
            studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex, null);
            long memberSize = sourcePowerRankOpt.getMemberSize(key);
            map.put("total", memberSize % pageSize > 0 ? memberSize / pageSize + 1 : memberSize / pageSize);
        }
        List<GauntletRankVo> studentRank = getStudentRank(studentIds);
        map.put("list", studentRank);
        return ServerResponse.createBySuccess(map);
    }

    private List<GauntletRankVo> getStudentRank(List<Long> studentIds) {
        List<GauntletRankVo> returnList = new ArrayList<>();
        studentIds.forEach(studentId -> {
            GauntletRankVo gauntletRankVo = simpleStudentMapper.selectGauntletRankVoByStudentId(studentId);
            gauntletRankVo.setHeadUrl(GetOssFile.getPublicObjectUrl(gauntletRankVo.getHeadUrl()));
            gauntletRankVo.setAddress(gauntletRankVo.getProvince() + "-" + gauntletRankVo.getCity() + "" + gauntletRankVo.getArea());
            gauntletRankVo.setCcie(rankOpt.getScore(RankKeysConst.COUNTRY_CCIE_RANK, studentId) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_CCIE_RANK, studentId));
            gauntletRankVo.setMedal(rankOpt.getScore(RankKeysConst.COUNTRY_MEDAL_RANK, studentId) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_MEDAL_RANK, studentId));
            gauntletRankVo.setWorship(rankOpt.getScore(RankKeysConst.COUNTRY_WORSHIP_RANK, studentId) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_WORSHIP_RANK, studentId));
            simplePersonalCentreServiceSimple.getLevelStr(gauntletRankVo.getGold(), redisOpt.getAllLevel());
            returnList.add(gauntletRankVo);
        });
        return returnList;
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
        Gauntlet gauntlet = gauntletMapper.getInformationById(gauntletId);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("courseId", gauntlet.getCourseId());
        //查询课程信息
        CourseNew course = courseNewMapper.selectById(gauntlet.getCourseId());
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
            Student student = simpleStudentMapper.selectById(gauntlet.getChallengerStudentId());
            returnMap.put("oneself", student.getNickname());
            returnMap.put("oneselfUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
            returnMap.put("oneselfPoint", gauntlet.getChallengerPoint());
            returnMap.put("challengePoint", gauntlet.getBeChallengerPoint());
            Student challengeStudent = simpleStudentMapper.selectById(gauntlet.getBeChallengerStudentId());
            returnMap.put("challenge", challengeStudent.getNickname());
            returnMap.put("challengeUrl", GetOssFile.getPublicObjectUrl(challengeStudent.getHeadUrl()));
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
            Student student = simpleStudentMapper.selectById(gauntlet.getChallengerStudentId());
            returnMap.put("challenge", student.getNickname());
            returnMap.put("challengeUrl", AliyunInfoConst.host + student.getHeadUrl());
            Student challengeStudent = simpleStudentMapper.selectById(gauntlet.getBeChallengerStudentId());
            returnMap.put("oneself", challengeStudent.getNickname());
            returnMap.put("oneselfUrl", AliyunInfoConst.host + challengeStudent.getHeadUrl());
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
     *
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
        Student winnerStudent = simpleStudentMapper.selectById(winnerStudentId);
        if (winnerStudent.getSystemGold() == null) {
            winnerStudent.setSystemGold(winnerGold.doubleValue());
        } else {
            winnerStudent.setSystemGold(winnerStudent.getSystemGold() + winnerGold);
        }
        simpleStudentMapper.updateById(winnerStudent);

        GoldLogUtil.saveStudyGoldLog(winnerStudentId, "pk对战胜利", winnerGold);
        //失败人
        StudentExpansion beChallengeStudentExpansion = simpleStudentExpansionMapper.selectByStudentId(failStudentId);
        if (challengeStudentExpansion.getStudyPower() - study < 0) {
            challengeStudentExpansion.setStudyPower(0);
        } else {
            challengeStudentExpansion.setStudyPower(challengeStudentExpansion.getStudyPower() - study);
        }
        simpleStudentExpansionMapper.updateById(beChallengeStudentExpansion);
        Student failStudent = simpleStudentMapper.selectById(failStudentId);
        if (failStudent.getSystemGold() - failGold < 0) {
            failStudent.setSystemGold(0.0);
        } else {
            failStudent.setSystemGold(failStudent.getSystemGold() - failGold);
        }
        simpleStudentMapper.updateById(failStudent);

        GoldLogUtil.saveStudyGoldLog(failStudentId, "pk对战失败", failGold);
    }


    /**
     * 添加挑战信息详情
     *
     * @param returnList
     * @param gauntlets
     * @param type
     */
    private void getGauntlet(List<Map<String, Object>> returnList, List<Gauntlet> gauntlets, Integer type, Long studentId) {

        for (Gauntlet gauntlet : gauntlets) {
            Map<String, Object> map = new HashMap<>();
            Student student = simpleStudentMapper.selectByPrimaryKey(gauntlet.getChallengerStudentId());
            Student students = simpleStudentMapper.selectByPrimaryKey(gauntlet.getBeChallengerStudentId());
            map.put("originator", student.getNickname());
            map.put("challenged", students.getNickname());
            map.put("originatorImgUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
            map.put("challengedImgUrl", GetOssFile.getPublicObjectUrl(students.getHeadUrl()));
            map.put("gauntletId", gauntlet.getId());
            map.put("createTime", gauntlet.getCreateTime());
            if (type == 1) {
                map.put("type", gauntlet.getChallengeStatus());
                StudentExpansion expansion = simpleStudentExpansionMapper.selectByStudentId(student.getId());
                map.put("pkNum", gauntlet.getChallengeStudy());
                map.put("sourcePower", expansion.getSourcePower());
            } else if (type == 2) {
                map.put("type", gauntlet.getBeChallengerStatus());
                StudentExpansion expansion = simpleStudentExpansionMapper.selectByStudentId(students.getId());
                map.put("pkNum", gauntlet.getBeChallengeStudy());
                map.put("sourcePower", expansion.getSourcePower());
            } else if (type == 3) {
                if (student.getId().equals(studentId)) {
                    StudentExpansion expansion = simpleStudentExpansionMapper.selectByStudentId(student.getId());
                    map.put("pkNum", gauntlet.getChallengeStudy());
                    map.put("sourcePower", expansion.getSourcePower());
                    map.put("type", gauntlet.getChallengeStatus());
                }
                if (students.getId().equals(studentId)) {
                    StudentExpansion expansion = simpleStudentExpansionMapper.selectByStudentId(students.getId());
                    map.put("pkNum", gauntlet.getBeChallengeStudy());
                    map.put("sourcePower", expansion.getSourcePower());
                    map.put("type", gauntlet.getBeChallengerStatus());
                }
            }
            returnList.add(map);
        }

    }


    private ServerResponse<Object> getGameTwo(Long courseId, int type) {
        // 从当前课程随机取10个已学的单词
        List<Vocabulary> gameTwoSubject = this.getGameTwoSubject(courseId, type);
        List<Long> wordIds = gameTwoSubject.stream().map(Vocabulary::getId).collect(Collectors.toList());
        // 从单词中随机取出11个单词
        List<Vocabulary> wordList = vocabularyMapper.getWord(0, 110, wordIds);
        return ServerResponse.createBySuccess(GameServiceImpl.packageGameTwoVos(gameTwoSubject, wordList, baiduSpeak));
    }

    /**
     * 获取游戏题目
     *
     * @return
     */
    private List<Vocabulary> getGameTwoSubject(Long courseId, int type) {
        List<Vocabulary> vocabularies = vocabularyMapper.getWordByCourseGetNumber(courseId, 0, 10, type);
        Collections.shuffle(vocabularies);
        return vocabularies;
    }

    private void getGameOne(Long courseId, List<Map<String, Object>> subjects, int type) {
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, 0, 80, type);
        int page = vocabularys.size() / 4;
        if (vocabularys.size() % 4 != 0) {
            if (vocabularys.size() > 4) {
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

    private void getGameThree(List<Map<String, Object>> subjects, Integer pageNum, Long courseId, int type) {
        Integer row = 10;
        Integer start = (pageNum - 1) * 10;
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, start, row, type);
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

    private void getGameFour(Map<String, Object> map, Long courseId, Student student, int type) {
        List<Vocabulary> vocabularys = vocabularyMapper.getWordByCourseGetNumber(courseId, 0, 20, type);
        Map<String, String> reMap = this.getWordMap(vocabularys);
        int size = vocabularys.size();
        int errorSize = size * 3;
        List<Vocabulary> errorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(courseId, vocabularys, type);
        List<Vocabulary> ignore = new ArrayList<>(errorVocabularies);
        reMap.putAll(this.getWordMap(ignore));
        if (errorVocabularies.size() < errorSize) {
            PageHelper.startPage(1, errorSize - errorVocabularies.size());
            List<Vocabulary> otherErrorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(courseId + 1, ignore, type);
            if (otherErrorVocabularies.size() > 0) {
                errorVocabularies.addAll(otherErrorVocabularies);
                ignore.addAll(otherErrorVocabularies);
                reMap.putAll(this.getWordMap(otherErrorVocabularies));
            }
            if (otherErrorVocabularies.size() < errorSize) {
                PageHelper.startPage(1, errorSize - errorVocabularies.size());
                otherErrorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(courseId - 1, ignore, type);
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
        Integer pkNumberforHis = gauntletMapper.getInformation(studentGauntletVo.getId(), 1);
        //查询我发起的总胜利pk次数
        Integer winnerNumberForHis = gauntletMapper.getInformation(studentGauntletVo.getId(), 2);
        //查询他人对我发起的总接受pk次数
        Integer pkNumberForMe = gauntletMapper.getInformation(studentGauntletVo.getId(), 3);
        //查询他人对我发起的总胜利pk次数
        Integer winnerNumberForMe = gauntletMapper.getInformation(studentGauntletVo.getId(), 4);
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
        studentGauntletVo.setSourcePower(studentExpansion.getSourcePower() != null ? studentExpansion.getSourcePower() : 0);
        studentGauntletVo.setStudy(studentExpansion.getStudyPower() != null ? studentExpansion.getStudyPower() : 0);
        if (type == 2) {
            Integer pkForMe = gauntletMapper.getCountPkForMe(studentId, studentGauntletVo.getId(), 1);
            pkForMe += gauntletMapper.getCountPkForMe(studentId, studentGauntletVo.getId(), 2);
            studentGauntletVo.setForMe(pkForMe);
            Gauntlet byStudentIdAndBeStudentId = gauntletMapper.getByStudentIdAndBeStudentId(studentId, studentGauntletVo.getId());
            if (byStudentIdAndBeStudentId != null) {
                studentGauntletVo.setStatus(2);
            } else {
                studentGauntletVo.setStatus(1);
            }
            getShipController(studentGauntletVo);
        } else {
            studentGauntletVo.setPkExplain(studentExpansion.getPkExplain());
            if (studentExpansion != null) {
                if (studentExpansion.getIsLook() == 2) {
                    studentGauntletVo.setFirst(true);
                    simpleStudentExpansionMapper.updateByIsLook(studentExpansion.getId());
                } else {
                    studentGauntletVo.setFirst(false);
                }
            } else {
                studentGauntletVo.setFirst(false);

            }

        }
        //计算总挑战次数
        int pkNumber = pkNumberforHis + pkNumberForMe;
        //计算总赢次数
        int winnerNumber = winnerNumberForHis + winnerNumberForMe;

        if (pkNumber != 0) {
            studentGauntletVo.setPkNumber(pkNumber);
            double pk = 1.0 * winnerNumber / pkNumber * 100;
            studentGauntletVo.setWinner((int) pk + "%");
        } else {
            studentGauntletVo.setPkNumber(0);
            studentGauntletVo.setWinner("0");
        }

        studentGauntletVo.setHeadUrl(GetOssFile.getPublicObjectUrl(studentGauntletVo.getHeadUrl()));
    }

    private void getShipController(StudentGauntletVo studentGauntletVo) {
        Long studentId = studentGauntletVo.getId();
        // 学生装备的飞船及装备信息
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);
        StudentGauntletVo.shipName shipName = getShipName(equipments, 1);
        studentGauntletVo.setIsPk(shipName.getName() == null ? false : true);
        IndexVO.BaseValue baseValue = shipIndexService.getBaseValue(equipments);
        StudentGauntletVo.Info build = StudentGauntletVo.Info.builder()
                .baseValue(StudentGauntletVo.BaseValue.builder()
                        .attack(baseValue.getAttack())
                        .durability(baseValue.getDurability())
                        .hitRate(baseValue.getHitRate())
                        .move(baseValue.getMove())
                        .source(baseValue.getSource())
                        .sourceAttack(baseValue.getSourceAttack()).build())
                .shipInfo(shipName)
                .armorInfo(getShipName(equipments, 4))
                .missileInfo(getShipName(equipments, 3))
                .weaponsInfo(getShipName(equipments, 2))
                .hero(getShipName(equipments, 5))
                .build();
        studentGauntletVo.setShipInfo(build);

    }

    private StudentGauntletVo.shipName getShipName(List<Map<String, Object>> equipments, int getType) {
        for (Map<String, Object> map : equipments) {
            Integer type = (Integer) map.get("type");
            if (type.equals(getType)) {
                StudentGauntletVo.shipName build = StudentGauntletVo.shipName
                        .builder()
                        .name(map.get("name").toString())
                        .imgUrl(GetOssFile.getPublicObjectUrl(map.get("imgUrl").toString()))
                        .build();
                return build;
            }
        }
        return StudentGauntletVo.shipName
                .builder()
                .name(null)
                .imgUrl(null)
                .build();
    }


    /**
     * 清楚72小时以上的未接受挑战 改为超时
     *
     * @param student
     */
    private void delGauntlets(Student student) {
        String date = DateUtil.beforeHoursTime(72);
        List<Gauntlet> gauntlets = gauntletMapper.selDelGauntlet(student.getId(), date);
        List<Long> gauntletIds = new ArrayList<>();
        for (Gauntlet gauntlet : gauntlets) {
            gauntletIds.add(gauntlet.getId());
        }
        if (gauntletIds.size() > 0) {
            gauntletMapper.updateStatus(4, gauntletIds);
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
    private void addGauntlet(Gauntlet gauntlet, Long challengerStudentId, Long beChallengerStudentId, Long
            courseId, Integer betGold,
                             String challengeName, Integer challengerPoint, Integer beChallengerPoint, String challengerMsg,
                             Integer challengeStatus, Integer beChallengerStatus, Integer challengeStudy, Integer beChallengeStudy,
                             Integer ChallengeGold, Integer beChallengeGold, Date createTime, Integer challengeStudyNow, Integer
                                     beChallengeStudyNow) {
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

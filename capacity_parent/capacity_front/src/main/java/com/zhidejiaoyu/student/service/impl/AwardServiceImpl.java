package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.AwardService;
import com.zhidejiaoyu.student.vo.AwardVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuchenxi
 * @date 2018/6/9 18:22
 */
@Slf4j
@Service
public class AwardServiceImpl extends BaseServiceImpl<AwardMapper, Award> implements AwardService {

    /**
     * 日奖励类型
     */
    private static final int DAILY_TYPE = 1;

    /**
     * 金币奖励类型
     */
    private static final int GOLD_TYPE = 2;

    /**
     * 勋章奖励类型
     */
    private static final int MEDAL_TYPE = 3;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private AwardContentTypeMapper awardContentTypeMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    @Autowired
    private DailyAwardAsync awardAsync;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    final String KEY = "simple_student_ranking";

    /**
     * 获取学生任务奖励信息
     *
     * @param session
     * @param type    1：日奖励；2：任务奖励；3：勋章
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<List<AwardVo>> getAwareInfo(HttpSession session, Integer type) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        List<AwardVo> awardVos = new ArrayList<>();
        switch (type) {
           /* case DAILY_TYPE:
                awardVos.addAll(this.getDailyAward(student));
                break;
            case GOLD_TYPE:
                awardVos.addAll(awardMapper.selectAwardVos(student, GOLD_TYPE));
                break;
            case MEDAL_TYPE:
                awardVos.addAll(awardMapper.selectMedalAwardVos(student));
                break;
            default:*/
        }
        sortAwardVos(awardVos);
        return ServerResponse.createBySuccess(awardVos);
    }


   /* @Override
    public ServerResponse<Map<String, Object>> getAwareSize(int type, HttpSession session, Integer model) {
        Student student = super.getStudent(session);
        Map<String, Object> map = new HashMap<>(16);
        int size = 0;

        int dailyAwardCanGet = awardMapper.countCanGet(student, DAILY_TYPE);
        int goldAwardCanGet = awardMapper.countCanGet(student, GOLD_TYPE);
        int medalAwardCanGet = awardMapper.countCanGet(student, MEDAL_TYPE);

        //获取日奖励领取数量
        if (type == 1) {
            map.put("dailyReward", dailyAwardCanGet);
        }
        //获取金币奖励
        if (type == 2) {
            map.put("goldReward", goldAwardCanGet);
        }
        //勋章奖励领取数量
        if (type == 3) {
            map.put("medalReward", medalAwardCanGet);
        }
        if (type == 4) {
            Object object = redisTemplate.opsForHash().get(KEY, student.getId());
            if (object == null) {
                Integer adminId = null;
                if (student.getTeacherId() != null) {
                    adminId = studentMapper.selSchoolAdminId(student.getId());
                    if (adminId == null) {
                        adminId = student.getTeacherId().intValue();
                    }
                }
                getWorship(student, map, adminId);
                getGoldRank(student, map, adminId);
            } else {
                map = (Map<String, Object>) object;
            }
            if ((boolean) map.get("isClass")) {
                size += 1;
            }
            if ((boolean) map.get("isSchool")) {
                size += 1;
            }
            if ((boolean) map.get("isCountry")) {
                size += 1;
            }
            map.put("all", size + dailyAwardCanGet + goldAwardCanGet + medalAwardCanGet);
        }
        if (type == 5) {
            try {
                Object object = redisTemplate.opsForHash().get(KEY, student.getId());
                if (object != null) {
                    map = (Map<String, Object>) object;
                } else {
                    Integer adminId = null;
                    if (student.getTeacherId() != null) {
                        adminId = studentMapper.selSchoolAdminId(student.getId());
                        if (adminId == null) {
                            adminId = student.getTeacherId().intValue();
                        }
                    }
                    getWorship(student, map, adminId);
                    getGoldRank(student, map, adminId);
                    return ServerResponse.createBySuccess(map);
                }
            } catch (Exception e) {
                log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e);
                Integer adminId = null;
                if (student.getTeacherId() != null) {
                    adminId = studentMapper.selSchoolAdminId(student.getId());
                    if (adminId == null) {
                        adminId = student.getTeacherId().intValue();
                    }
                }
                getWorship(student, map, adminId);
                getGoldRank(student, map, adminId);
                return ServerResponse.createBySuccess(map);
            }
            if (!model.equals(0)) {
                if (model.equals(1)) {
                    boolean isTrue = (boolean) map.get("isClass");
                    if (isTrue) {
                        map.put("isClass", false);
                        redisTemplate.opsForHash().put(KEY, student.getId(), map);
                        redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
                    }
                }
                if (model.equals(2)) {
                    boolean isTrue = (boolean) map.get("isSchool");
                    if (isTrue) {
                        map.put("isSchool", false);
                        redisTemplate.opsForHash().put(KEY, student.getId(), map);
                        redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
                    }
                }
                if (model.equals(3)) {
                    boolean isTrue = (boolean) map.get("isCountry");
                    if (isTrue) {
                        map.put("isCountry", false);
                        redisTemplate.opsForHash().put(KEY, student.getId(), map);
                        redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
                    }
                }
            }
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createBySuccess(map);
    }

    private void getGoldRank(Student student, Map<String, Object> map, Integer adminId) {

        Ranking ranking = rankingMapper.selByStudentId(student.getId());

        List<Integer> teacherIds = null;
        if (student.getTeacherId() != null) {
            adminId = studentMapper.selSchoolAdminId(student.getId());
            if (adminId == null) {
                adminId = student.getTeacherId().intValue();
            }
            teacherIds = teacherMapper.getTeacherIdByAdminId(adminId);
        }
        List<Map<String, Object>> classStudents = studentMapper.getRanking(student.getClassId(), student.getTeacherId(), "2", null, "1", adminId, 0, 100, teacherIds);
        List<Map<String, Object>> schoolStudents = studentMapper.getRanking(student.getClassId(), student.getTeacherId(), "2", null, "2", adminId, 0, 100, teacherIds);
        List<Map<String, Object>> countryStudents = studentMapper.getRanking(student.getClassId(), student.getTeacherId(), "2", null, "3", adminId, 0, 100, teacherIds);
        int classRank = -1;
        int schoolRank = -1;
        int countryRank = -1;
        for (int i = 0; i < classStudents.size(); i++) {
            Long id = Long.parseLong(classStudents.get(i).get("id").toString());
            if (id.equals(student.getId())) {
                classRank = i + 1;
            }
        }
        for (int i = 0; i < schoolStudents.size(); i++) {
            Long id = Long.parseLong(schoolStudents.get(i).get("id").toString());
            if (id.equals(student.getId())) {
                schoolRank = i + 1;
            }
        }
        for (int i = 0; i < countryStudents.size(); i++) {
            Long id = Long.parseLong(countryStudents.get(i).get("id").toString());
            if (id.equals(student.getId())) {
                countryRank = i + 1;
            }
        }
        if (ranking != null) {
            if (classRank != -1) {
                if (ranking.getGoldClassRank() == null) {
                    ranking.setGoldClassRank(classRank);
                    rankingMapper.updateById(ranking);
                    map.put("goldClassRank", 100);
                    map.put("isClass", false);
                } else {
                    classRank = ranking.getGoldClassRank() - classRank;
                    if (map.get("isClass") == null && !((Boolean) map.get("isClass"))) {
                        if (classRank != 0) {
                            map.put("isClass", true);
                        } else {
                            map.put("isClass", false);
                        }
                    }
                    map.put("goldClassRank", classRank);
                }
            } else {
                if (ranking.getGoldClassRank() == null) {
                    ranking.setGoldClassRank(0);
                    rankingMapper.updateById(ranking);
                }
                map.put("goldClassRank", 0);
                if (map.get("isClass") == null && !((Boolean) map.get("isClass"))) {
                    map.put("isClass", false);
                }
            }
            if (schoolRank != -1) {
                if (ranking.getGoldSchoolRank() == null) {
                    ranking.setGoldSchoolRank(schoolRank);
                    rankingMapper.updateById(ranking);
                    map.put("goldSchoolRank", 100);
                    map.put("isSchool", false);
                } else {
                    schoolRank = ranking.getGoldSchoolRank() - schoolRank;
                    if (map.get("isSchool") == null && !((Boolean) map.get("isSchool"))) {
                        if (schoolRank != 0) {
                            map.put("isSchool", true);
                        } else {
                            map.put("isSchool", false);
                        }
                    }
                    map.put("goldSchoolRank", schoolRank);
                }
            } else {
                if (ranking.getGoldSchoolRank() == null) {
                    ranking.setGoldSchoolRank(0);
                    rankingMapper.updateById(ranking);
                }
                map.put("goldSchoolRank", 0);
                if (map.get("isSchool") == null && !((Boolean) map.get("isSchool"))) {
                    map.put("isSchool", false);
                }
            }
            if (countryRank != -1) {
                if (ranking.getGoldCountryRank() == null) {
                    ranking.setGoldCountryRank(countryRank);
                    rankingMapper.updateById(ranking);
                    map.put("goldCountryRank", 100);
                    map.put("isCountry", false);
                } else {
                    countryRank = ranking.getGoldCountryRank() - countryRank;
                    if (map.get("isCountry") == null && !((Boolean) map.get("isCountry"))) {
                        if (countryRank != 0) {
                            map.put("isCountry", true);
                        } else {
                            map.put("isCountry", false);
                        }
                    }
                    map.put("goldCountryRank", countryRank);

                }
            } else {
                if (ranking.getGoldCountryRank() == null) {
                    ranking.setGoldCountryRank(0);
                    rankingMapper.updateById(ranking);
                }
                map.put("goldCountryRank", 0);
                if (map.get("isCountry") == null && !((Boolean) map.get("isCountry"))) {
                    map.put("isCountry", false);
                }
            }
        } else {
            Ranking rank = new Ranking();
            rank.setStudentId(student.getId());
            if (classRank != -1) {
                rank.setGoldClassRank(classRank);
            } else {
                rank.setGoldClassRank(0);
            }
            if (countryRank != -1) {
                rank.setGoldCountryRank(countryRank);
            } else {
                rank.setGoldCountryRank(0);
            }
            if (schoolRank != -1) {
                rank.setGoldSchoolRank(schoolRank);
            } else {
                rank.setGoldSchoolRank(0);
            }
            map.put("goldClassRank", 0);
            map.put("goldSchoolRank", 0);
            map.put("goldCountryRank", 0);
            map.put("isClass", false);
            map.put("isSchool", false);
            map.put("isCountry", false);
            rankingMapper.insert(rank);
        }
        redisTemplate.opsForHash().put(KEY, student.getId(), map);
        redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
    }

    private void getWorship(Student student, Map<String, Object> map, Integer adminId) {
        Ranking ranking = rankingMapper.selByStudentId(student.getId());
        Integer number = worshipMapper.getNumberByStudent(student.getId());
        map.put("number", number);
        List<Integer> teacherIds = null;
        if (student.getTeacherId() != null) {
            adminId = studentMapper.selSchoolAdminId(student.getId());
            if (adminId == null) {
                adminId = student.getTeacherId().intValue();
            }
            teacherIds = teacherMapper.getTeacherIdByAdminId(adminId);
        }
        List<Map<String, Object>> classStudents = studentMapper.getRanking(student.getClassId(), student.getTeacherId(), null, "2", "1", adminId, 0, 100, teacherIds);
        List<Map<String, Object>> schoolStudents = studentMapper.getRanking(student.getClassId(), student.getTeacherId(), null, "2", "2", adminId, 0, 100, teacherIds);
        List<Map<String, Object>> countryStudents = studentMapper.getRanking(student.getClassId(), student.getTeacherId(), null, "2", "3", adminId, 0, 100, teacherIds);
        boolean classStudentRank = false;
        int worshipClassRank = -1;
        int worshipSchoolRank = -1;
        int worshipCountryRank = -1;
        for (int i = 0; i < classStudents.size(); i++) {
            Long id = Long.parseLong(classStudents.get(i).get("id").toString());
            if (id.equals(student.getId())) {
                classStudentRank = true;
                worshipClassRank = i + 1;
            }
        }
        if (worshipClassRank != -1) {
            if (number != null && number > 0) {
                map.put("isClass", true);
            } else {
                map.put("isClass", false);
            }
        } else {
            map.put("isClass", false);
        }
        boolean schoolStudentRank = false;
        for (int i = 0; i < schoolStudents.size(); i++) {
            Long id = Long.parseLong(schoolStudents.get(i).get("id").toString());
            if (id.equals(student.getId())) {
                schoolStudentRank = true;
                worshipSchoolRank = i + 1;
            }
        }
        if (worshipSchoolRank != -1) {
            if (number != null && number > 0) {
                map.put("isSchool", true);
            } else {
                map.put("isSchool", false);
            }
        } else {
            map.put("isSchool", false);
        }
        boolean countryStudentRank = false;
        for (int i = 0; i < countryStudents.size(); i++) {
            Long id = Long.parseLong(countryStudents.get(i).get("id").toString());
            if (id.equals(student.getId())) {
                countryStudentRank = true;
                worshipCountryRank = i + 1;
            }
        }
        if (worshipCountryRank != -1) {
            if (number != null && number > 0) {
                map.put("isCountry", true);
            } else {
                map.put("isCountry", false);
            }
        } else {
            map.put("isCountry", false);
        }
        if (ranking != null) {
            if (classStudentRank) {
                if (ranking.getWorshipClassRank() != null) {
                    map.put("worshipClassRank", ranking.getWorshipClassRank() - worshipClassRank);
                } else {
                    ranking.setWorshipClassRank(worshipClassRank);
                    rankingMapper.updateById(ranking);
                    map.put("worshipClassRank", 100);
                }
            } else {
                map.put("worshipClassRank", 0);

            }
            if (schoolStudentRank) {
                if (ranking.getWorshipSchoolRank() != null) {
                    map.put("worshipSchoolRank", ranking.getWorshipSchoolRank() - worshipSchoolRank);
                } else {
                    ranking.setWorshipClassRank(worshipSchoolRank);
                    rankingMapper.updateById(ranking);
                    map.put("worshipSchoolRank", 100);

                }
            } else {
                map.put("worshipSchoolRank", 0);
            }
            if (countryStudentRank) {
                if (ranking.getWorshipCountryRank() != null) {
                    map.put("worshipCountryRank", ranking.getWorshipCountryRank() - worshipCountryRank);
                } else {
                    ranking.setWorshipClassRank(worshipCountryRank);
                    rankingMapper.updateById(ranking);
                    map.put("worshipCountryRank", 100);
                }
            } else {
                map.put("worshipCountryRank", 0);
            }
        } else {
            Ranking rank = new Ranking();
            rank.setStudentId(student.getId());
            if (worshipClassRank != -1) {
                rank.setWorshipClassRank(worshipClassRank);
            } else {
                rank.setWorshipClassRank(0);
            }
            if (worshipCountryRank != -1) {
                rank.setWorshipCountryRank(worshipCountryRank);
            } else {
                rank.setWorshipCountryRank(0);
            }
            if (worshipSchoolRank != -1) {
                rank.setWorshipSchoolRank(worshipSchoolRank);
            } else {
                rank.setWorshipSchoolRank(0);
            }
            map.put("worshipClassRank", 0);
            map.put("worshipSchoolRank", 0);
            map.put("worshipCountryRank", 0);
            rankingMapper.insert(rank);
        }
    }*/

    /**
     * 对奖励信息进行排序<br>
     * 将可领取的奖励排在头部，紧接着是已领取的奖励，然后是不可领取的奖励
     *
     * @param awardVos
     */
    private void sortAwardVos(List<AwardVo> awardVos) {
        List<AwardVo> canGet = new ArrayList<>();
        List<AwardVo> got = new ArrayList<>();
        List<AwardVo> noGet = new ArrayList<>();

        awardVos.forEach(awardVo -> {
            if (awardVo.getId() != null) {
                if (awardVo.getGetFlag()) {
                    got.add(awardVo);
                } else if (awardVo.getCanGet()) {
                    canGet.add(awardVo);
                } else {
                    noGet.add(awardVo);
                }
            }
        });

        awardVos.clear();
        awardVos.addAll(canGet);
        awardVos.addAll(got);
        awardVos.addAll(noGet);
    }

    /**
     * 获取日奖励信息
     *
     * @param student
     */
    private List<AwardVo> getDailyAward(Student student) {
        /*awardAsync.completeAllDailyAward(student);
        return awardMapper.selectAwardVos(student, DAILY_TYPE);*/
        return null;
    }

    /**
     * 领取奖励
     *
     * @param session
     * @param awareId 奖励id
     * @param getType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> getAware(HttpSession session, Long awareId, Integer getType) {
        /*Student student = super.getStudent(session);
        Award award = awardMapper.selectByIdAndStuId(awareId, student.getId());
        Medal medal = null;
        String msg = null;
        String awardType = null;
        String awardContent = null;
        if (award != null && award.getGetFlag() == 2) {
            if (getType == 1) {
                // 领取金币奖励
                AwardContentType awardContentType = awardContentTypeMapper.selectByPrimaryKey(award.getAwardContentType());
                if (awardContentType != null) {
                    awardContent = awardContentType.getAwardContent();
                    Integer awardGold = awardContentType.getAwardGold();
                    // 金币奖励
                    awardType = award.getType() == 1 ? "日奖励" : "任务奖励";
                    msg = "id为[" + student.getId() + "]的学生[" + student.getStudentName() + "]在["
                            + DateUtil.DateTime(new Date()) + "]领取了[" + awardType + "]下[" + awardContent + "]的#" + awardGold + "#个金币";
                    // 更新学生金币信息
                    student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
                    try {
                        studentMapper.updateByPrimaryKeySelective(student);
                    } catch (Exception e) {
                        log.error("id为[{}]的学生在领取[{}]中[{}]奖励时更新学生金币信息出错", student.getId(), awardType, awardContent, e);
                        return ServerResponse.createByErrorMessage("更新学生金币信息出错!");
                    }
                    // 保存领取奖励日志
                    RunLog runLog = new RunLog(student.getId(), 4, msg, new Date());
                    runLog.setUnitId(student.getUnitId());
                    runLog.setCourseId(student.getCourseId());
                    try {
                        runLogMapper.insert(runLog);
                        student = studentMapper.selectById(student.getId());
                        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                        getLevel(session);
                    } catch (Exception e) {
                        log.error("id为[{}]的学生在领取[{}]中[{}]奖励时保存日志出错！", student.getId(), awardType, awardContent, e);
                    }
                }
            } else {
                // 领取勋章奖励
                medal = medalMapper.selectById(award.getMedalType());
                awardType = "勋章";
                awardContent = medal.getMarkedWords();
                msg = "id为[" + student.getId() + "]的学生[" + student.getStudentName() + "]在["
                        + DateUtil.DateTime(new Date()) + "]领取了勋章[" + medal.getParentName() + "-" + medal.getChildName() + "]#" + medal.getChildImgUrl() + "# ";
                RunLog runLog = new RunLog(student.getId(), 7, msg, new Date());
                runLog.setCourseId(student.getCourseId());
                runLog.setUnitId(student.getUnitId());
                try {
                    runLogMapper.insert(runLog);
                } catch (Exception e) {
                    log.error("id为[{}]的学生在领取勋章[{}-{}]时保存日志出错！", student.getId(), medal.getParentName(), medal.getChildName(), e);
                }
            }
            log.info(msg);
            // 更新奖励领取状态
            award.setGetFlag(1);
            award.setGetTime(new Date());
            try {
                awardMapper.updateById(award);
                if (getType == 2) {
                    // 判断学生是否能够领取勋章大人
                    medalAwardAsync.expertMan(student);
                }
            } catch (Exception e) {
                log.error("id为[{}]的学生在领取[{}]中[{}]奖励时更新奖励领取状态信息出错", student.getId(), awardType, awardContent, e);
                return ServerResponse.createByErrorMessage("更新奖励领取状态信息出错!");
            }

            if (getType == 2) {
                return ServerResponse.createBySuccess(medal.getParentName());
            }
            return ServerResponse.createBySuccessMessage("领取成功！");

        }*/
        return ServerResponse.createByErrorMessage("未查询到当前奖励信息！");
    }
}

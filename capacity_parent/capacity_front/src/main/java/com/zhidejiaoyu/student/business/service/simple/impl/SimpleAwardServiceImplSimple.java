package com.zhidejiaoyu.student.business.service.simple.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.vo.simple.AwardVo;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleAwardServiceSimple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author wuchenxi
 * @date 2018/6/9 18:22
 */
@Slf4j
@Service
public class SimpleAwardServiceImplSimple extends SimpleBaseServiceImpl<SimpleAwardMapper, Award> implements SimpleAwardServiceSimple {

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
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleMedalMapper simpleMedalMapper;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private SimpleAwardContentTypeMapper simpleAwardContentTypeMapper;

    @Autowired
    private SimpleWorshipMapper worshipMapper;

    @Autowired
    private DailyAwardAsync awardAsync;

    @Autowired
    private SimpleRankingMapper simpleRankingMapper;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private RankOpt rankOpt;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

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
        Student student = super.getStudent(session);
        List<AwardVo> awardVos = new ArrayList<>();
        switch (type) {
            case DAILY_TYPE:
                awardVos.addAll(this.getDailyAward(student));
                break;
            case GOLD_TYPE:
                awardVos.addAll(simpleAwardMapper.selectAwardVos(student, GOLD_TYPE));
                break;
            case MEDAL_TYPE:
                List<AwardVo> awardVos1 = simpleAwardMapper.selectMedalAwardVos(student);
                awardVos1.forEach(awardVo -> awardVo.setImgUrl(AliyunInfoConst.host + awardVo.getImgUrl()));
                awardVos.addAll(awardVos1);
                break;
            default:
        }
        sortAwardVos(awardVos);
        return ServerResponse.createBySuccess(awardVos);
    }


    @Override
    public ServerResponse<Object> getAwareSize(int type, HttpSession session, Integer model) {
        Student student = super.getStudent(session);
        Map<String, Object> map = new HashMap<>(16);
        int size = 0;

        // 日奖励可领取个数
        int dailyAwardCanGet = simpleAwardMapper.countCanGet(student, DAILY_TYPE);
        // 金币奖励可领取个数
        int goldAwardCanGet = simpleAwardMapper.countCanGet(student, GOLD_TYPE);
        // 勋章奖励可领取个数
        int medalAwardCanGet = simpleAwardMapper.countCanGet(student, MEDAL_TYPE);

        //获取日奖励领取数量
        if (type == 1) {
            map.put("dailyReward", dailyAwardCanGet);
            return ServerResponse.createBySuccess(map);
        }
        //获取金币奖励
        if (type == 2) {
            map.put("goldReward", goldAwardCanGet);
            return ServerResponse.createBySuccess(map);
        }
        //勋章奖励领取数量
        if (type == 3) {
            map.put("medalReward", medalAwardCanGet);
            return ServerResponse.createBySuccess(map);
        }

        if (type == 4) {
            getWorship(student, map);
            getGoldRank(student, map);

            if ((boolean) map.get("isClass")) {
                size += 1;
            }
            if ((boolean) map.get("isSchool")) {
                size += 1;
            }
            if ((boolean) map.get("isCountry")) {
                size += 1;
            }
            map = new HashMap<>(16);
            map.put("all", size + dailyAwardCanGet + goldAwardCanGet + medalAwardCanGet);
        }
        if (type == 5) {

            getWorship(student, map);
            getGoldRank(student, map);

            // 消除指定模块的红色角标
            if (Objects.equals(model, 1)) {
                map.put("isClass", false);
            } else if (Objects.equals(model, 2)) {
                map.put("isSchool", false);
            } else if (Objects.equals(model, 3)) {
                map.put("isCountry", false);
            }

        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> updateRanking(HttpSession session, Integer type) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        Ranking ranking = simpleRankingMapper.selByStudentId(studentId);

        switch (type) {
            case 1:
                // 更新班级排行名次
                long classGoldRank = rankOpt.getRank(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), studentId);
                long classWorshipRank = rankOpt.getRank(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), studentId);
                ranking.setGoldClassRank(classGoldRank == -1 ? 0 : (int) classGoldRank);
                ranking.setWorshipClassRank(classWorshipRank == -1 ? 0 : (int) classWorshipRank);
                simpleRankingMapper.updateById(ranking);
                break;
            case 2:
                // 更新学校排行名次
                long schoolGoldRank = rankOpt.getRank(RankKeysConst.SCHOOL_GOLD_RANK + TeacherInfoUtil.getSchoolAdminId(student), studentId);
                long schoolWorshipRank = rankOpt.getRank(RankKeysConst.SCHOOL_WORSHIP_RANK + TeacherInfoUtil.getSchoolAdminId(student), studentId);
                ranking.setGoldSchoolRank(schoolGoldRank == -1 ? 0 : (int) schoolGoldRank);
                ranking.setWorshipSchoolRank(schoolWorshipRank == -1 ? 0 : (int) schoolWorshipRank);
                simpleRankingMapper.updateById(ranking);
                break;
            case 3:
                // 更新全国排行名次
                long countryGoldRank = rankOpt.getRank(RankKeysConst.COUNTRY_GOLD_RANK, studentId);
                long countryWorshipRank = rankOpt.getRank(RankKeysConst.COUNTRY_WORSHIP_RANK, studentId);
                ranking.setGoldCountryRank(countryGoldRank == -1 ? 0 : (int) countryGoldRank);
                ranking.setWorshipCountryRank(countryWorshipRank == -1 ? 0 : (int) countryWorshipRank);
                simpleRankingMapper.updateById(ranking);
                break;
            default:
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取学生的金币排名及排名上升或下降的名次
     *
     * @param student
     * @param map
     */
    private void getGoldRank(Student student, Map<String, Object> map) {
        Long studentId = student.getId();

        Ranking ranking = simpleRankingMapper.selByStudentId(studentId);

        long classRank = rankOpt.getRank(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), studentId);
        long schoolRank = rankOpt.getRank(RankKeysConst.SCHOOL_GOLD_RANK + TeacherInfoUtil.getSchoolAdminId(student), studentId);
        long countryRank = rankOpt.getRank(RankKeysConst.COUNTRY_GOLD_RANK, studentId);

        if (ranking != null) {
            // 学生金币班级排行变化名次
            if (ranking.getGoldClassRank() == null) {
                map.put("goldClassRank", 0);
                map.put("isClass", false);
            } else {
                int changeRank = (int) (ranking.getGoldClassRank() - classRank);
                if (map.get("isClass") != null && !((Boolean) map.get("isClass"))) {
                    map.put("isClass", changeRank != 0);
                }
                map.put("goldClassRank", changeRank);
            }

            // 学生金币学校排行变化名次
            if (ranking.getGoldSchoolRank() == null) {
                map.put("goldSchoolRank", 0);
                map.put("isSchool", false);
            } else {
                int changeRank = (int) (ranking.getGoldSchoolRank() - schoolRank);
                if (map.get("isSchool") != null && !((Boolean) map.get("isSchool"))) {
                    map.put("isSchool", changeRank != 0);
                }
                map.put("goldSchoolRank", changeRank);
            }

            // 学生金币全国排行变化名次
            if (ranking.getGoldCountryRank() == null) {
                map.put("goldCountryRank", 0);
                map.put("isCountry", false);
            } else {
                int changeRank = (int) (ranking.getGoldCountryRank() - countryRank);
                if (map.get("isCountry") != null && !((Boolean) map.get("isCountry"))) {
                    map.put("isCountry", changeRank != 0);
                }
                map.put("goldCountryRank", changeRank);
            }
        } else {
            Ranking rank = new Ranking();
            rank.setStudentId(studentId);
            rank.setGoldClassRank(classRank == -1 ? 0 : (int) classRank);
            rank.setGoldSchoolRank(schoolRank == -1 ? 0 : (int) schoolRank);
            rank.setGoldCountryRank(countryRank == -1 ? 0 : (int) countryRank);
            simpleRankingMapper.insert(rank);

            map.put("goldClassRank", 0);
            map.put("goldSchoolRank", 0);
            map.put("goldCountryRank", 0);
            map.put("isClass", false);
            map.put("isSchool", false);
            map.put("isCountry", false);
        }
    }

    /**
     * 获取学生的勋章排名及排名上升或下降的名次
     *
     * @param student
     * @param map
     */
    private void getWorship(Student student, Map<String, Object> map) {
        Long studentId = student.getId();

        Ranking ranking = simpleRankingMapper.selByStudentId(studentId);
        // 学生新被膜拜次数
        Integer numberByStudent = worshipMapper.getNumberByStudent(student.getId());
        map.put("number", numberByStudent);

        long classRank = rankOpt.getRank(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), studentId);
        long schoolRank = rankOpt.getRank(RankKeysConst.SCHOOL_WORSHIP_RANK + TeacherInfoUtil.getSchoolAdminId(student), studentId);
        long countryRank = rankOpt.getRank(RankKeysConst.COUNTRY_WORSHIP_RANK, studentId);

        if (ranking != null) {
            // 膜拜班级排行变化名次
            if (ranking.getWorshipClassRank() != null && classRank != -1) {
                long change = ranking.getWorshipClassRank() - classRank;
                map.put("worshipClassRank", change);
                map.put("isClass", change != 0);
            } else {
                map.put("worshipClassRank", 0);
                map.put("isClass", false);
            }

            // 膜拜学校排行变化名次
            if (ranking.getWorshipSchoolRank() != null && schoolRank != -1) {
                long change = ranking.getWorshipSchoolRank() - schoolRank;
                map.put("worshipSchoolRank", change);
                map.put("isSchool", change != 0);
            } else {
                map.put("worshipSchoolRank", 0);
                map.put("isSchool", false);
            }

            // 膜拜全校排行变化名次
            if (ranking.getWorshipCountryRank() != null && countryRank != -1) {
                long change = ranking.getWorshipCountryRank() - countryRank;
                map.put("worshipCountryRank", change);
                map.put("isCountry", change != 0);
            } else {
                map.put("worshipCountryRank", 0);
                map.put("isCountry", false);
            }
        } else {
            Ranking rank = new Ranking();
            rank.setStudentId(studentId);
            rank.setWorshipClassRank(classRank == -1 ? 0 : (int) classRank);
            rank.setWorshipSchoolRank(schoolRank == -1 ? 0 : (int) schoolRank);
            rank.setWorshipCountryRank(countryRank == -1 ? 0 : (int) countryRank);
            simpleRankingMapper.insert(rank);

            map.put("isClass", false);
            map.put("isSchool", false);
            map.put("isCountry", false);

            map.put("worshipClassRank", 0);
            map.put("worshipSchoolRank", 0);
            map.put("worshipCountryRank", 0);
        }
    }

    /**
     * 对奖励信息进行排序<br>
     * 将可领取的奖励排在头部，紧接着是已领取的奖励，然后是不可领取的奖励
     *
     * @param awardVos
     */
    private void sortAwardVos(List<AwardVo> awardVos) {
        // 可领取奖励集合
        List<AwardVo> canGet = new ArrayList<>();
        // 已领取奖励集合
        List<AwardVo> got = new ArrayList<>();
        // 不可领取奖励集合
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
        dailyAwardAsync.deleteOtherDailyAward(student);
        awardAsync.completeAllDailyAward(student);
        return simpleAwardMapper.selectAwardVos(student, DAILY_TYPE);
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
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> getAware(HttpSession session, Long awareId, Integer getType) {
        Student student = super.getStudent(session);
        Award award = simpleAwardMapper.selectByIdAndStuId(awareId, student.getId());
        Medal medal = null;
        String msg = null;
        String awardType = null;
        String awardContent = null;
        if (award != null && award.getGetFlag() == 2) {
            if (getType == 1) {
                // 领取金币奖励
                AwardContentType awardContentType = simpleAwardContentTypeMapper.selectByPrimaryKey(award.getAwardContentType());
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
                        simpleStudentMapper.updateById(student);
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
                        getLevel(session);
                    } catch (Exception e) {
                        log.error("id为[{}]的学生在领取[{}]中[{}]奖励时保存日志出错！", student.getId(), awardType, awardContent, e);
                    }
                }
            } else {
                // 领取勋章奖励
                medal = simpleMedalMapper.selectById(award.getMedalType());
                awardType = "勋章";
                awardContent = medal.getMarkedWords();
                msg = "id为[" + student.getId() + "]的学生[" + student.getStudentName() + "]在["
                        + DateUtil.DateTime(new Date()) + "]领取了勋章[" + medal.getParentName() + "-" + medal.getChildName() + "]#" + medal.getChildImgUrl() + "# ";
                RunLog runLog = new RunLog(student.getId(), 7, msg, new Date());
                runLog.setCourseId(student.getCourseId());
                runLog.setUnitId(student.getUnitId());
                try {
                    runLogMapper.insert(runLog);
                    rankOpt.optMedalRank(student);
                } catch (Exception e) {
                    log.error("id为[{}]的学生在领取勋章[{}-{}]时保存日志出错！", student.getId(), medal.getParentName(), medal.getChildName(), e);
                }
            }
            log.info(msg);
            // 更新奖励领取状态
            award.setGetFlag(1);
            award.setGetTime(new Date());
            try {
                simpleAwardMapper.updateById(award);
                if (getType == 2) {
                    // 判断学生是否能够领取勋章达人
                    medalAwardAsync.expertMan(student);
                }
            } catch (Exception e) {
                log.error("id为[{}]的学生在领取[{}]中[{}]奖励时更新奖励领取状态信息出错", student.getId(), awardType, awardContent, e);
                return ServerResponse.createByErrorMessage("更新奖励领取状态信息出错!");
            }

            if (getType == 2) {
                return ServerResponse.createBySuccess(GetOssFile.getPublicObjectUrl(medal.getGetGifImgUrl()));
            }
            return ServerResponse.createBySuccessMessage("领取成功！");

        }
        return ServerResponse.createByErrorMessage("未查询到当前奖励信息！");
    }
}

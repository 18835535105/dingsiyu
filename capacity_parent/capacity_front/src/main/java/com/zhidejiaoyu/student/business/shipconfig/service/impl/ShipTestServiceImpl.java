package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.SimpleGauntletMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.goldUtil.StudentGoldAdditionUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipTestService;
import com.zhidejiaoyu.student.common.redis.PkCopyRedisOpt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ShipTestServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ShipTestService {

    /**
     * 挑战副本成功奖励金币数
     */
    private static final int AWARD_GOLD = 10;

    @Resource
    private ShipIndexService shipIndexService;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private SimpleGauntletMapper simpleGauntletMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;
    @Resource
    private SourcePowerRankOpt sourcePowerRankOpt;
    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;
    @Resource
    private RunLogMapper runLogMapper;

    @Resource
    private PkCopyStateMapper pkCopyStateMapper;

    @Resource
    private PkCopyBaseMapper pkCopyBaseMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private PkCopyRedisOpt pkCopyRedisOpt;

    @Override
    public Object getTest(HttpSession session, Long studentId) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        //查询一小时内的pk次数
        int pkCount = getPkCount(student);
        if (pkCount == 1) {
            returnMap.put("status", 2);
            return returnMap;
        }
        //查询pk信息
        //1.pk发起人的信息
        // 学生装备的飞船及装备信息
        returnMap.put("status", 1);
        returnMap.put("originator", shipIndexService.getStateOfWeek(student.getId()));
        returnMap.put("originatorImgUrl", GetOssFile.getPublicObjectUrl(studentEquipmentMapper.selectImgUrlByStudentId(student.getId())));
        //2.被pk人的信息
        returnMap.put("challenged", shipIndexService.getStateOfWeek(studentId));
        returnMap.put("challengedImgUrl", GetOssFile.getPublicObjectUrl(studentEquipmentMapper.selectImgUrlByStudentId(studentId)));
        //3.查询题目
        returnMap.put("subject", getSubject(student.getId()));
        return returnMap;
    }


    @Override
    public Object getSingleTesting(HttpSession session, Long bossId) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        PkCopyBase pkCopyBase = pkCopyBaseMapper.selectById(bossId);
        //获得学生当天挑战次数
        int count = simpleGauntletMapper.countByStudentIdAndBossId(student.getId(), bossId);
        //判断是否到达每天挑战次数上限
        if (pkCopyBase.getChallengeCycle() <= count) {
            returnMap.put("status", 2);
            return returnMap;
        }
        //查询当前boss剩余学量
        PkCopyState pkCopyState = pkCopyStateMapper.selectByStudentIdAndBossId(student.getId(), bossId);
        if (pkCopyState != null) {
            pkCopyBase.setDurability(pkCopyState.getDurability());
        }
        returnMap.put("status", 1);
        returnMap.put("originator", shipIndexService.getStateOfWeek(student.getId()));
        returnMap.put("originatorImgUrl", GetOssFile.getPublicObjectUrl(studentEquipmentMapper.selectImgUrlByStudentId(student.getId())));
        returnMap.put("challenged", GetOssFile.getPublicObjectUrl(pkCopyBase.getImgUrl()));
        returnMap.put("challengedImgUrl", pkCopyBase);
        //3.查询题目
        returnMap.put("subject", getSubject(student.getId()));
        return returnMap;
    }

    @Override
    public Object saveSingleTesting(HttpSession session, Long bossId, Integer bloodVolume) {
        Student student = getStudent(session);
        //查询当前boss剩余学量
        PkCopyState pkCopyState = pkCopyStateMapper.selectByStudentIdAndBossId(student.getId(), bossId);
        Gauntlet gauntlet = new Gauntlet();
        gauntlet.setBeChallengerStudentId(bossId);
        gauntlet.setChallengerStudentId(student.getId());
        gauntlet.setType(2);
        gauntlet.setCreateTime(new Date());
        //如果有信息，在这里减少学量
        if (pkCopyState != null) {
            bloodVolume = pkCopyState.getDurability() - bloodVolume;
            if (bloodVolume <= 0) {
                gauntlet.setChallengeStatus(1);
                gauntlet.setBeChallengerStatus(2);
                this.saveStudentGold(student, 10);

                pkCopyState.setDurability(0);
            } else {
                gauntlet.setChallengeStatus(2);
                gauntlet.setBeChallengerStatus(1);
                pkCopyState.setDurability(bloodVolume);
            }
            pkCopyStateMapper.updateById(pkCopyState);
        } else {
            pkCopyState = new PkCopyState();
            pkCopyState.setStudentId(student.getId());
            pkCopyState.setType(1);
            pkCopyState.setCreateTime(new Date());
            pkCopyState.setPkCopyBaseId(bossId);
            pkCopyState.setSchoolAdminId(TeacherInfoUtil.getSchoolAdminId(student));
            PkCopyBase pkCopyBase = pkCopyBaseMapper.selectById(bossId);
            Integer durability = pkCopyBase.getDurability();
            durability -= bloodVolume;
            if (durability <= 0) {
                gauntlet.setChallengeStatus(1);
                gauntlet.setBeChallengerStatus(2);
                pkCopyState.setDurability(0);
                this.saveStudentGold(student, 10);
            } else {
                gauntlet.setChallengeStatus(2);
                gauntlet.setBeChallengerStatus(1);
                pkCopyState.setDurability(durability);
            }
            pkCopyStateMapper.insert(pkCopyState);
            simpleGauntletMapper.insert(gauntlet);
        }
        return ServerResponse.createBySuccess();
    }

    private void saveStudentGold(Student student, double gold) {
        Double goldAddition = StudentGoldAdditionUtil.getGoldAddition(student, gold);
        studentMapper.updateBySystemGold(goldAddition, student.getId());
        RunLog runLog = new RunLog(student.getId(), 4, "学生飞船个人挑战获得#" + goldAddition + "#金币", new Date());
        runLogMapper.insert(runLog);
    }


    @Override
    public Object saveTest(HttpSession session, Long beChallenged, Integer type) {
        Student student = getStudent(session);
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(student.getId());
        Map<String, Object> returnMap = new HashMap<>();
        //查询一小时内的pk次数
        int pkCount = getPkCount(student);
        if (pkCount == 1) {
            returnMap.put("status", 2);
            return returnMap;
        }
        Gauntlet gauntlet = new Gauntlet();
        if (type.equals(1)) {
            gauntlet.setChallengeStatus(1);
            gauntlet.setBeChallengerStatus(2);
            StudentExpansion beChallengedStudent = studentExpansionMapper.selectByStudentId(beChallenged);
            if (beChallengedStudent.getStudyPower() > expansion.getStudyPower()) {
                expansion.setStudyPower(expansion.getStudyPower() + 10);
            }
        } else {
            gauntlet.setChallengeStatus(2);
            gauntlet.setBeChallengerStatus(1);
            expansion.setStudyPower(expansion.getStudyPower() - 5 >= 0 ? expansion.getStudyPower() - 5 : 0);
        }
        //获取飞船图片
        gauntlet.setType(1);
        gauntlet.setChallengerImgUrl(studentEquipmentMapper.selectImgUrlByStudentId(student.getId()));
        gauntlet.setBeChallengerImgUrl(studentEquipmentMapper.selectImgUrlByStudentId(beChallenged));
        gauntlet.setChallengerStudentId(student.getId());
        gauntlet.setBeChallengerStudentId(beChallenged);
        gauntlet.setCreateTime(new Date());
        simpleGauntletMapper.insert(gauntlet);
        shipAddEquipmentService.updateLeaderBoards(student);
        Long currentRanking = this.getCurrentRanking(student);
        if (currentRanking < expansion.getRanking() && currentRanking != 0) {
            expansion.setRanking(currentRanking);
        }
        studentExpansionMapper.updateById(expansion);
        returnMap.put("status", 1);
        return returnMap;
    }

    @Override
    public Object getPKRecord(HttpSession session, int type) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        //获取最高排名
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(student.getId());
        //获取当前排名
        returnMap.put("currentRanking", getCurrentRanking(student));
        //获取pk信息
        int pkGames = simpleGauntletMapper.getPkGames(student.getId(), 1);
        int winPkGames = simpleGauntletMapper.getPkGames(student.getId(), 2);
        double win = Math.floor(1.0 * pkGames / winPkGames * 100);
        //pk场数
        returnMap.put("pkGames", pkGames);
        //pk胜率
        returnMap.put("winPk", win);
        returnMap.put("highestRanking", expansion.getRanking());
        List<Map<String, Object>> pkRecord = simpleGauntletMapper.getPkRecord(student.getId(), type);
        List<Map<String, Object>> returnList = new ArrayList<>();
        pkRecord.forEach(record -> {
            String headUrl1 = record.get("imgUrl1").toString();
            headUrl1 = GetOssFile.getPublicObjectUrl(headUrl1);
            record.put("imgUrl1", headUrl1);
            String headUrl2 = record.get("imgUrl2").toString();
            headUrl2 = GetOssFile.getPublicObjectUrl(headUrl2);
            record.put("imgUrl2", headUrl2);
            returnList.add(record);
        });
        returnMap.put("list", returnList);
        return returnMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveSchoolCopyInfo(Long copyId, Integer reduceDurability) {

        Student student = super.getStudent();
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        pkCopyRedisOpt.saveSchoolCopyStudentInfo(schoolAdminId, copyId, student.getId());

        /*
         查询当前校区有没有学生正在挑战当前副本
         如果有，当前副本耐久度=当前副本剩余耐久度-本次减少的耐久度
         如果没有，当前副本耐久度=当前副本总耐久度-本次减少的耐久度
         */
        PkCopyState pkCopyState = pkCopyStateMapper.selectBySchoolAdminIdAndPkCopyBaseId(schoolAdminId, copyId);
        Date nowTime = new Date();
        if (pkCopyState == null) {
            PkCopyBase pkCopyBase = pkCopyBaseMapper.selectById(copyId);
            // 剩余耐久度
            int durability = pkCopyBase.getDurability() - reduceDurability;
            pkCopyStateMapper.insert(PkCopyState.builder()
                    .type(2)
                    .pkCopyBaseId(copyId)
                    .schoolAdminId(schoolAdminId)
                    .durability(Math.max(0, durability))
                    .createTime(nowTime)
                    .updateTime(nowTime)
                    .build());
            savePkSchoolCopyAward(copyId, schoolAdminId, durability);
        } else if (pkCopyState.getDurability() > 0) {
            int durability = pkCopyState.getDurability() - reduceDurability;

            pkCopyState.setDurability(Math.max(0, durability));
            pkCopyState.setUpdateTime(nowTime);
            pkCopyStateMapper.updateById(pkCopyState);

            savePkSchoolCopyAward(copyId, schoolAdminId, durability);

        }

        return ServerResponse.createBySuccess();
    }

    public void savePkSchoolCopyAward(Long copyId, Integer schoolAdminId, Integer durability) {
        if (durability <= 0) {
            // 挑战成功，对校区内所有挑战该副本的学生奖励金币
            Set<Long> schoolCopyStudentInfoSet = pkCopyRedisOpt.getSchoolCopyStudentInfo(schoolAdminId, copyId);
            for (Long studentId : schoolCopyStudentInfoSet) {
                Student student1 = studentMapper.selectById(studentId);
                Double goldBonus = StudentGoldAdditionUtil.getGoldAddition(student1, AWARD_GOLD);
                student1.setSystemGold(BigDecimalUtil.add(student1.getSystemGold(), goldBonus));
                studentMapper.updateById(student1);

                super.saveRunLog(student1, 4,"学生[" + student1.getStudentName() + "]在校区副本挑战胜利，奖励#" + goldBonus + "#枚金币");
            }
        }
    }


    private long getCurrentRanking(Student student) {
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        String key = SourcePowerKeysConst.SCHOOL_RANK + schoolAdminId;
        return sourcePowerRankOpt.getRank(key, student.getId());
    }

    private int getPkCount(Student student) {
        Date date = new Date();
        String beforeTime = DateUtil.beforeHoursTime(1);
        int count = simpleGauntletMapper.getCountByStudentIdAndTime(student.getId(), date.toString(), beforeTime);
        if (count >= 5) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 查询题目信息
     *
     * @param studentId
     * @return
     */
    private Object getSubject(Long studentId) {
        //1，获取单元
        List<Long> unitIds = learnNewMapper.getUnitIdByStudentIdAndType(studentId, 1);
        //2,获取单元题目
        if (unitIds != null && unitIds.size() > 0) {
            List<SubjectsVO> subjectsVos = vocabularyMapper.selectSubjectsVOByUnitIds(unitIds);
            if (subjectsVos.size() > 15) {
                subjectsVos = subjectsVos.subList(0, 15);
            } else {
                List<SubjectsVO> subjectsVos1 = new ArrayList<>();
                subjectsVos1.addAll(subjectsVos);
                while (subjectsVos1.size() < 15) {
                    subjectsVos.forEach(subject -> {
                        if (subjectsVos1.size() < 15) {
                            subjectsVos1.add(subject);
                        }
                    });
                }
                subjectsVos = subjectsVos1;
            }
            return subjectsVos;
        }
        return null;
    }


}

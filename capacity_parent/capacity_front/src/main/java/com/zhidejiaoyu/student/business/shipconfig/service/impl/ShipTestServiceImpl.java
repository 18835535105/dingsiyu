package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.goldUtil.StudentGoldAdditionUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.ship.EquipmentVo;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipTestService;
import com.zhidejiaoyu.student.business.shipconfig.vo.EquipmentEquipmentExperienceVo;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import com.zhidejiaoyu.student.business.shipconfig.vo.PkInfoVO;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import com.zhidejiaoyu.student.common.redis.PkCopyRedisOpt;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

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
    private GauntletMapper gauntletMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;
    @Resource
    private SourcePowerRankOpt sourcePowerRankOpt;
    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;

    @Resource
    private PkCopyStateMapper pkCopyStateMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private PkCopyRedisOpt pkCopyRedisOpt;

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private EquipmentExpansionMapper equipmentExpansionMapper;

    @Resource
    private PkCopyBaseMapper pkCopyBaseMapper;

    /**
     * @param session
     * @param studentId 被挑战人id
     * @return
     */
    @Override
    public Object getTest(HttpSession session, Long studentId) {
        Student student = getStudent(session);
        //查询一小时内的pk次数
        int pkCount = getPkCount(student);
        if (pkCount == 1) {
            return ServerResponse.createByError(401, "挑战次数达到上限");
        }
        //用完就删除
        PkInfoVO.Challenged equipmentMap = getEquipmentMap(student.getId());
        IndexVO.BaseValue battle = equipmentMap.getBattle();
        battle.setAttack(25);
        equipmentMap.setBattle(battle);
        return ServerResponse.createBySuccess(PkInfoVO.builder()
                .challenged(equipmentMap)
                .originator(equipmentMap)
                .subject(getSubject(student.getId()))
                .build());
        //正确的数据
       /* return ServerResponse.createBySuccess(PkInfoVO.builder()
                .challenged(getEquipmentMap(studentId))
                .originator(getEquipmentMap(student.getId()))
                .subject(getSubject(student.getId()))
                .build());*/

    }

    /**
     * 返回学生的返回值信息
     *
     * @param studentId
     * @return
     */
    private PkInfoVO.Challenged getEquipmentMap(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        return PkInfoVO.Challenged.builder()
                .battle(shipIndexService.getStateOfWeek(studentId))
                .armorEquipment(getEquipmentInformation(studentId, 4))
                .missileEquipment(getEquipmentInformation(studentId, 3))
                .shipEquipment(getEquipmentInformation(studentId, 1))
                .armsEquipment(getEquipmentInformation(studentId, 2))
                .hardImg(GetOssFile.getPublicObjectUrl(student.getHeadUrl()))
                .nickName(student.getNickname())
                .build();
    }

    /**
     * @param studentId 学生id
     * @param type      1，飞船 2，武器 3，导弹 4，装甲
     * @return
     */
    private EquipmentVo getEquipmentInformation(Long studentId, int type) {
        EquipmentVo equipment = equipmentMapper.selectNameAndGradeByStudentId(studentId, type);
        if (equipment != null) {
            equipment.setImgUrl(getImg(studentEquipmentMapper.selectImgUrlByStudentId(studentId, type)));
            equipment.setLeftImgUrl(getImg(studentEquipmentMapper.selectLeftUrlByStudentIdAndType(studentId, type)));
        }
        return equipment;
    }

    private String getImg(String allImg) {
        int indexOf = allImg.lastIndexOf("/") + 1;
        allImg = allImg.substring(indexOf);
        int indexOf1 = allImg.lastIndexOf(".");
        allImg = allImg.substring(0, indexOf1);
        allImg = allImg.replace("-right", "").replace("-left", "");
        return allImg;
    }


    @Override
    public Object getSingleTesting(HttpSession session, Long bossId) {
        Student student = getStudent(session);
        PkCopyBase pkCopyBase = pkCopyRedisOpt.getPkCopyBaseById(bossId);
        //获得学生当天挑战次数
        Long studentId = student.getId();
        int count = gauntletMapper.countByStudentIdAndBossId(studentId, bossId, 2);
        //判断是否到达每天挑战次数上限
        if (pkCopyBase.getChallengeCycle() <= count) {
            return ServerResponse.createByError(401, "挑战次数达到上限");
        }
        //查询当前boss剩余学量
        PkCopyState pkCopyState = pkCopyStateMapper.selectByStudentIdAndBossId(studentId, bossId);
        if (pkCopyState != null) {
            pkCopyBase.setDurability(pkCopyState.getDurability());
        }

        return ServerResponse.createBySuccess(PkInfoVO.builder()
                .challenged(getEquipmentMap(studentId))
                .boss(getBossEquipment(pkCopyBase))
                .subject(getSubject(studentId))
                .build());
    }

    /**
     * 获取被挑战的副本信息
     *
     * @param pkCopyBase
     * @return
     */
    private PkInfoVO.BossPoll getBossEquipment(PkCopyBase pkCopyBase) {
        return PkInfoVO.BossPoll.builder()
                .imgUrl(getImg(pkCopyBase.getImgUrl()))
                .grade(pkCopyBase.getLevelName())
                .name(pkCopyBase.getName())
                .battle(IndexVO.BaseValue.builder()
                        .attack(pkCopyBase.getCommonAttack())
                        .durability(pkCopyBase.getDurability())
                        .hitRate(pkCopyBase.getHitRate())
                        .move(pkCopyBase.getMobility())
                        .source(pkCopyBase.getSourceForce())
                        .sourceAttack(pkCopyBase.getSourceForceAttack())
                        .build())
                .build();
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
                this.saveStudentGold(student);

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
            PkCopyBase pkCopyBase = pkCopyRedisOpt.getPkCopyBaseById(bossId);
            Integer durability = pkCopyBase.getDurability();
            durability -= bloodVolume;
            if (durability <= 0) {
                gauntlet.setChallengeStatus(1);
                gauntlet.setBeChallengerStatus(2);
                pkCopyState.setDurability(0);
                this.saveStudentGold(student);
            } else {
                gauntlet.setChallengeStatus(2);
                gauntlet.setBeChallengerStatus(1);
                pkCopyState.setDurability(durability);
            }
            pkCopyStateMapper.insert(pkCopyState);
            gauntletMapper.insert(gauntlet);
        }
        return ServerResponse.createBySuccess();
    }

    public static void main(String[] args) {
        System.out.println(new DateTime().dayOfWeek().get());
    }

    @Override
    public ServerResponse<Object> getSchoolCopyInfo(Long bossId) {

        // 判断当前日期是否可以挑战校区副本
        int dayOfWeek = new DateTime().dayOfWeek().get();
        final int saturday = 6;
        final int sunday = 7;
        if (dayOfWeek != saturday && dayOfWeek != sunday) {
            return ServerResponse.createBySuccess(400, "校区副本只有周六周日才开放挑战！");
        }

        Student student = super.getStudent();
        Long studentId = student.getId();

        // 判断学生当前是否已经挑战过当前校区副本
        int count = gauntletMapper.countByStudentIdAndBossId(studentId, bossId, 3);
        if (count > 0) {
            return ServerResponse.createBySuccess(401, "您今天已经挑战过该副本！");
        }

        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        // 返回副本信息
        PkCopyState pkCopyState = pkCopyStateMapper.selectBySchoolAdminIdAndPkCopyBaseId(schoolAdminId, bossId);
        PkCopyBase pkCopyBase = pkCopyRedisOpt.getPkCopyBaseById(bossId);
        if (pkCopyState != null) {
            pkCopyBase.setDurability(pkCopyState.getDurability());
        }

        return ServerResponse.createBySuccess(PkInfoVO.builder()
                .challenged(getEquipmentMap(studentId))
                .boss(getBossEquipment(pkCopyBase))
                .subject(getSubject(studentId))
                .build());
    }

    /**
     * 获得所有装备数据
     *
     * @return
     */
    @Override
    public Object getTrainingGround() {
        Map<String, Object> returnMap = new HashMap<>(16);
        Map<Integer, List<EquipmentEquipmentExperienceVo>> pkMap = new HashMap<>(16);
        //获得所有装备名称
        List<Equipment> equipment = equipmentMapper.selectAll();
        List<EquipmentExpansion> equipmentExpansions = equipmentExpansionMapper.selectAll();
        Map<Integer, List<Equipment>> collect = equipment.stream().collect(Collectors.groupingBy(Equipment::getType));
        Map<Long, List<EquipmentExpansion>> collect1 = equipmentExpansions.stream().collect(Collectors.groupingBy(EquipmentExpansion::getEquipmentId));
        Set<Integer> integers = collect.keySet();
        List<PkCopyBase> pkCopyBases = pkCopyBaseMapper.selectList(null);
        integers.forEach(number -> {
            List<Equipment> equipments = collect.get(number);
            equipments.forEach(ment -> {
                List<EquipmentEquipmentExperienceVo> equipmentEquipmentExperienceVos = pkMap.get(number);
                EquipmentEquipmentExperienceVo vo = new EquipmentEquipmentExperienceVo();
                if (equipmentEquipmentExperienceVos == null) {
                    equipmentEquipmentExperienceVos = new ArrayList<>();
                }
                vo.setId(ment.getId());
                vo.setEmpiricalValue(ment.getEmpiricalValue());
                vo.setLevel(ment.getLevel());
                vo.setGrade(ment.getGrade());
                vo.setName(ment.getName());
                vo.setType(ment.getType());
                List<EquipmentExpansion> equipmentExpansions1 = collect1.get(ment.getId());
                vo.setExperienceMap(new HashMap<>(16));
                equipmentExpansions1.forEach(expansion -> {
                    Map<Integer, EquipmentExpansion> experienceMap = vo.getExperienceMap();
                    expansion.setImgUrl(getImg(expansion.getImgUrl()));
                    expansion.setName(ment.getName());
                    expansion.setGrade(ment.getGrade());
                    experienceMap.put(expansion.getIntensificationDegree(), expansion);
                });
                equipmentEquipmentExperienceVos.add(vo);
                pkMap.put(number, equipmentEquipmentExperienceVos);
            });
        });
        returnMap.put("pkEqu", pkMap);
        returnMap.put("bossEqu", pkCopyBases);
        return returnMap;
    }

    private void saveStudentGold(Student student) {
        Double goldAddition = StudentGoldAdditionUtil.getGoldAddition(student, AWARD_GOLD);
        studentMapper.updateBySystemGold(BigDecimalUtil.add(goldAddition, student.getSystemGold()), student.getId());
        GoldLogUtil.saveStudyGoldLog(student.getId(), "飞船个人挑战", Integer.parseInt(goldAddition.toString()));
    }


    @Override
    public Object saveTest(HttpSession session, Long beChallenged, Integer type) {
        Student student = getStudent(session);
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(student.getId());
        //查询一小时内的pk次数
        int pkCount = getPkCount(student);
        if (pkCount == 1) {
            return ServerResponse.createByError(401, "今日挑战次数以超出");
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
            expansion.setStudyPower(Math.max(expansion.getStudyPower() - 5, 0));
        }
        //获取飞船图片
        gauntlet.setType(1);
        gauntlet.setChallengerImgUrl(studentEquipmentMapper.selectImgUrlByStudentId(student.getId(), 1));
        gauntlet.setBeChallengerImgUrl(studentEquipmentMapper.selectImgUrlByStudentId(beChallenged, 1));
        gauntlet.setChallengerStudentId(student.getId());
        gauntlet.setBeChallengerStudentId(beChallenged);
        gauntlet.setCreateTime(new Date());
        gauntletMapper.insert(gauntlet);
        shipAddEquipmentService.updateLeaderBoards(student);
        long currentRanking = this.getCurrentRanking(student);
        if (currentRanking < expansion.getRanking() && currentRanking != 0) {
            expansion.setRanking(currentRanking);
        }
        studentExpansionMapper.updateById(expansion);

        return ServerResponse.createBySuccess();
    }

    @Override
    public Object getPkRecord(HttpSession session, int type) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>(16);
        //获取最高排名
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(student.getId());
        //获取当前排名
        returnMap.put("currentRanking", getCurrentRanking(student));
        //获取pk信息
        int pkGames = gauntletMapper.getPkGames(student.getId(), 1);
        int winPkGames = gauntletMapper.getPkGames(student.getId(), 2);
        double win = 0;
        if (pkGames != 0) {
            win = Math.floor(1.0 * winPkGames / pkGames * 100);
        }

        //pk场数
        returnMap.put("pkGames", pkGames);
        //pk胜率
        returnMap.put("winPk", win);
        returnMap.put("highestRanking", expansion.getRanking());
        List<Map<String, Object>> pkRecord = gauntletMapper.getPkRecord(student.getId(), type);
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
        PkCopyBase pkCopyBase = pkCopyRedisOpt.getPkCopyBaseById(copyId);

        // 剩余耐久度
        int durability;
        Date nowTime = new Date();
        if (pkCopyState == null) {
            durability = pkCopyBase.getDurability() - reduceDurability;
            pkCopyStateMapper.insert(PkCopyState.builder()
                    .type(2)
                    .pkCopyBaseId(copyId)
                    .schoolAdminId(schoolAdminId)
                    .durability(Math.max(0, durability))
                    .createTime(nowTime)
                    .updateTime(nowTime)
                    .build());

            this.savePkSchoolCopyAward(pkCopyBase, schoolAdminId, durability);
            this.saveGauntlet(copyId, student, pkCopyBase, durability, nowTime);
        } else if (pkCopyState.getDurability() > 0) {
            durability = pkCopyState.getDurability() - reduceDurability;

            pkCopyState.setDurability(Math.max(0, durability));
            pkCopyState.setUpdateTime(nowTime);
            pkCopyStateMapper.updateById(pkCopyState);

            this.savePkSchoolCopyAward(pkCopyBase, schoolAdminId, durability);
            this.saveGauntlet(copyId, student, pkCopyBase, durability, nowTime);
        }

        Double goldAddition = StudentGoldAdditionUtil.getGoldAddition(student, pkCopyBase.getGold());
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldAddition));
        studentMapper.updateById(student);

        GoldLogUtil.saveStudyGoldLog(student.getId(), "参与校区副本挑战", (int) Math.floor(goldAddition));

        return ServerResponse.createBySuccess();
    }

    public void saveGauntlet(Long copyId, Student student, PkCopyBase pkCopyBase, int durability, Date nowTime) {
        String shipImgUrl = studentEquipmentMapper.selectImgUrlByStudentId(student.getId(), 1);
        gauntletMapper.insert(Gauntlet.builder()
                .challengerStudentId(student.getId())
                .beChallengerStudentId(copyId)
                .challengeStatus(durability > 0 ? 2 : 1)
                .beChallengerStatus(durability <= 0 ? 2 : 1)
                .challengerImgUrl(shipImgUrl)
                .beChallengerImgUrl(pkCopyBase.getImgUrl())
                .type(3)
                .createTime(nowTime)
                .build());
    }

    public void savePkSchoolCopyAward(PkCopyBase pkCopyBase, Integer schoolAdminId, Integer durability) {
        Long copyId = pkCopyBase.getId();
        boolean flag = pkCopyRedisOpt.judgeSchoolCopyAward(schoolAdminId, copyId);
        if (durability <= 0 && flag) {
            // todo:校区金币工厂增加金币

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
        Date parse = DateUtil.parse(beforeTime, DateUtil.YYYYMMDDHHMMSS);
        int count = gauntletMapper.getCountByStudentIdAndTime(student.getId(), date, parse);
        int maxCount = 5;
        return count >= maxCount ? 1 : 2;
    }

    /**
     * 查询题目信息
     *
     * @param studentId
     * @return
     */
    private List<SubjectsVO> getSubject(Long studentId) {
        //1，获取单元
        List<Long> unitIds = learnNewMapper.getUnitIdByStudentIdAndType(studentId, 1);
        //2,获取单元题目
        if (unitIds != null && unitIds.size() > 0) {
            List<SubjectsVO> subjectsVos = vocabularyMapper.selectSubjectsVOByUnitIds(unitIds);
            //题目够15到截取，不够的话 循环添加
            int subjectNum = 15;
            if (subjectsVos.size() > subjectNum) {
                Collections.shuffle(subjectsVos);
                subjectsVos = subjectsVos.subList(0, subjectNum);
            } else {
                List<SubjectsVO> subjectsVos1 = new ArrayList<>(subjectsVos);
                while (subjectsVos1.size() < subjectNum) {
                    subjectsVos.forEach(subject -> {
                        if (subjectsVos1.size() < subjectNum) {
                            subjectsVos1.add(subject);
                        }
                    });
                }
                subjectsVos = subjectsVos1;
            }
            List<SubjectsVO> returnList = new ArrayList<>();
            subjectsVos.forEach(vo -> {
                vo.setReadUrl(GetOssFile.getPublicObjectUrl(vo.getReadUrl()));
                returnList.add(vo);
            });
            Collections.shuffle(returnList);
            return returnList;
        }
        return Collections.emptyList();
    }


}

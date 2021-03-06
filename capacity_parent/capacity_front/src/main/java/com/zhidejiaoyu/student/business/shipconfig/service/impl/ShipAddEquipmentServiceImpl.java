package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.constant.EquipmentTypeConstant;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.util.CalculateUtil;
import com.zhidejiaoyu.student.business.shipconfig.vo.EquipmentExperienceVo;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ShipAddEquipmentServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ShipAddEquipmentService {

    private static RedisOpt redisOptStatic;
    private static EquipmentMapper equipmentMapperStatic;
    private static StudentEquipmentMapper studentEquipmentMapperStatic;
    private static EquipmentExpansionMapper equipmentExpansionMapperStatic;

    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private EquipmentMapper equipmentMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Resource
    private SourcePowerRankOpt sourcePowerRankOpt;
    @Resource
    private RedisOpt redisOpt;
    @Resource
    private DurationMapper durationMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private LearnHistoryMapper learnHistoryMapper;
    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private EquipmentExpansionMapper equipmentExpansionMapper;
    @Resource
    private ShipIndexService shipIndexService;
    @Resource
    private WeekHistoryPlanMapper weekHistoryPlanMapper;
    @Resource
    private TotalHistoryPlanMapper totalHistoryPlanMapper;

    @PostConstruct
    public void init() {
        redisOptStatic = this.redisOpt;
        equipmentMapperStatic = this.equipmentMapper;
        studentEquipmentMapperStatic = this.studentEquipmentMapper;
        equipmentExpansionMapperStatic = equipmentExpansionMapper;
    }

    @Override
    public Object queryAddStudentEquipment(HttpSession session) {
        Student student = getStudent(session);
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Long> addIdList = new ArrayList<>();
        getStudentEqu(student, returnList, addIdList,1);
        if (addIdList.size() > 0) {
            addEquipment(addIdList, student.getId(), studentEquipmentMapper);
        }
        if (returnList.size() > 0) {
            List<String> returnStr = getReturnStr(returnList);
            return ServerResponse.createBySuccess(returnStr);
        } else {
            return ServerResponse.createBySuccess();
        }

    }

    @Override
    public void getStudentEqu(Student student, List<Map<String, Object>> returnList, List<Long> addIdList,Integer type) {
        //获取等级开启奖品
        //判断是否通过摸底测试
        //获取所有装备
        List<Equipment> equipments = equipmentMapper.selectAll();
        //将装备分组
        Map<Integer, List<Equipment>> collect = equipments.stream().collect(Collectors.groupingBy(Equipment::getType));
        if(type.equals(1)){
            boolean flag = redisOpt.getTestBeforeStudy(student.getId());
            //获取所有学生装备
            List<Long> studentEquipmentIds = studentEquipmentMapper.selectEquipmentIdsByStudentId(student.getId());
            //获取全部装备lv1的图片
            Map<Long, Map<String, Object>> map = equipmentExpansionMapper.selectLvOneAllUrl();
            //获取经验值
            EquipmentExperienceVo empiricalValue = getEmpiricalValue(student.getId(), 0);
            //获取飞船需要添加的物品
            addEquipmentByType(collect.get(1), studentEquipmentIds, empiricalValue.getShipExperience(), returnList, addIdList, map, flag);
            //添加武器需要的物品
            addEquipmentByType(collect.get(2), studentEquipmentIds, empiricalValue.getWeaponExperience(), returnList, addIdList, map, null);
            //添加导弹需要的物品
            addEquipmentByType(collect.get(3), studentEquipmentIds, empiricalValue.getMissileExperience(), returnList, addIdList, map, null);
            //添加装备需要的物品
            addEquipmentByType(collect.get(4), studentEquipmentIds, empiricalValue.getArmorExperience(), returnList, addIdList, map, null);
            addEquipmentPeople(collect.get(5), student.getId());
        }else{
            addEquipmentPeople(collect.get(5), student.getId());
        }
    }

    private List<String> getReturnStr(List<Map<String, Object>> returnList) {
        Map<Integer, List<Map<String, Object>>> groupMap =
                returnList.stream().collect(Collectors.groupingBy(map -> Integer.parseInt(map.get("type").toString())));
        List<String> returnStr = new ArrayList<>();
        List<Map<String, Object>> shipMap = groupMap.get(1);
        if (shipMap != null && shipMap.size() > 0) {
            String shipStr = "新增飞船x" + shipMap.size();
            returnStr.add(shipStr);
        }
        List<Map<String, Object>> armsMap = groupMap.get(2);
        if (armsMap != null && armsMap.size() > 0) {
            String armsStr = "新增武器x" + armsMap.size();
            returnStr.add(armsStr);
        }
        List<Map<String, Object>> missileMap = groupMap.get(3);
        if (missileMap != null && missileMap.size() > 0) {
            String missileStr = "新增导弹x" + missileMap.size();
            returnStr.add(missileStr);
        }
        List<Map<String, Object>> armorMap = groupMap.get(4);
        if (armorMap != null && armorMap.size() > 0) {
            String armorStr = "新增装甲x" + armorMap.size();
            returnStr.add(armorStr);
        }
        return returnStr;
    }

    private void addEquipmentPeople(List<Equipment> equipments, Long studentId) {
        equipments.forEach(equipment -> {
            StudentEquipment studentEquipment = studentEquipmentMapper.selectByStudentIdAndEquipmentId(studentId, equipment.getId());
            if (studentEquipment == null) {
                studentEquipment = new StudentEquipment();
                studentEquipment.setIntensificationDegree(1);
                studentEquipment.setStudentId(studentId);
                studentEquipment.setEquipmentId(equipment.getId());
                studentEquipment.setType(2);
                studentEquipment.setCreateTime(new Date());
                studentEquipmentMapper.insert(studentEquipment);
            }
        });


    }

    /**
     * 根据装备id强化装备
     *
     * @param session
     * @param equipmentId
     * @return
     */
    @Override
    public Object strengthenStudentEquipment(HttpSession session, Long equipmentId) {
        Student student = getStudent(session);
        //获取学生有没有添加过该物品
        StudentEquipment studentEquipment = studentEquipmentMapper.selectByStudentIdAndEquipmentId(student.getId(), equipmentId);
        //判断学生是否含有该装备
        if (studentEquipment == null) {
            return ServerResponse.createByError(400, "未获得该装备");
        }
        //获取装备等级
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        //添加装备，扣除学生金币
        Integer flag = addEquipmentGold(student, equipment.getLevel(), studentEquipment.getIntensificationDegree(), equipment);
        if (flag.equals(2)) {
            return ServerResponse.createByError(400, "金币不足");
        }
        if (flag.equals(3)) {
            return ServerResponse.createByError(400, "装备以强化到最高级");
        }
        studentEquipment.setIntensificationDegree(studentEquipment.getIntensificationDegree() + 1);
        studentEquipmentMapper.updateById(studentEquipment);
        updateLeaderBoards(student);
        Map<String, Object> returnMap = new HashMap<>();
        getReturnMap(equipment, returnMap, true,
                studentEquipment.getIntensificationDegree() < 3, studentEquipment.getIntensificationDegree(), studentEquipment.getType());
        //获取装备图片
        returnMap.put("imgUrl", GetOssFile.getPublicObjectUrl(equipmentExpansionMapper.selectUrlByEquipmentIdAndType(equipmentId,
                studentEquipment.getIntensificationDegree() > 3 ? 3 : studentEquipment.getIntensificationDegree())));
        returnMap.put("gold", student.getSystemGold());
        returnMap.put("strengthenGold", useStrengthenGold(equipment.getLevel(), studentEquipment.getIntensificationDegree(), equipment));
        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public Object getEquipmentInterface(HttpSession session, Integer type) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        //获取当前类别所有装备
        List<Equipment> equipment = equipmentMapper.selectByType(type);
        //获取当前类别所有学生装备
        Map<Long, StudentEquipment> studentEquiments = studentEquipmentMapper.selectByStudentIdAndType(student.getId(), type);
        //获取全部装备的图片
        List<Map<String, Object>> urlList = equipmentExpansionMapper.selectAllUrlByType(type);
        List<Map<String, Object>> maps = equipmentMapper.selectByStudentId(student.getId());
        Map<Long, List<Map<String, Object>>> equMap =
                maps.stream().collect(Collectors.groupingBy(map -> Long.parseLong(map.get("id").toString())));
        //获取经验值
        EquipmentExperienceVo empiricalValue = getEmpiricalValue(student.getId(), type);
        //将装备图片分组
        Map<Long, List<Map<String, Object>>> equipmentMap = urlList.stream().collect(Collectors.groupingBy(ment -> Long.parseLong(ment.get("equipmentId").toString())));
        List<Map<String, Object>> equSort = getEquSort(studentEquiments, equipment, empiricalValue, returnMap, type, equMap, student);
        getImgUrl(equSort, equipmentMap, returnMap);
        return returnMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object wearEquipment(HttpSession session, Long equipmentId, Integer type, String petName) {
        Student student = getStudent(session);
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        updateUseEqu(student, equipment);
        if (type != null && type == 5) {
            student.setPartUrl(getPetImgUrl(petName));
            student.setPetName(equipment.getName());
            studentMapper.updateById(student);
        }
        return ServerResponse.createBySuccess();
    }

    private String getPetImgUrl(String petName) {
        if (petName.contains("大明白")) {
            return FileConstant.PetNameConstant.DAMINGBAI;
        }
        if (petName.contains("威士顿")) {
            return FileConstant.PetNameConstant.WEISIDUN;
        }
        if (petName.contains("无名")) {
            return FileConstant.PetNameConstant.WUMING;
        }
        if (petName.contains("李糖心")) {
            return FileConstant.PetNameConstant.LITANGXIN;
        }
        return null;
    }

    @Override
    public void updateUseEqu(Student student, Equipment equipment) {
        //获取全部同类型装备id
        List<Equipment> equipments = equipmentMapper.selectByType(equipment.getType());
        List<Long> equipmentIds = new ArrayList<>();
        equipments.forEach(ment -> equipmentIds.add(ment.getId()));
        //修改学生装备状态
        studentEquipmentMapper.updateTypeByEquipmentId(equipmentIds, student.getId());
        StudentEquipment studentEquipment = studentEquipmentMapper.selectByStudentIdAndEquipmentId(student.getId(), equipment.getId());
        studentEquipment.setType(1);
        studentEquipmentMapper.updateById(studentEquipment);
        updateLeaderBoards(student);
    }

    /**
     * 获取装备图片
     *
     * @param equSort      需要显示的装备信息
     * @param equipmentMap 图片信息
     * @param returnMap    返回集合
     */
    private void getImgUrl(List<Map<String, Object>> equSort, Map<Long, List<Map<String, Object>>> equipmentMap, Map<String, Object> returnMap) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        equSort.forEach(equ -> {
            List<Map<String, Object>> equipmentUrls = equipmentMap.get(Long.parseLong(equ.get("equipmentId").toString()));
            List<Map<String, Object>> collect = equipmentUrls.stream()
                    .filter(url -> Objects.equals(Integer.parseInt(url.get("degree").toString()), Integer.parseInt(equ.get("enhancementGrade").toString())))
                    .collect(Collectors.toList());
            if (collect.size() > 0) {
                equ.put("imgUrl", GetOssFile.getPublicObjectUrl(collect.get(0).get("imgUrl").toString()));
            } else {
                equ.put("imgUrl", null);
            }
            returnList.add(equ);
        });
        returnMap.put("list", returnList);
    }

    /**
     * 获取装备
     *
     * @param studentEquiments 学生装备
     * @param equipments       全部装备
     * @param empiricalValue   经验值
     * @param returnMap        返回值
     * @param type             类型
     */
    private List<Map<String, Object>> getEquSort(Map<Long, StudentEquipment> studentEquiments, List<Equipment> equipments,
                                                 EquipmentExperienceVo empiricalValue, Map<String, Object> returnMap,
                                                 Integer type, Map<Long, List<Map<String, Object>>> informationMap, Student student) {
        //获取当前类型经验值
        int empValue = 0;
        //当前等级
        int currentLevel = 0;
        //下一等级
        int nextLevel = 0;
        //下一等级经验值
        long nextLevelValue = 0;
        boolean currentLevelFalg = true;
        if (type.equals(1)) {
            empValue = empiricalValue.getShipExperience();
        }
        if (type.equals(2)) {
            empValue = empiricalValue.getWeaponExperience();
        }
        if (type.equals(3)) {
            empValue = empiricalValue.getMissileExperience();
        }
        if (type.equals(4)) {
            empValue = empiricalValue.getArmorExperience();
        }
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (Equipment equipment : equipments) {
            Map<String, Object> equMap = new HashMap<>();
            if (type != 5) {
                if (currentLevelFalg) {
                    nextLevel = equipment.getLevel();
                    nextLevelValue = equipment.getEmpiricalValue();
                    currentLevelFalg = false;
                }
                if (equipment.getEmpiricalValue() <= empValue) {
                    currentLevel = equipment.getLevel();
                    currentLevelFalg = true;
                }
            } else {
                currentLevelFalg = true;
            }

            getReturnMap(equipment, equMap, false, true, 1, 2);
            StudentEquipment studentEquipment = studentEquiments.get(equipment.getId());
            List<Map<String, Object>> maps = informationMap.get(equipment.getId());
            Map<String, Object> equInforMap;
            if (maps != null && maps.size() > 0) {
                equInforMap = maps.get(0);
            } else {
                equInforMap = equipmentExpansionMapper.selectByEquipmentIdAndLevel(equipment.getId(), 1);
            }
            equMap.put("information", shipIndexService.getShipConfigInfoDTO(equInforMap));
            if (studentEquipment != null) {
                //是否开启
                equMap.put("open", true);
                if (studentEquipment.getIntensificationDegree() >= 3) {
                    //是否可强化
                    equMap.put("strengthen", false);
                }
                equMap.put("strengthenGold", useStrengthenGold(equipment.getLevel(), studentEquipment.getIntensificationDegree(), equipment));
                //强化等级
                equMap.put("enhancementGrade", studentEquipment.getIntensificationDegree());
                equMap.put("wear", studentEquipment.getType().equals(1));
            } else {
                long number = (equipment.getEmpiricalValue() - empValue);
                if (!type.equals(5)) {
                    getLevelValue(type, equMap, number, "levelValue", null);
                }

                equMap.put("strengthenGold", useStrengthenGold(equipment.getLevel(), 1, equipment));
            }
            returnList.add(equMap);
        }
        returnMap.put("currentLevel", currentLevel);
        returnMap.put("nextLevel", nextLevel);
        double number = 1.0 * empValue / nextLevelValue;
        long levelValue = nextLevelValue - empValue;
        if (!type.equals(5)) {
            if (levelValue < 0) {
                returnMap.put("progressBarValue", "已到最大等级");
            } else {
                getLevelValue(type, returnMap, levelValue, "progressBarValue", nextLevel);
            }
        }
        returnMap.put("percentage", number > 1 ? 1 : number);
        returnMap.put("gold", student.getSystemGold());
        return returnList;
    }

    private void getLevelValue(Integer type, Map<String, Object> equMap, long number, String levelValue, Integer nextValue) {
        if (type.equals(1)) {
            long size = number / 3600;
            if (size <= 0) {
                number /= 60;
                if (nextValue == null) {
                    equMap.put(levelValue, "还差" + number + "分钟在线时长解锁");
                } else {
                    equMap.put(levelValue, "还差" + number + "分钟在线时长到达lv" + nextValue);
                }
            } else {
                number /= 3600;
                if (nextValue == null) {
                    equMap.put(levelValue, "还差" + number + "小时在线时长解锁");
                } else {
                    equMap.put(levelValue, "还差" + number + "小时在线时长到达lv" + nextValue);
                }
            }
        }
        if (type.equals(2)) {
            if (nextValue == null) {
                equMap.put(levelValue, "还差学习" + number + "单词解锁");
            } else {
                equMap.put(levelValue, "还差学习" + number + "单词到达lv" + nextValue);
            }
        }
        if (type.equals(3)) {
            if (nextValue == null) {
                equMap.put(levelValue, "还差积累" + number + "成绩解锁");
            } else {
                equMap.put(levelValue, "还差积累" + number + "成绩到达lv" + nextValue);
            }
        }
        if (type.equals(4)) {
            long size = number / 3600;
            if (size <= 0) {
                number /= 60;
                if (nextValue == null) {
                    equMap.put(levelValue, "还差" + number + "分钟有效时长解锁");
                } else {
                    equMap.put(levelValue, "还差" + number + "分钟有效时长到达lv" + nextValue);
                }
            } else {
                number /= 3600;
                if (nextValue == null) {
                    equMap.put(levelValue, "还差" + number + "小时有效时长解锁");
                } else {
                    equMap.put(levelValue, "还差" + number + "小时有效时长到达lv" + nextValue);
                }
            }
        }
    }

    private void getReturnMap(Equipment ment, Map<String, Object> equMap, boolean openFlag, boolean strengthen, Integer grade, Integer wear) {
        equMap.put("equipmentId", ment.getId());
        equMap.put("name", ment.getName());
        equMap.put("level", ment.getLevel());
        //是否可强化
        equMap.put("strengthen", strengthen);
        //是否开启
        equMap.put("open", openFlag);
        //强化等级
        equMap.put("enhancementGrade", grade);
        equMap.put("wear", wear.equals(1));
        Map<String, Object> map = equipmentExpansionMapper.selectByEquipmentIdAndLevel(ment.getId(), grade);
        equMap.put("information", shipIndexService.getShipConfigInfoDTO(map));
    }

    /**
     * 获取物品等级解锁加强所需金币
     *
     * @param student    学生信息
     * @param lv         等级
     * @param strengthen 强化度
     * @return
     */
    private Integer addEquipmentGold(Student student, Integer lv, Integer strengthen, Equipment equipment) {
        double gold;
        if (strengthen >= 3) {
            return 3;
        }

        //计算所需金币
        gold = useStrengthenGold(lv, strengthen, equipment);

        if (student.getSystemGold() > gold) {
            student.setSystemGold(student.getSystemGold() - gold);
            student.setOfflineGold(student.getOfflineGold() + gold);
            studentMapper.updateById(student);
            return 1;
        } else {
            return 2;
        }
    }

    private Double useStrengthenGold(Integer lv, Integer strengthen, Equipment equipment) {
        double gold = 0.0;
        if (strengthen >= 3) {
            return 0.0;
        }
        if (equipment.getType().equals(5)) {
            if (equipment.getName().contains("大明白") || equipment.getName().contains("威士顿")) {
                if (strengthen == 1) {
                    gold = 300;
                }
                if (strengthen == 2) {
                    gold = 800;
                }
            }
            if (equipment.getName().contains("李糖心")) {
                if (strengthen == 1) {
                    gold = 800;
                }
                if (strengthen == 2) {
                    gold = 1000;
                }
            }
            if (equipment.getName().contains("无名")) {
                if (strengthen == 1) {
                    gold = 750;
                }
                if (strengthen == 2) {
                    gold = 1000;
                }
            }
        } else {
            if (strengthen.equals(1)) {
                gold = 50 * 1.2 * (lv - 1);
                if (gold == 0.0) {
                    gold = 50.0;
                }
            }
            if (strengthen.equals(2)) {
                gold = 100 * 1.2 * (lv - 1);
                if (gold == 0.0) {
                    gold = 100.0;
                }
            }
        }
        return gold;
    }

    @Override
    public void updateLeaderBoards(Student student) {
        // 获取更新后的源分战力
        Date date = new Date();
        String beforeSevenDaysDateStr = DateUtil.getBeforeDayDateStr(date, 7, DateUtil.YYYYMMDDHHMMSS);
        String now = DateUtil.formatDate(new Date(), DateUtil.YYYYMMDDHHMMSS);
        Integer sourceForceAttack = CalculateUtil.getSourcePoint(student.getId(), beforeSevenDaysDateStr, now);
        // 获取pk值
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(student.getId());
        expansion.setSourcePower(sourceForceAttack);
        studentExpansionMapper.updateById(expansion);

        sourcePowerRankOpt.optSourcePowerRank(student, sourceForceAttack, expansion.getStudyPower());
    }

    @Override
    public ServerResponse<Object> getEquipmentNexLevlInfromation(Long equipmentId, HttpSession session) {
        Long studentId = getStudentId(session);
        //获取当前装备学生是否拥有
        StudentEquipment studentEquipment = studentEquipmentMapper.selectByStudentIdAndEquipmentId(studentId, equipmentId);
        Map<String, Object> equInforMap;
        if (studentEquipment != null) {
            if (studentEquipment.getIntensificationDegree() >= 3) {
                equInforMap = equipmentExpansionMapper.selectByEquipmentIdAndLevel(equipmentId, 3);
            } else {
                equInforMap = equipmentExpansionMapper.selectByEquipmentIdAndLevel(equipmentId, studentEquipment.getIntensificationDegree() + 1);
            }
        } else {
            equInforMap = equipmentExpansionMapper.selectByEquipmentIdAndLevel(equipmentId, 1);
        }
        return ServerResponse.createBySuccess(shipIndexService.getShipConfigInfoDTO(equInforMap));
    }

    @Override
    public long getEmpValue(Long studentId, Integer type, String date) {
        if (type.equals(1)) {
            Long integers = durationMapper.selectByStudentIdAndDate(studentId, date);
            if (integers == null) {
                return 0;
            }
            return integers;
        }
        if (type.equals(2)) {
            // 获取所有已学单词数据
            List<Long> learnList = learnExtendMapper.selectWordListByStudentIdAndDate(studentId, date);
            // 获取历史记录单词学习数
            List<Long> historyList = learnHistoryMapper.selectWordListByStudentIdAndDate(studentId, date);
            Set<Long> set = new HashSet<>(learnList);
            set.addAll(historyList);
            return set.size();
        }
        if (type.equals(3)) {
            Integer point = testRecordMapper.selectFractionByStudentIdAndDate(studentId, date);
            if (point == null) {
                return 0L;
            } else {
                return point;
            }
        }
        if (type.equals(4)) {
            Long aLong = durationMapper.selectValidTimeByStudentIdAndDate(studentId, date);
            if (aLong == null) {
                return 0L;
            }
            return aLong;
        }
        return 0;
    }

    /**
     * 分数足够添加的飞船物品
     *
     * @param studentId
     * @return 奖励的飞船名称和飞船图片
     */
    public static List<Map<String, String>> getTestAddEquipment(Long studentId) {
        List<Equipment> equipmentList = equipmentMapperStatic.selectIdByTypeAndLevel(1, 1);
        StudentEquipment studentEquipment = studentEquipmentMapperStatic.selectByStudentIdAndEquipmentId(studentId, equipmentList.get(0).getId());
        if (studentEquipment == null) {
            redisOptStatic.initShip(studentId);
            return equipmentList.stream().map(equipment -> {
                EquipmentExpansion equipmentExpansion = equipmentExpansionMapperStatic.selectOneByEquipmentId(equipment.getId());
                Map<String, String> map = new HashMap<>(16);
                map.put("name", equipment.getName());
                map.put("url", GetOssFile.getPublicObjectUrl(equipmentExpansion.getImgUrl()));
                return map;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static void addEquipment(List<Long> equipmentIds, Long studentId, StudentEquipmentMapper studentEquipmentMapper) {
        for (Long equipmentId : equipmentIds) {
            // 添加装备
            StudentEquipment studentEquipment = new StudentEquipment()
                    .setEquipmentId(equipmentId)
                    .setIntensificationDegree(1)
                    .setStudentId(studentId)
                    .setType(2)
                    .setCreateTime(new Date());
            studentEquipmentMapper.insert(studentEquipment);
        }
    }

    private EquipmentExperienceVo getEmpiricalValue(long studentId, Integer type) {
        TotalHistoryPlan totalHistoryPlan = totalHistoryPlanMapper.selectByStudentId(studentId);
        String date = DateUtil.formatYYYYMMDDHHMMSS(new Date());
        WeekHistoryPlan weekHistoryPlan = weekHistoryPlanMapper.selectByTimeAndStudentId(date, studentId);
        EquipmentExperienceVo vo = new EquipmentExperienceVo();
        if (type.equals(0) || type.equals(1)) {
            //获取每天在线时常
            int timeIndex = getTime(studentId, date, weekHistoryPlan, EquipmentTypeConstant.ONLINE_TIME_MAX, 1);
            Long totalOnlineTime = totalHistoryPlan.getTotalOnlineTime();
            if (totalOnlineTime != null) {
                timeIndex += totalOnlineTime.intValue();
            }
            vo.setShipExperience(timeIndex);
        }
        if (type.equals(0) || type.equals(2)) {

            Integer totalWord = totalHistoryPlan.getTotalWord();
            int maxWord = EquipmentTypeConstant.WORD_MAX;
            Integer word = weekHistoryPlan.getWord();
            Long empValue = getEmpValue(studentId, 2, date);
            int wordSize = 0;
            wordSize = getWordAnPoint(maxWord, empValue, word);
            if (totalWord != null) {
                wordSize += totalWord;
            }
            vo.setWeaponExperience(wordSize);
        }
        if (type.equals(0) || type.equals(3)) {

            Integer totalPoint = totalHistoryPlan.getTotalPoint();
            int MaxPonit = EquipmentTypeConstant.POINT_MAX;
            Long empValue = getEmpValue(studentId, 3, date);
            Integer point = weekHistoryPlan.getPoint();
            Integer returnPoint = 0;
            returnPoint = getWordAnPoint(MaxPonit, empValue, point);
            if (totalPoint != null) {
                returnPoint += totalPoint;
            }
            vo.setMissileExperience(returnPoint);
        }
        if (type.equals(0) || type.equals(4)) {
            //获取每天在线时常

            Long totalOnlineTime = totalHistoryPlan.getTotalOnlineTime();
            int timeIndex = getTime(studentId, date, weekHistoryPlan, EquipmentTypeConstant.VALID_TIME_MAX, 4);

            if (totalOnlineTime != null) {
                timeIndex += totalOnlineTime.intValue();
            }
            vo.setArmorExperience(timeIndex);
        }
        return vo;
    }

    @Override
    public Integer getWordAnPoint(int maxPonit, Long empValue, Integer point) {
        Integer returnPoint;
        if (point != null) {
            returnPoint = point + empValue.intValue();
            if (returnPoint > maxPonit) {
                returnPoint = maxPonit;
            }
        } else {
            if (empValue > maxPonit) {
                returnPoint = maxPonit;
            } else {
                returnPoint = empValue.intValue();
            }
        }
        return returnPoint;
    }

    @Override
    public Integer getTime(long studentId, String date, WeekHistoryPlan weekHistoryPlan, int validTimeMax, int type) {
        int maxTime = validTimeMax;
        Long time = 0L;
        if (type == 1) {
            time = weekHistoryPlan.getOnlineTime();
        } else {
            time = weekHistoryPlan.getValidTime();
        }
        Long empValue = getEmpValue(studentId, type, date);
        int timeIndex = 0;
        if (time != null) {
            empValue += time;
            if (empValue > maxTime) {
                timeIndex = maxTime;
            } else {
                timeIndex = empValue.intValue();
            }
        } else {
            if (empValue > maxTime) {
                timeIndex = maxTime;
            } else {
                timeIndex = empValue.intValue();
            }
        }
        return timeIndex;
    }

    /**
     * 添加飞船装备
     *
     * @param equipments         飞船装备
     * @param studentEquimentIds 学生已有装备
     * @param shipExperience     经验值
     * @param returnList         返回集合
     * @param addIdList          需要添加的装备
     */
    private void addEquipmentByType(List<Equipment> equipments, List<Long> studentEquimentIds, Integer shipExperience,
                                    List<Map<String, Object>> returnList, List<Long> addIdList,
                                    Map<Long, Map<String, Object>> imgMap, Boolean falg) {
        Map<Long, Equipment> addEquipments = getEquipments(equipments, studentEquimentIds, shipExperience, falg);
        if (addEquipments.size() > 0) {
            Set<Long> longs = addEquipments.keySet();
            addIdList.addAll(longs);
            addIdList.forEach(addId -> {
                Equipment equipment = addEquipments.get(addId);
                if (equipment != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", equipment.getName());
                    map.put("type", equipment.getType());
                    //imgMap.get(addId).get("imgUrl").toString()
                    Map<String, Object> map1 = imgMap.get(addId);
                    map.put("url", GetOssFile.getPublicObjectUrl(map1.get("imgUrl").toString()));
                    returnList.add(map);
                }
            });
        }
    }

    /**
     * 获取可添加的装备
     *
     * @param equipments
     * @param studentEquimentIds
     * @param shipExperience
     * @return
     */
    private Map<Long, Equipment> getEquipments(List<Equipment> equipments, List<Long> studentEquimentIds,
                                               Integer shipExperience, Boolean flag) {
        Map<Long, Equipment> addEquipment = new HashMap<>();
        if (equipments != null && equipments.size() > 0) {

            if (flag != null && flag) {
                //获取第一个飞船信息
                List<Equipment> equipment = equipmentMapper.selectIdByTypeAndLevel(1, 1);
                equipment.forEach(equ -> {
                    addEquipment.put(equ.getId(), equ);
                });

            }
            //获取可添加物品
            equipments.forEach(ment -> {
                if (ment.getEmpiricalValue() < shipExperience) {
                    addEquipment.put(ment.getId(), ment);
                }
            });
            studentEquimentIds.forEach(studentMentId -> {
                Equipment equipment = addEquipment.get(studentMentId);
                if (equipment != null) {
                    addEquipment.remove(studentMentId);
                }
            });
        }
        return addEquipment;
    }


}

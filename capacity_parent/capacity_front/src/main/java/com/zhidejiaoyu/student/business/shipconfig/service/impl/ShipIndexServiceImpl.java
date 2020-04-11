package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.AwardUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.constant.EquipmentTypeConstant;
import com.zhidejiaoyu.student.business.shipconfig.dto.ShipConfigInfoDTO;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.util.CalculateUtil;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import com.zhidejiaoyu.student.business.shipconfig.vo.RankVO;
import com.zhidejiaoyu.student.common.redis.WorshipRedisOpt;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 飞船配置首页
 *
 * @author: wuchenxi
 * @date: 2020/2/27 15:29:29
 */
@Service
public class ShipIndexServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ShipIndexService {

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private StudentSkinMapper studentSkinMapper;

    @Resource
    private SyntheticRewardsListMapper syntheticRewardsListMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private SourcePowerRankOpt sourcePowerRankOpt;

    @Resource
    private MedalMapper medalMapper;

    @Resource
    private WorshipMapper worshipMapper;

    @Resource
    private WorshipRedisOpt worshipRedisOpt;

    /**
     * 强化度对应的中文等级
     */
    private static final Map<Integer, String> DEGREE = new HashMap<>(16);

    static {
        DEGREE.put(1, "普通");
        DEGREE.put(2, "精英");
        DEGREE.put(3, "传奇");
    }

    @Override
    public ServerResponse<Object> index() {
        Student student = super.getStudent();

        String nickname = student.getNickname();
        String headUrl = GetOssFile.getPublicObjectUrl(student.getHeadUrl());
        int gold = (int) Math.floor(student.getSystemGold());

        Long studentId = student.getId();
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(studentId);

        // 学生被膜拜总次数
        int byWorshipCount = worshipMapper.countByWorship(studentId);

        // 皮肤
        IndexVO.Info skinInfo = getSkinInfo(studentId);

        // 勋章图片
        List<IndexVO.Info> medalInfos = this.getMedalImgList(studentExpansion);

        // 学生装备的飞船及装备信息
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);
        if (CollectionUtils.isEmpty(equipments)) {
            // 学生还没有装备数据
            return ServerResponse.createBySuccess(IndexVO.builder()
                    .nickname(nickname)
                    .headUrl(headUrl)
                    .gold(gold)
                    .skinInfo(skinInfo)
                    .medalInfos(medalInfos)
                    .byWorshipedCount(byWorshipCount)
                    .build());
        }

        IndexVO indexVO = this.getIndexVoTmp(equipments);
        IndexVO.BaseValue baseValue = this.getBaseValue(equipments);

        IndexVO.StateOfWeek stateOfWeek = this.getStateOfWeek(studentId, baseValue);

        IndexVO.Radar radar = this.getRadar(baseValue, stateOfWeek);

        SyntheticRewardsList syntheticRewardsList = syntheticRewardsListMapper.selectUseGloveOrFlower(studentId);

        return ServerResponse.createBySuccess(IndexVO.builder()
                .nickname(nickname)
                .headUrl(headUrl)
                .gold(gold)
                .sourcePoser(studentExpansion.getSourcePower())
                .skinInfo(skinInfo)
                .medalInfos(medalInfos)
                .armorInfo(indexVO.getArmorInfo())
                .missileInfo(indexVO.getMissileInfo())
                .shipInfo(indexVO.getShipInfo())
                .weaponsInfo(indexVO.getWeaponsInfo())
                .heroImgInfo(indexVO.getHeroImgInfo())
                .sourceInfo(getSourceInfo(syntheticRewardsList))
                .baseValue(baseValue)
                .stateOfWeek(stateOfWeek)
                .radar(radar)
                .byWorshipedCount(byWorshipCount)
                .build());
    }

    public IndexVO.Info getSourceInfo(SyntheticRewardsList syntheticRewardsList) {
        if (syntheticRewardsList != null) {
            String name = syntheticRewardsList.getName();
            return IndexVO.Info.builder().id(Long.valueOf(syntheticRewardsList.getId()))
                    .url(GetOssFile.getPublicObjectUrl(syntheticRewardsList.getImgUrl()))
                    .explain(name + "，得到的金币加成" + AwardUtil.getBonusByName(name) + "%")
                    .build();
        }
        return null;
    }

    public IndexVO.Info getSkinInfo(Long studentId) {
        StudentSkin studentSkin = studentSkinMapper.selectUseSkinByStudentId(studentId);
        if (studentSkin != null) {
            return IndexVO.Info.builder().id(Long.valueOf(studentSkin.getId()))
                    .url(GetOssFile.getPublicObjectUrl(studentSkin.getImgUrl()))
                    .explain(studentSkin.getSkinName()).build();
        }
        return null;
    }

    /**
     * 获取雷达图数据
     *
     * @param baseValue
     * @param stateOfWeek
     * @return
     */
    private IndexVO.Radar getRadar(IndexVO.BaseValue baseValue, IndexVO.StateOfWeek stateOfWeek) {
        IndexVO.Radar radar = new IndexVO.Radar();
        radar.setAttack(Math.min(1000, stateOfWeek.getAttack() * baseValue.getAttack()));
        radar.setDurability(Math.min(15000, stateOfWeek.getDurability() * baseValue.getDurability()));
        radar.setHitRate(Math.min(1.5, BigDecimalUtil.mul(stateOfWeek.getHitRate(), baseValue.getHitRate(), 2)));
        radar.setMove(Math.min(500, stateOfWeek.getMove() * baseValue.getMove()));
        radar.setSource(Math.min(10000, stateOfWeek.getSource() * baseValue.getSource()));
        radar.setSourceAttack(stateOfWeek.getSourceAttack() * baseValue.getSourceAttack());
        return radar;
    }

    private IndexVO.StateOfWeek getStateOfWeek(Long studentId, IndexVO.BaseValue baseValue) {
        Date date = new Date();
        String beforeSevenDaysDateStr = DateUtil.getBeforeDayDateStr(date, 7, DateUtil.YYYYMMDD);
        String now = DateUtil.formatDate(new Date(), DateUtil.YYYYMMDD);

        IndexVO.StateOfWeek stateOfWeek = new IndexVO.StateOfWeek();

        stateOfWeek.setAttack(CalculateUtil.getAttack(baseValue.getAttack(), studentId, beforeSevenDaysDateStr, now));
        stateOfWeek.setDurability(CalculateUtil.getDurability(baseValue.getDurability(), studentId, beforeSevenDaysDateStr, now));
        stateOfWeek.setHitRate(CalculateUtil.getHitRate(baseValue.getHitRate(), studentId, beforeSevenDaysDateStr, now));
        stateOfWeek.setMove(CalculateUtil.getMove(baseValue.getMove(), studentId, beforeSevenDaysDateStr, now));
        stateOfWeek.setSource(CalculateUtil.getSource(baseValue, studentId, beforeSevenDaysDateStr, now));
        stateOfWeek.setSourceAttack((int) CalculateUtil.getSourceAttack(baseValue, studentId, beforeSevenDaysDateStr, now));

        return stateOfWeek;
    }

    @Override
    public IndexVO.BaseValue getStateOfWeek(Long studentId) {
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);

        if (CollectionUtils.isEmpty(equipments)) {
            return IndexVO.StateOfWeek.builder()
                    .attack(0)
                    .durability(0)
                    .hitRate(0.0)
                    .move(0)
                    .source(0)
                    .sourceAttack(0)
                    .build();
        }

        IndexVO.BaseValue baseValue = this.getBaseValue(equipments);
        return this.getStateOfWeek(studentId, baseValue);
    }

    @Override
    public ServerResponse<Object> saveMedal(String medalId) {

        Long studentId = getStudentId();
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(studentId);
        studentExpansion.setMedalNo(medalId);
        studentExpansionMapper.updateById(studentExpansion);
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取各项最大值（基础值）
     *
     * @param equipments
     * @return
     */
    @Override
    public IndexVO.BaseValue getBaseValue(List<Map<String, Object>> equipments) {
        IndexVO.BaseValue baseValue = new IndexVO.BaseValue();
        equipments.forEach(map -> {
            // 攻击力
            Object commonAttack1 = map.get("commonAttack");
            if (commonAttack1 != null) {
                int commonAttack = Integer.parseInt(commonAttack1.toString());
                if (baseValue.getAttack() == null) {
                    baseValue.setAttack(commonAttack);
                } else {
                    baseValue.setAttack(baseValue.getAttack() + commonAttack);
                }
            } else {
                baseValue.setAttack(0);
            }


            // 耐久度
            Object durability1 = map.get("durability");
            if (durability1 != null) {
                int durability = Integer.parseInt(durability1.toString());
                if (baseValue.getDurability() == null) {
                    baseValue.setDurability(durability);
                } else {
                    baseValue.setDurability(durability + baseValue.getDurability());
                }
            } else {
                baseValue.setDurability(0);
            }

            // 源分
            Object sourceForce1 = map.get("sourceForce");
            if (sourceForce1 != null) {
                int sourceForce = Integer.parseInt(sourceForce1.toString());
                int sourceForceAttack = Integer.parseInt(map.get("sourceForceAttack").toString());
                if (baseValue.getSource() == null) {
                    baseValue.setSource(sourceForce * sourceForceAttack);
                } else {
                    baseValue.setSource(baseValue.getSource() + sourceForce * sourceForceAttack);
                }

                // 源分攻击
                if (baseValue.getSourceAttack() == null) {
                    baseValue.setSourceAttack(sourceForceAttack);
                } else {
                    baseValue.setSourceAttack(baseValue.getSourceAttack() + sourceForceAttack);
                }
            } else {
                baseValue.setSource(0);
                baseValue.setSourceAttack(0);
            }

            // 命中率
            Object hitRate1 = map.get("hitRate");
            if (hitRate1 != null) {
                double hitRate = Double.parseDouble(hitRate1.toString());
                if (baseValue.getHitRate() == null) {
                    baseValue.setHitRate(hitRate);
                } else {
                    baseValue.setHitRate(baseValue.getHitRate() + hitRate);
                }
            } else {
                baseValue.setHitRate(0.0);
            }

            // 机动力
            Object mobility1 = map.get("mobility");
            if (mobility1 != null) {
                int mobility = Integer.parseInt(mobility1.toString());
                if (baseValue.getMove() == null) {
                    baseValue.setMove(mobility);
                } else {
                    baseValue.setMove(baseValue.getMove() + mobility);
                }
            } else {
                baseValue.setMove(0);
            }
        });
        return baseValue;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initRank() {
        List<Student> students = studentMapper.selectList(new QueryWrapper<Student>().isNotNull("head_url"));
        for (Student student : students) {
            if (student.getTeacherId() == null) {
                continue;
            }
            sourcePowerRankOpt.optSourcePowerRank(student, 0, 100);
        }
    }

    @Override
    public ServerResponse<Object> rank(Integer type) {

        Student student = super.getStudent();
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        int pageNum = PageUtil.getPageNum();
        int pageSize = PageUtil.getPageSize();

        long startIndex = (pageNum - 1) * pageSize;
        long endIndex = startIndex + pageSize;

        if (type == 2) {
            // 校区排行（全部学生）
            // 我在校区的排行
            String key = SourcePowerKeysConst.SCHOOL_RANK + schoolAdminId;

            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex, null);
            return this.packageRankVO(key, studentIds, student);
        } else {
            // 全国排行（前50名）
            String key = SourcePowerKeysConst.COUNTRY_RANK;
            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex, 50);

            return this.packageRankVO(key, studentIds, student);
        }
    }

    @Override
    public IndexVO.Radar getRadar(Long studentId) {
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);
        IndexVO.BaseValue baseValue = this.getBaseValue(equipments);

        IndexVO.StateOfWeek stateOfWeek = this.getStateOfWeek(studentId, baseValue);

        return this.getRadar(baseValue, stateOfWeek);
    }

    public ServerResponse<Object> packageRankVO(String key, List<Long> studentIds, Student student) {
        Map<Long, Map<String, Object>> infoMap = studentMapper.selectSourcePowerRankByIds(studentIds);
        long rank = sourcePowerRankOpt.getRank(key, student.getId());

        // 学生今天已膜拜的学生
        Map<String, Long> byWorshipedStudentMap = worshipRedisOpt.getTodayByWorshipedStudentIds(student.getId());

        List<RankVO.RankInfo> collect = studentIds.stream().map(id -> {
            Map<String, Object> map = infoMap.get(id);
            return RankVO.RankInfo.builder()
                    .nickName(map == null || map.get("nickName") == null ? "默认姓名" : String.valueOf(map.get("nickName")))
                    .sourcePower(map == null || map.get("sourcePower") == null ? 0 : (int) map.get("sourcePower"))
                    .headUrl(map == null || map.get("headUrl") == null ? "" : GetOssFile.getPublicObjectUrl(map.get("headUrl").toString()))
                    .studentId(id)
                    .canWorship(!byWorshipedStudentMap.containsKey(String.valueOf(id)))
                    .build();
        }).collect(Collectors.toList());

        // 参与排行总人数
        long studentCount = sourcePowerRankOpt.getMemberSize(key);

        return ServerResponse.createBySuccess(RankVO.builder()
                .myRank(rank)
                .total(studentCount)
                .rankInfoList(collect)
                .pages((int) Math.ceil(studentCount * 1.0 / PageUtil.getPageSize()))
                .build());
    }

    public IndexVO getIndexVoTmp(List<Map<String, Object>> equipments) {
        IndexVO indexVO = new IndexVO();
        if (CollectionUtils.isNotEmpty(equipments)) {
            equipments.forEach(map -> {

                Integer type = (Integer) map.get("type");
                Long id = (Long) map.get("id");
                String imgUrl = GetOssFile.getPublicObjectUrl((String) map.get("imgUrl"));

                ShipConfigInfoDTO shipConfigInfoDTO = this.getShipConfigInfoDTO(map);

                String explain = getExplain(shipConfigInfoDTO);

                IndexVO.Info info = IndexVO.Info.builder()
                        .id(id)
                        .url(imgUrl)
                        .explain(explain)
                        .build();
                switch (type) {
                    case EquipmentTypeConstant.SHIP:
                        indexVO.setShipInfo(info);
                        break;
                    case EquipmentTypeConstant.WEAPONS:
                        indexVO.setWeaponsInfo(info);
                        break;
                    case EquipmentTypeConstant.MISSILE:
                        indexVO.setMissileInfo(info);
                        break;
                    case EquipmentTypeConstant.ARMOR:
                        indexVO.setArmorInfo(info);
                        break;
                    case EquipmentTypeConstant.HERO:
                        indexVO.setHeroImgInfo(info);
                        break;
                    default:
                }
            });
        }
        return indexVO;
    }

    @Override
    public ShipConfigInfoDTO getShipConfigInfoDTO(Map<String, Object> map) {
        int degree = map.get("degree") == null ? 0 : (int) map.get("degree");
        String name = map.get("name") == null ? "" : map.get("name").toString();
        int sourceForce = map.get("sourceForce") == null ? 0 : (int) map.get("sourceForce");
        int sourceForceAttack = map.get("sourceForceAttack") == null ? 0 : (int) map.get("sourceForceAttack");
        int commonAttack = map.get("commonAttack") == null ? 0 : (int) map.get("commonAttack");
        int durability = map.get("durability") == null ? 0 : (int) map.get("durability");
        double hitRate = map.get("hitRate") == null ? 0.0D : (double) map.get("hitRate");
        int mobility = map.get("mobility") == null ? 0 : (int) map.get("mobility");

        return ShipConfigInfoDTO.builder()
                .baseValue(IndexVO.BaseValue.builder()
                        .source(sourceForce)
                        .sourceAttack(sourceForceAttack)
                        .attack(commonAttack)
                        .durability(durability)
                        .hitRate(hitRate)
                        .move(mobility)
                        .build())
                .name(name)
                .degree(degree)
                .build();
    }

    /**
     * 获取配置说明文字
     *
     * @param shipConfigInfoDTO
     * @return
     */
    public static String getExplain(ShipConfigInfoDTO shipConfigInfoDTO) {
        StringBuilder explain = new StringBuilder();
        explain.append(shipConfigInfoDTO.getName()).append("，")
                .append(DEGREE.get(shipConfigInfoDTO.getDegree())).append("，");
        int sourceForce = shipConfigInfoDTO.getBaseValue().getSource();
        if (sourceForce != 0) {
            explain.append("源分次数").append(sourceForce > 0 ? "+" : "").append(sourceForce).append("，");
        }
        int sourceForceAttack = shipConfigInfoDTO.getBaseValue().getSourceAttack();
        if (sourceForceAttack != 0) {
            explain.append("源分攻击").append(sourceForceAttack > 0 ? "+" : "").append(sourceForceAttack).append("，");
        }
        int commonAttack = shipConfigInfoDTO.getBaseValue().getAttack();
        if (commonAttack != 0) {
            explain.append("普通攻击").append(commonAttack > 0 ? "+" : "").append(commonAttack).append("，");
        }
        int durability = shipConfigInfoDTO.getBaseValue().getDurability();
        if (durability != 0) {
            explain.append("耐久度").append(durability > 0 ? "+" : "").append(durability).append("，");
        }
        double hitRate = shipConfigInfoDTO.getBaseValue().getHitRate();
        if (Objects.equals(hitRate, 0.0)) {
            explain.append("命中率").append(hitRate > 0 ? "+" : "").append(hitRate * 100).append("%，");
        }
        int mobility = shipConfigInfoDTO.getBaseValue().getMove();
        if (mobility != 0) {
            explain.append("机动力").append(mobility > 0 ? "+" : "").append(mobility).append("，");
        }
        return StringUtils.removeEnd(explain.toString(), "，");
    }

    public List<IndexVO.Info> getMedalImgList(StudentExpansion studentExpansion) {
        String medalNo = studentExpansion.getMedalNo();
        if (StringUtils.isEmpty(medalNo)) {
            return null;
        }

        List<Long> medalIdList = Arrays.stream(medalNo.split(",")).map(Long::parseLong).collect(Collectors.toList());

        List<Medal> medals = medalMapper.selectBatchIds(medalIdList);

        List<IndexVO.Info> medalInfos = new ArrayList<>(medals.size());

        medalIdList.forEach(id -> {
            for (Medal medal : medals) {
                if (Objects.equals(medal.getId(), id)) {
                    String[] explainArr = medal.getMarkedWords().split("点亮");
                    String explain = StringUtils.removeEnd(explainArr[1].trim(), "。") + "，"
                            + StringUtils.removeEnd(explainArr[0].trim(), "，");

                    medalInfos.add(IndexVO.Info.builder()
                            .id(id)
                            .url(GetOssFile.getPublicObjectUrl(medal.getChildImgUrl()))
                            .explain(explain)
                            .build());
                }
            }
        });

        return medalInfos;
    }
}

package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.PetImageConstant;
import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.pojo.StudentSkin;
import com.zhidejiaoyu.common.pojo.SyntheticRewardsList;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.constant.EquipmentTypeConstant;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.util.CalculateUtil;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import com.zhidejiaoyu.student.business.shipconfig.vo.RankVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Override
    public ServerResponse<Object> index() {
        Student student = super.getStudent();
        Long studentId = student.getId();
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(studentId);

        // 皮肤
        StudentSkin studentSkin = studentSkinMapper.selectUseSkinByStudentId(studentId);

        // 勋章图片
        List<String> medalImgList = this.getMedalImgList(studentExpansion);

        // 学生装备的飞船及装备信息
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);
        if (CollectionUtils.isEmpty(equipments)) {
            // 学生还没有装备数据
            return ServerResponse.createBySuccess(IndexVO.builder()
                    .skinImgUrl(studentSkin == null ? "" : GetOssFile.getPublicObjectUrl(studentSkin.getImgUrl()))
                    .medalUrl(medalImgList)
                    .build());
        }

        IndexVO indexVO = this.getIndexVoTmp(equipments);
        IndexVO.BaseValue baseValue = this.getBaseValue(equipments);

        IndexVO.StateOfWeek stateOfWeek = this.getStateOfWeek(studentId, baseValue);

        IndexVO.Radar radar = this.getRadar(baseValue, stateOfWeek);

        SyntheticRewardsList syntheticRewardsList = syntheticRewardsListMapper.selectUseGloveOrFlower(studentId);

        return ServerResponse.createBySuccess(IndexVO.builder()
                .sourcePoser(studentExpansion.getSourcePower())
                .skinImgUrl(studentSkin == null ? "" : GetOssFile.getPublicObjectUrl(studentSkin.getImgUrl()))
                .medalUrl(medalImgList)
                .armorUrl(indexVO.getArmorUrl())
                .missileUrl(indexVO.getMissileUrl())
                .shipUrl(indexVO.getShipUrl())
                .weaponsUrl(indexVO.getWeaponsUrl())
                .heroImgUrl(indexVO.getHeroImgUrl())
                .sourceUrl(syntheticRewardsList == null ? "" : GetOssFile.getPublicObjectUrl(syntheticRewardsList.getImgUrl()))
                .baseValue(baseValue)
                .stateOfWeek(stateOfWeek)
                .radar(radar)
                .build());
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
        List<Student> students = studentMapper.selectList(null);
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
            long rank = sourcePowerRankOpt.getRank(key, student.getId());

            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex, null);
            return this.packageRankVO(key, rank, studentIds);
        } else {
            // 全国排行（前50名）
            String key = SourcePowerKeysConst.COUNTRY_RANK;
            long rank = sourcePowerRankOpt.getRank(key, student.getId());
            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex, 50);

            return this.packageRankVO(key, rank, studentIds);
        }
    }

    @Override
    public IndexVO.Radar getRadar(Long studentId) {
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);
        IndexVO.BaseValue baseValue = this.getBaseValue(equipments);

        IndexVO.StateOfWeek stateOfWeek = this.getStateOfWeek(studentId, baseValue);

        return this.getRadar(baseValue, stateOfWeek);
    }

    public ServerResponse<Object> packageRankVO(String key, long rank, List<Long> studentIds) {
        Map<Long, Map<String, Object>> infoMap = studentMapper.selectSourcePowerRankByIds(studentIds);

        List<RankVO.RankInfo> collect = studentIds.stream().map(id -> {
            Map<String, Object> map = infoMap.get(id);
            return RankVO.RankInfo.builder()
                    .nickName(map == null || map.get("nickName") == null ? "默认姓名" : String.valueOf(map.get("nickName")))
                    .sourcePower(map == null || map.get("sourcePower") == null ? 0 : (int) map.get("sourcePower"))
                    .headUrl(map == null || map.get("headUrl") == null ? "" : GetOssFile.getPublicObjectUrl(map.get("headUrl").toString()))
                    .studentId(id)
                    .build();
        }).collect(Collectors.toList());

        // 参与排行总人数
        long studentCount = sourcePowerRankOpt.getMemberSize(key);

        return ServerResponse.createBySuccess(RankVO.builder()
                .myRank(rank)
                .total(studentCount)
                .rankInfoList(collect)
                .build());
    }

    public IndexVO getIndexVoTmp(List<Map<String, Object>> equipments) {
        IndexVO indexVO = new IndexVO();
        if (CollectionUtils.isNotEmpty(equipments)) {
            equipments.forEach(map -> {
                Integer type = (Integer) map.get("type");
                String imgUrl = GetOssFile.getPublicObjectUrl((String) map.get("imgUrl"));
                switch (type) {
                    case EquipmentTypeConstant.SHIP:
                        indexVO.setShipUrl(imgUrl);
                        break;
                    case EquipmentTypeConstant.WEAPONS:
                        indexVO.setWeaponsUrl(imgUrl);
                        break;
                    case EquipmentTypeConstant.MISSILE:
                        indexVO.setMissileUrl(imgUrl);
                        break;
                    case EquipmentTypeConstant.ARMOR:
                        indexVO.setArmorUrl(imgUrl);
                        break;
                    case EquipmentTypeConstant.HERO:
                        indexVO.setHeroImgUrl(imgUrl);
                        break;
                    default:
                }
            });
        }
        return indexVO;
    }

    public List<String> getMedalImgList(StudentExpansion studentExpansion) {
        String medalNo = studentExpansion.getMedalNo();
        if (StringUtils.isEmpty(medalNo)) {
            return null;
        }
        return Arrays.asList(medalNo.split(","));
    }
}

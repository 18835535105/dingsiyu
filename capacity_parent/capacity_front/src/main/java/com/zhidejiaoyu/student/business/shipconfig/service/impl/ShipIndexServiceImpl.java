package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.pojo.StudentSkin;
import com.zhidejiaoyu.common.pojo.SyntheticRewardsList;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
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
        radar.setAttack(stateOfWeek.getAttack() * baseValue.getAttack());
        radar.setDurability(stateOfWeek.getDurability() * baseValue.getDurability());
        radar.setHitRate(stateOfWeek.getHitRate() * baseValue.getHitRate());
        radar.setMove(stateOfWeek.getMove() * baseValue.getMove());
        radar.setSource(stateOfWeek.getSource() * baseValue.getSource());
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

        return stateOfWeek;
    }

    /**
     * 获取各项最大值
     *
     * @param equipments
     * @return
     */
    private IndexVO.BaseValue getBaseValue(List<Map<String, Object>> equipments) {
        IndexVO.BaseValue baseValue = new IndexVO.BaseValue();
        equipments.forEach(map -> {
            // 攻击力
            int commonAttack = Integer.parseInt(map.get("commonAttack").toString());
            if (baseValue.getAttack() == null) {
                baseValue.setAttack(commonAttack);
            } else {
                baseValue.setAttack(baseValue.getAttack() + commonAttack);
            }

            // 耐久度
            int durability = Integer.parseInt(map.get("durability").toString());
            if (baseValue.getDurability() == null) {
                baseValue.setDurability(durability);
            } else {
                baseValue.setDurability(durability + baseValue.getDurability());
            }

            // 源力
            int sourceForce = Integer.parseInt(map.get("sourceForce").toString());
            int sourceForceAttack = Integer.parseInt(map.get("sourceForceAttack").toString());
            if (baseValue.getSource() == null) {
                baseValue.setSource(sourceForce * sourceForceAttack);
            } else {
                baseValue.setSource(baseValue.getSource() + sourceForce * sourceForceAttack);
            }

            // 源力攻击
            if (baseValue.getSourceAttack() == null) {
                baseValue.setSourceAttack(sourceForceAttack);
            } else {
                baseValue.setSourceAttack(baseValue.getSourceAttack() + sourceForceAttack);
            }

            // 命中率
            double hitRate = Double.parseDouble(map.get("hitRate").toString());
            if (baseValue.getHitRate() == null) {
                baseValue.setHitRate(hitRate);
            } else {
                baseValue.setHitRate(baseValue.getHitRate() + hitRate);
            }

            // 机动力
            int mobility = Integer.parseInt(map.get("mobility").toString());
            if (baseValue.getMove() == null) {
                baseValue.setMove(mobility);
            } else {
                baseValue.setMove(baseValue.getMove() + mobility);
            }

        });
        return baseValue;
    }

    @Override
    public ServerResponse<Object> rank(Integer type) {

        Student student = super.getStudent();
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        if (type == 2) {
            // 校区排行（全部学生）
            // 我在校区的排行
            String key = SourcePowerKeysConst.SCHOOL_RANK + schoolAdminId;
            long rank = sourcePowerRankOpt.getRank(key, student.getId());

            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, (long) PageUtil.getPageNum(), (long) PageUtil.getPageSize(), null);
            return this.packageRankVO(key, rank, studentIds);
        } else {
            // 全国排行（前50名）
            String key = SourcePowerKeysConst.COUNTRY_RANK;
            long rank = sourcePowerRankOpt.getRank(key, student.getId());
            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, (long) PageUtil.getPageNum(), (long) PageUtil.getPageSize(), 50);

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
                    .nickName(String.valueOf(map.get("nickName")))
                    .sourcePower((int) map.get("sourcePower"))
                    .studentId(id)
                    .build();
        }).collect(Collectors.toList());

        // 参与排行总人数
        long studentCount = sourcePowerRankOpt.getMemberSize(key);

        return ServerResponse.createBySuccess(RankVO.builder()
                .myRank(rank)
                .total(studentCount)
                .rankInfoList(collect));
    }

    public IndexVO getIndexVoTmp(List<Map<String, Object>> equipments) {
        IndexVO indexVO = new IndexVO();
        if (CollectionUtils.isNotEmpty(equipments)) {
            equipments.forEach(map -> {
                Integer type = (Integer) map.get("type");
                String imgUrl = (String) map.get("imgUrl");
                if (type == EquipmentTypeConstant.SHIP) {
                    indexVO.setShipUrl(imgUrl);
                } else if (type == EquipmentTypeConstant.WEAPONS) {
                    indexVO.setWeaponsUrl(imgUrl);
                } else if (type == EquipmentTypeConstant.MISSILE) {
                    indexVO.setMissileUrl(imgUrl);
                } else if (type == EquipmentTypeConstant.ARMOR) {
                    indexVO.setArmorUrl(imgUrl);
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

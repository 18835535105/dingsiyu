package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.pojo.StudentSkin;
import com.zhidejiaoyu.common.pojo.SyntheticRewardsList;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.constant.EquipmentTypeConstant;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Override
    public ServerResponse<Object> index() {
        Student student = super.getStudent();
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());

        // 皮肤
        StudentSkin studentSkin = studentSkinMapper.selectUseSkinByStudentId(student.getId());

        // 勋章图片
        List<String> medalImgList = this.getMedalImgList(studentExpansion);

        // 学生装备的飞船及装备信息
        IndexVO indexVO = this.getIndexVoTmp(student);

        SyntheticRewardsList syntheticRewardsList = syntheticRewardsListMapper.selectUseGloveOrFlower(student.getId());

        return ServerResponse.createBySuccess(IndexVO.builder()
                .sourcePoser(studentExpansion.getSourcePower())
                .skinImgUrl(studentSkin == null ? "" : GetOssFile.getPublicObjectUrl(studentSkin.getImgUrl()))
                .medalUrl(medalImgList)
                .armorUrl(indexVO.getArmorUrl())
                .missileUrl(indexVO.getMissileUrl())
                .shipUrl(indexVO.getShipUrl())
                .weaponsUrl(indexVO.getWeaponsUrl())
                .sourceUrl(syntheticRewardsList == null ? "" : GetOssFile.getPublicObjectUrl(syntheticRewardsList.getImgUrl()))
                .build());
    }

    public IndexVO getIndexVoTmp(Student student) {
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(student.getId());
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

package com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service.impl;

import com.zhidejiaoyu.common.mapper.PartnerMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Partner;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.constant.PartnerConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service.PartnerService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.vo.SavePartnerDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartnerServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements PartnerService {

    @Resource
    private PartnerMapper partnerMapper;

    /**
     * 保存数据  商业大佬1 销售大咖2 管理专家3 创新4 初心5 校长6 完美均衡7 发奋8 踏实9
     *
     * @param savePartnerDto
     * @return
     */
    @Override
    public Object savePartner(SavePartnerDto savePartnerDto) {
        //判断账号是否已经有过测试
        Integer integer = partnerMapper.countByOpenId(savePartnerDto.getOpenId());
        if (integer != null && integer > 0) {
            partnerMapper.deleteByOpenId(savePartnerDto.getOpenId());
        }
        //获取map集合
        Map<Integer, Integer> partnerMap = getPartnerMap(savePartnerDto);
        //计算最高5个排行奖励
        partnerMap = getRanking(partnerMap);
        //计算经济价值
        Integer economicFalseValue = getEconomicFalseValue(partnerMap, savePartnerDto.getTotalSorce());
        //计算超过人数
        Double overPopulation = getOverPopulation(economicFalseValue);
        //保存结果
        savePartnerMapper(partnerMap, savePartnerDto, economicFalseValue, overPopulation);
        return ServerResponse.createBySuccess();
    }

    private void savePartnerMapper(Map<Integer, Integer> partnerMap, SavePartnerDto savePartnerDto,
                                   Integer economicFalseValue, Double overPopulation) {
        Set<Integer> integers = partnerMap.keySet();
        for (Integer i : integers) {
            Partner partner = new Partner();
            partner.setEconomicValue(economicFalseValue);
            partner.setImgUrl(savePartnerDto.getImgUrl());
            partner.setOpenId(savePartnerDto.getOpenId());
            partner.setOverPerson(overPopulation);
            partner.setTotalSorce(savePartnerDto.getTotalSorce());
            partner.setType(getType(i));
            partnerMapper.insert(partner);
        }
    }

    /**
     * 计算超过人数 70%+（经济价值/500万）*30%
     */
    private Double getOverPopulation(Integer economicFalseValue) {
        double overPopulation = 70 + (economicFalseValue * 1.0 / 500) * 0.3;
        DecimalFormat df = new DecimalFormat("#.0");
        return Double.parseDouble(df.format(overPopulation));
    }

    /**
     * 计算经济价值
     *
     * @param partnerMap
     * @param totalSorce
     */
    private Integer getEconomicFalseValue(Map<Integer, Integer> partnerMap, Integer totalSorce) {

        Integer economicFalseValue = 0;
        Set<Integer> integers = partnerMap.keySet();
        for (Integer key : integers) {
            economicFalseValue += getEconomicFalseValueFormula(key, partnerMap.get(key), totalSorce).intValue();
        }
        return economicFalseValue;
    }

    private Double getEconomicFalseValueFormula(Integer key, Integer integer, Integer totalSorce) {
        //公式（类型得分/个人总分）*类型价值。
        Double proportion = integer * 1.0 / totalSorce;
        if (key.equals(1)) {
            return proportion * PartnerConstant.BUSINESS;
        }
        if (key.equals(2)) {
            return proportion * PartnerConstant.SALE;
        }
        if (key.equals(3)) {
            return proportion * PartnerConstant.ADMINISTRATION;
        }
        if (key.equals(4)) {
            return proportion * PartnerConstant.INNOVATE;
        }
        if (key.equals(5)) {
            return proportion * PartnerConstant.PRIMORDIAL_MIND;
        }
        if (key.equals(6)) {
            return proportion * PartnerConstant.PRIVCIPAL;
        }
        if (key.equals(7)) {
            return proportion * PartnerConstant.PERFECTBALANCE;
        }
        if (key.equals(8)) {
            return proportion * PartnerConstant.TO_WORK_HARD;
        }
        if (key.equals(9)) {
            return proportion * PartnerConstant.DOWN_TO_EARTH;
        }
        return 0.0;
    }

    private Map<Integer, Integer> getRanking(Map<Integer, Integer> partnerMap) {
        Map<Integer, Integer> returnMap = new HashMap<>();
        List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(partnerMap.size());
        list.addAll(partnerMap.entrySet());
        List<Integer> keys = list.stream()
                .sorted(Comparator.comparing(Map.Entry<Integer, Integer>::getValue).reversed())
                .map(Map.Entry<Integer, Integer>::getKey)
                .collect(Collectors.toList());
        for (int i = 0; i < keys.size(); i++) {
            if (i < 5) {
                returnMap.put(keys.get(i), partnerMap.get(keys.get(i)));
            }
        }
        return returnMap;
    }

    private Map<Integer, Integer> getPartnerMap(SavePartnerDto savePartnerDto) {
        Map<Integer, Integer> map = new HashMap<>();
        if(savePartnerDto.getTotalSorce()==null){
            savePartnerDto.setTotalSorce(0);
        }
        if (savePartnerDto.getBusiness() != null && savePartnerDto.getBusiness() > 0) {
            map.put(1, savePartnerDto.getBusiness());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getBusiness());
        }
        if (savePartnerDto.getSale() != null && savePartnerDto.getSale() > 0) {
            map.put(2, savePartnerDto.getSale());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getSale());
        }
        if (savePartnerDto.getAdministration() != null && savePartnerDto.getAdministration() > 0) {
            map.put(3, savePartnerDto.getAdministration());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getAdministration());
        }
        if (savePartnerDto.getInnovate() != null && savePartnerDto.getInnovate() > 0) {
            map.put(4, savePartnerDto.getInnovate());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getInnovate());
        }
        if (savePartnerDto.getPrimordialMind() != null && savePartnerDto.getPrimordialMind() > 0) {
            map.put(5, savePartnerDto.getPrimordialMind());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getPrimordialMind());
        }
        if (savePartnerDto.getPrincipal() != null && savePartnerDto.getPrincipal() > 0) {
            map.put(6, savePartnerDto.getPrincipal());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getPrincipal());
        }
        if (savePartnerDto.getPerfectBalance() != null && savePartnerDto.getPerfectBalance() > 0) {
            map.put(7, savePartnerDto.getPerfectBalance());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getPerfectBalance());
        }
        if (savePartnerDto.getToWorkHard() != null && savePartnerDto.getToWorkHard() > 0) {
            map.put(8, savePartnerDto.getToWorkHard());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getToWorkHard());
        }
        if (savePartnerDto.getDownToEarth() != null && savePartnerDto.getDownToEarth() > 0) {
            map.put(9, savePartnerDto.getDownToEarth());
            savePartnerDto.setTotalSorce(savePartnerDto.getTotalSorce()+ savePartnerDto.getDownToEarth());
        }
        return map;
    }

    private String getType(Integer type) {
        if (type.equals(1)) {
            return "商业大佬";
        }
        if (type.equals(2)) {
            return "销售大咖";
        }
        if (type.equals(3)) {
            return "管理专家";
        }
        if (type.equals(4)) {
            return "创新";
        }
        if (type.equals(5)) {
            return "初心";
        }
        if (type.equals(6)) {
            return "校长";
        }
        if (type.equals(7)) {
            return "完美均衡";
        }
        if (type.equals(8)) {
            return "发奋";
        }
        if (type.equals(9)) {
            return "踏实";
        }
        return null;
    }
}

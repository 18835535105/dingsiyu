package com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service.impl;

import com.zhidejiaoyu.common.mapper.center.PartnerMapper;
import com.zhidejiaoyu.common.pojo.center.Partner;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.constant.PartnerConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.dto.SavePartnerDTO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service.PartnerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartnerServiceImpl extends BaseServiceImpl<PartnerMapper, Partner> implements PartnerService {

    @Resource
    private PartnerMapper partnerMapper;

    /**
     * 保存数据  商业大佬1 销售大咖2 管理专家3 创新4 初心5 校长6 完美均衡7 发奋8 踏实9
     *
     * @param savePartnerDto
     * @return
     */
    @Override
    public Object savePartner(SavePartnerDTO savePartnerDto) {
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
        Integer economicFalseValue = getEconomicFalseValue(partnerMap, savePartnerDto.getTotalScore());
        //计算超过人数
        Double overPopulation = getOverPopulation(economicFalseValue);
        //保存结果
        savePartnerMapper(partnerMap, savePartnerDto, economicFalseValue, overPopulation);
        return ServerResponse.createBySuccess();
    }

    private void savePartnerMapper(Map<Integer, Integer> partnerMap, SavePartnerDTO savePartnerDto,
                                   Integer economicFalseValue, Double overPopulation) {
        Date date = new Date();
        this.saveBatch(partnerMap.keySet()
                .stream()
                .map(key -> {
                    Partner partner = new Partner();
                    partner.setEconomicValue(economicFalseValue);
                    partner.setImgUrl(savePartnerDto.getImgUrl());
                    partner.setOpenId(savePartnerDto.getOpenId());
                    partner.setOverPerson(overPopulation);
                    partner.setTotalSorce(savePartnerDto.getTotalScore());
                    partner.setType(getType(key));
                    partner.setNickname(savePartnerDto.getNickname());
                    partner.setCreateTime(date);
                    return partner;
                }).collect(Collectors.toList()));
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
     * @param totalScore
     */
    private Integer getEconomicFalseValue(Map<Integer, Integer> partnerMap, Integer totalScore) {

        int economicFalseValue = 0;
        Set<Integer> integers = partnerMap.keySet();
        for (Integer key : integers) {
            economicFalseValue += getEconomicFalseValueFormula(key, partnerMap.get(key), totalScore).intValue();
        }
        return economicFalseValue;
    }

    private Double getEconomicFalseValueFormula(Integer key, Integer integer, Integer totalScore) {
        //公式（类型得分/个人总分）*类型价值。
        Double proportion = integer * 1.0 / totalScore;
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
        Map<Integer, Integer> returnMap = new HashMap<>(16);
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(partnerMap.entrySet());
        list.stream()
                .sorted(Comparator.comparing(Map.Entry<Integer, Integer>::getValue).reversed())
                .limit(5)
                .forEach(entry -> {
                    Integer key = entry.getKey();
                    // 得分
                    Integer score = partnerMap.get(key);
                    returnMap.put(key, score == null ? 0 : score);
                });
        return returnMap;
    }

    private Map<Integer, Integer> getPartnerMap(SavePartnerDTO savePartnerDto) {
        Map<Integer, Integer> map = new HashMap<>(16);
        int totalScore = 0;

        Integer business = savePartnerDto.getBusiness();
        if (business != null && business > 0) {
            map.put(1, business);
            totalScore += business;
        }

        Integer sale = savePartnerDto.getSale();
        if (sale != null && sale > 0) {
            map.put(2, sale);
            totalScore += sale;
        }

        Integer administration = savePartnerDto.getAdministration();
        if (administration != null && administration > 0) {
            map.put(3, administration);
            totalScore += administration;
        }

        Integer innovate = savePartnerDto.getInnovate();
        if (innovate != null && innovate > 0) {
            map.put(4, innovate);
            totalScore += innovate;
        }

        Integer primordialMind = savePartnerDto.getPrimordialMind();
        if (primordialMind != null && primordialMind > 0) {
            map.put(5, primordialMind);
            totalScore += primordialMind;
        }

        Integer principal = savePartnerDto.getPrincipal();
        if (principal != null && principal > 0) {
            map.put(6, principal);
            totalScore += principal;
        }

        Integer perfectBalance = savePartnerDto.getPerfectBalance();
        if (perfectBalance != null && perfectBalance > 0) {
            map.put(7, perfectBalance);
            totalScore += perfectBalance;
        }

        Integer toWorkHard = savePartnerDto.getToWorkHard();
        if (toWorkHard != null && toWorkHard > 0) {
            map.put(8, toWorkHard);
            totalScore += toWorkHard;
        }

        Integer downToEarth = savePartnerDto.getDownToEarth();
        if (downToEarth != null && downToEarth > 0) {
            map.put(9, downToEarth);
            totalScore += downToEarth;
        }
        savePartnerDto.setTotalScore(totalScore);
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

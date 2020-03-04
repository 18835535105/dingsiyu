package com.zhidejiaoyu.student.business.shipconfig.util;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.EquipmentMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.TestRecordMapper;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 计算规则工具
 *
 * @author: wuchenxi
 * @date: 2020/3/2 10:55:55
 */
@Component
public class CalculateUtil {

    private static LearnNewMapper learnNewMapper;

    private static DurationMapper durationMapper;

    private static TestRecordMapper testRecordMapper;

    private static EquipmentMapper equipmentMapper;

    private static ShipIndexService shipIndexService;

    @Resource(name = "learnNewMapper")
    private LearnNewMapper learnNewMapperTmp;

    @Resource(name = "durationMapper")
    private DurationMapper durationMapperTmp;

    @Resource(name = "testRecordMapper")
    private TestRecordMapper testRecordMapperTmp;

    @Resource(name = "equipmentMapper")
    private EquipmentMapper equipmentMapperTmp;

    @Resource
    private ShipIndexService shipIndexServiceTmp;

    @PostConstruct
    public void init() {
        learnNewMapper = this.learnNewMapperTmp;
        durationMapper = this.durationMapperTmp;
        testRecordMapper = this.testRecordMapperTmp;
        equipmentMapper = this.equipmentMapperTmp;
        shipIndexService = this.shipIndexServiceTmp;
    }

    /**
     * 攻击状态
     *
     * @param baseValue              攻击基础值
     * @param studentId
     * @param beforeSevenDaysDateStr 7天前的日期
     * @param endDateStr             当前日期
     * @return
     */
    public static int getAttack(Integer baseValue, Long studentId, String beforeSevenDaysDateStr, String endDateStr) {
        int count = learnNewMapper.countLearnedWordCountByStartDateAndEndDate(studentId, beforeSevenDaysDateStr, endDateStr);
        return Math.min((int) (baseValue * (0.2 + count * 1.0 / 30)), 3000);
    }

    /**
     * 耐久度状态    耐久度基础值*(50%+学习时长/8小时)
     *
     * @param baseValue              耐久度基础值
     * @param studentId
     * @param beforeSevenDaysDateStr 7天前的日期
     * @param endDateStr             当前日期
     * @return
     */
    public static int getDurability(Integer baseValue, Long studentId, String beforeSevenDaysDateStr, String endDateStr) {
        Integer validTime = durationMapper.selectValidTime(studentId, beforeSevenDaysDateStr, endDateStr);
        if (validTime == null) {
            validTime = 0;
        }
        return Math.min((int) (baseValue * (0.5 + validTime * 1.0 / 28800)), 30000);
    }

    /**
     * 命中率状态 命中百分比基础值*(50%+复习频次/7)
     *
     * @param baseValue              命中率基础值
     * @param studentId
     * @param beforeSevenDaysDateStr 7天前的日期
     * @param endDateStr             当前日期
     * @return
     */
    public static double getHitRate(Double baseValue, Long studentId, String beforeSevenDaysDateStr, String endDateStr) {
        int count = testRecordMapper.countByGenreWithBeginTimeAndEndTime(studentId, GenreConstant.SMALLAPP_GENRE, beforeSevenDaysDateStr, endDateStr);
        return Math.min(baseValue * (0.5 + count * 1.0 / 7), 2);
    }

    /**
     * 机动力状态    机动力基础值* (50 % +有效时长 / 在线时长)
     *
     * @param baseValue              耐久度基础值
     * @param studentId
     * @param beforeSevenDaysDateStr 7天前的日期
     * @param endDateStr             当前日期
     * @return
     */
    public static int getMove(Integer baseValue, Long studentId, String beforeSevenDaysDateStr, String endDateStr) {
        Integer onlineTime = durationMapper.selectOnlineTime(studentId, beforeSevenDaysDateStr, endDateStr);
        Integer validTime = durationMapper.selectValidTime(studentId, beforeSevenDaysDateStr, endDateStr);
        if (onlineTime == null) {
            onlineTime = 0;
        }
        if (validTime == null) {
            validTime = 0;
        }
        if (validTime > onlineTime) {
            validTime = (int) (onlineTime * 0.98);
        }

        return Math.min((int) (baseValue * (0.5 + validTime * 1.0 / onlineTime)), 200);
    }

    /**
     * 源力状态 源力攻击状态*源力
     * 源力攻击状态=源力攻击基础值*(50%+本周平均成绩/100)
     *
     * @param baseValue
     * @param studentId
     * @param beforeSevenDaysDateStr
     * @param endDateStr
     * @return
     */
    public static int getSource(IndexVO.BaseValue baseValue, Long studentId, String beforeSevenDaysDateStr, String endDateStr) {
        double avg = testRecordMapper.selectScoreAvgByStartDateAndEndDate(studentId, beforeSevenDaysDateStr, endDateStr);
        return Math.min(30000, (int) (baseValue.getSourceAttack() * (0.5 + avg * 1.0 / 100) * baseValue.getSource()));
    }

    /**
     * 获取源力攻击
     */
    public static int getSouintrceForceAttack(Long studentId, String beforeSevenDaysDateStr, String endDateStr) {

        // 学生装备的飞船及装备信息
        List<Map<String, Object>> equipments = equipmentMapper.selectUsedByStudentId(studentId);
        IndexVO.BaseValue baseValue = shipIndexService.getMaxValue(equipments);
        //获取攻击力
        int attack = getAttack(baseValue.getAttack(), studentId, beforeSevenDaysDateStr, endDateStr);
        //获取耐久度
        int durability = getDurability(baseValue.getDurability(), studentId, beforeSevenDaysDateStr, endDateStr);
        //获取命中率
        double hitRate = getHitRate(baseValue.getHitRate(), studentId, beforeSevenDaysDateStr, endDateStr);
        //获取机动力
        int move = getMove(baseValue.getMove(), studentId, beforeSevenDaysDateStr, endDateStr);
        //获取源力
        int source = getSource(baseValue, studentId, beforeSevenDaysDateStr, endDateStr);
        return (int)((durability + attack * 5 + baseValue.getSourceAttack() * source) * (hitRate + move / 10000));
    }


}

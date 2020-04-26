package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.pojo.GoldLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * 金币变化记录
 *
 * @author: wuchenxi
 * @date: 2020/3/31 10:05:05
 */
@Slf4j
@Component
public class GoldLogUtil {

    private static ExecutorService executorService;
    private static GoldLogMapper goldLogMapper;

    @Resource
    private ExecutorService executorServiceTmp;

    @Resource
    private GoldLogMapper goldLogMapperTmp;

    @PostConstruct
    private void init() {
        executorService = this.executorServiceTmp;
        goldLogMapper = this.goldLogMapperTmp;
    }

    /**
     * 保存学习期间金币增加日志
     *
     * @param studentId
     * @param reason    增加原因
     * @param gold      金币增加数量（数量为增加的正数）
     */
    public static void saveStudyGoldLog(Long studentId, String reason, int gold) throws RuntimeException {
        executorService.execute(() -> saveGoldLog(studentId, reason, gold, 1));
    }

    /**
     * 保存学生兑奖金币减少日志
     *
     * @param studentId
     * @param reason    减少原因
     * @param gold      金币减少数量
     */
    public static void savePrizeGoldLog(Long studentId, String reason, int gold) {
        executorService.execute(() -> saveGoldLog(studentId, reason, -gold, 2));
    }

    /**
     * 保存补卡金币减少日志
     *
     * @param studentId
     * @param reason    减少原因
     * @param gold      金币减少数量
     */
    public static void saveReplenishGoldLog(Long studentId, String reason, int gold) throws RuntimeException {
        executorService.execute(() -> saveGoldLog(studentId, reason, -gold, 5));
    }

    private static void saveGoldLog(Long studentId, String reason, int gold, int type) {
        goldLogMapper.insert(GoldLog.builder()
                .studentId(studentId)
                .operatorId(Math.toIntExact(studentId))
                .reason(reason)
                .readFlag(2)
                .goldReduce(gold >= 0 ? 0 : Math.abs(gold))
                .goldAdd(Math.max(gold, 0))
                .createTime(new Date())
                .type(type)
                .build());
    }

    private GoldLogUtil() {
    }

}

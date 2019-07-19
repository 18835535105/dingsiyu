package com.zhidejiaoyu.common.award;

import com.zhidejiaoyu.common.mapper.AwardMapper;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.TestRecordMapper;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 异步保存金币奖励信息
 *
 * @author wuchenxi
 * @date 2019-03-02
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class GoldAwardAsync extends BaseAwardAsync{

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    /**
     * 金币奖励总进度为1的通用方法
     *
     * @param student
     */
    public void dailyAward(Student student, Integer awardContentType) {
        Long studentId = student.getId();
        // 查看奖励
        Award award = super.getByAwardContentTypeAndType(studentId, GOLD_TYPE, awardContentType);

        try {
            if (this.checkAward(award, GOLD_TYPE)) {
                optAward(studentId, awardContentType, 1, award, GOLD_TYPE);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存金币奖励信息失败"), e);
        }
    }

    /**
     * 学习总有效时长信息
     *
     * @param student
     */
    public void totalValidTime(Student student) {
        Long studentId = student.getId();
        final int[] awardContentType = {15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
        int length = awardContentType.length;

        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = super.getByAwardContentTypeAndType(studentId, GOLD_TYPE, awardContentType[length - 1]);
        try {
            if (super.checkAward(award, GOLD_TYPE)) {
                // 总有效时长
                Long totalValidTime = durationMapper.countTotalValidTime(studentId);
                if (totalValidTime == null) {
                    totalValidTime = 0L;
                } else {
                    totalValidTime /= 60;
                }

                for (int i1 : awardContentType) {
                    award = super.getByAwardContentTypeAndType(studentId, GOLD_TYPE, i1);
                    if (this.checkAward(award, GOLD_TYPE)) {
                        super.optAward(studentId, i1, Integer.parseInt(totalValidTime.toString()), award, GOLD_TYPE);
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.error(super.logErrorMsg(student, "保存金币奖励信息失败"), e);
        }
    }

    /**
     * 单元闯关成功奖励
     *
     * @param student
     */
    public void completeUnitTest(Student student) {
        Long studentId = student.getId();
        final int[] awardContentType = {105, 106, 107, 108, 109, 110, 111, 112, 113, 114};
        int length = awardContentType.length;

        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = super.getByAwardContentTypeAndType(studentId, GOLD_TYPE, awardContentType[length - 1]);
        try {
            if (super.checkAward(award, GOLD_TYPE)) {
                // 查看学生所有课程下单元闯关成功个数
                int count = testRecordMapper.countUnitTestSuccessByStudentId(student.getId());

                for (int i1 : awardContentType) {
                    award = super.getByAwardContentTypeAndType(studentId, GOLD_TYPE, i1);
                    if (this.checkAward(award, GOLD_TYPE)) {
                        super.optAward(studentId, i1, count, award, GOLD_TYPE);
                    }
                }
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存金币奖励信息失败"), e);
        }
    }
}

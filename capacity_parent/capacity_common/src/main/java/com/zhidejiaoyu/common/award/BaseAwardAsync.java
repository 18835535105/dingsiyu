package com.zhidejiaoyu.common.award;

import com.zhidejiaoyu.common.mapper.AwardContentTypeMapper;
import com.zhidejiaoyu.common.mapper.AwardMapper;
import com.zhidejiaoyu.common.mapper.MedalMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.AwardContentType;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author wuchenxi
 * @date 2019-03-02
 */
@Slf4j
@Component
class BaseAwardAsync {

    /**
     * 日奖励类型
     */
    static final int DAILY_TYPE = 1;

    /**
     * 金币奖励类型
     */
    static final int GOLD_TYPE = 2;

    /**
     * 勋章奖励类型
     */
    static final int MEDAL_TYPE = 3;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private AwardContentTypeMapper awardContentTypeMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 检验是否需要保存或更新 award
     *
     * @param award
     * @param type 1:日奖励；2：金币奖励，3：勋章奖励
     * @return true:需要保存或更新 award；false：不需要保存或更新 award
     */
    boolean checkAward(Award award, int type) {
        boolean flag = award == null || !Objects.equals(award.getCurrentPlan(), award.getTotalPlan()) || Objects.equals(award.getTotalPlan(), 0);
        if (flag) {
            return true;
        }
        // 如果从数据库中直接修改了需要完成奖励的总个数，也需要修改 award 表中相应的总个数
        if (type == 1 || type == 2) {
            AwardContentType awardContentType = awardContentTypeMapper.selectById(award.getAwardContentType());
            return awardContentType != null && !Objects.equals(awardContentType.getTotalPlan(), award.getTotalPlan());
        } else {
            Medal medal = medalMapper.selectById(award.getMedalType());
            return medal != null && !Objects.equals(medal.getTotalPlan(), award.getTotalPlan());
        }
    }

    /**
     * 保存日奖励、更新奖励完成进度
     * <ul>
     * <li>奖励为空，新增奖励信息，并记录奖励进度</li>
     * <li>奖励不为空，更新奖励进度</li>
     * </ul>
     *
     * @param studentId
     * @param id 奖励类型 id
     * @param currentPlan      当前完成进度
     * @param award
     */
    void optAward(Long studentId, long id, int currentPlan, Award award, int type) {
        Integer totalPlan;
        if (MEDAL_TYPE == type) {
            Medal medal = medalMapper.selectById(id);
            totalPlan = medal.getTotalPlan();
        } else {
            AwardContentType contentType = awardContentTypeMapper.selectById(id);
            totalPlan = contentType.getTotalPlan();
        }

        if (award == null) {
            if (currentPlan >= totalPlan) {
                this.saveAward(studentId, id, type, totalPlan, totalPlan);
            } else {
                this.saveAward(studentId, id, type, currentPlan, totalPlan);
            }
        } else {
            // 当前进度为空时更新当前进度以及总进度
            if (award.getCurrentPlan() == null) {
                award.setCurrentPlan(currentPlan >= totalPlan ? totalPlan : currentPlan);
                award.setTotalPlan(totalPlan);
                this.updateAward(award);
            } else if (!Objects.equals(award.getCurrentPlan(), totalPlan)) {
                if (Objects.equals(award.getCanGet(), 1)) {
                    // 如果从数据库变更需要完成奖励总个数，已经完成的奖励当前进度置为总进度
                    award.setCurrentPlan(totalPlan);
                } else {
                    // 更新完成进度
                    award.setCurrentPlan(currentPlan >= totalPlan ? totalPlan : currentPlan);
                    if (Objects.equals(award.getCurrentPlan(), totalPlan)) {
                        award.setCanGet(1);
                    }
                }
                this.updateAward(award);
            }
        }
    }

    private void updateAward(Award award) {
        if (award != null && !Objects.equals(award.getCurrentPlan(), 0)) {
            try {
                awardMapper.updateById(award);
            } catch (Exception e) {
                log.error("更新奖励进度失败：award=[{}]", award.toString(), e);
            }
        }
    }

    private void saveAward(Long studentId, long awardContentType, int type, int currentPlan, int totalPlan) {
        Award award = new Award();
        if (currentPlan >= totalPlan) {
            award.setCanGet(1);
        } else {
            award.setCanGet(2);
        }
        award.setGetFlag(2);
        award.setStudentId(studentId);
        award.setCreateTime(new Date());
        award.setType(type);
        award.setCurrentPlan(currentPlan >= totalPlan ? totalPlan : currentPlan);
        award.setTotalPlan(totalPlan);
        if (type == MEDAL_TYPE) {
            award.setMedalType(awardContentType);
        } else {
            award.setAwardContentType((int) awardContentType);
        }
        try {
            awardMapper.insert(award);
        } catch (Exception e) {
            log.error("保存奖励进度失败：award=[{}]", award.toString(), e);
        }
    }

    /**
     * 拼接错误日志信息
     *
     * @param student
     * @param msg
     * @return
     */
    String logErrorMsg(Student student, String msg) {
        return "学生[" + student.getId() + " - " + student.getAccount() + " - " + student.getStudentName() + "] " + msg;
    }

    Integer getSchoolAdminId(Student student) {
        if (student.getTeacherId() == null) {
            return null;
        }
        Integer schoolAdminId = teacherMapper.getSchoolAdminById(Integer.valueOf(student.getTeacherId().toString()));
        if (schoolAdminId == null) {
            return Integer.valueOf(student.getTeacherId().toString());
        }
        return schoolAdminId;
    }

    Award getByAwardContentTypeAndType(Long studentId, int type, int awardContentType) {
        List<Award> awards = awardMapper.selectByAwardContentTypeAndType(studentId, type, awardContentType);
        if (awards != null) {
            if (awards.size() > 1) {
                Award award = awards.get(0);
                awardMapper.deleteById(award.getId());
                return awards.get(1);
            }
            return awards.size() == 0 ? null : awards.get(0);
        }
        return null;
    }
}

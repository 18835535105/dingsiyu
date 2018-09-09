package com.zhidejiaoyu.common.Vo.student;

import com.zhidejiaoyu.common.pojo.Ccie;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author wuchenxi
 * @date 2018/8/28
 */
public class CcieVo extends Ccie {
    @Setter
    @Getter
    private String testName;

    @Setter
    @Getter
    private String studyModelName;

    @Getter
    @Setter
    private String ccieName;

    public CcieVo(Ccie ccie) {
        super.setCcieNo(ccie.getCcieNo());
        super.setEncourageWord(ccie.getEncourageWord());
        super.setGetTime(ccie.getGetTime());
        super.setReadFlag(ccie.getReadFlag());
        super.setStudentId(ccie.getStudentId());
        super.setStudentName(ccie.getStudentName());
        super.setUnitId(ccie.getUnitId());
        super.setId(ccie.getId());
        this.testName = getTestName(ccie);
        this.studyModelName = getStudyModel(ccie);
        this.ccieName = getCcieName(ccie);
    }

    private String getCcieName(Ccie ccie) {
        String substring = ccie.getCcieNo().substring(0, 1);
        if (Objects.equals("K", substring)) {
            return "课程证书";
        } else if (Objects.equals("N", substring)) {
            return "牛人证书";
        }
        return null;
    }

    private String getTestName(Ccie ccie) {

        if (ccie.getTestType() == null) {
            return "";
        }

        int[] type = {1, 2, 3, 4, 5, 6};
        String[] name = {"单元闯关测试", "复习测试", "已学测试", "生词测试", "熟词测试", "五维测试"};

        int length = type.length;
        for (int i = 0; i < length; i++) {
            if (type[i] == ccie.getTestType()) {
                return name[i];
            }
        }
        return null;
    }

    private String getStudyModel(Ccie ccie) {
        if (ccie.getStudyModel() == null) {
            return "";
        }

        int[] type = {0, 1, 2, 3, 4, 5, 6, 7};
        String[] name = {"单词图鉴", "慧记忆", "慧听写", "生词测试", "熟词测试", "五维测试"};

        int length = type.length;
        for (int i = 0; i < length; i++) {
            if (type[i] == ccie.getStudyModel()) {
                return name[i];
            }
        }
        return null;
    }
}

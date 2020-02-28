package com.zhidejiaoyu.common.constant;

import java.io.FileInputStream;

/**
 * 文件上传目录常量类
 *
 * @author wuchenxi
 * @date 2018年5月16日 下午1:09:28
 */
public interface FileConstant {

    /**
     * 宠物录音地址
     */
    String PET_SAY_AUDIO = "audio/pet-tip/";

    /**
     * 意见反馈图片地址
     */
    String FEEDBACK_IMG = "imgs/feedBackImg/";

    /**
     * 学生报表excel
     */
    String STUDENT_REPORT_EXCEL = "excel/student_report/";

    /**
     * 生成的临时excel文件存储位置（服务器）
     */
    String TMP_EXCEL = "/var/tmp/";

    /**
     * 生成的二维码（服务器）
     */
    String QR_CODE = TMP_EXCEL + "QR_CODE";

    /**
     * 小程序码（oss）
     */
    String QR_CODE_OSS = "imgs/qr-code/";
}

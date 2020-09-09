package com.zhidejiaoyu.common.constant;

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

    /**
     * 教师上传的学生智慧飞行记录图片地址
     */
    String STUDENT_FLY_RECORD = "imgs/student_fly_record/";

    /**
     * 小程序海报图片地址
     */
    String SHARE_CONFIG_IMG = "imgs/shareConfigImg/";

    /**
     * 飞船配置-飞船路径
     */
    String SHIP_IMG = "/static/img/ship-config/base_info/ship/";

    /**
     * 飞船配置-武器路径
     */
    String WEAPON_IMG = "/static/img/ship-config/base_info/weapon/";

    /**
     * 飞船配置-英雄路径
     */
    String HERO_IMG = "/static/img/ship-config/base_info/hero/";

    /**
     * 飞船配置-导弹路径
     */
    String MISSILE_IMG = "/static/img/ship-config/base_info/missile/";

    /**
     * 飞船配置-装甲路径
     */
    String ARMOR_IMG = "/static/img/ship-config/base_info/armor/";

    /**
     * 副本挑战图片路径
     */
    String COPY_IMG = "/static/img/ship-config/base_info/copy/";

    /**
     * 金币测试图片路径
     */
    String TEST_STORE_IMG = "imgs/testImg/";

    /**
     * 保存奖品封面路径
     */
    String PRIZE_IMG = "imgs/prizeImg/";
    /**
     * 保存飞行测试结果页路径
     */
    String CODE_IMG = "imgs/codeImg";

    interface PetNameConstant {
        String DAMINGBAI = "/static/img/edit-user-msg/tips1-1.png";
        //String LITANGXIN = "/static/img/edit-user-msg/tips-3.png";
        String LITANGXIN = "/static/img/edit-user-msg/honey-pass.png";
        String WUMING = "/static/img/edit-user-msg/tips-4.png";
        String WEISIDUN = "/static/img/edit-user-msg/tips-2.png";
    }
}

package com.zhidejiaoyu.common.constant.redis;

import com.zhidejiaoyu.common.constant.ServerNoConstant;

/**
 * 学生排行相关key
 *
 * @author wuchenxi
 * @date 2019-06-21
 */
public class RankKeysConst {

    /**
     * 学生班级金币排行 key
     */
    public static final String CLASS_GOLD_RANK = "CLASS_GOLD_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 学生班级金币排行 key
     */
    public static final String SCHOOL_GOLD_RANK = "SCHOOL_GOLD_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 同服务器金币排行 key
     */
    public static final String SERVER_GOLD_RANK = "SERVER_GOLD_RANK:" + ServerNoConstant.SERVER_NO;
    /**
     * 学生全国金币排行 key
     */
    public static final String COUNTRY_GOLD_RANK = "COUNTRY_GOLD_RANK";


    /**
     * 学生班级膜拜排行 key
     */
    public static final String CLASS_WORSHIP_RANK = "CLASS_WORSHIP_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 学生班级膜拜排行 key
     */
    public static final String SCHOOL_WORSHIP_RANK = "SCHOOL_WORSHIP_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 同服务器膜拜排行 key
     */
    public static final String SERVER_WORSHIP_RANK = "SERVER_WORSHIP_RANK:" + ServerNoConstant.SERVER_NO;
    /**
     * 学生全国膜拜排行 key
     */
    public static final String COUNTRY_WORSHIP_RANK = "COUNTRY_WORSHIP_RANK";


    /**
     * 学生班级勋章排行 key
     */
    public static final String CLASS_MEDAL_RANK = "CLASS_MEDAL_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 学生学校勋章排行 key
     */
    public static final String SCHOOL_MEDAL_RANK = "SCHOOL_MEDAL_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 同服务器勋章排行 key
     */
    public static final String SERVER_MEDAL_RANK = "SERVER_MEDAL_RANK:" + ServerNoConstant.SERVER_NO;
    /**
     * 学生全国勋章排行 key
     */
    public static final String COUNTRY_MEDAL_RANK = "COUNTRY_MEDAL_RANK";


    /**
     * 学生班级证书排行 key
     */
    public static final String CLASS_CCIE_RANK = "CLASS_CCIE_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 学生学校证书排行 key
     */
    public static final String SCHOOL_CCIE_RANK = "SCHOOL_CCIE_RANK:" + ServerNoConstant.SERVER_NO + ":";
    /**
     * 同服务器证书排行 key
     */
    public static final String SERVER_CCIE_RANK = "SERVER_CCIE_RANK:" + ServerNoConstant.SERVER_NO;
    /**
     * 学生全国证书排行 key
     */
    public static final String COUNTRY_CCIE_RANK = "COUNTRY_CCIE_RANK";
}

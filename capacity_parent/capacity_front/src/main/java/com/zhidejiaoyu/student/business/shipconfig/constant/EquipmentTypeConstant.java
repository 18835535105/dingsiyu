package com.zhidejiaoyu.student.business.shipconfig.constant;

/**
 * 飞船及装备类型type
 *
 * @author: wuchenxi
 * @date: 2020/2/27 17:44:44
 */
public interface EquipmentTypeConstant {

    /**
     * 武器类型
     */
     int WEAPONS = 2;

    /**
     * 装甲类型
     */
    int ARMOR = 4;

    /**
     * 飞船类型
     */
    int SHIP = 1;

    /**
     * 导弹类型
     */
    int MISSILE = 3;

    /**
     * 英雄类型
     */
    int HERO = 5;
    /**
     * 每周最大时常
     */
    int ONLINE_TIME_MAX=60*60*8;
    /**
     * 每周最大有效时常
     */
    int VALID_TIME_MAX=60*60*8;
    /**
     * 每周最大单词学习数
     */
    int WORD_MAX=100;
    /**
     * 每周最大分数
     */
    int POINT_MAX =2000;

}

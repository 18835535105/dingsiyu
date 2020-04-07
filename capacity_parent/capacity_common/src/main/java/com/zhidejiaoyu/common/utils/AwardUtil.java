package com.zhidejiaoyu.common.utils;

import java.util.HashMap;
import java.util.Map;

public class AwardUtil {

    private static final String[] AWARD_NAME_ARR = {
            "皮肤碎片", "金币x2", "钻石x5", "美丽印记-碎片", "觉醒手套-戒指", "金币x5", "钻石x10",
            "温柔百合", "紫露凝香", "粉色回忆", "阡陌红尘", "梦幻流苏", "生如夏花", "默默相向", "水仙罗裙", "郁金含香", "紫若罗兰",
            "赤焰的力量", "雷霆惊日", "纳罗时空", "邪灵之幻魄", "呐迦鳞甲", "暗弑光影", "复仇者盾牌", "恶灵之光", "火令法杖", "黑翼暗影",
            "斑驳了流年", "浮光掠影", "海滨夏日", "浩瀚宇宙", "金属酷炫", "梦幻光影", "山水涧影", "手绘梦想", "水果派", "烟雨故人来"
    };

    /**
     * 名称对应的数组索引
     */
    private static final Map<String, Integer> NAME_TO_INDEX = new HashMap<>(16);

    /**
     * 索引对应的加成
     */
    private static final Map<Integer, Integer> INDEX_TO_BONUS = new HashMap<>(16);

    /**
     * 奖品对应的加成
     */
    private static final Map<String, Integer> NAME_TO_BONUS = new HashMap<>(16);

    static {
        NAME_TO_INDEX.put("皮肤碎片", 1);
        NAME_TO_INDEX.put("金币", 2);
        NAME_TO_INDEX.put("钻石", 3);
        NAME_TO_INDEX.put("美丽印记-碎片", 4);
        NAME_TO_INDEX.put("觉醒手套-戒指", 5);
        NAME_TO_INDEX.put("金币x10", 6);
        NAME_TO_INDEX.put("钻石x10", 7);
        NAME_TO_INDEX.put("温柔百合", 8);
        NAME_TO_INDEX.put("紫露凝香", 9);
        NAME_TO_INDEX.put("粉色回忆", 10);
        NAME_TO_INDEX.put("阡陌红尘", 11);
        NAME_TO_INDEX.put("梦幻流苏", 12);
        NAME_TO_INDEX.put("生如夏花", 13);
        NAME_TO_INDEX.put("默默相向", 14);
        NAME_TO_INDEX.put("水仙罗裙", 15);
        NAME_TO_INDEX.put("郁金含香", 16);
        NAME_TO_INDEX.put("紫若罗兰", 17);
        NAME_TO_INDEX.put("赤焰的力量", 18);
        NAME_TO_INDEX.put("雷霆惊日", 19);
        NAME_TO_INDEX.put("纳罗时空", 20);
        NAME_TO_INDEX.put("邪灵之幻魄", 21);
        NAME_TO_INDEX.put("呐迦鳞甲", 22);
        NAME_TO_INDEX.put("暗弑光影", 23);
        NAME_TO_INDEX.put("复仇者盾牌", 24);
        NAME_TO_INDEX.put("恶灵之光", 25);
        NAME_TO_INDEX.put("火令法杖", 26);
        NAME_TO_INDEX.put("黑翼暗影", 27);
        NAME_TO_INDEX.put("斑驳了流年", 28);
        NAME_TO_INDEX.put("浮光掠影", 29);
        NAME_TO_INDEX.put("海滨夏日", 30);
        NAME_TO_INDEX.put("浩瀚宇宙", 31);
        NAME_TO_INDEX.put("金属酷炫", 32);
        NAME_TO_INDEX.put("梦幻光影", 33);
        NAME_TO_INDEX.put("山水涧影", 34);
        NAME_TO_INDEX.put("手绘梦想", 35);
        NAME_TO_INDEX.put("水果派", 36);
        NAME_TO_INDEX.put("烟雨故人来", 37);

        INDEX_TO_BONUS.put(8, 20);
        INDEX_TO_BONUS.put(9, 22);
        INDEX_TO_BONUS.put(10, 17);
        INDEX_TO_BONUS.put(11, 19);
        INDEX_TO_BONUS.put(12, 21);
        INDEX_TO_BONUS.put(13, 18);
        INDEX_TO_BONUS.put(14, 19);
        INDEX_TO_BONUS.put(15, 20);
        INDEX_TO_BONUS.put(16, 17);
        INDEX_TO_BONUS.put(17, 22);
        INDEX_TO_BONUS.put(18, 20);
        INDEX_TO_BONUS.put(19, 17);
        INDEX_TO_BONUS.put(20, 18);
        INDEX_TO_BONUS.put(21, 19);
        INDEX_TO_BONUS.put(22, 21);
        INDEX_TO_BONUS.put(23, 18);
        INDEX_TO_BONUS.put(24, 19);
        INDEX_TO_BONUS.put(25, 20);
        INDEX_TO_BONUS.put(26, 22);
        INDEX_TO_BONUS.put(27, 19);

        int length = AWARD_NAME_ARR.length;
        for (int i = 0; i < length; i++) {
            if (INDEX_TO_BONUS.containsKey(i)) {
                NAME_TO_BONUS.put(AWARD_NAME_ARR[i], INDEX_TO_BONUS.get(i));
            }
        }
    }

    /**
     * 将清学版 抽奖 数字转化为产品
     *
     * @param type
     * @return
     */
    public static String getAward(Integer type) {
        if (type <= AWARD_NAME_ARR.length) {
            return AWARD_NAME_ARR[type - 1];
        }
        return null;
    }

    /**
     * 根据奖品名获取奖品在数据总的索引
     *
     * @param name
     * @return
     */
    public static Object getIndexByName(String name) {
        return NAME_TO_INDEX.get(name);
    }

    /**
     * 通过索引获取指定奖品的加成
     *
     * @param index
     * @return
     */
    public static Object getBonusByIndex(Integer index) {
        return INDEX_TO_BONUS.get(index);
    }

    /**
     * 通过奖品名称获取其对应的加成信息
     *
     * @param name
     * @return
     */
    public static Integer getBonusByName(String name) {
        return NAME_TO_BONUS.get(name);
    }


}

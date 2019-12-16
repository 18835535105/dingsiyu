package com.zhidejiaoyu.common.utils;

import java.util.HashMap;
import java.util.Map;

public class AwardUtil {

    /**
     * 将清学版 抽奖 数字转化为产品
     *
     * @param type
     * @return
     */
    public static String getAward(Integer type) {
        String[] awardStr = {
                "皮肤碎片", "金币x2", "钻石x5", "美丽印记-碎片", "觉醒手套-戒指", "金币x5", "钻石x10",
                "温柔百合", "紫露凝香", "粉色回忆", "阡陌红尘", "梦幻流苏", "生如夏花", "默默相向", "水仙罗裙", "郁金含香", "紫若罗兰",
                "赤焰的力量", "雷霆惊日", "纳罗时空", "邪灵之幻魄", "呐迦鳞甲", "暗弑光影", "复仇者盾牌", "恶灵之光", "火令法杖", "黑翼暗影",
                "斑驳了流年", "浮光掠影", "海滨夏日", "浩瀚宇宙", "金属酷炫", "梦幻光影", "山水涧影", "手绘梦想", "水果派", "烟雨故人来"
        };
        if (type <= awardStr.length) {
            return awardStr[type - 1];
        }
        return null;
    }

    public static Object getMaps(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("皮肤碎片", 1);
        map.put("金币", 2);
        map.put("钻石", 3);
        map.put("美丽印记-碎片", 4);
        map.put("觉醒手套-戒指", 5);
        map.put("金币x10", 6);
        map.put("钻石x10", 7);
        map.put("温柔百合", 8);
        map.put("紫露凝香", 9);
        map.put("粉色回忆", 10);
        map.put("阡陌红尘", 11);
        map.put("梦幻流苏", 12);
        map.put("生如夏花", 13);
        map.put("默默相向", 14);
        map.put("水仙罗裙", 15);
        map.put("郁金含香", 16);
        map.put("紫若罗兰", 17);
        map.put("赤焰的力量", 18);
        map.put("雷霆惊日", 19);
        map.put("纳罗时空", 20);
        map.put("邪灵之幻魄", 21);
        map.put("呐迦鳞甲", 22);
        map.put("暗弑光影", 23);
        map.put("复仇者盾牌", 24);
        map.put("恶灵之光", 25);
        map.put("火令法杖", 26);
        map.put("黑翼暗影", 27);
        map.put("斑驳了流年", 28);
        map.put("浮光掠影", 29);
        map.put("海滨夏日", 30);
        map.put("浩瀚宇宙", 31);
        map.put("金属酷炫", 32);
        map.put("梦幻光影", 33);
        map.put("山水涧影", 34);
        map.put("手绘梦想", 35);
        map.put("水果派", 36);
        map.put("烟雨故人来", 37);
        return map.get(name);
    }

    public static Object getNumber(Integer number) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(8, 20);
        map.put(9, 22);
        map.put(10, 17);
        map.put(11, 19);
        map.put(12, 21);
        map.put(13, 18);
        map.put(14, 19);
        map.put(15, 20);
        map.put(16, 17);
        map.put(17, 22);
        map.put(18, 20);
        map.put(19, 17);
        map.put(20, 18);
        map.put(21, 19);
        map.put(22, 21);
        map.put(23, 18);
        map.put(24, 19);
        map.put(25, 20);
        map.put(26, 22);
        map.put(27, 19);
        return map.get(number);
    }


}

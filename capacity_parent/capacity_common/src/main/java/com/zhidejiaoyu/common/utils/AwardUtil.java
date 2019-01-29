package com.zhidejiaoyu.common.utils;

import java.util.HashMap;
import java.util.Map;

public class AwardUtil {

    /**
     * 将清学版 抽奖 数字转化为产品
     * @param type
     * @return
     */
    public static String getAward(Integer type){
        String[] awardStr={
               "皮肤碎片","金币x2","钻石x10","美丽印记-碎片","觉醒手套-戒指","金币x10","钻石x20",
                "温柔百合","紫露凝香","粉色回忆","阡陌红尘","梦幻流苏","生如夏花","默默相向","水仙罗裙","郁金含香","紫若罗兰",
                "力量","时间","空间","灵魂","现实","心灵","能量","欲望","光明","黑暗",
                "斑驳了流年","浮光掠影","海滨夏日","浩瀚宇宙","金属酷炫","梦幻光影","山水涧影","手绘梦想","水果派","烟雨故人来"
        };
        if(type<=awardStr.length){
            return awardStr[type - 1];
        }
        return null;
    }

    public static Object getMaps(String name){
        Map<String,Object> map=new HashMap<>();
        map.put("皮肤碎片",1);
        map.put("金币",2);
        map.put("钻石",3);
        map.put("美丽印记-碎片",4);
        map.put("觉醒手套-戒指",5);
        map.put("金币x10",6);
        map.put("钻石x10",7);
        map.put("温柔百合",8);
        map.put("紫露凝香",9);
        map.put("粉色回忆",10);
        map.put("阡陌红尘",11);
        map.put("梦幻流苏",12);
        map.put("生如夏花",13);
        map.put("默默相向",14);
        map.put("水仙罗裙",15);
        map.put("郁金含香",16);
        map.put("紫若罗兰",17);
        map.put("力量",18);
        map.put("时间",19);
        map.put("空间",20);
        map.put("灵魂",21);
        map.put("现实",22);
        map.put("心灵",23);
        map.put("能量",24);
        map.put("欲望",25);
        map.put("光明",26);
        map.put("黑暗",27);
        map.put("斑驳了流年",28);
        map.put("浮光掠影",29);
        map.put("海滨夏日",30);
        map.put("浩瀚宇宙",31);
        map.put("金属酷炫",32);
        map.put("梦幻光影",33);
        map.put("山水涧影",34);
        map.put("手绘梦想",35);
        map.put("水果派",36);
        map.put("烟雨故人来",37);
        return map.get(name);
    }

}

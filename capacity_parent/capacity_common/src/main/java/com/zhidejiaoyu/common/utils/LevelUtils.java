package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.common.mapper.AwardMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人学习力与战力排版
 */
public class LevelUtils {

    @Autowired
    private static AwardMapper awardMapper;

    /**
     * 获取每一个等级所能获取的战斗力
     * @param getMap
     * @return
     */
    public static Integer getStudy(Integer getMap){
        Map<Integer,Integer> map=new HashMap<>();
        map.put(1,100);
        map.put(2,120);
        map.put(3,150);
        map.put(4,200);
        map.put(5,230);
        map.put(6,270);
        map.put(7,330);
        map.put(8,370);
        map.put(9,420);
        map.put(10,490);
        map.put(11,540);
        map.put(12,600);
        map.put(13,680);
        map.put(14,740);
        map.put(15,810);
        map.put(16,900);
        map.put(17,970);
        map.put(18,1050);
        map.put(19,1150);
        map.put(20,1230);
        map.put(21,1320);
        map.put(0,0);
        return map.get(getMap);
    }


    /**
     * 获取大等级数
     * @param challLevel
     * @return
     */
    private static Integer getLevel(Integer challLevel){
        if(challLevel>=1&&challLevel<=3){
            return 1;
        }
        if(challLevel>=4&&challLevel<=6){
            return 2;
        }
        if(challLevel>=7&&challLevel<=9){
            return 3;
        }
        if(challLevel>=10&&challLevel<=12){
            return 4;
        }
        if(challLevel>=13&&challLevel<=15){
            return 5;
        }
        if(challLevel>=16&&challLevel<=18){
            return 6;
        }
        if(challLevel>=19&&challLevel<=21){
            return 7;
        }
        return 0;
    }

    /**
     * 获取小等级数
     * @param challLevel
     * @return
     */
    private static Integer getSmallLevel(Integer challLevel){
        if(challLevel>=1&&challLevel<=3){
            if(challLevel!=3){
                return 3-challLevel;
            }else{
                return 1;
            }

        }
        if(challLevel>=4&&challLevel<=6){
            if(challLevel!=6){
                return 6-challLevel;
            }else{
                return 1;
            }
        }
        if(challLevel>=7&&challLevel<=9){
            if(challLevel!=9){
                return 9-challLevel;
            }else{
                return 1;
            }
        }
        if(challLevel>=10&&challLevel<=12){
            if(challLevel!=12){
                return 12-challLevel;
            }else{
                return 1;
            }
        }
        if(challLevel>=13&&challLevel<=15){
            if(challLevel!=15){
                return 15-challLevel;
            }else{
                return 1;
            }

        }
        if(challLevel>=16&&challLevel<=18){
            if(challLevel!=18){
                return 18-challLevel;
            }else{
                return 1;
            }

        }
        if(challLevel>=19&&challLevel<=21){
            if(challLevel!=21){
                return 21-challLevel;
            }else{
                return 1;
            }

        }
        return 0;
    }

    /**
     * 胜局加成比例
     * @param level
     * @return
     */
    private static Double getProportion(Integer level){
       if(level==1){
            return 0.02;
       }
        if(level==2){
            return 0.04;
        }
        if(level==3){
            return 0.1;
        }
        if(level==4){
            return 0.2;
        }
        if(level==5){
            return 0.45;
        }
        if(level>=6){
            return 1.0;
        }
        return 0.0;
    }
    /**
     * 平局加成比例
     * @param level
     * @return
     */
    private static Double getDraw(Integer level){
        if(level==1){
            return 0.01;
        }
        if(level==2){
            return 0.02;
        }
        if(level==3){
            return 0.05;
        }
        if(level==4){
            return 0.1;
        }
        if(level==5){
            return 0.2;
        }
        if(level>=6){
            return 0.5;
        }
        return 0.0;
    }

    private static Double getAward(Long studentId){
        Double isDouble=0.0;
        List<Map<String, Object>> maps = awardMapper.selAwardCountByStudentId(studentId);
        for(Map<String,Object> map:maps){
            String name = (String)map.get("name");
            if(name.equals("LV1")){
                Integer number = (Integer)map.get("number");
                isDouble+= number*0.01;
            }
            if(name.equals("LV2")){
                Integer number = (Integer)map.get("number");
                isDouble+= number*0.05;
            }
            if(name.equals("LV3")){
                Integer number = (Integer)map.get("number");
                isDouble+= number*0.1;
            }
            if(name.equals("LV4")){
                Integer number = (Integer)map.get("number");
                isDouble+= number*0.3;
            }
            if(name.equals("LV5")){
                Integer number = (Integer)map.get("number");
                isDouble+= number*0.5;
            }
        }
        return isDouble;
    }

    /**
     * 获取加成
     * @param challLevel 挑战者等级
     * @param beChallLevel 被挑战者等级
     * @return
     */
    public static Map<String,Double> getAddition(Integer challLevel,Integer beChallLevel,Integer type,Long studentId,Map<String,Double> map){
        if(beChallLevel-challLevel>0){
            Integer challenge = getLevel(challLevel);
            Integer beChallenge = getLevel(challLevel);
            Integer level=0;
            if(beChallenge-challenge>0){
                level=beChallenge-challenge;
                level+=getSmallLevel(challLevel);
            }else{
                level=beChallLevel-challLevel;
            }
            if(type ==1){
                map.put("award",getProportion(level));

            }else{
                map.put("award",getDraw(level));
            }
            map.put("level",getAward(studentId));
        }else{
            map.put("level",0.0);
            map.put("award",0.0);
        }
        return map;
    }





}

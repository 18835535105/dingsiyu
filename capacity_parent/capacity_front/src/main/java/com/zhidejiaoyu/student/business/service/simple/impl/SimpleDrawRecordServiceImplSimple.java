package com.zhidejiaoyu.student.business.service.simple.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.AwardUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleConsumeServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimpleDrawRecordServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimpleStudentSkinServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimpleSyntheticRewardsListServiceSimple;
import com.zhidejiaoyu.student.common.redis.AwardRedisOpt;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 抽奖记录表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Service
@Log4j
public class SimpleDrawRecordServiceImplSimple extends SimpleBaseServiceImpl<SimpleDrawRecordMapper, DrawRecord> implements SimpleDrawRecordServiceSimple {

    @Autowired
    private SimpleDrawRecordMapper simpleDrawRecordMapper;
    @Autowired
    private SimpleExhumationServiceImplSimple exhumationService;
    @Autowired
    private SimpleSyntheticRewardsListServiceSimple synService;
    @Autowired
    private SimpleStudentSkinServiceSimple studentSkinService;
    @Autowired
    private SimpleConsumeServiceSimple consumeService;
    @Autowired
    private SimpleSyntheticRewardsListMapper synMapper;
    @Autowired
    private SimpleExhumationMapper simpleExhumationMapper;
    @Autowired
    private SimpleConsumeMapper simpleConsumeMapper;
    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Resource
    private AwardRedisOpt awardRedisOpt;


    @Override
    @GoldChangeAnnotation
    public int[] addAward(HttpSession session, Integer type, String explain, String imgUrl) {
        //获取学生信息
        Student student = getStudent(session);
        Date date = new Date();
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        String format = simple.format(date);
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", student.getId());
        map.put("time", format);
        int[] resultInt = new int[2];
        //判断是否为一天第一次抽奖
        Integer integer = simpleDrawRecordMapper.selAwardNow(map);
        if (integer > 0) {
            if (student.getEnergy() - 5 < 0) {
                resultInt[0] = 3;
                return resultInt;
            }
            //将能量重新存入student中
            student.setEnergy(student.getEnergy() - 5);
            simpleStudentMapper.updateByPrimaryKey(student);
            //讲student重新放入session中
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }
        int studentId = Integer.parseInt(student.getId() + "");
        //将抽奖数据储存到DrawRecord表中
        String name;
        if (type == 0) {
            name = "名言";
        } else {
            name = AwardUtil.getAward(type);
        }
        DrawRecord draw = getDraw(name, date, explain, studentId);
        Integer insert = simpleDrawRecordMapper.insert(draw);
        if (insert > 0) {
            if (type == 0) {
                resultInt[0] = 1;
                return resultInt;
            }
            Integer size;
            //获取当前抽取的奖品是今天第多少位获取的
            Random random = new Random();
            // 第多少位抽到奖励
            Integer returnSize = 0;
            if (type >= 1 && type <= 7) {
                size = simpleDrawRecordMapper.selDrawSize(name, format);
                // 当天第一位抽奖的时候第 xx 位获得奖品初始值
                if (size == 1) {
                    // 初始值
                    final int[] init = {1750, 1236, 1236, 1750, 1750, 1236, 1236};
                    returnSize = init[type - 1];
                } else {
                    // 当天不是第一位抽奖人，在原奖品类型基础上加上随机数
                    returnSize = awardRedisOpt.selDrawRecordIndex(name) + random.nextInt(100);
                }
                awardRedisOpt.addDrawRecordIndex(name, returnSize);
            } else if (type >= 8 && type <= 17) {
                List<String> names = new ArrayList<>(10);
                for (int i = 8; i <= 17; i++) {
                    names.add(AwardUtil.getAward(i));
                }
                size = simpleDrawRecordMapper.selDrawSizes(names, date);
                if (size == 1) {
                    returnSize = 87;
                } else {
                    returnSize = awardRedisOpt.selDrawRecordIndex("美丽印记") + random.nextInt(100);
                }
                awardRedisOpt.addDrawRecordIndex("美丽印记", returnSize);
            } else if (type >= 18 && type <= 27) {
                List<String> names = new ArrayList<>(10);
                for (int i = 18; i <= 27; i++) {
                    names.add(AwardUtil.getAward(i));
                }
                size = simpleDrawRecordMapper.selDrawSizes(names, date);
                if (size == 1) {
                    returnSize = 87;
                    awardRedisOpt.addDrawRecordIndex("觉醒手套", returnSize);
                } else {
                    returnSize = awardRedisOpt.selDrawRecordIndex("觉醒手套") + random.nextInt(100);
                    awardRedisOpt.addDrawRecordIndex("觉醒手套", returnSize);
                }
            } else if (type >= 28 && type <= 37) {
                List<String> names = new ArrayList<>(10);
                for (int i = 28; i <= 37; i++) {
                    names.add(AwardUtil.getAward(i));
                }
                size = simpleDrawRecordMapper.selDrawSizes(names, date);
                if (size == 1) {
                    returnSize = 103;
                    awardRedisOpt.addDrawRecordIndex("皮肤", returnSize);
                } else {
                    returnSize = awardRedisOpt.selDrawRecordIndex("皮肤") + random.nextInt(100);
                    awardRedisOpt.addDrawRecordIndex("皮肤", returnSize);
                }
            }
            resultInt[1] = returnSize;
            if (type == 1 || type == 4 || type == 5) {
                //添加碎片
                int x;
                if (type == 1) {
                    x = 3;
                } else if (type == 4) {
                    x = 2;
                } else {
                    x = 1;
                }
                resultInt[0] = exhumationService.addExhumation(name, imgUrl, x, date, studentId, null);
                return resultInt;
            } else if (type >= 8 && type <= 17) {
                //添加印记
                resultInt[0] = synService.addSynthetic(name, imgUrl, 2, studentId, 1);
                return resultInt;
            } else if (type >= 18 && type <= 27) {
                //添加手套
                resultInt[0] = synService.addSynthetic(name, imgUrl, 1, studentId, 1);
                return resultInt;
            } else if (type >= 28) {
                //添加皮肤
                int i = synService.addSynthetic(name, imgUrl, 3, studentId, 1);
                if (i > 0) {
                    //将皮肤放入皮肤表中
                    resultInt[0] = studentSkinService.addStudentSkin(name, studentId, null, imgUrl);
                    return resultInt;
                } else {
                    return resultInt;
                }
            } else {
                //添加金币或钻石
                if (type == 2 || type == 6) {
                    //添加金币
                    //金币信息放入runlog中
                    if (type == 2) {
                        consumeService.addConsume(1, 2, session);
                        super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在抽獎中奖励#" + 2 + "#枚金币");
                    } else {
                        consumeService.addConsume(1, 5, session);
                        super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在抽獎中奖励#" + 5 + "#枚金币");
                    }
                    resultInt[0] = 1;
                    return resultInt;
                } else if (type == 3 || type == 7) {
                    //添加钻石
                    if (type == 3) {
                        consumeService.addConsume(2, 5, session);
                    } else {
                        consumeService.addConsume(2, 10, session);
                    }
                    resultInt[0] = 1;
                    return resultInt;
                }
            }
        }
        return resultInt;
    }

    /**
     * 查询抽奖记录
     *
     * @param session
     * @param page
     * @param rows
     * @return
     */
    @Override
    public ServerResponse<Object> selDrawRecordByStudentId(HttpSession session, Integer page, Integer rows) {
        //获取学生信息
        Student student = getStudent(session);
        //创建查询条件
        HashMap<String, Object> map = new HashMap<>();
        map.put("studentId", student.getId());
        map.put("start", (page - 1) > 0 ? (page - 1) * rows : 0);
        map.put("end", rows);
        //获取抽奖数量
        Integer integer = simpleDrawRecordMapper.selNumber(student.getId().intValue());
        //获取抽奖信息
        List<DrawRecord> drawRecords = simpleDrawRecordMapper.selByStudentId(map);
        List<DrawRecord> resultRecords = new ArrayList<>();
        for (DrawRecord drawRecord : drawRecords) {
            if (drawRecord.getExplain().contains("惊喜奖励")) {
                drawRecord.setExplain("惊喜奖励");
            }
            if (drawRecord.getExplain().contains("男生") || drawRecord.getExplain().contains("女生")) {

                drawRecord.setExplain(drawRecord.getExplain().substring(0, drawRecord.getExplain().length() - 4));
            }
            resultRecords.add(drawRecord);
        }
        Map<String, Object> maps = new HashMap<>();
        if (integer == null) {
            maps.put("countPage", 0);
        } else {
            maps.put("countPage", integer % rows > 0 ? (integer / rows) + 1 : (integer / rows));
        }

        maps.put("drawRecords", resultRecords);
        return ServerResponse.createBySuccess(maps);
    }

    /**
     * 查询所有抽奖记录和信息
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> selAllRecord(HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>();
        //获取学生使用,未使用的手套,印记碎片
        List<Exhumation> exhumations = simpleExhumationMapper.selExhumation(student.getId().intValue());
        //获取已使用戒指,花瓣 碎片数量
        List<Map<String, Object>> exhumationss = simpleExhumationMapper.selExhumationByStudentId(student.getId());
        Map<String, Object> flowOrglove = new HashMap<>();
        if (exhumationss != null && exhumationss.size() > 0) {
            flowOrglove.put("total", exhumations.size() + ((Long) (exhumationss.get(0).get("count"))));
            flowOrglove.put("have", exhumations.size());
            flowOrglove.put("use", exhumationss.get(0).get("count"));
        } else {
            flowOrglove.put("total", exhumations.size());
            flowOrglove.put("have", exhumations.size());
            flowOrglove.put("use", 0);
        }
        flowOrglove.put("isEnter", false);
        map.put("flowOrGlove", flowOrglove);
        //获取学生未使用的皮肤碎片
        int exhumations1 = simpleExhumationMapper.selExhumationByStudentIdTOSkin(student.getId());
        //获取学生使用的皮肤碎片
        List<Map<String, Object>> maps = simpleExhumationMapper.selExhumationSkinByStudentId(student.getId());
        Map<String, Object> skin = new HashMap<>();
        if (maps != null && maps.size() > 0) {
            int have = 0;
            for (Map<String, Object> mapsss : maps) {
                have += (Long) (mapsss.get("count"));
            }
            skin.put("total", exhumations1 + have);
            skin.put("have", exhumations1);
            skin.put("use", have);
        } else {
            skin.put("total", exhumations1);
            skin.put("have", exhumations1);
            skin.put("use", 0);
        }
        skin.put("isEnter", false);
        map.put("skin", skin);

        //获取学生获取金币和消耗金币
        Consume consume = new Consume();
        consume.setState(1);
        consume.setStudentId(student.getId().intValue());
        consume.setType(1);

        //获取共拥有金币
        Integer allGold = simpleConsumeMapper.getAllGoladAndDiamond(consume);
        //获取共使用金币
        consume.setState(2);
        Integer downGold = simpleConsumeMapper.getAllGoladAndDiamond(consume);
        Map<String, Object> gold = new HashMap<>();
        gold.put("total", student.getSystemGold().intValue() + student.getOfflineGold().intValue());
        gold.put("have", student.getSystemGold().intValue());
        gold.put("use", student.getOfflineGold().intValue());
        if (allGold != null) {
            gold.put("luckDrawAll", allGold);
        } else {
            gold.put("luckDrawAll", 0);
        }

        if (student.getOfflineGold() != null && allGold != null) {
            if (allGold <= student.getOfflineGold()) {
                gold.put("luckDrawUse", allGold);
            } else {
                gold.put("luckDrawUse", student.getOfflineGold().intValue());
            }
        } else {
            gold.put("luckDrawUse", 0);
        }
        gold.put("isEnter", false);
        map.put("gold", gold);

        //获取学生获取钻石和消耗的钻石
        consume.setType(2);
        //已经使用的钻石
        Integer downDiamond = simpleConsumeMapper.getAllGoladAndDiamond(consume);
        consume.setState(1);
        //全部获取的钻石
        Integer allDiamond = simpleConsumeMapper.getAllGoladAndDiamond(consume);
        ;
        Map<String, Object> diamond = new HashMap<>();
        int total = 0;
        if (allDiamond != null) {
            if (downDiamond != null) {
                diamond.put("have", allDiamond - downDiamond);
            } else {
                diamond.put("have", allDiamond);
            }
            total += allDiamond;
        } else {
            diamond.put("have", 0);
        }
        if (downDiamond == null) {
            diamond.put("use", 0);
        } else {
            diamond.put("use", downDiamond);
        }
        diamond.put("total", total);
        diamond.put("isEnter", false);
        map.put("diamond", diamond);

        //获取全部惊喜皮肤
        List<Map<String, Object>> maps1 = synMapper.selSurprised(student.getId().intValue());
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> mapss : maps1) {
            mapss.put("isEnter", false);
            mapss.put("img_url", GetOssFile.getPublicObjectUrl(mapss.get("img_url").toString()));
            resultList.add(mapss);
        }
        map.put("surprised", resultList);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 查看抽奖状态
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> selAwardNow(HttpSession session) {
        //获取学生信息
        Student student = getStudent(session);
        //创建查找条件
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //添加搜索条件
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", student.getId());
        map.put("time", time);
        //获取当前日抽奖数量
        Integer integer = simpleDrawRecordMapper.selAwardNow(map);
        Map<String, Object> sel = new HashMap<>();
        //获取能量数量
        sel.put("energy", student.getEnergy());
        //判断是否为当天第一次抽奖
        if (integer > 0) {
            sel.put("first", false);
        } else {
            sel.put("first", true);
        }
        return ServerResponse.createBySuccess(sel);
    }

    private DrawRecord getDraw(String name, Date date, String explain, Integer studentId) {
        DrawRecord drawRecord = new DrawRecord();
        drawRecord.setExplain(explain);
        drawRecord.setCreateTime(date);
        drawRecord.setName(name);
        drawRecord.setStudentId(studentId);
        return drawRecord;
    }


}

package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.SimpleExhumationMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentSkinMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleSyntheticRewardsListMapper;
import com.zhidejiaoyu.common.pojo.Exhumation;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentSkin;
import com.zhidejiaoyu.common.pojo.SyntheticRewardsList;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.SimpleAwardUtil;
import com.zhidejiaoyu.student.service.simple.SimpleExhumationServiceSimple;
import com.zhidejiaoyu.student.service.simple.SimpleSyntheticRewardsListServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * 合成奖励表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Service
public class SimpleSyntheticRewardsListServiceImplSimple extends SimpleBaseServiceImpl<SimpleSyntheticRewardsListMapper, SyntheticRewardsList> implements SimpleSyntheticRewardsListServiceSimple {

    @Autowired
    private SimpleSyntheticRewardsListMapper simpleSyntheticRewardsListMapper;

    @Autowired
    private SimpleExhumationServiceSimple exhumationService;

    @Autowired
    private SimpleExhumationMapper simpleExhumationMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleStudentSkinMapper simpleStudentSkinMapper;

    /**
     * 添加合成奖励
     *
     * @param name
     * @param imgUrl
     * @param type
     * @param studentId
     * @return
     */
    @Override
    public int addSynthetic(String name, String imgUrl, Integer type, Integer studentId, Integer model) {
        return simpleSyntheticRewardsListMapper.insert(syntheticRewardsList(name, imgUrl, type, studentId, new Date(), model));
    }

    /**
     * 查询碎片数量和手套
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getGloveOrFlower(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        Student student = getStudent(session);
        Integer sex = student.getSex();
        Integer studentId = Integer.parseInt(student.getId() + "");
        //获取所有未使用碎片
        List<Exhumation> exhumations = exhumationService.selExhumationByStudentId(studentId);
        //获取正在使用的手套或者印记
        Map<String, Object> map2 = new HashMap<>();
        map2.put("studentId", student.getId());
        map2.put("type", student.getSex());
        List<Map<String, Object>> maps = simpleExhumationMapper.selExhumationByStudentId(studentId.longValue());
        Map<Object, Object> retrun = new HashMap<>();
        Map<Integer, Object> maps1 = new HashMap<>();
        for (Map<String, Object> ma : maps) {
            Integer finalName = (Integer) SimpleAwardUtil.getMaps((String) ma.get("finalName"));
            maps1.put(finalName, ma);
        }
        List<SyntheticRewardsList> gloveOrFlower = simpleSyntheticRewardsListMapper.getGloveOrFlower(studentId);
        if (sex == 2) {
            for (int i = 8; i <= 17; i++) {
                Object o = maps1.get(i);
                boolean isTrue=true;
                for(SyntheticRewardsList syn:gloveOrFlower){
                    if(SimpleAwardUtil.getAward(i).equals(syn.getName())){
                        isTrue=false;
                    }
                }
                if(isTrue){
                    if (o == null) {
                        Map<String, Object> setMap = new HashMap<>();
                        setMap.put("finalName", SimpleAwardUtil.getAward(i));
                        setMap.put("number", SimpleAwardUtil.getNumber(i));
                        setMap.put("count", 0);
                        setMap.put("finalNameInteger", i);
                        setMap.put("isEnter", false);
                        retrun.put(i, setMap);
                    } else {
                        Map<String, Object> setMap = new HashMap<>();
                        HashMap o1 = (HashMap) maps1.get(i);
                        setMap.put("finalName", o1.get("finalName"));
                        setMap.put("number", SimpleAwardUtil.getNumber(i));
                        setMap.put("count", o1.get("count"));
                        setMap.put("finalNameInteger", i);
                        setMap.put("isEnter", false);
                        retrun.put(i, setMap);
                    }
                }else{
                    Map<String, Object> setMap = new HashMap<>();
                    setMap.put("finalName", SimpleAwardUtil.getAward(i));
                    setMap.put("number", SimpleAwardUtil.getNumber(i));
                    setMap.put("count", 6);
                    setMap.put("finalNameInteger", i);
                    setMap.put("isEnter", false);
                    retrun.put(i, setMap);
                }
            }
        } else {
            for (int i = 18; i <= 27; i++) {
                Object o = maps1.get(i);
                boolean isTrue=true;
                for(SyntheticRewardsList syn:gloveOrFlower){
                    if(SimpleAwardUtil.getAward(i).equals(syn.getName())){
                        isTrue=false;
                    }
                }
                if(isTrue){
                    if (o == null) {
                        Map<String, Object> setMap = new HashMap<>();
                        setMap.put("finalName", SimpleAwardUtil.getAward(i));
                        setMap.put("number", SimpleAwardUtil.getNumber(i));
                        setMap.put("count", 0);
                        setMap.put("finalNameInteger", i);
                        setMap.put("isEnter", false);
                        retrun.put(i, setMap);
                    } else {
                        Map<String, Object> setMap = new HashMap<>();
                        HashMap o1 = (HashMap) maps1.get(i);
                        setMap.put("finalName", o1.get("finalName"));
                        setMap.put("number", SimpleAwardUtil.getNumber(i));
                        setMap.put("count", o1.get("count"));
                        setMap.put("finalNameInteger", i);
                        setMap.put("isEnter", false);
                        retrun.put(i, setMap);
                    }
                }else{
                    Map<String, Object> setMap = new HashMap<>();
                    setMap.put("finalName", SimpleAwardUtil.getAward(i));
                    setMap.put("number", SimpleAwardUtil.getNumber(i));
                    setMap.put("count", 5);
                    setMap.put("finalNameInteger", i);
                    setMap.put("isEnter", false);
                    retrun.put(i, setMap);
                }

            }
        }
        map.put("exhumations", exhumations.size());
        map.put("SyntheticReward", retrun);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 手套印记显示
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> selSyntheticList(HttpSession session) {
        //获取学生session
        Student student =getStudent(session);
        //获取全部的手套印记`
        HashMap<String, Object> mapss = new HashMap<>();
        mapss.put("studentId", student.getId());
        mapss.put("type", student.getSex());
        List<HashMap<String, Object>> hashMaps = simpleSyntheticRewardsListMapper.selListMap(mapss);
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (HashMap<String, Object> map : hashMaps) {
            Date endTime = (Date) map.get("endTime");
            if (endTime != null) {
                if (endTime.getTime() > System.currentTimeMillis()) {
                    map.put("status", 2);
                    map.put("beUseing", true);
                    map.put("nameId", SimpleAwardUtil.getMaps((String) map.get("name")));
                    map.put("time", (endTime.getTime() - System.currentTimeMillis()) / 1000);
                    map.put("type", 1);
                    map.put("imgUrl", GetOssFile.getPublicObjectUrl(map.get("imgUrl").toString()));
                    list.add(map);
                } else {
                    map.put("status", 1);
                    Integer count = ((Long) map.get("count")).intValue();
                    if (count == 1) {
                        map.put("time", 48 + "小时0分0秒");
                    } else {
                        map.put("time", (48 * count) + "小时0分0秒");
                    }
                    map.put("imgUrl", GetOssFile.getPublicObjectUrl(map.get("imgUrl").toString()));
                    map.put("beUseing", false);
                    if (((Long) map.get("useNumber")) .equals( (Long) map.get("count"))) {
                        map.put("nameId", SimpleAwardUtil.getMaps((String) map.get("name")));
                        map.put("type", 1);
                        list.add(map);
                    }
                }
            } else {
                map.put("status", 1);
                Integer count = ((Long) map.get("count")).intValue();
                map.put("imgUrl", GetOssFile.getPublicObjectUrl(map.get("imgUrl").toString()));
                if (count == 1) {
                    map.put("time", 48 + "小时0分0秒");
                } else {
                    map.put("time", (48 * count) + "小时0分0秒");
                }
                map.put("beUseing", false);
                map.put("nameId", SimpleAwardUtil.getMaps((String) map.get("name")));
                map.put("type", 1);
                list.add(map);
            }
        }
        return ServerResponse.createBySuccess("200", list);
    }


    /**
     * 使用手套或印记
     *
     * @param session
     * @param nameInteger
     * @return
     */
    @Override
    public ServerResponse<Object> updSyntheticList(HttpSession session, Integer nameInteger) {
        //获取学生session
        Student student = getStudent(session);
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                return ServerResponse.createByError();
            }
        }
        String name = SimpleAwardUtil.getAward(nameInteger);
        //根据学生id和name获取手套和印记的数
        SyntheticRewardsList syntheticRewardsList = new SyntheticRewardsList();
        syntheticRewardsList.setStudentId(student.getId().intValue());
        syntheticRewardsList.setName(name);
        Integer count = simpleSyntheticRewardsListMapper.selCountByStudentIdAndName(syntheticRewardsList);
        //判断使用时间
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + (count * 2));
        //添加修改条件
        syntheticRewardsList.setUseState(1);
        syntheticRewardsList.setUseTime(date);
        Date time = calendar.getTime();
        syntheticRewardsList.setUseEndTime(time);
        //修改所有未使用的当前手套,印记
        simpleSyntheticRewardsListMapper.updUse(syntheticRewardsList);
        //修改学生信息
        student.setBonusExpires(time);

        simpleStudentMapper.updateByPrimaryKeySelective(student);
        HashMap mapsss = new HashMap();
        mapsss.put("studentId", student.getId());
        mapsss.put("name", name);
        simpleExhumationMapper.updExhumationFinalNameByStudentId(mapsss);
        //查找修改后的信息
        Student student1 = simpleStudentMapper.selectByPrimaryKey(student.getId());
        //将修改后的信息放入session中
        session.setAttribute(UserConstant.CURRENT_STUDENT, student1);
        //正常结束返回
        return ServerResponse.createBySuccess(200, "使用成功");
    }

    /**
     * 获取手套、印记或皮肤信息
     *
     * @param session
     * @param nameInteger 1,手套印记   2,皮肤
     * @param type
     * @return
     */
    @Override
    public ServerResponse<Object> getMessage(HttpSession session, Integer nameInteger, Integer type) {
        //获取学生信息
        Student student = getStudent(session);
        //获取查找名
        String name = SimpleAwardUtil.getAward(nameInteger);
        //返回值格式
        Map<String, Object> map = new HashMap<>();
        //1为手套,印记    2为皮肤
        if (type == 1) {
            //放置搜索信息
            SyntheticRewardsList syntheticRewardsList = new SyntheticRewardsList();
            syntheticRewardsList.setStudentId(student.getId().intValue());
            syntheticRewardsList.setName(name);
            //获取手套,印记个数
            Integer count = simpleSyntheticRewardsListMapper.selCountByStudentIdAndName(syntheticRewardsList);
            //获取手套,印记信息
            List<SyntheticRewardsList> syntheticRewardsLists = simpleSyntheticRewardsListMapper.selGloveOrFlowerByStudentIdAndName(syntheticRewardsList);
            //判断是否为空
            if (syntheticRewardsLists.size() > 0) {
                //取第一个信息
                syntheticRewardsList = syntheticRewardsLists.get(0);
                //放置返回信息
                map.put("name", name);
                map.put("message", "得到的金币加成" + SimpleAwardUtil.getNumber(nameInteger) + "%");
                map.put("createName", syntheticRewardsList.getCreateTime());
                map.put("time", (48 * count) + "小时0分0秒");
                Date date = student.getBonusExpires();
                if (date != null) {
                    if (date.getTime() > System.currentTimeMillis()) {
                        map.put("state", 0);
                    } else {
                        map.put("state", 1);
                    }
                } else {
                    map.put("state", 1);
                }
            }
        } else if (type == 2) {
            //放置搜索信息
            StudentSkin studentSkin = new StudentSkin();
            studentSkin.setStudentId(student.getId().intValue());
            studentSkin.setSkinName(name);
            //搜索皮肤信息
            studentSkin = simpleStudentSkinMapper.selSkinBystudentIdAndName(studentSkin);
            //放置返回信息
            map.put("name", name);
            map.put("message", "个性装扮");
            map.put("createName", studentSkin.getCreateTime());
            map.put("state", studentSkin.getState());
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> getLucky(Integer studentId, HttpSession session) {
        Map<String, Object> useMap = new HashMap<>();
        if (studentId == null) {
            Student student = getStudent(session);
            studentId = student.getId().intValue();
            useMap.put("sex",student.getSex()==1?"男":"女");
        }else{
            Student student = simpleStudentMapper.selectById(studentId);
            useMap.put("sex",student.getSex()==1?"男":"女");
        }
        Map<String, Object> resultMap = new HashMap<>();
        //查询手套印记
        List<SyntheticRewardsList> gloveOrFlower = simpleSyntheticRewardsListMapper.getGloveOrFlower(studentId);
        SyntheticRewardsList useGloveOrFlower = simpleSyntheticRewardsListMapper.getUseGloveOrFlower(studentId);
        List<Map<String, Object>> gloveOrFlowerList = new ArrayList<>();

        for (SyntheticRewardsList synthetic : gloveOrFlower) {
            Map<String, Object> map = new HashMap<>();
            map.put("url", synthetic.getImgUrl());
            if(useGloveOrFlower!=null){
                map.put("state", true);
                map.put("time",48+"小时00分00秒");
            }else{
                SyntheticRewardsList isUse = simpleSyntheticRewardsListMapper.getIsUse(studentId, synthetic.getName());
                if(isUse!=null){
                    Integer count = simpleSyntheticRewardsListMapper.selCountByStudentIdAndName(synthetic);
                    map.put("state", false);
                    map.put("time",48*count+"小时00分00秒");
                }else{
                    map.put("state", true);
                    map.put("time",48+"小时00分00秒");
                }
            }
            map.put("syntheticInteger", SimpleAwardUtil.getMaps(synthetic.getName()));
            map.put("type", "gloveOrFlower");
            map.put("name",synthetic.getName());
            map.put("message", "得到的金币加成" + SimpleAwardUtil.getNumber(Integer.parseInt(SimpleAwardUtil.getMaps(synthetic.getName()).toString())) + "%");
            map.put("createTime", synthetic.getCreateTime());
            gloveOrFlowerList.add(map);
        }
        if (useGloveOrFlower!=null) {
            Map<String, Object> useNowMap = new HashMap<>();
            useNowMap.put("url", useGloveOrFlower.getImgUrl());
            useNowMap.put("endTime",useGloveOrFlower.getUseEndTime());
            useMap.put("gloveOrFlower", useNowMap);
        }
        resultMap.put("gloveOrFlower", gloveOrFlowerList);
        List<Map<String, Object>> skinList = new ArrayList<>();
        List<StudentSkin> studentSkins = simpleStudentSkinMapper.selSkinByStudentIdIsHave(studentId.longValue());
        for (StudentSkin studentSkin : studentSkins) {
            if (studentSkin.getState() == 1) {
                useMap.put("skin", studentSkin.getImgUrl());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("url", studentSkin.getImgUrl());
            if(studentSkin.getState() == 1){
                map.put("state", true);
            }else{
                map.put("state", false);
            }
            map.put("type", "skin");
            map.put("skinIngter", SimpleAwardUtil.getMaps(studentSkin.getSkinName()));
            map.put("id",studentSkin.getId());
            map.put("name",studentSkin.getSkinName());
            map.put("message", "个性装扮");
            map.put("createTime", studentSkin.getCreateTime());
            skinList.add(map);
        }
        resultMap.put("skin", skinList);
        resultMap.put("use",useMap);
        return ServerResponse.createBySuccess(resultMap);
    }


    private SyntheticRewardsList syntheticRewardsList(String name, String imgUrl, Integer type, Integer studentId, Date date, int model) {
        SyntheticRewardsList syn = new SyntheticRewardsList();
        syn.setImgUrl(imgUrl);
        syn.setName(name);
        syn.setStudentId(studentId);
        syn.setType(type);
        syn.setUseState(0);
        syn.setCreateTime(date);
        syn.setModel(model);
        return syn;
    }


}

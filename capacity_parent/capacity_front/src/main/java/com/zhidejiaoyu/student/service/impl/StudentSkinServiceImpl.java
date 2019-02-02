package com.zhidejiaoyu.student.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.ExhumationMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.StudentSkinMapper;
import com.zhidejiaoyu.common.pojo.Exhumation;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentSkin;
import com.zhidejiaoyu.common.utils.AwardUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ConsumeService;
import com.zhidejiaoyu.student.service.StudentSkinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * 学生皮肤表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Service
public class StudentSkinServiceImpl extends ServiceImpl<StudentSkinMapper, StudentSkin> implements StudentSkinService {

    @Autowired
    private StudentSkinMapper studentSkinMapper;
    @Autowired
    private ExhumationMapper exhumationMapper;
    @Autowired
    private ConsumeService consumeService;
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 添加皮肤
     *
     * @param name
     * @param studentId
     * @param date
     * @return
     */
    @Override
    public int addStudentSkin(String name, Integer studentId, Date date, String imgUrl) {
        StudentSkin skin = new StudentSkin();
        skin.setSkinName(name);
        skin.setStudentId(studentId);
        //查找是否已经有试用皮肤
        StudentSkin studentSkin = studentSkinMapper.selSkinBystudentIdAndName(skin);
        Date now = new Date();
        if (studentSkin == null) {
            //没有添加
            return studentSkinMapper.insert(getStudentSkin(name, studentId, date, now, imgUrl));
        } else {
            //有 修改到期时间
            studentSkin.setEndTime(null);
            studentSkin.setImgUrl(imgUrl);
            studentSkin.setCreateTime(new Date());
            Integer integer = studentSkinMapper.updUseSkin(studentSkin);
            return integer;
        }

    }


    /**
     * 钻石添加皮肤
     *
     * @param session
     * @param number
     * @param skinInteger
     * @param imgUrl
     * @return
     */
    @Override
    public ServerResponse<Object> addStudentSkinByDiamond(HttpSession session, int number, int skinInteger, String imgUrl) {
        //获取学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        //获取学生钻石数量
        Integer diamond = student.getDiamond();
        //根据皮肤编号获取皮肤名称
        String name = AwardUtil.getAward(skinInteger);
        //储存皮肤信息
        StudentSkin skin = new StudentSkin();
        skin.setSkinName(name);
        skin.setStudentId(student.getId().intValue());
        skin.setImgUrl(imgUrl);
        //查看将皮肤信息
        StudentSkin studentSkin = studentSkinMapper.selSkinBystudentIdAndName(skin);
        if (studentSkin != null) {
            return ServerResponse.createByError(300, "已有皮肤");
        }
        if (diamond < number) {
            return ServerResponse.createByError(300, "钻石数量不够");
        }

        int i = consumeService.reduceConsume(2, number, session);
        if (i > 0) {
            int result = addStudentSkin(name, student.getId().intValue(), new Date(), imgUrl);
            if (result > 0) {
                return ServerResponse.createBySuccess("添加皮肤成功");
            }
            return ServerResponse.createByError();
        }
        return ServerResponse.createByError();
    }

    /**
     * 获取皮肤和皮肤碎片
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> selSkinAndExhumation(HttpSession session) {
        //返回值格式确定
        Map<String, Object> map = new HashMap<>();
        //获取学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        //获取未使用的皮肤碎片信息
        List<Exhumation> exhumations = exhumationMapper.selExhumationByStudentIdTOSkin(student.getId());
        //储存皮肤碎片数量
        map.put("exhumations", exhumations.size());
        //每个皮肤合成使用的碎片数量
        List<Map<String, Object>> maps = exhumationMapper.selExhumationByStudentIdTOSkinState(student.getId());
        Map<Integer, Object> mapss = new HashMap<>();
        for (Map<String, Object> ma : maps) {
            Integer finalName = (Integer) AwardUtil.getMaps((String) ma.get("finalName"));
            mapss.put(finalName, ma);
        }
        //获取皮肤
        List<StudentSkin> studentSkins = studentSkinMapper.selSkinByStudentId(student.getId());
        Map<Integer, Object> map1 = new HashMap<>();
        for (StudentSkin studentSkin : studentSkins) {
            Integer finalName = (Integer) AwardUtil.getMaps(studentSkin.getSkinName());
            map1.put(finalName, true);
        }

        //查看已使用的皮肤和试用过得皮肤
        List<Map<String, Object>> maps1 = studentSkinMapper.selTrySkinAndHaveSkin(student.getId());
        Map<Object, Map> mapsss = new HashMap<>();
        for (Map<String, Object> map2 : maps1) {
            Object finalName = AwardUtil.getMaps((String) map2.get("finalName"));
            Date endTime = (Date) map2.get("endTime");
            //判断皮肤是使用的皮肤还是试用的皮肤
            if (endTime != null) {
                if (new Date().getTime() >= endTime.getTime()) {
                    //当使用时间小于现在的时间时调用
                    map2.put("use", false);
                } else {
                    //当使用时间大于现在的时间时调用
                    if ((Integer) map2.get("state") == 1) {
                        //判断当前试用皮肤是否在使用
                        map2.put("use", true);
                    } else {
                        //判断当前试用皮肤是否在使用
                        map2.put("use", false);
                    }
                }
                //试用皮肤现在为未拥有状态
                map2.put("isHave", false);
            } else {

                if ((Integer) map2.get("state") == 1) {
                    //判断当前皮肤是否在使用
                    map2.put("use", true);
                } else {
                    //判断当前皮肤是否在使用
                    map2.put("use", false);
                }
                map2.put("isHave", true);
            }
            //皮肤现在为未拥有状态
            mapsss.put(finalName, map2);
        }
        //现实的信息
        Map<Integer, Object> retrun = new HashMap<>();
        for (int i = 28; i <= 37; i++) {
            Object o = mapss.get(i);
            Map<String, Object> setMap = new HashMap<>();
            if (o == null) {
                setMap.put("finalName", AwardUtil.getAward(i));
                setMap.put("count", 0);
                setMap.put("finalNameInteger", i);
            } else {
                setMap.put("finalName", AwardUtil.getAward(i));
                HashMap o1 = (HashMap) mapss.get(i);
                setMap.put("count", o1.get("count"));
                setMap.put("finalNameInteger", i);
            }
            //判断是否可以试用
            if (map1.get(i) != null) {
                setMap.put("have", map1.get(i));
            } else {
                setMap.put("have", false);
            }
            if (mapsss.get(i) != null) {
                //判断当前皮肤是否在使用
                setMap.put("isHave", mapsss.get(i).get("isHave"));
                setMap.put("use", mapsss.get(i).get("use"));
            } else {
                //判断当前皮肤是否在使用
                setMap.put("isHave", false);
                setMap.put("use", false);
            }

            retrun.put(i, setMap);
        }
        map.put("skin", retrun);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 获取皮肤
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> selSkin(HttpSession session) {
        //获取学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        //查询学生下皮肤信息
        List<StudentSkin> studentSkins = studentSkinMapper.selSkinByStudentIdAndEndTime(student.getId());
        //返回值格式确定
        Map<String, Object> map = new HashMap<>();
        //获取钻石数量
        Integer diamond = student.getDiamond();
        //填入的批复信息格式
        List<Map<String, Object>> studentSkinss = new ArrayList<>();
        for (StudentSkin studentSkin : studentSkins) {
            //判断皮肤endtime是否为空  为空进行下一步
            if (studentSkin.getEndTime() == null) {
                Map<String, Object> maps = new HashMap<>();
                //皮肤地址
                maps.put("imgUrl", studentSkin.getImgUrl());
                //皮肤对应编号
                maps.put("nameId", AwardUtil.getMaps(studentSkin.getSkinName()));
                //皮肤使用状态
                if (studentSkin.getState() == 1) {
                    //正在使用的皮肤状态
                    maps.put("status", 2);
                } else {
                    //未使用的皮肤状态
                    maps.put("status", 1);
                }
                //type区分是皮肤还是手套印记
                maps.put("type", 2);
                studentSkinss.add(maps);
            }
        }
        //存入皮肤信息
        map.put("studentSkin", studentSkinss);
        //判断钻石数量是否小于50
        if (diamond != null) {
            if (diamond < 50) {
                return ServerResponse.createBySuccess("true", map);
            }
        }
        return ServerResponse.createBySuccess("false", map);
    }

    /**
     * 使用皮肤
     *
     * @param session
     * @param skinInteger
     * @param dateInteger
     * @return
     */
    @Override
    public ServerResponse<Object> useSkin(HttpSession session, Integer skinInteger, Integer dateInteger, Integer type, String imgUrl) {
        //获取学生对象
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String name = AwardUtil.getAward(skinInteger);
        if (type == 1) {
            //使用功能
            //获取是否有已经使用的皮肤
            if (dateInteger != 1) {
                if (student.getDiamond() == null) {
                    return ServerResponse.createByError(300, "未能使用皮肤，您的钻石数量为0，不足50");
                }
                if (student.getDiamond() < 50) {
                    return ServerResponse.createByError(300, "未能使用皮肤，您的钻石数量为" + student.getDiamond() + "，不足50");
                }
            }
            StudentSkin aLong = studentSkinMapper.selUseSkinByStudentId(student.getId());
            if (aLong != null) {
                //当学生使用皮肤不为空则修改已使用皮肤状态
                StudentSkin skin = new StudentSkin();
                skin.setId(aLong.getId());
                skin.setImgUrl(aLong.getImgUrl());
                skin.setState(0);
                skin.setEndTime(aLong.getEndTime());
                studentSkinMapper.updUseSkin(skin);
            }
            //判断是否为试用皮肤
            if (dateInteger == 1) {
                //当为试用皮肤时添加皮肤

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
                StudentSkin studentSkin = getStudentSkin(name, student.getId().intValue(), calendar.getTime(), new Date(), imgUrl);
                StudentSkin studentSkin1 = studentSkinMapper.selSkinBystudentIdAndName(studentSkin);
                if (studentSkin1 == null) {
                    studentSkin.setState(1);
                    studentSkinMapper.insert(studentSkin);
                } else {
                    studentSkin1.setState(1);
                    studentSkinMapper.updUseSkin(studentSkin1);

                }

            } else {
                //使用皮肤
                StudentSkin studentSkin = new StudentSkin();
                studentSkin.setStudentId(student.getId().intValue());
                studentSkin.setSkinName(name);
                studentSkin.setImgUrl(imgUrl);

                StudentSkin studentSkin1 = studentSkinMapper.selSkinBystudentIdAndName(studentSkin);
                studentSkin1.setState(1);
                Integer integer = studentSkinMapper.updUseSkin(studentSkin1);
                if (integer > 0) {
                    student.setDiamond(student.getDiamond() - 50);
                    consumeService.reduceConsume(2, 50, session);
                    studentMapper.updateByPrimaryKey(student);
                    session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                }
            }
        } else {
            //卸下皮肤
            StudentSkin studentSkin = new StudentSkin();
            studentSkin.setStudentId(student.getId().intValue());
            studentSkin.setSkinName(name);
            StudentSkin studentSkin1 = studentSkinMapper.selSkinBystudentIdAndName(studentSkin);
            if (studentSkin1 != null) {
                studentSkin1.setState(0);
                studentSkinMapper.updUseSkin(studentSkin1);
            }

        }

        return ServerResponse.createBySuccess();
    }

    /**
     * 获取正在使用的皮肤
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> selUseSkinById(HttpSession session) {
        //获取学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        //根据学生id获取使用的皮肤信息
        StudentSkin studentSkin = studentSkinMapper.selUseSkinByStudentId(student.getId());
        //返回数据放入map集合中
        Map<String, Object> map = new HashMap<>();
        if (studentSkin != null) {
            //判断skin的使用时间为空是已获得皮肤,不为空为未获得皮肤
            if (studentSkin.getEndTime() == null) {
                //已获得皮肤储存皮肤对应编号
                map.put("skinId", AwardUtil.getMaps(studentSkin.getSkinName()));
                //未获得皮肤判断使用时间
            } else if (studentSkin.getEndTime().getTime() > new Date().getTime()) {
                //使用时间大于当前时间储存皮肤编号
                map.put("skinId", AwardUtil.getMaps(studentSkin.getSkinName()));
            } else {
                //当前无使用皮肤返回0
                map.put("skinId", 0);
            }
        } else {
            map.put("skinId", 0);
        }

        return ServerResponse.createBySuccess("200", map);
    }


    /**
     * 生成对象
     *
     * @param name
     * @param studentId
     * @param date
     * @return
     */
    private StudentSkin getStudentSkin(String name, Integer studentId, Date date, Date now, String imgUrl) {
        StudentSkin studentSkin = new StudentSkin();
        studentSkin.setEndTime(date);
        studentSkin.setCreateTime(now);
        studentSkin.setState(0);
        studentSkin.setSkinName(name);
        studentSkin.setStudentId(studentId);
        studentSkin.setImgUrl(imgUrl);
        return studentSkin;
    }

}
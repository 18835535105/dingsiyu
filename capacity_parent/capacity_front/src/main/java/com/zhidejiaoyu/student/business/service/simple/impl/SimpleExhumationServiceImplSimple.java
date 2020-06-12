package com.zhidejiaoyu.student.business.service.simple.impl;

import com.zhidejiaoyu.common.mapper.simple.SimpleExhumationMapper;
import com.zhidejiaoyu.common.pojo.Exhumation;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.AwardUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleExhumationServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimpleStudentSkinServiceSimple;
import com.zhidejiaoyu.student.business.service.simple.SimpleSyntheticRewardsListServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽取的碎片记录表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Service
public class SimpleExhumationServiceImplSimple extends SimpleBaseServiceImpl<SimpleExhumationMapper, Exhumation> implements SimpleExhumationServiceSimple {

    @Autowired
    private SimpleExhumationMapper simpleExhumationMapper;

    @Autowired
    private SimpleSyntheticRewardsListServiceSimple synService;
    @Autowired
    private SimpleStudentSkinServiceSimple studentSkinService;

    @Override
    public int addExhumation(String name, String imgUrl, int type, Date date, Integer studentId, String finalName) {
        return simpleExhumationMapper.insert(getExhumation(name, imgUrl, type, date, studentId, finalName));
    }

    /**
     * 根据学生id查询学生下的信息
     *
     * @param studentId
     * @return
     */
    @Override
    public List<Exhumation> selExhumationByStudentId(Integer studentId) {
        return simpleExhumationMapper.selExhumation(studentId);
    }

    /**
     * 使用碎片换区手套 印记
     *
     * @param nameInage
     * @param finalNameInage
     * @param finalImgUrl
     * @param number
     * @param type
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> addExhumationAndSyntheticRewardsList(Integer nameInage, Integer finalNameInage, String finalImgUrl, Integer number, Integer type, HttpSession session) {
        //获取手套印记名称
        String name = AwardUtil.getAward(nameInage);
        //获取学生信息
        Student student = getStudent(session);
        //获取最终合成物名称
        String finalName = AwardUtil.getAward(finalNameInage);
        //查看碎片数量
        Map<String, Object> selmap = new HashMap<>();
        selmap.put("name", name);
        selmap.put("studentId", student.getId());
        List<Integer> integers = simpleExhumationMapper.selExhumationId(selmap);
        //查看每个手套碎片合成的数量
        Map<String, Object> selMap = new HashMap<>();
        selMap.put("name", name);
        selMap.put("finalName", finalName);
        selMap.put("studentId", student.getId());
        Integer count = simpleExhumationMapper.selExhumationCountByNameAndFinalName(selMap);
        if (nameInage == 4) {
            if (count != null) {
                if (count + number > 6) {
                    return ServerResponse.createByError(300, "放入数量过多");
                }
            } else if (number > 6) {
                return ServerResponse.createByError(300, "放入数量过多");
            }
        } else if (nameInage == 5) {
            if (count != null) {
                if (count + number > 5) {
                    return ServerResponse.createByError(300, "放入数量过多");
                }
            } else if (number > 5) {
                return ServerResponse.createByError(300, "放入数量过多");
            }
        }
        for (int i = 0; i < number; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("finalName", finalName);
            map.put("id", integers.get(i));
            simpleExhumationMapper.updExhumationFinalNameById(map);
        }
        //type==1时说明可以合成手套或花瓣
        if (type == 1) {
            if (nameInage == 4) {
                //合成花瓣
                synService.addSynthetic(finalName, finalImgUrl, 2, student.getId().intValue(), 0);
            } else if (nameInage == 5) {
                //合成手套
                synService.addSynthetic(finalName, finalImgUrl, 1, student.getId().intValue(), 0);
            }
        }
        return ServerResponse.createBySuccess("成功");
    }

    /**
     * 皮肤碎片换取皮肤
     *
     * @param session
     * @param nameInteger
     * @param finalNameInteger
     * @param finalImageUrl
     * @param number
     * @param type
     * @return
     */
    @Override
    public ServerResponse<Object> addSkinExhumationAndStudentSkin(HttpSession session, Integer nameInteger, Integer finalNameInteger, String finalImageUrl, Integer number, Integer type, String imgUrl) {
        //获取皮肤碎片名称
        String name = AwardUtil.getAward(nameInteger);
        //获取最终合成物名称
        String finalName = AwardUtil.getAward(finalNameInteger);
        //获取学生信息
        Student student = getStudent(session);
        Map<String, Object> selmap = new HashMap<>();
        selmap.put("name", name);
        selmap.put("studentId", student.getId());
        //获取未使用的碎片id
        List<Integer> integers = simpleExhumationMapper.selSkinExhumationId(selmap);
        if (integers.size() < number) {
            ServerResponse.createByError(400,"碎片不足");
        }
        //使用碎片
        for (int i = 0; i < number; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("finalName", finalName);
            map.put("id", integers.get(i));
            simpleExhumationMapper.updExhumationFinalNameById(map);
        }
        //当合成最终皮肤时添加皮肤
        if (type == 1) {
            synService.addSynthetic(finalName, finalImageUrl, 3, student.getId().intValue(), 0);
            studentSkinService.addStudentSkin(finalName, student.getId().intValue(), null, finalImageUrl);
        }
        return ServerResponse.createBySuccess("成功");
    }


    private Exhumation getExhumation(String name, String imgUrl, int type, Date date, Integer studentId, String finalName) {
        Exhumation exhumation = new Exhumation();
        exhumation.setCreateTime(date);
        exhumation.setImgUrl(imgUrl);
        exhumation.setState(0);
        exhumation.setStudentId(studentId);
        exhumation.setType(type);
        exhumation.setName(name);
        exhumation.setFinalName(finalName);
        return exhumation;
    }
}



















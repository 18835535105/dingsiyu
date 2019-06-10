package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.Exhumation;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 抽取的碎片记录表 服务类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface ExhumationService extends BaseService<Exhumation> {

    /**
     * 添加碎片记录信息
     * @param name
     * @param imgUrl
     * @param type  1,手套  2,花瓣  3,皮肤碎片
     * @param date
     * @param studentId
     * @return
     */
    int addExhumation(String name, String imgUrl, int type, Date date, Integer studentId, String finalName);


    List<Exhumation> selExhumationByStudentId(Integer studentId);

    /**
     * 碎片添加
     * @param name
     * @param finalName
     * @param finalImgUrl
     * @return
     */
    ServerResponse<Object> addExhumationAndSyntheticRewardsList(Integer name, Integer finalName, String finalImgUrl, Integer number, Integer type, HttpSession session);

    ServerResponse<Object> addSkinExhumationAndStudentSkin(HttpSession session, Integer name, Integer finalName, String finalImageUrl, Integer number, Integer type, String imgUrl);
}

package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.constant.SmallAppApiConstant;
import com.zhidejiaoyu.student.business.smallapp.dto.AuthorizationDTO;
import com.zhidejiaoyu.student.business.smallapp.dto.BindAccountDTO;
import com.zhidejiaoyu.student.business.smallapp.enums.AuthorizationEnum;
import com.zhidejiaoyu.student.business.smallapp.serivce.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author: wuchenxi
 * @date: 2020/2/17 09:41:41
 */
@Slf4j
@Service
public class AuthorizationServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements AuthorizationService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public ServerResponse<Object> bind(BindAccountDTO dto) {

        String currentOpenid = HttpUtil.getHttpSession().getAttribute(SessionConstant.OPENID).toString();
        // 判断当前用户是否已绑定队长账号
        Student checkStudent = studentMapper.selectByOpenId(currentOpenid);
        if (checkStudent != null) {
            log.warn("openid=[{}]的用户已绑定学生账号[{}]，无法再次绑定其他学生账号！", currentOpenid, checkStudent.getAccount());
            return ServerResponse.createBySuccess(401, "您已绑定其他队长账号！");
        }

        Student student = studentMapper.selectByAccount(dto.getAccount().trim());
        if (student == null || !Objects.equals(student.getPassword(), dto.getPassword().trim())) {
            return ServerResponse.createByError(400, "账号或密码输入错误！");
        }

        String openid = student.getOpenid();
        String splitCode = ",";
        if (StringUtils.isNotEmpty(openid) && openid.split(splitCode).length >= 3) {
            // 已绑定三个小程序，不能绑定
            return ServerResponse.createByError(400, "英雄学员你好，外部绑定已经超过限额，请联系基地导师解除原有绑定。");
        }

        if (StringUtils.isEmpty(openid)) {
            updateOpenId(student, currentOpenid);
            return ServerResponse.createBySuccess();
        }

        if (openid.contains(currentOpenid)) {
            return ServerResponse.createBySuccess();
        }

        if (openid.endsWith(splitCode)) {
            openid = openid + currentOpenid;
        } else {
            openid = openid + splitCode + currentOpenid;
        }

        updateOpenId(student, openid);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> authorization(HttpServletRequest request) {
        String code = request.getParameter("code");

        String forObject = this.authorization(code);

        AuthorizationDTO authorizationDTO = JSON.parseObject(forObject, AuthorizationDTO.class);
        if (authorizationDTO.getErrcode() != 0) {
            log.warn("小程序授权失败！授权结果=[{}]", authorizationDTO.toString());
            throw new ServiceException(500, AuthorizationEnum.getMsg(authorizationDTO.getErrcode()));
        }

        // 验证小程序是否已经绑定队长账号
        String openid = authorizationDTO.getOpenid();
        Student student = studentMapper.selectByOpenId(openid);
        request.getSession().setAttribute(SessionConstant.OPENID, openid);
        if (student == null) {
            return ServerResponse.createBySuccess(501, "当前用户暂未绑定队长账号！");
        }

        request.getSession().setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess(openid);
    }

    /**
     * 请求微信授权接口，获取授权响应数据
     *
     * @param code
     * @return
     */
    public String authorization(String code) {
        try {
            return restTemplate.getForObject(SmallAppApiConstant.AUTHORIZATION_API_URL +
                            "appid=" + SmallAppApiConstant.APP_ID +
                            "&secret=" + SmallAppApiConstant.SECRET +
                            "&js_code=" + code +
                            "&grant_type=authorization_code",
                    String.class);
        } catch (RestClientException e) {
            log.error("微信小程序授权出错！", e);
            throw new ServiceException(500, "微信小程序授权出错");
        }
    }


    public void updateOpenId(Student student, String openId) {
        student.setOpenid(openId);
        studentMapper.updateById(student);
        HttpUtil.getHttpSession().setAttribute(UserConstant.CURRENT_STUDENT, student);
    }
}

package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.constant.SmallAppApiConstant;
import com.zhidejioayu.center.business.wechat.smallapp.dto.AuthorizationDTO;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;
import com.zhidejioayu.center.business.wechat.smallapp.enums.AuthorizationEnum;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.AuthorizationService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.AuthorizationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: wuchenxi
 * @date: 2020/2/17 09:41:41
 */
@Slf4j
@Service
public class AuthorizationServiceImpl extends ServiceImpl<StudentMapper, Student> implements AuthorizationService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    public ServerResponse<Object> bind(BindAccountDTO dto) {

        String currentOpenid = dto.getOpenId();
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(currentOpenid);

        if (serverConfig != null) {
            String s = restTemplate.postForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/authorization/bind", dto, String.class);
            return JSONObject.parseObject(s, ServerResponse.class);
        }

        throw new ServiceException(400, "中台服务器为查询到openid=" + currentOpenid + "的学生或者校管信息！");
    }

    @Override
    public ServerResponse<Object> authorization(HttpServletRequest request) {
        String code = request.getParameter("code");

        String forObject = this.authorization(code);

        AuthorizationDTO authorizationDTO = JSON.parseObject(forObject, AuthorizationDTO.class);
        if (StringUtils.isEmpty(authorizationDTO.getOpenid())) {
            log.warn("小程序授权失败！授权结果=[{}]", authorizationDTO.toString());
            throw new ServiceException(500, AuthorizationEnum.getMsg(authorizationDTO.getErrcode()));
        }

        AuthorizationVO authorizationVo = AuthorizationVO.builder()
                .openId(authorizationDTO.getOpenid())
                .sessionKey(authorizationDTO.getSession_key())
                .build();

        // 验证小程序是否已经绑定队长账号
        String openid = authorizationDTO.getOpenid();
        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectStudentInfoByOpenId(openid);
        if (businessUserInfo == null) {
            // 当前用户还未绑定队长账号
            return ServerResponse.createBySuccess(501, authorizationVo);
        }

        return ServerResponse.createBySuccess(authorizationVo);
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
}

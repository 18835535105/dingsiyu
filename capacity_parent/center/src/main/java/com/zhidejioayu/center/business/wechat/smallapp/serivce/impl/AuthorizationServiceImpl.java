package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.constant.SmallAppApiConstant;
import com.zhidejioayu.center.business.wechat.smallapp.dto.AuthorizationDTO;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;
import com.zhidejioayu.center.business.wechat.smallapp.enums.AuthorizationEnum;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.AuthorizationService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.AuthorizationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author: wuchenxi
 * @date: 2020/2/17 09:41:41
 */
@Slf4j
@Service
public class AuthorizationServiceImpl extends ServiceImpl<StudentMapper, Student> implements AuthorizationService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> bind(BindAccountDTO dto) {

        ServerConfig serverConfig = serverConfigMapper.selectByAccount(dto.getAccount());
        if (serverConfig == null) {
            return ServerResponse.createByError(400, "账号或密码输入错误！");
        }

        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        ServerResponse<Object> response = smallAppFeignClient.bind(dto);

        if (Objects.equals(response.getStatus(), ResponseCode.SUCCESS.getCode())) {
            BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectByAccount(dto.getAccount());
            String openid = businessUserInfo.getOpenid() + "," + dto.getOpenId();

            Set<String> set = new HashSet<>(Arrays.asList(openid.split(",")));

            StringBuilder sb = new StringBuilder();
            for (String s : set) {
                sb.append(s).append(",");
            }
            businessUserInfo.setOpenid(StringUtils.removeEnd(sb.toString(), ","));
            businessUserInfoMapper.updateById(businessUserInfo);
        }

        return response;
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

        AuthorizationVO.AuthorizationVOBuilder authorizationVoBuilder = AuthorizationVO.builder()
                .openId(authorizationDTO.getOpenid())
                .sessionKey(authorizationDTO.getSession_key());
        // 验证小程序是否已经绑定队长账号
        String openid = authorizationDTO.getOpenid();
        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectStudentInfoByOpenId(openid);
        if (businessUserInfo == null) {
            // 当前用户还未绑定队长账号
            return ServerResponse.createBySuccess(501, authorizationVoBuilder.build());
        }
        authorizationVoBuilder.uuid(businessUserInfo.getUserUuid());
        return ServerResponse.createBySuccess(authorizationVoBuilder.build());
    }

    @Override
    public ServerResponse<Object> unbundling(String openId) {
        BusinessUserInfo businessUserInfo1 = businessUserInfoMapper.selectStudentInfoByOpenId(openId);
        ServerConfig serverConfig = serverConfigMapper.selectByAccount(businessUserInfo1.getAccount());
        if (serverConfig == null) {
            return ServerResponse.createByError(400, "账号或密码输入错误！");
        }
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        ServerResponse<Object> response = smallAppFeignClient.unbundling(openId);
        if (Objects.equals(response.getStatus(), ResponseCode.SUCCESS.getCode())) {
            BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectStudentInfoByOpenId(openId);
            String openid = businessUserInfo.getOpenid();
            String[] split = openid.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                if(!split[i].contains(openId)){
                    sb.append(split[i]).append(",");
                }
            }
            businessUserInfo.setOpenid(sb.toString());
            businessUserInfoMapper.updateById(businessUserInfo);
        }
        return ServerResponse.createBySuccess();
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

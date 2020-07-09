package com.zhidejioayu.center.business.userinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.dto.student.SaveStudentInfoToCenterDTO;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejioayu.center.business.redis.UserInfoRedisOpt;
import com.zhidejioayu.center.business.userinfo.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 16:35:35
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<BusinessUserInfoMapper, BusinessUserInfo> implements UserInfoService {

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;
    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Resource
    private UserInfoRedisOpt userInfoRedisOpt;

    @Override
    public BusinessUserInfo getUserInfoByUserUuid(String uuid) {
        return businessUserInfoMapper.selectByUserUuid(uuid);
    }

    @Override
    public void getUser(BusinessUserInfo businessUserInfo, String no) {
        BusinessUserInfo info = businessUserInfoMapper.selectByUserUuid(businessUserInfo.getUserUuid());
        if (info == null) {
            ServerConfig serverConfig = serverConfigMapper.selectByServerNo(no);
            businessUserInfo.setCreateTime(new Date());
            businessUserInfo.setUpdateTime(new Date());
            businessUserInfo.setServerConfigId(serverConfig.getId());
            businessUserInfo.setId(IdUtil.getId());
            businessUserInfoMapper.insert(businessUserInfo);
        }
    }

    @Override
    public Boolean saveUserInfo(SaveStudentInfoToCenterDTO dto) {

        ServerConfig serverConfig = serverConfigMapper.selectByServerNo(dto.getServerNo());
        if (serverConfig == null) {
            log.error("server_config未配置server_no=" + dto.getServerNo() + "的服务器信息！请联系管理员！");
            return false;
        }

        Boolean exist = userInfoRedisOpt.userInfoIsExist(dto.getUuid());
        if (exist) {
            return true;
        }

        BusinessUserInfo businessUserInfo = new BusinessUserInfo();
        businessUserInfo.setAccount(dto.getAccount());
        businessUserInfo.setCreateTime(new Date());
        businessUserInfo.setId(IdUtil.getId());
        businessUserInfo.setOpenid(dto.getOpenid());
        businessUserInfo.setPassword(dto.getPassword());
        businessUserInfo.setServerConfigId(serverConfig.getId());
        businessUserInfo.setUpdateTime(new Date());
        businessUserInfo.setUserUuid(dto.getUuid());

        boolean save = this.save(businessUserInfo);
        userInfoRedisOpt.saveUserInfoToCenterServer(businessUserInfo.getUserUuid());
        return save;
    }
}

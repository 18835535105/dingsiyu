package com.zhidejioayu.center.business.userinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.dto.student.SaveStudentInfoToCenterDTO;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejioayu.center.business.redis.UserInfoRedisOpt;
import com.zhidejioayu.center.business.userinfo.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (info != null) {
            return;
        }

        ServerConfig serverConfig = serverConfigMapper.selectByServerNo(no);
        if (serverConfig == null) {
            throw new ServiceException("server_config未配置server_no=" + no + "的服务器信息！请联系管理员！");
        }
        businessUserInfo.setCreateTime(new Date());
        businessUserInfo.setUpdateTime(new Date());
        businessUserInfo.setServerConfigId(serverConfig.getId());
        businessUserInfo.setId(IdUtil.getId());
        businessUserInfoMapper.insert(businessUserInfo);
    }

    @Override
    public Boolean saveUserInfo(SaveStudentInfoToCenterDTO dto) {

        ServerConfig serverConfig = serverConfigMapper.selectByServerNo(dto.getServerNo());
        if (serverConfig == null) {
            log.error("server_config未配置server_no=" + dto.getServerNo() + "的服务器信息！请联系管理员！");
            return false;
        }

        BusinessUserInfo businessUserInfo = getBusinessUserInfo(dto, serverConfig);

        if (businessUserInfo == null) {
            return true;
        }

        boolean save = this.save(businessUserInfo);
        userInfoRedisOpt.saveUserInfoToCenterServer(businessUserInfo.getUserUuid());
        return save;
    }

    private BusinessUserInfo getBusinessUserInfo(SaveStudentInfoToCenterDTO dto, ServerConfig serverConfig) {

        Boolean exist = userInfoRedisOpt.userInfoIsExist(dto.getUuid());
        if (exist) {
            return null;
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
        return businessUserInfo;
    }

    @Override
    public Boolean saveUserInfos(List<SaveStudentInfoToCenterDTO> dto) {
        String serverNo = dto.get(0).getServerNo();
        ServerConfig serverConfig = serverConfigMapper.selectByServerNo(serverNo);
        if (serverConfig == null) {
            log.error("server_config未配置server_no=" + serverNo + "的服务器信息！请联系管理员！");
            return false;
        }

        List<BusinessUserInfo> businessUserInfos = new ArrayList<>(dto.size());
        dto.forEach(d -> {
            BusinessUserInfo businessUserInfo = this.getBusinessUserInfo(d, serverConfig);
            if (businessUserInfo != null) {
                businessUserInfos.add(businessUserInfo);
            }
        });

        boolean b = this.saveBatch(businessUserInfos);
        if (b) {
            businessUserInfos.forEach(businessUserInfo -> userInfoRedisOpt.saveUserInfoToCenterServer(businessUserInfo.getUserUuid()));
        }

        return b;
    }
}

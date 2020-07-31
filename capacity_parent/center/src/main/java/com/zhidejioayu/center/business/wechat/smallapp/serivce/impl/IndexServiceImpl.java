package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.PetImageConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.IndexService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.TotalDataVO;
import com.zhidejioayu.center.business.wechat.smallapp.vo.index.IndexVO;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Service("smallAppIndexService")
public class IndexServiceImpl extends ServiceImpl<StudentMapper, Student> implements IndexService {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    public ServerResponse<Object> index(String openId) {

        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(openId);
        if (serverConfig == null) {
            return packageDefaultIndexVo();
        }
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.index(openId);
    }

    private ServerResponse<Object> packageDefaultIndexVo() {
        // 说明是游客，返回默认值
        return ServerResponse.createBySuccess(IndexVO.builder()
                .adsenses(Collections.emptyList())
                .totalData(TotalDataVO.builder()
                        .headImg(GetOssFile.getPublicObjectUrl(PetImageConstant.VISITOR))
                        .say(this.getSay())
                        .studentName("默认昵称")
                        .systemGold("0")
                        .build())
                .build());
    }

    /**
     * 问候语
     */
    private String getSay() {
        int hourOfDay = new DateTime().getHourOfDay();
        if (hourOfDay <= 12 && hourOfDay >= 6) {
            return "上午好";
        }
        if (hourOfDay < 6 || hourOfDay > 18) {
            return "晚上好";
        }
        return "下午好";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> replenish(String date, String openId) {

        ServerConfig serverConfig = this.getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.replenish(date, openId);
    }

    @Override
    public ServerResponse<Object> record(String openId) {
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(openId);
        if (serverConfig == null) {
            return this.packageDefaultRecord();
        }
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.record(openId);
    }

    /**
     * 默认学习记录
     * @return
     */
    private ServerResponse<Object> packageDefaultRecord() {
        return this.packageDefaultPrize();
    }

    public ServerConfig getServerConfig(String openId) {
        return ServerConfigUtil.getServerInfoByStudentOpenid(openId);
    }

    @Override
    public ServerResponse<Object> prize(PrizeDTO dto) {
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(dto.getOpenId());
        if (serverConfig == null) {
            return packageDefaultPrize();
        }
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.prize(dto);
    }

    /**
     * 返回藏宝阁默认信息
     *
     * @return
     */
    private ServerResponse<Object> packageDefaultPrize() {
        return ServerResponse.createBySuccess(PageUtil.packagePage(Collections.emptyList(), 0L));
    }

    @Override
    public ServerResponse<Object> cardInfo(String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.cardInfo(openId);
    }

    @Override
    public ServerResponse<Object> myState(String openId) {
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(openId);
        if (serverConfig == null) {
            return packageDefaultState();
        }
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.myState(openId);
    }

    private ServerResponse<Object> packageDefaultState() {
        // 说明是游客，返回默认值
        com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO.Radar defaultRadar = new com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO.Radar();
        defaultRadar.setAttack(60);
        defaultRadar.setDurability(1100);
        defaultRadar.setHitRate(0.7);
        defaultRadar.setMove(50);
        defaultRadar.setSource(3);
        defaultRadar.setSourceAttack(120);

        return ServerResponse.createBySuccess(com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO.MyState.builder()
                .petUrl(GetOssFile.getPublicObjectUrl(PetImageConstant.DEFAULT_IMG))
                .shipName("联盟I型")
                .armorInfo(this.getDefaultInfo(55L, GetOssFile.getPublicObjectUrl("/static/img/ship-config/base_info/armor/FangDanJia.png")))
                .missileInfo(this.getDefaultInfo(43L, GetOssFile.getPublicObjectUrl("/static/img/ship-config/base_info/missile/FeiMaoTui.png")))
                .shipInfo(this.getDefaultInfo(1L, GetOssFile.getPublicObjectUrl("/static/img/airships/LianMengIXing-right.png")))
                .weaponsInfo(this.getDefaultInfo(25L, GetOssFile.getPublicObjectUrl("/static/img/ship-config/base_info/weapon/APaQiJiQiang.png")))
                .radar(defaultRadar)
                .build());
    }

    private com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO.Info getDefaultInfo(Long id, String url) {
        return com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO.Info.builder()
                    .explain("")
                    .id(id)
                    .url(url)
                    .build();
    }

}

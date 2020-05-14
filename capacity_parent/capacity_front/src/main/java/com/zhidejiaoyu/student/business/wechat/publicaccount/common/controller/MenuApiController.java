package com.zhidejiaoyu.student.business.wechat.publicaccount.common.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.PublicAccountConstant;
import com.zhidejiaoyu.student.business.wechat.util.AccessTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author: wuchenxi
 * @date: 2020/5/14 11:17:17
 */
@Slf4j
@Controller
@RequestMapping("/publicAccount/menu")
public class MenuApiController {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 创建菜单
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/create")
    public ServerResponse<Object> createMenu() {
        String menu = "{\n" +
                "     \"button\":[\n" +
                "     {\t\n" +
                "          \"type\":\"click\",\n" +
                "          \"name\":\"今日歌曲\",\n" +
                "          \"key\":\"V1001_TODAY_MUSIC\"\n" +
                "      },\n" +
                "      {\n" +
                "           \"name\":\"菜单\",\n" +
                "           \"sub_button\":[\n" +
                "           {\t\n" +
                "               \"type\":\"view\",\n" +
                "               \"name\":\"搜索\",\n" +
                "               \"url\":\"" + PublicAccountConstant.getAuthPageApiUrl() + "\"\n" +
                "            },\n" +
                "            {\n" +
                "               \"type\":\"click\",\n" +
                "               \"name\":\"赞一下我们\",\n" +
                "               \"key\":\"V1001_GOOD\"\n" +
                "            }]\n" +
                "       }]\n" +
                " }";

        String publicAccountAccessToken = AccessTokenUtil.getPublicAccountAccessToken();
        String createMenuApiUrl = PublicAccountConstant.getCreateMenuApiUrl(publicAccountAccessToken);

        Map map = restTemplate.postForObject(createMenuApiUrl, menu, Map.class);
        if (Objects.equals(String.valueOf(map.get("errcode")), "0")) {
            return ServerResponse.createBySuccess();
        }
        log.error("目录创建失败！map={}", map.toString());
        return ServerResponse.createByError();


    }
}

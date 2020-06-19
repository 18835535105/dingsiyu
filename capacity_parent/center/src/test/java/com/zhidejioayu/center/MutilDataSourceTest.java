package com.zhidejioayu.center;

import com.zhidejiaoyu.common.mapper.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.ServerConfig;
import com.zhidejioayu.center.mutidatasource.annotion.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/19 12:10:10
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MutilDataSourceTest {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Test
    @DataSource(name = "primary")
    public void selectAllPrimary() {
        List<ServerConfig> serverConfigs = serverConfigMapper.selectList(null);
        log.info("serverConfigs={}", serverConfigs.toString());
    }
}

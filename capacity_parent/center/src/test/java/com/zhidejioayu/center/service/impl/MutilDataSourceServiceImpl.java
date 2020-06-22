package com.zhidejioayu.center.service.impl;

import com.zhidejiaoyu.common.mapper.ServerConfigMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.ServerConfig;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejioayu.center.mutidatasource.annotion.DataSource;
import com.zhidejioayu.center.mutidatasource.constant.DataSourceConstant;
import com.zhidejioayu.center.service.MutilDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/19 18:10:10
 */
@Slf4j
@Service
public class MutilDataSourceServiceImpl implements MutilDataSourceService {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Resource
    private StudentMapper studentMapper;

    @Override
    @DataSource(name = DataSourceConstant.PRIMARY)
    public void getServerConfig() {
        List<ServerConfig> serverConfigs = serverConfigMapper.selectList(null);
        log.info("serverConfigs={}，primary", serverConfigs.toString());
    }

    @Override
    @DataSource(name = DataSourceConstant.SERVER_1)
    public void getStudentById() {
        Student student = studentMapper.selectById(7846);
        log.info("student={}，server1", student.toString());
    }
}

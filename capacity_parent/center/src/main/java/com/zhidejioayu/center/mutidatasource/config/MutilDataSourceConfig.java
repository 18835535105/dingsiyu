package com.zhidejioayu.center.mutidatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.zhidejioayu.center.mutidatasource.DynamicDataSource;
import com.zhidejioayu.center.mutidatasource.constant.DataSourceConstant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/19 13:54:54
 */
//@Configuration
public class MutilDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource dataSourcePrimary() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.server1")
    public DataSource dataSourceServer1() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 多数据源连接池配置
     */
    @Bean
    public DynamicDataSource mutiDataSource(@Qualifier("dataSourcePrimary") DataSource dataSourcePrimary,
                                            @Qualifier("dataSourceServer1") DataSource dataSourceServer1) {

        DruidDataSource primaryDataSource = (DruidDataSource) dataSourcePrimary;
        DruidDataSource server1DataSource = (DruidDataSource) dataSourceServer1;

        try {
            primaryDataSource.init();
            server1DataSource.init();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(DataSourceConstant.PRIMARY, primaryDataSource);
        hashMap.put(DataSourceConstant.SERVER_1, server1DataSource);
        dynamicDataSource.setTargetDataSources(hashMap);
        dynamicDataSource.setDefaultTargetDataSource(primaryDataSource);
        return dynamicDataSource;
    }
}

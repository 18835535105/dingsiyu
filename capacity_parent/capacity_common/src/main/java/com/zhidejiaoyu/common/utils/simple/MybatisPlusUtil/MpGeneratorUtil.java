package com.zhidejiaoyu.common.utils.simple.MybatisPlusUtil;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wuchenxi
 * @date 2018/9/5
 */
@Slf4j
public class MpGeneratorUtil {

    public static void main(String[] args) {
        MpGeneratorUtil.create();
    }

    private static void create() {
        String[] tableName = {"campus"};

        /**
         * 配置：
         * 1.全局配置
         * 2.数据源配置
         * 3.策略配置
         * 4.包名策略配置
         * 5.整合配置
         */

        //1.全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig
                //设置代码生成路径
                .setOutputDir("/Users/wuchenxi/Desktop/zdjy")
                //设置作者
                .setAuthor("zdjy")
                //设置二级缓存的开闭
                .setEnableCache(false)
                //设置数据库id自增
                .setIdType(IdType.AUTO)
                //设置覆盖更新
                .setFileOverride(true)
                //设置去I
                .setServiceName("%sService")
                //设置生产结果映射map
                .setBaseResultMap(true);

        //2.数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig
                //设置数据库类型
                .setDbType(DbType.MYSQL)
                .setUrl("jdbc:mysql://192.168.31.183:3306/zdjy?useUnicode=true&characterEncoding=utf8&useSSL=false")
                .setUsername("root")
                .setPassword("root")
                .setDriverName("com.mysql.jdbc.Driver");

        //3.策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
                //开启全局大写命名
                .setCapitalMode(true)
                //开启下划线转换
                .setDbColumnUnderline(true)
                //开启驼峰命名
                .setNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setInclude(tableName);

        //4.包名策略配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig
                .setParent("com.zhidejiaoyu")
                .setController("student.controller")
                .setEntity("common.pojo")
                .setMapper("common.mapper")
                .setService("student.service")
                .setServiceImpl("student.service.impl")
                .setXml("common.mapper");

        //5.整合配置
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator
                .setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(packageConfig);

        autoGenerator.execute();
    }
}

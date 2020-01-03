package com.zhidejiaoyu.aliyunoss.common;

/**
 * @author wuchenxi
 * @date 2019-06-18
 */
public interface AliyunInfoConst {
    String endpoint = "oss.yydz100.com";
    String accessKeyId = "LTAIxQh72AIRDnKY";
    String accessKeySecret = "XNACDGtXpOYHfFNG4hN6m5SBnYJgS0";
    String bucketName = "zdjy-back-oss";

    /**
     * 如果需要获取 oss 中文件，请调用 GetOssFile.getPublicObjectUrl 方法，不要使用该字段直接拼接！！！
     */
    String host = "https://oss.yydz100.com/";
}

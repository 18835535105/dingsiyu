package com.zhidejiaoyu.aliyunvod.filetrans;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.zhidejiaoyu.aliyuncommon.constant.AliyunInfoConstant;

import java.io.File;

/**
 * 阿里云语音识别
 */
public class FileTransJavaDemo {
    // 地域ID，常量内容，请勿改变
    public static final String REGIONID = "cn-shanghai";
    public static final String ENDPOINTNAME = "cn-shanghai";
    public static final String PRODUCT = "nls-filetrans";
    public static final String DOMAIN = "filetrans.cn-shanghai.aliyuncs.com";
    public static final String API_VERSION = "2018-08-17";
    public static final String POST_REQUEST_ACTION = "SubmitTask";
    public static final String GET_REQUEST_ACTION = "GetTaskResult";
    // 请求参数key
    public static final String KEY_APP_KEY = "appkey";
    public static final String KEY_FILE_LINK = "file_link";
    public static final String KEY_VERSION = "version";
    public static final String KEY_ENABLE_WORDS = "enable_words";
    public static final String ENABLE_SAMPLE_RATE_ADAPTIVE = "enable_sample_rate_adaptive";
    // 响应参数key
    public static final String KEY_TASK = "Task";
    public static final String KEY_TASK_ID = "TaskId";
    public static final String KEY_STATUS_TEXT = "StatusText";
    public static final String KEY_RESULT = "Result";
    // 状态值
    public static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_QUEUEING = "QUEUEING";
    // 阿里云鉴权client
    IAcsClient client;

    public FileTransJavaDemo(String accessKeyId, String accessKeySecret) {
        // 设置endpoint
        try {
            DefaultProfile.addEndpoint(ENDPOINTNAME, REGIONID, PRODUCT, DOMAIN);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(REGIONID, accessKeyId, accessKeySecret);
        this.client = new DefaultAcsClient(profile);
    }

    public String submitFileTransRequest(String appKey, String fileLink) {
        /**
         * 1. 创建CommonRequest 设置请求参数
         */
        CommonRequest postRequest = new CommonRequest();
        // 设置域名
        postRequest.setDomain(DOMAIN);
        // 设置API的版本号，格式为YYYY-MM-DD
        postRequest.setVersion(API_VERSION);
        // 设置action
        postRequest.setAction(POST_REQUEST_ACTION);
        // 设置产品名称
        postRequest.setProduct(PRODUCT);
        /**
         * 2. 设置录音文件识别请求参数，以JSON字符串的格式设置到请求的Body中
         */
        JSONObject taskObject = new JSONObject();
        // 设置appkey
        taskObject.put(KEY_APP_KEY, appKey);
        // 设置音频文件访问链接
        taskObject.put(KEY_FILE_LINK, fileLink);
        // 新接入请使用4.0版本，已接入(默认2.0)如需维持现状，请注释掉该参数设置
        taskObject.put(KEY_VERSION, "4.0");
        taskObject.put(ENABLE_SAMPLE_RATE_ADAPTIVE, true);
        // 设置是否输出词信息，默认为false，开启时需要设置version为4.0及以上
        taskObject.put(KEY_ENABLE_WORDS, true);
        String task = taskObject.toJSONString();
        System.out.println(task);
        // 设置以上JSON字符串为Body参数
        postRequest.putBodyParameter(KEY_TASK, task);
        // 设置为POST方式的请求
        postRequest.setMethod(MethodType.POST);
        /**
         * 3. 提交录音文件识别请求，获取录音文件识别请求任务的ID，以供识别结果查询使用
         */
        String taskId = null;
        try {
            CommonResponse postResponse = client.getCommonResponse(postRequest);
            System.err.println("提交录音文件识别请求的响应：" + postResponse.getData());
            if (postResponse.getHttpStatus() == 200) {
                JSONObject result = JSONObject.parseObject(postResponse.getData());
                String statusText = result.getString(KEY_STATUS_TEXT);
                if (STATUS_SUCCESS.equals(statusText)) {
                    taskId = result.getString(KEY_TASK_ID);
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return taskId;
    }

    public String getFileTransResult(String taskId) {
        /**
         * 1. 创建CommonRequest 设置任务ID
         */
        CommonRequest getRequest = new CommonRequest();
        // 设置域名
        getRequest.setDomain(DOMAIN);
        // 设置API版本
        getRequest.setVersion(API_VERSION);
        // 设置action
        getRequest.setAction(GET_REQUEST_ACTION);
        // 设置产品名称
        getRequest.setProduct(PRODUCT);
        // 设置任务ID为查询参数
        getRequest.putQueryParameter(KEY_TASK_ID, taskId);
        // 设置为GET方式的请求
        getRequest.setMethod(MethodType.GET);
        /**
         * 2. 提交录音文件识别结果查询请求
         * 以轮询的方式进行识别结果的查询，直到服务端返回的状态描述为“SUCCESS”,或者为错误描述，则结束轮询。
         */
        String result = null;
        while (true) {
            try {
                CommonResponse getResponse = client.getCommonResponse(getRequest);
                System.err.println("识别查询结果：" + getResponse.getData());
                if (getResponse.getHttpStatus() != 200) {
                    break;
                }
                JSONObject rootObj = JSONObject.parseObject(getResponse.getData());
                String statusText = rootObj.getString(KEY_STATUS_TEXT);
                if (STATUS_RUNNING.equals(statusText) || STATUS_QUEUEING.equals(statusText)) {
                    // 继续轮询，注意设置轮询时间间隔
                    Thread.sleep(3000);
                } else {
                    // 状态信息为成功，返回识别结果；状态信息为异常，返回空
                    if (STATUS_SUCCESS.equals(statusText)) {
                        result = rootObj.getString(KEY_RESULT);
                        // 状态信息为成功，但没有识别结果，则可能是由于文件里全是静音、噪音等导致识别为空
                        if (result == null) {
                            result = "";
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        final String accessKeyId = AliyunInfoConstant.accessKeyId;
        final String accessKeySecret = AliyunInfoConstant.accessKeySecret;
        final String appKey = "7tCOZLl5ZGQ7XP06";

        // 第几个录音文件序号
        int num = 5;
        String path = "https://oss.yydz100.com/audio/mav-test/" + num + "/";

        // 文件总数
        int fileCount = 2000;
        for (int i = 0; i < fileCount; i++) {

            String oldFile = "/var/tmp/Laura_solo 单词-" + num + "-wav/" + i + ".wav";
            File file = new File(oldFile);
            if (!file.exists()) {
                System.out.println("未找到文件：" + oldFile);
                return;
            }

            String fileLink = path + i + ".wav";
            FileTransJavaDemo demo = new FileTransJavaDemo(accessKeyId, accessKeySecret);
            // 第一步：提交录音文件识别请求，获取任务ID用于后续的识别结果轮询
            String taskId = demo.submitFileTransRequest(appKey, fileLink);
            if (taskId != null) {
                System.out.println("录音文件识别请求成功，task_id: " + taskId);
            } else {
                System.out.println("录音文件识别请求失败！");
                return;
            }
            // 第二步：根据任务ID轮询识别结果
            String result = demo.getFileTransResult(taskId);
            if (result != null) {
                System.out.println("录音文件识别结果查询成功：" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);

                JSONArray words = jsonObject.getJSONArray("Words");

                if (words == null) {
                    // 文件新名称
                    String fileName = "/var/tmp/wav_new/" + i + "*" + i + ".wav";

                    if (!file.exists()) {
                        System.out.println("文件：" + oldFile + " 未找到！");
                    } else {
                        File file1 = new File(fileName);
                        if (file1.exists()) {
                            System.out.println("文件：" + fileName + " 已存在！");
                            continue;
                        }
                        file.renameTo(file1);
                        System.out.println("重命名文件：" + oldFile + " -> " + fileName + " 成功！");
                    }

                    System.out.println(i + ".wav 未能正确识别！");
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                for (int i1 = 0; i1 < words.size(); i1++) {
                    sb.append(" ").append(words.getJSONObject(i1).getString("Word"));
                }
                // 文件新名称
                String fileName = "/var/tmp/wav_new/" + i + "*" + sb.toString().trim().toLowerCase() + ".wav";

                if (!file.exists()) {
                    System.out.println("文件：" + oldFile + " 未找到！");
                } else {
                    File file1 = new File(fileName);
                    if (file1.exists()) {
                        System.out.println("文件：" + fileName + " 已存在！");
                        continue;
                    }
                    file.renameTo(file1);
                    System.out.println("重命名文件：" + oldFile + " -> " + fileName + " 成功！");
                }

            } else {
                System.out.println("录音文件识别结果查询失败！");
            }
        }
    }

    /**
     * 重命名文件，去除文件名中的*
     */
    public static void splitStarWithFile() {
        String filePath = "";
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File file1 : files) {
            String name = file1.getName();
            String[] split = name.split("\\*");

            String newFileName = filePath + "/" + split[1];

            file1.renameTo(new File(newFileName));
        }
    }
}
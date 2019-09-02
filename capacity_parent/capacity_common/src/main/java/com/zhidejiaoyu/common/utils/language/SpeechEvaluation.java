package com.zhidejiaoyu.common.utils.language;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.soe.v20180724.SoeClient;
import com.tencentcloudapi.soe.v20180724.models.InitOralProcessRequest;
import com.tencentcloudapi.soe.v20180724.models.InitOralProcessResponse;
import com.tencentcloudapi.soe.v20180724.models.TransmitOralProcessRequest;
import com.tencentcloudapi.soe.v20180724.models.TransmitOralProcessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.UUID;

/**
 * 腾讯语音评测
 * <p><a href="https://cloud.tencent.com/document/product/884/32821">文档中心</a></p>
 *
 * @author wuchenxi
 * @date 2018/6/20 17:01
 */
@Slf4j
@Component
public class SpeechEvaluation {

    private static final String REGION = "ap-beijing";
    private static final String END_POINT = "soe.ap-beijing.tencentcloudapi.com";
    private static final String SECRET_ID = "AKIDZrWvYvmtwlPYAbjGm6E5lyvxmJtrO6Q5";
    private static final String SECRET_KEY = "b0CrHSRAesq0x87uNegSQ9cqCG73paHq";

    public static final int EVAL_MODE_WORD = 0;
    public static final int EVAL_MODE_SENTENCE = 1;
    public static final int EVAL_MODE_PARA = 2;
    public static final int EVAL_MODE_FREETALK = 3;

    public static final int WORK_MODE_STREAM = 0;
    public static final int WORK_MODE_ONCE = 1;

    public static final int MP3 = 3;
    public static final int WAV = 2;
    public static final int RAW = 1;

    public static final int CN = 1;
    public static final int EN = 0;


    public static final int PKG_SIZE = 16 * 1024;


    public static final String FINISHED = "Finished";
    public static final String FAILED = "Failed";
    public static final String EVALUATING = "Evaluating";

    public String getEvaluationResult(String text, MultipartFile file) {

        Credential cred = new Credential(SECRET_ID, SECRET_KEY);
        HttpProfile httpProfile = new HttpProfile();


        // 设置访问域名，如果需要就近部署，可以使用 soe-tencentcloudapi.com, 腾讯云将根据访问的地域解析到合适的服务器上，如果调用服务已确定地域，如华南地区
        // 可以直接使用地域域名，加快访问速度

        httpProfile.setEndpoint(END_POINT);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        SoeClient client = new SoeClient(cred, REGION, clientProfile);


        InitOralProcessRequest req = new InitOralProcessRequest();
        //设置初始化，参数详情可见官方文档
        String sessionId = getSessionId();
        req.setSessionId(sessionId);
        req.setEvalMode(EVAL_MODE_WORD);
        req.setRefText(text);
        req.setStorageMode(1);
        // 设置为流式评估
        req.setWorkMode(WORK_MODE_ONCE);
        req.setScoreCoeff(1.0f);
        try {
            InitOralProcessResponse resp = initWithRetry(client,req);
        } catch (Exception e) {
            log.error("初始化语音评测失败！", e);
            return null;
        }

        TransmitOralProcessRequest transReq = new TransmitOralProcessRequest();
        transReq.setSessionId(sessionId);
        transReq.setVoiceEncodeType(1);
        transReq.setVoiceFileType(WAV);
        try {
            byte[] buf = file.getBytes();
            String base64Str = Base64.getEncoder().encodeToString(buf);
            transReq.setUserVoiceData(base64Str);
            transReq.setSeqId(1);
            transReq.setIsEnd(1);
            TransmitOralProcessResponse transmitOralProcessResponse = transmitWithRetry(client, transReq);
            return TransmitOralProcessResponse.toJsonString(transmitOralProcessResponse);
        } catch (Exception e) {
            log.error("获取语音评测信息失败！", e);
        }
        return null;
    }

    /**
     * 当网络错误导致参数缺失重试数据传输请求
     * @param client
     * @param req
     * @return
     * @throws TencentCloudSDKException
     */
    private TransmitOralProcessResponse transmitWithRetry(SoeClient client, TransmitOralProcessRequest req) throws TencentCloudSDKException {
        if (req == null) {
            return null;
        }
        if (client == null) {
            return null;
        }
        try {
            TransmitOralProcessResponse resp = client.TransmitOralProcess(req);
            return resp;
        } catch (TencentCloudSDKException e) {
            // 避免网络丢包数据，可以根据业务情景修改重试策略
            if (e.getMessage().contains("MissingParameter")){
                TransmitOralProcessResponse resp = client.TransmitOralProcess(req);
                return resp;
            }
            throw e;
        }
    }

    /**
     * 过长文本转段落模式重试的初始化请求
     * @param client
     * @param req
     * @return
     * @throws TencentCloudSDKException
     */
    private InitOralProcessResponse initWithRetry(SoeClient client, InitOralProcessRequest req) throws TencentCloudSDKException {
        if (req == null) {
            return null;
        }
        if (client == null) {
            return null;
        }

        try {
            InitOralProcessResponse resp = client.InitOralProcess(req);
            return resp;
        } catch (TencentCloudSDKException e) {
            // 长字符串采用段落模式，可以根据业务情景修改重试策略
            if (e.getMessage().contains("InvalidParameterValue.RefTxtTooLang")) {
                if (req.getEvalMode() == EVAL_MODE_WORD || req.getEvalMode() == EVAL_MODE_SENTENCE) {
                    req.setEvalMode(EVAL_MODE_PARA);
                    return client.InitOralProcess(req);
                }
            }
            throw e;
        }
    }

    private static String getSessionId() {
        // sessionID 保证唯一性,建议使用时间戳加随机数或者直接使用 uuid
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

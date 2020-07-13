package com.zhidejiaoyu.aliyunvod.vedioediting;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.vod.model.v20170321.ProduceEditingProjectVideoRequest;
import com.aliyuncs.vod.model.v20170321.ProduceEditingProjectVideoResponse;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <a href=
 * "https://help.aliyun.com/document_detail/100649.html?spm=a2c4g.11186623.6.917.31984968PQXB02">视频剪辑</a>
 * 
 * @author wuchenxi
 * @date 2020-07-13 17:46:58
 */
@Slf4j
@Component
public class VedioEditing {

    private static DefaultAcsClient client;

    @Resource(name = "acsClient")
    private DefaultAcsClient defaultAcsClient;

    @PostConstruct
    public void init() {
        client = this.defaultAcsClient;
    }

    /**
     * 视频合成
     * 
     * @param mediaIdArray 需要合并的视频id数组，顺序要正确
     * @param title        媒体名
     * @param cateId       媒体分类id
     * @param tags         媒体标签
     * @param coverUrl     媒体封面图片url
     */
    public static void concatMedia(String[] mediaIdArray, String title, Long cateId, String tags, String coverUrl) {
        ProduceEditingProjectVideoRequest request = new ProduceEditingProjectVideoRequest();
        // Build Editing Project Timeline
        request.setTimeline(buildTimeline(mediaIdArray));
        // Set Produce Media Metadata
        request.setMediaMetadata(buildMediaMetadata(title, cateId, tags, coverUrl));
        // Set Produce Configuration
        request.setProduceConfig(buildProduceConfig());
        ProduceEditingProjectVideoResponse response = null;

        try {
            response = client.getAcsResponse(request);
            if (response != null) {
                // Produce Media ID
                log.info("MediaId:" + response.getMediaId());
                // Request ID
                log.info("RequestId:" + response.getRequestId());
            }
        } catch (ServerException e) {
            log.error("ServerError code:" + e.getErrCode() + ", message:" + e.getErrMsg());
        } catch (ClientException e) {
            log.error("ClientError code:" + e.getErrCode() + ", message:" + e.getErrMsg());
        } catch (Exception e) {
            log.error("ErrorMessage:" + e.getLocalizedMessage());
        }
    }

    /**
     * 媒体信息
     * 
     * @param title    媒体名
     * @param cateId   媒体分类id
     * @param tags     媒体标签
     * @param coverUrl 媒体封面图片url
     */
    public static String buildMediaMetadata(String title, Long cateId, String tags, String coverUrl) {
        JSONObject mediaMetadata = new JSONObject();
        // Produce Media Title
        mediaMetadata.put("Title", title);
        // Produce Media Description
        mediaMetadata.put("Description", title);
        // Produce Media UserDefined Cover URL
        mediaMetadata.put("CoverURL", coverUrl);
        // Produce Media Category ID
        mediaMetadata.put("CateId", cateId);
        // Produce Media Category Name
        mediaMetadata.put("Tags", tags);
        return mediaMetadata.toString();
    }

    public static String buildProduceConfig() {
        JSONObject produceConfig = new JSONObject();
        /*
         * The produce process can generate media mezzanine file. You can use the
         * mezzanine file to transcode other media files，just like the transcode process
         * after file upload finished. This field describe the Transocde TemplateGroup
         * ID after produce mezzanine finished. 1. Not required 2. Use default transcode
         * template group id when empty
         */
        produceConfig.put("TemplateGroupId", null);
        return produceConfig.toString();
    }

    /**
     * This Sample shows how to merge two videos
     */
    public static String buildTimeline(String[] mediaIdArray) {
        JSONObject timeline = new JSONObject();
        // Video Track
        JSONArray videoTracks = new JSONArray();
        JSONObject videoTrack = new JSONObject();
        // Video Track Clips
        JSONArray videoTrackClips = new JSONArray();

        for (String mediaId : mediaIdArray) {
            JSONObject videoTrackClip = new JSONObject();
            videoTrackClip.put("MediaId", mediaId);
            videoTrackClips.add(videoTrackClip);
        }

        videoTrack.put("VideoTrackClips", videoTrackClips);
        videoTracks.add(videoTrack);
        timeline.put("VideoTracks", videoTracks);
        return timeline.toString();
    }
}
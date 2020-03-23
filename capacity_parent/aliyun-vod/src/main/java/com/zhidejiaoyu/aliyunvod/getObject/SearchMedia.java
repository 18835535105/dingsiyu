package com.zhidejiaoyu.aliyunvod.getObject;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaRequest;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 获取媒体内容 <a href="https://help.aliyun.com/document_detail/61065.html?spm=a2c4g.11186623.6.910.56163bd9koGCJA">参考</a>
 *
 * @author: wuchenxi
 * @date: 2020/3/6 15:07:07
 */
@Component
public class SearchMedia {

    private static DefaultAcsClient client;

    @Resource(name = "acsClient")
    private DefaultAcsClient defaultAcsClient;

    @PostConstruct
    public void init() {
        client = this.defaultAcsClient;
    }

    /**
     * 搜索媒体信息
     *
     * @return
     * @throws Exception
     */
    public static SearchMediaResponse searchMedia() throws Exception {
        SearchMediaRequest request = new SearchMediaRequest();
        request.setSearchType("video");
        request.setFields("Title,CoverURL,CateName");
        request.setPageNo(1);
        request.setPageSize(50);
        return client.getAcsResponse(request);
    }

    /**
     * 获取媒体播放地址
     *
     * @param id 媒体id
     * @return
     * @throws Exception
     */
    public static GetPlayInfoResponse getPlayInfo(String id) throws Exception {
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        request.setVideoId(id);
        return client.getAcsResponse(request);
    }
}

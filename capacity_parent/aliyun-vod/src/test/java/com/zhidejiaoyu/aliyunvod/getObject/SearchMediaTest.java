package com.zhidejiaoyu.aliyunvod.getObject;

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import com.zhidejiaoyu.aliyunvod.VodApplication;
import com.zhidejiaoyu.aliyunvod.util.CreateQrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/3/6 15:12:12
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VodApplication.class)
public class SearchMediaTest {

    @Test
    public void searchMedia() {
        try {
            SearchMediaResponse searchMediaResponse = SearchMedia.searchMedia(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPlayInfo() throws Exception {
        SearchMediaResponse searchMediaResponse = SearchMedia.searchMedia(null);
        List<SearchMediaResponse.Media> mediaList = searchMediaResponse.getMediaList();
        for (SearchMediaResponse.Media media : mediaList) {
            GetPlayInfoResponse playInfo = SearchMedia.getPlayInfo(media.getMediaId());
            String playURL = playInfo.getPlayInfoList().get(0).getPlayURL();
            String title = media.getVideo().getTitle();
            log.info("媒体名：{}，媒体播放地址：{}", title, playURL);

            CreateQrCodeUtil.encodeQRCode(playURL, "/var/tmp/qrcode/" + title + ".jpg");
        }
    }
}

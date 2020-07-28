package com.zhidejiaoyu.aliyunvod.vedioediting;

import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import com.zhidejiaoyu.aliyunvod.VodApplication;
import com.zhidejiaoyu.aliyunvod.getObject.SearchMedia;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/7/27 16:22:22
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VodApplication.class)
public class VideoEditingTest {

    @Test
    void concatMedia() throws Exception {
        // 五年级 138
        String match = "CateId=1000169199";
        for (int i = 0; i < 5; i++) {
            SearchMediaResponse searchMediaResponse = SearchMedia.searchMedia(match, i + 1, 100);
            List<SearchMediaResponse.Media> mediaList = searchMediaResponse.getMediaList();
            for (SearchMediaResponse.Media media : mediaList) {
                String title = media.getVideo().getTitle();
                log.info("开始合成视频{}", title);
                String[] mediaIdArray = {"f7c6fc10da6e4fe99b863a5a82587f21", media.getMediaId(), "2a9531dd8b8f434798dbc0678637929c"};
                VideoEditing.concatMedia(mediaIdArray, title, 1000163011L, null, null);
                log.info("{} 合成完成", title);
            }
        }

    }
}

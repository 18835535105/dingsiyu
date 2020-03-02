package com.zhidejiaoyu.aliyunoss.deleteobject;

import com.zhidejiaoyu.common.constant.FileConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: wuchenxi
 * @date: 2020/3/2 09:40:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OssDeleteTest {

    @Test
    public void deleteObject() {
        String objectName = FileConstant.QR_CODE_OSS + "1582886406994.png";
        OssDelete.deleteObject(objectName);
    }
}

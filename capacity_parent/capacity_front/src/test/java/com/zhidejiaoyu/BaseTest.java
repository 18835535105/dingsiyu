package com.zhidejiaoyu;

import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.student.business.wechat.smallapp.dto.GetLimitQRCodeDTO;
import com.zhidejiaoyu.student.business.wechat.smallapp.util.CreateWxQrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Set;

/**
 * @author wuchenxi
 * @date 2018/7/14
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class BaseTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void say() {
        System.out.println("hello".hashCode());
    }

    @Test
    public void testSortedSet() {
        ZSetOperations<String, Object> stringObjectZSetOperations = redisTemplate.opsForZSet();
        Double score = stringObjectZSetOperations.score(RankKeysConst.CLASS_GOLD_RANK + 301 + ":" + null, 9604);
        log.info("修改前分数：{}", score);

        stringObjectZSetOperations.add(RankKeysConst.CLASS_GOLD_RANK + 301 + ":" + null, 9604, score + 10);
        log.info("修改后分数:{}", stringObjectZSetOperations.score(RankKeysConst.CLASS_GOLD_RANK + 301 + ":" + null, 9604));


    }

    @Test
    public void testReverseRangeWithScores() {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(RankKeysConst.COUNTRY_GOLD_RANK, 0, 11);
        for (ZSetOperations.TypedTuple<Object> typedTuple : typedTuples) {
            log.info("typedTuple.getScore()", typedTuple.getScore());
            log.info("typedTuple.getValue()", typedTuple.getValue());
        }
    }

    @Test
    public void createQRCode() {
        for (int i = 0; i < 1; i++) {
            byte[] qrCode = CreateWxQrCodeUtil.createQRCode(GetLimitQRCodeDTO.builder()
                    .path("pages/support2/support?scene=" + URLEncoder.encode("num=" + i))
                    .build());
            String fileName = System.currentTimeMillis() + ".png";
            String pathname = FileConstant.QR_CODE + fileName;
            File file = new File(pathname);
            try (OutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(qrCode);
                outputStream.flush();
            } catch (Exception e) {
                log.error("生成小程序码出错！", e);
                throw new ServiceException("生成小程序码出错！");
            }
        }
    }
}

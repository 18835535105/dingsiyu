package com.zhidejiaoyu.student.utils;

import aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.FileConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 宠物提示语
 *
 * @author wuchenxi
 * @date 2018/7/5
 */
@Component
public class PetSayUtil {

    private String[] petNames = {"李糖心", "威士顿", "大明白", "无名"};

    /**
     * 拼接MP3路径
     *
     * @param petName 宠物名称
     * @param mp3Name mp3名称
     * @return
     */
    public String getMP3Url(String petName, int[] mp3Name) {
        petNameIsNull(petName);
        checkMp3Name(mp3Name);
        return appendMP3Url(petName, mp3Name);
    }

    private String appendMP3Url(String petName, int[] mp3Name) {
        for (int i = 0; i < petNames.length; i++) {
            if (petNames[i].equals(petName)) {
               return GetOssFile.getUrl(FileConstant.PET_SAY_AUDIO + mp3Name[i] + ".mp3");
            }
        }
        return null;
    }

    /**
     * 检查 mp3Name 合法性
     * @param mp3Name
     */
    private void checkMp3Name(int[] mp3Name) {
        if (mp3Name == null || mp3Name.length != petNames.length) {
            throw new RuntimeException("mp3Name 长度不合法！");
        }
    }

    private void petNameIsNull(String petName) {
        if (StringUtils.isEmpty(petName)) {
            throw new RuntimeException("petName 不能为空！");
        }
    }
}

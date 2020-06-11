package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class CurrentDayOfStudyUtil {


    /**
     * 保存学习信息
     *
     * @param attributeStr
     * @param feldId
     */
    public static void saveSessionCurrent(String attributeStr, Long feldId) {
        HttpSession session = HttpUtil.getHttpSession();
        session.setAttribute(attributeStr, feldId);
    }

    /**
     * 查找最后的错误信息
     *
     * @param attributeStr
     * @return
     */
    public static Long getSessionCurrent(String attributeStr) {
        HttpSession session = HttpUtil.getHttpSession();
        String feldId = session.getAttribute(attributeStr).toString();
        if (feldId == null) {
            return 0L;
        } else {
            return Long.parseLong(feldId);
        }

    }

}

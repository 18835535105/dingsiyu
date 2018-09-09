package com.zhidejiaoyu.student.token;

/**
 * @author wuchenxi
 * @date 2018/7/5
 */
/**
 * 重复提交异常
 */
public class DuplicateSubmitException extends RuntimeException {
    public DuplicateSubmitException(String msg) {
        super(msg);
    }

    public DuplicateSubmitException(String msg, Throwable cause){
        super(msg,cause);
    }
}

package com.zhidejiaoyu.app;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 异常处理
 * 
 * @author 1126
 */
@ControllerAdvice
public class CatchException {

	/*@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public String resultError(){
		return "错误";
	}*/
}

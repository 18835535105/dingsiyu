package com.zhidejiaoyu.app;

import com.zhidejiaoyu.student.token.DuplicateSubmitToken;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebLogAspect {

	private static Logger logeer = Logger.getLogger(WebLogAspect.class);
	private static final String  DUPLICATE_TOKEN_KEY="duplicate_token_key";
	
	@Pointcut("execution(public * com.zhidejiaoyu.*.controller..*.*(..))")
	public void webLog(){
		
	}
	
//	@Before("webLog()")
	@Before("webLog() && @annotation(token)")
	public void doBefore(JoinPoint joinPoint, DuplicateSubmitToken token) {
		System.out.println("qwe");
		/*if (token!=null){
			Object[]args=joinPoint.getArgs();
			HttpServletRequest request=null;
			HttpServletResponse response=null;
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof HttpServletRequest){
					request= (HttpServletRequest) args[i];
				}
				if (args[i] instanceof HttpServletResponse){
					response= (HttpServletResponse) args[i];
				}
			}

			boolean isSaveSession=token.save();
			if (isSaveSession){
				String key = getDuplicateTokenKey(joinPoint);
				Object t = request.getSession().getAttribute(key);
				if (null==t){
					String uuid= UUID.randomUUID().toString();
					request.getSession().setAttribute(key.toString(),uuid);
					logeer.info("token-key="+key);
					logeer.info("token-value="+uuid.toString());
				}else {
					throw new DuplicateSubmitException("");

				}
			}

		}*/
	}
	/**
	 * 获取重复提交key-->duplicate_token_key+','+请求方法名
	 * @param joinPoint
	 * @return
	 */
	public String getDuplicateTokenKey(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		StringBuilder key=new StringBuilder(DUPLICATE_TOKEN_KEY);
		key.append(",").append(methodName);
		return key.toString();
	}

	@AfterReturning(returning="ret", pointcut="webLog()")
	public void doAfterReturning(Object ret) throws Throwable{
	}
	
}

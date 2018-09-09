package com.zhidejiaoyu.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import java.util.List;
import java.util.Map;

/**
 * json处理工具
 * 
 * @author 1126
 */
public class ResultUtil {
	
	private static ObjectMapper om = new ObjectMapper();
	
	/**
	 * 将一个对象格式化成json(字符串类型)响应给页面
	 * 
	 * @param obj
	 *            �?要格式化的对�?
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String toJsonStr(Object obj) throws JsonProcessingException {
		return om.writeValueAsString(obj);
	}
	
	/**
	 * 将一个json串转换成�?个pojo对象
	 * 
	 * @param jsonStr
	 *            json�?
	 * @param cls
	 *            pojo类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToBean(String jsonStr , Class<T> cls) {
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonStr);
			Object obj = JSONObject.toBean(jsonObject , cls);
			return (T)obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * json 转集�?
	 * 
	 * @param jsonString
	 *            json字符�?
	 * @param classT
	 *            类型
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> List<T> jsonToList(String jsonString , Class<T> classT) throws InstantiationException , IllegalAccessException {
		JsonConfig jsonConfig = new JsonConfig();
		return toList(jsonString , classT , jsonConfig);
	}
	
	/**
	 * json 转集�?
	 * 
	 * @param jsonString
	 *            json字符�?
	 * @param classMap
	 *            类型映射
	 * @param classT
	 *            类型
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> jsonToList(String jsonString , Map<String , Class<?>> classMap , Class<T> classT) throws InstantiationException , IllegalAccessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setClassMap(classMap);
		return toList(jsonString , classT , jsonConfig);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> toList(String jsonString , Class<T> classT , JsonConfig jsonConfig) throws InstantiationException , IllegalAccessException {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return JSONArray.toList(jsonArray , classT.newInstance() , jsonConfig);
	}
}
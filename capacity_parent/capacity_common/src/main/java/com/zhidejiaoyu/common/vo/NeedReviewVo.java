package com.zhidejiaoyu.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 慧追踪 查看当前模块的单词
 *
 * @author liumaoyu
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class NeedReviewVo implements Serializable {
	private Map<String,Object> map;

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
}

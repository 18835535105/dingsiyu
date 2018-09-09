package com.zhidejiaoyu.common.study;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.pojo.CapacityMemory;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.LearnExample;

/**
 * 计算指定单词的记忆难度
 * @author wuchenxi
 * @date 2018年5月11日
 */
@Component
public class MemoryDifficulty {
	
	@Autowired
	private LearnMapper learnMapper;
	
	/**
	 * 计算当前单词的记忆难度
	 * 
	 * @param capacityMemory
	 *            慧记忆中记忆追踪数据
	 * @return 当前单词的记忆难度
	 */
	public Integer getMemoryDifficulty(CapacityMemory capacityMemory) {
		// 获取单词的错误次数
		Integer errCount = capacityMemory.getFaultTime();
		// 获取单词的学习次数
		Integer studyCount = this.getStudyCount(capacityMemory);
		// 错误率
		double errorRate = errCount / studyCount;
		// 获取记忆强度
		double memoryStrength = capacityMemory.getMemoryStrength();
		if (errorRate == 1) {
			return 5;
		} else if (errorRate < 1 && errorRate >= 0.8) {
			return 4;
		} else if (errorRate < 0.8 && errorRate >= 0.6) {
			return 3;
		} else if (errorRate < 0.6 && errorRate >= 0.4) {
			return 2;
		} else if (errorRate < 0.4 && errorRate > 0) {
			return 1;
		} else if (memoryStrength == 1) {
			return 0;
		}
		return null;
	}
	
	/**
	 * 获取学生在当前单元下学习当前单词的次数
	 * 
	 * @param capacityMemory
	 * @return
	 */
	private Integer getStudyCount(CapacityMemory capacityMemory) {
		LearnExample learnExample = new LearnExample();
		learnExample.createCriteria().andStudentIdEqualTo(capacityMemory.getStudentId())
				.andUnitIdEqualTo(capacityMemory.getUnitId())
				.andVocabularyIdEqualTo(capacityMemory.getVocabularyId());
		List<Learn> learns = learnMapper.selectByExample(learnExample);
		return learns.size() > 0 ? learns.get(0).getStudyCount() : 1;
	}
}

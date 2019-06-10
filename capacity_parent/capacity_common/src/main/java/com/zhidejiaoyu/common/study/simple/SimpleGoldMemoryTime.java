package com.zhidejiaoyu.common.study.simple;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 记忆强度，黄金记忆点 0%-10%,本单词最后一次学习结束+0s， 10%-20%，本单词最后一次学习结束+10s，
 * 20%-30%，本单词最后一次学习结束+1分钟， 30%-40%，本单词最后一次学习结束+7分钟， 40%-50%，本单词最后一次学习结束+60分钟
 * 50%-60%，本单词最后一次学习结束+2小时 60%-70%，本单词最后一次学习结束+6小时 70%-80%，本单词最后一次学习结束+1天后
 * 80%-90%，本单词最后一次学习结束+2天后 90%-100%，本单词最后一次学习结束+3天后
 */
@Component
public class SimpleGoldMemoryTime {
	/**
	 * 获取黄金记忆点
	 *
	 * @param memoryStrength
	 *            记忆强度,学习本单词之前的记忆强度。（在更新本次记忆追踪之前获取到的记忆强度）
	 * @param studyEnd
	 *            本单词最后学习结束时间
	 * @return 黄金记忆点
	 */
	public static Date getGoldMemoryTime(Double memoryStrength, Date studyEnd) {
		if (studyEnd == null) {
			throw new RuntimeException(" 本单词最后学习结束时间不能为空！");
		}
		if (memoryStrength >= 0 && memoryStrength < 0.2) {
			return getTime(studyEnd, 10);
		} else if (memoryStrength >= 0.2 && memoryStrength < 0.3) {
			return getTime(studyEnd, 60);
		} else if (memoryStrength >= 0.3 && memoryStrength < 0.4) {
			return getTime(studyEnd, 7 * 60);
		} else if (memoryStrength >= 0.4 && memoryStrength < 0.5) {
			return getTime(studyEnd, 60 * 60);
		} else if (memoryStrength >= 0.5 && memoryStrength < 0.6) {
			return getTime(studyEnd, 2 * 60 * 60);
		} else if (memoryStrength >= 0.6 && memoryStrength < 0.7) {
			return getTime(studyEnd, 6 * 60 * 60);
		} else if (memoryStrength >= 0.7 && memoryStrength < 0.8) {
			return getTime(studyEnd, 24 * 60 * 60);
		} else if (memoryStrength >= 0.8 && memoryStrength < 0.9) {
			return getTime(studyEnd, 2 * 24 * 60 * 60);
		} else if (memoryStrength >= 0.9 && memoryStrength <= 1) {
			return getTime(studyEnd, 3 * 24 * 60 * 60);
		}
		return null;
	}

	/**
	 * 获取本单词最后学习结束时间若干秒后的时间
	 *
	 * @param studyEnd
	 * @param second
	 * @return
	 */
	private static Date getTime(Date studyEnd, int second) {
		long time = studyEnd.getTime() + second * 1000;
		return new Date(time);
	}

}

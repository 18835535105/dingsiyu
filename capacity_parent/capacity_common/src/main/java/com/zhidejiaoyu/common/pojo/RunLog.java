package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RunLog extends Model<RunLog> {
	@TableId(type = IdType.AUTO)
	private Long id;

	/** 操作人id，0：系统；其余为学生、教师、管理员、学校等人员id */
	private Long operateUserId;

	private Long courseId;

	private Long unitId;

	/** 日志类型，1：学生登录日志；2：错误日志；3：普通日志信息，4：金币奖励，5金币花费 ,6：重置学生信息,7:勋章奖励 */
	private Integer type;

	/** 日志内容,当type为4的时候，金币数与内容用#隔开 */
	private String logContent;

	private Date createTime;

	public RunLog(Integer type, String logContent, Date createTime) {
		super();
		this.type = type;
		this.logContent = logContent;
		this.createTime = createTime;
	}

	public RunLog(Long operateUserId, Integer type, String logContent, Date createTime) {
		this.operateUserId = operateUserId;
		this.type = type;
		this.logContent = logContent;
		this.createTime = createTime;
	}

	public RunLog(Long operateUserId, Integer type, String logContent, Date createTime,Long courseId,Long unitId) {
		this.operateUserId = operateUserId;
		this.type = type;
		this.logContent = logContent;
		this.createTime = createTime;
		this.courseId=courseId;
		this.unitId=unitId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}

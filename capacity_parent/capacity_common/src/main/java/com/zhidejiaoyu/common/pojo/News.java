package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息通知表
 * 
 * @author qizhentao
 * @version 1.0
 */
public class News extends Model<News> {
	
	/** 消息通知id*/
	@TableId(type = IdType.AUTO)
    private Long id;

    /** 学生id */
    private Long studentid;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 消息产生时间 */
    private Date time;

    /** 类型: 提醒消息 / 系统通知 */
    private String type;

    /** 机器人url  或  不使用该字段,当前用户使用机器人 */
    private String robot;

    /** 提示语,机器人话 */
    private String robotspeak;

    /** 默认2 : 已读=1 / 未读=2  */
    private Integer read;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentid() {
        return studentid;
    }

    public void setStudentid(Long studentid) {
        this.studentid = studentid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getRobot() {
        return robot;
    }

    public void setRobot(String robot) {
        this.robot = robot == null ? null : robot.trim();
    }

    public String getRobotspeak() {
        return robotspeak;
    }

    public void setRobotspeak(String robotspeak) {
        this.robotspeak = robotspeak == null ? null : robotspeak.trim();
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
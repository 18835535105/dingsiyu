package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zdjy
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BugFeedback implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 学习模块
     */
    private String studyModel;

    /**
     * 学习内容（保存学生反馈bug时的所有响应数据），用于问题排查 
     */
    private String studyContent;

    /**
     * 问题原因
     */
    private String reason;

    /**
     * 问题是否解决 1：未解决；2：已解决；
     */
    private Integer fixed;

    /**
     * bug创建时间
     */
    private LocalDateTime createTime;

    /**
     * bug处理时间
     */
    private LocalDateTime fixedTime;


}

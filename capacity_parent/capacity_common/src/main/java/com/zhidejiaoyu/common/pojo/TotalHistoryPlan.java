package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 解锁总时常
 * </p>
 *
 * @author zdjy
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TotalHistoryPlan implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;

    /**
     * 总时长
     */
    private Long totalOnlineTime;

    /**
     * 总有效时常
     */
    private Long totalVaildTime;

    /**
     * 总单词数
     */
    private Integer totalWord;

    /**
     * 总分数
     */
    private Integer totalPoint;


}

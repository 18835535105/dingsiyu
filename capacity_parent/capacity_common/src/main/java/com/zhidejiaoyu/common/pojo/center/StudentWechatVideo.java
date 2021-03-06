package com.zhidejiaoyu.common.pojo.center;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zdjy
 * @since 2020-07-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentWechatVideo extends Model<StudentWechatVideo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 小程序视频id
     */
    private String wechatVideoId;

    /**
     * 学生uuid
     */
    private String studentUuid;

    /**
     * 1：本轮观看的视频；2：上一轮观看的视频，视频观看完之后会将之前看的视频状态置为2
     */
    private Integer state;

    private Date createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zdjy
 * @since 2019-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentClassHourDetails extends Model<StudentClassHourDetails> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学校
     */
    private String school;
    /**
     * 校管id
     */
    private Long schoolAdminId;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 充课时间
     */
    private Date createTime;
    /**
     * 充课详情
     */
    private String details;
    /**
     * 最后登入时间
     */
    private Date maxLoginTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

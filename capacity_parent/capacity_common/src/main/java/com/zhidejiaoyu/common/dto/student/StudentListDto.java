package com.zhidejiaoyu.common.dto.student;

import com.zhidejiaoyu.common.utils.StringUtil;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

/**
 * 教师后台学生用户列表搜索条件
 *
 * @author: wuchenxi
 * @Date: 2019/9/26 13:48
 */
@Data
public class StudentListDto {

    /**
     * 学管openId
     */
    @NotEmpty(message = "openId can't be null!")
    private String openId;

    /**
     * 账号或姓名
     */
    private String accountOrStudentName;


    /**
     * 1,正常用户  2,已过期， 3：体验
     */
    private Integer type;


    /**
     * 排序字段
     * <ul>
     *     <li>accountTime：有效期</li>
     * </ul>
     */
    private String orderField;

    /**
     * 排序方式 desc asc
     */
    private String orderWay;

    private Integer pageNum;

    private Integer pageSize;

    public void setAccountOrStudentName(String accountOrStudentName) {
        this.accountOrStudentName = StringUtil.trim(accountOrStudentName);
    }

    public void setOrderField(String orderField) {
        this.orderField = StringUtil.trim(orderField);
    }

    public void setOrderWay(String orderWay) {
        this.orderWay = StringUtil.trim(orderWay);
    }

}

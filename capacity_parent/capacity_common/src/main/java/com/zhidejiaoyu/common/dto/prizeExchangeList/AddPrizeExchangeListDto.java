package com.zhidejiaoyu.common.dto.prizeExchangeList;

import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddPrizeExchangeListDto extends PrizeExchangeList {
    /**
     * 教师openId
     */
    @NotNull(message = "openId can't be null!")
    private String openId;

    private MultipartFile file;

    private Boolean flag;


}

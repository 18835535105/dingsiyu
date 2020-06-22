package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * server_config
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServerConfig extends Model<ServerConfig> implements Serializable {
    /**
     * uuid主键
     */
    @TableId(type = IdType.ASSIGN_UUID )
    private String id;

    /**
     * 服务器编号
     */
    private String serverNo;

    /**
     * 服务器名称，与多数据源元的数据源名称保持一致
     */
    private String serverName;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 服务器网址url
     */
    private String serverUrl;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    private static final long serialVersionUID = 1L;
}

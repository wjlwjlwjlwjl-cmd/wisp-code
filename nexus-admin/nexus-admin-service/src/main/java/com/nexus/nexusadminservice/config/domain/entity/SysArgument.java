package com.nexus.nexusadminservice.config.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("sys_argument")
public class SysArgument {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;                // 自增主键
    private String name;            // 参数名
    private String configKey;       // 参数键
    private String value;           // 参数值
    private String remark;          // 备注
}

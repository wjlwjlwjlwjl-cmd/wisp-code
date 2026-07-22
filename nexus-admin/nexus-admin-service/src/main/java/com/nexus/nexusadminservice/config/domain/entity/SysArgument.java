package com.nexus.nexusadminservice.config.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_argument")
public class SysArgument extends BaseDO{
    private String name;            // 参数名
    private String configKey;       // 参数键
    private String value;           // 参数值
    private String remark;          // 备注
}

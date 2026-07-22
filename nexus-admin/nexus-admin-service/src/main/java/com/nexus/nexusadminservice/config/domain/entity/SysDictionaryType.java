package com.nexus.nexusadminservice.config.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库表 sys_dictionary_type 对应实体类
 * 
 * SysDictionaryType
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dictionary_type")
public class SysDictionaryType extends BaseDO{
    private String typeKey;     //字典类型键

    private String value;       //字典类型值

    private String remark;      //备注

    private Integer status;     //状态（1正常 0停用）
}

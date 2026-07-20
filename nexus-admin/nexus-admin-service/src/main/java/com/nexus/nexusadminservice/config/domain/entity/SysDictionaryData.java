package com.nexus.nexusadminservice.config.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Getter;
import lombok.Setter;

/**
 * sys_dictionary_data，字典数据对应表
 */
@Getter
@Setter
@TableName("sys_dictionary_data")
public class SysDictionaryData extends BaseDO{
    /**
     * 字典类型主键
     */
    private String typeKey;

    /**
     * 字典数据主键
     */
    private String dataKey;

    /**
     * 字典数据名称
     */
    private String value;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 1正常 0停用
     */
    private Integer status;
}

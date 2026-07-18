package com.nexus.nexusadminservice.config.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 数据库表 sys_dictionary_type 对应实体类
 * 
 * SysDictionaryType
 */
@Data
@TableName("sys_dictionary_type")
public class SysDictionaryType {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;            //自增主键

    private String typeKey;     //字典类型键

    private String value;       //字典类型值

    private String remark;      //备注

    private Integer status;     //状态（1正常 0停用）
}

package com.nexus.nexusportalservice.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("app")
public class App extends BaseDO {
    @TableId(value="id", type= IdType.AUTO)
    private Long id;                //主键 id
    private String userId;          //用户ID
    private String appName;         //应用名称
    private String appDesc;         //应用描述
    private String appDoc;          //应用文档
    private String appType;         //应用类型
    private String previewUrl;      //应用预览地址
    private Boolean deploy;         //是否部署
}

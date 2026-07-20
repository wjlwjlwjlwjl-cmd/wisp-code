package com.nexus.nexusportal.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("app")
public class App extends BaseDO {
    private Long userId;            //用户ID
    private String appName;         //应用名称
    private String appDesc;         //应用描述
    private String appDoc;          //应用文档
    private String appType;         //应用类型
    private String appScreenshot;   //应用截图
}

package com.nexus.nexusportalservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum MessageTypeEnum {
    USER(0, "user"),
    ASSISTANT(1, "assistant");

    MessageTypeEnum(int appType, String appDesc){
        this.appType = appType;
        this.appDesc = appDesc;
    }

    private int appType;
    private String appDesc;
}

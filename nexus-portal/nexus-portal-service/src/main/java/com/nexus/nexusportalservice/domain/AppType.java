package com.nexus.nexusportalservice.domain;

import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppType {
    HTML("HTML", 0),

    VUE3("VUE3", 1),

    VUE3_SPRING("VUE3_SPRING", 2);

    private String type; 
    private int value;

    public static Integer getTypeNum(String typeStr) throws ServiceException{
        if(typeStr.equals("HTML")){
            return 0;
        }
        else if(typeStr.equals("VUE3")){
            return 1;
        }
        else if(typeStr.equals("VUE3_SPRING")){
            return 2;
        }
        else{
            throw new ServiceException("Unknown Application Type");
        }
    }
}

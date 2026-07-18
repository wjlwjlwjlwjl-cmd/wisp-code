package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

/**
 * 腾讯位置服务响应基类（QQ表示腾讯）
 * 
 * QQMapBaseResponseDTO
 */
@Data
public class QQMapBaseResponseDTO {
    private Integer stauts;         //0 表示成功
    private String message;         //响应消息
    private String request_id;      //请求id
}
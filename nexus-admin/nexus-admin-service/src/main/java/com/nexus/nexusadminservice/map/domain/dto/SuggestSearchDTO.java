package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

/**
 * 城市搜索地点查询条件（配合腾讯位置服务查询条件）
 * 
 * SuggestSearchDTO
 */
@Data
public class SuggestSearchDTO {
    private String keyword;     // 搜索的关键字

    private String id;          // 城市 id

    private Integer pageIndex;  // 页码

    private Integer pageSize;   // 每页的数量
}
package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 地图 poi（继承腾讯位置服务响应类，poi表示 point of intrest，兴趣点）
 * 
 * PoiListDTO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PoiListDTO extends QQMapBaseResponseDTO{
    private Integer count;          //本次搜索结果数量
    private List<PoiDTO> data;      //查出来的 Poi 列表（具体地点内容）
}

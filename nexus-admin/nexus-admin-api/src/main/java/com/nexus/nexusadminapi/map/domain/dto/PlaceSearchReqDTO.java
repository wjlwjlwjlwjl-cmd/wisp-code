package com.nexus.nexusadminapi.map.domain.dto;

import com.nexus.nexuscommondomain.domain.dto.BasePageReqDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求 DTO
 * 
 * PlaceSearchReqDTO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PlaceSearchReqDTO extends BasePageReqDTO {
    @NotNull(message = "请求关键字b不能为空")
    private String keyword;

    @NotNull(message = "请求区域 ID 不能为空")
    private Long id;
}

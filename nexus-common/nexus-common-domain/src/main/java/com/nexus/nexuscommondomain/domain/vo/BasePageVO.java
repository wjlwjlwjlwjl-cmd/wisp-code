package com.nexus.nexuscommondomain.domain.vo;


import lombok.Data;

import java.util.List;

/**
 * 分页VO
 * @param <T> 报文类型
 */
@Data
public class BasePageVO<T> {
    /**
     * 查询结果总数
     */
    private Integer totals;

    /**
     * 总页数
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer current;

    /**
     * 数据列表
     */
    private List<T> list;
}

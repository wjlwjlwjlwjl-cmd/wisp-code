package com.nexus.nexusadminservice.map.service;

import com.nexus.nexusadminservice.map.domain.dto.GeoResultDTO;
import com.nexus.nexusadminservice.map.domain.dto.LocationDTO;
import com.nexus.nexusadminservice.map.domain.dto.PoiListDTO;
import com.nexus.nexusadminservice.map.domain.dto.SuggestSearchDTO;
import com.nexus.nexuscommondomain.exception.ServiceException;

/**
 * 封装出接口，调用腾讯位置服务
 * 
 * IMapProvider
 */
public interface IMapProvider {
    /**
     * 地图搜索信息
     * 
     * @param suggestSearchDTO 搜索条件
     * @return 搜索结果（兴趣点的集合）
     */
    PoiListDTO searchQQMapPlaceByRegion(SuggestSearchDTO suggestSearchDTO) throws ServiceException;

    /**
     * 根据经纬度获取区划信息
     *
     * @param location 位置信息
     * @return 区划信息
     */
    GeoResultDTO getQQMapDistrictByLonLat(LocationDTO location) throws ServiceException;
}

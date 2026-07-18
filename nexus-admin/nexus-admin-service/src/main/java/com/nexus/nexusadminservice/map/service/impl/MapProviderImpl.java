package com.nexus.nexusadminservice.map.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nexus.nexusadminapi.map.constants.MapConstants;
import com.nexus.nexusadminservice.map.domain.dto.GeoResultDTO;
import com.nexus.nexusadminservice.map.domain.dto.LocationDTO;
import com.nexus.nexusadminservice.map.domain.dto.PoiListDTO;
import com.nexus.nexusadminservice.map.domain.dto.SuggestSearchDTO;
import com.nexus.nexusadminservice.map.service.IMapProvider;
import com.nexus.nexuscommondomain.domain.ResultCode;
import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 腾讯地图服务封装接口的实现类
 */
@RefreshScope
@Component
@Data
@Slf4j
@ConditionalOnProperty(value = "map.type", havingValue = "qqmap")
@SuppressWarnings({ "null" })
public class MapProviderImpl implements IMapProvider {
    @Value("${qqmap.apiServer}")
    private String apiServer; // 腾讯位置服务域名

    @Value("${qqmap.key}")
    private String key; // 调用腾讯位置服务的秘钥

    private RestTemplate restTemplate = new RestTemplate(); // 可以认为就是一个客户端，可以直接用来发起 HTTP 请求

    /**
     * 根据关键词搜索地点
     * 
     * @param suggestSearchDTO 搜索条件（搜索关键字、城市id、页码、每页的数量）
     * @return 搜索结果
     */
    @Override
    public PoiListDTO searchQQMapPlaceByRegion(SuggestSearchDTO suggestSearchDTO) throws ServiceException {
        // 1 构建请求url
        String url = String.format(
                apiServer + MapConstants.QQMAP_API_PLACE_SUGGESTION
                        + "?key=%s&region=%s&region_fix=1&page_index=%s&page_size=%s&keyword=%s",
                key, suggestSearchDTO.getId(),
                suggestSearchDTO.getPageIndex(),
                suggestSearchDTO.getPageSize(), suggestSearchDTO.getKeyword());

        // 2 直接发送请求，并拿到返回结果再做对象转换（getForEntity 已经完成对象转换，查询到的兴趣点数量，以及每个具体的兴趣点对象）
        ResponseEntity<PoiListDTO> response = restTemplate.getForEntity(url, PoiListDTO.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("获取关键词查询结果异常", response);
            throw new ServiceException(ResultCode.QQMAP_QUERY_FAILED);
        }

        log.info("调用地点查询结果数目：", response.getBody().getCount());
        return response.getBody();
    }

    /**
     * 根据经纬度来获取区域信息（也就是定位功能，提供当前经纬度就可以获取当前位置的文字信息）
     * 
     * @param locationReqDTO 经纬度
     * @return 区域信息
     */
    @Override
    public GeoResultDTO getQQMapDistrictByLonLat(LocationDTO locationReqDTO) throws ServiceException {
        // 1 构建请求url
        String url = String.format(apiServer + MapConstants.QQMAP_GEOCODER + "?key=%s&location=%s", key,
                locationReqDTO.formatInfo());

        // 2 直接发送请求，并拿到返回结果再做对象转换
        ResponseEntity<GeoResultDTO> response = restTemplate.getForEntity(url, GeoResultDTO.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("根据经纬度来获取区域信息查询结果异常", response);
            throw new ServiceException(ResultCode.QQMAP_QUERY_FAILED);
        }
        return response.getBody();
    }
}

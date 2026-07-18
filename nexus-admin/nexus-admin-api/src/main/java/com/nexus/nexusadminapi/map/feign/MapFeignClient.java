package com.nexus.nexusadminapi.map.feign;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nexus.nexusadminapi.map.domain.dto.LocationReqDTO;
import com.nexus.nexusadminapi.map.domain.dto.PlaceSearchReqDTO;
import com.nexus.nexusadminapi.map.domain.vo.RegionCityVo;
import com.nexus.nexusadminapi.map.domain.vo.RegionVO;
import com.nexus.nexusadminapi.map.domain.vo.SearchPoiVo;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;

@FeignClient(contextId = "mapFeignClient", value = "nexus-admin") //声明 HTTP 远程调用
public interface MapFeignClient {
    @GetMapping("/map/get_list")
    public R<List<RegionVO>> get_list();

    @GetMapping("/map/get_py_list")
    public R<Map<String, List<RegionVO>>> get_py_list();

    @GetMapping("/map/get_child_list")
    public R<List<RegionVO>> get_child_list(int parentId);

    @PostMapping("/map/search")
    R<BasePageVO<SearchPoiVo>> searchSuggestOnMap(@RequestBody PlaceSearchReqDTO dto);

    @PostMapping("/map/locate_city_by_location")
    R<RegionCityVo> locateCityByLocation(@RequestBody LocationReqDTO locationReqDTO);
}
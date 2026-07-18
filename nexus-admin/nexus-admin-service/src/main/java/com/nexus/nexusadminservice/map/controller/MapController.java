package com.nexus.nexusadminservice.map.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusadminapi.map.domain.dto.LocationReqDTO;
import com.nexus.nexusadminapi.map.domain.dto.PlaceSearchReqDTO;
import com.nexus.nexusadminapi.map.domain.vo.RegionCityVo;
import com.nexus.nexusadminapi.map.domain.vo.RegionVO;
import com.nexus.nexusadminapi.map.domain.vo.SearchPoiVo;
import com.nexus.nexusadminapi.map.feign.MapFeignClient;
import com.nexus.nexusadminservice.map.domain.dto.RegionCityDTO;
import com.nexus.nexusadminservice.map.domain.dto.SearchPoiDTO;
import com.nexus.nexusadminservice.map.domain.dto.SysRegionDTO;
import com.nexus.nexusadminservice.map.service.impl.NexusMapServiceImpl;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;

@RestController
public class MapController implements MapFeignClient{
    @Autowired
    NexusMapServiceImpl mapServiceImpl;

    @Override
    public R<List<RegionVO>> get_list(){
        List<SysRegionDTO> regions = mapServiceImpl.get_list();
        List<RegionVO> ret = BeanCopyUtil.copyListProperties(regions, RegionVO::new);
        return R.ok(ret);
    }

    @Override
    public R<Map<String, List<RegionVO>>> get_py_list(){
        Map<String, List<RegionVO>> py_list = new LinkedHashMap<>();
        Map<String, List<SysRegionDTO>> list = mapServiceImpl.get_py_list();

        for(Map.Entry<String, List<SysRegionDTO>> item: list.entrySet()){
            List<RegionVO> regionVOs = BeanCopyUtil.copyListProperties(item.getValue(), RegionVO::new);
            py_list.put(item.getKey(), regionVOs);
        }
        return R.ok(py_list);
    }

    @Override
    public R<List<RegionVO>> get_child_list(int parentId){
        List<SysRegionDTO> list = mapServiceImpl.get_child_list(parentId);
        List<RegionVO> ret = BeanCopyUtil.copyListProperties(list, RegionVO::new);
        return R.ok(ret);
    }

    @Override
    public R<BasePageVO<SearchPoiVo>> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO){
        BasePageVO<SearchPoiVo> ret = new BasePageVO<>();
        BasePageDTO<SearchPoiDTO> basePageDTO = mapServiceImpl.searchSuggestOnMap(placeSearchReqDTO);
        if(basePageDTO == null){
            return R.fail("未能获取到兴趣点信息");
        }
        BeanCopyUtil.copyProperties(basePageDTO, ret);
        return R.ok(ret);
    }

    /**
     * 根据经纬度来定位城市
     * @param locationReqDTO 经纬度信息
     * @return 城市信息
     */
    @Override
    public R<RegionCityVo> locateCityByLocation(LocationReqDTO locationReqDTO) {
        RegionCityDTO regionCityDTO = mapServiceImpl.getCityByLocation(locationReqDTO);
        RegionCityVo result = new RegionCityVo();
        BeanCopyUtil.copyProperties(regionCityDTO, result);
        return R.ok(result);
    }
}

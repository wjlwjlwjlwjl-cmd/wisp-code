package com.nexus.nexusadminservice.map.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import com.nexus.nexusadminapi.map.constants.MapConstants;
import com.nexus.nexusadminapi.map.domain.dto.LocationReqDTO;
import com.nexus.nexusadminapi.map.domain.dto.PlaceSearchReqDTO;
import com.nexus.nexusadminservice.map.dao.MapDao;
import com.nexus.nexusadminservice.map.domain.dto.GeoResultDTO;
import com.nexus.nexusadminservice.map.domain.dto.LocationDTO;
import com.nexus.nexusadminservice.map.domain.dto.PoiDTO;
import com.nexus.nexusadminservice.map.domain.dto.PoiListDTO;
import com.nexus.nexusadminservice.map.domain.dto.RegionCityDTO;
import com.nexus.nexusadminservice.map.domain.dto.SearchPoiDTO;
import com.nexus.nexusadminservice.map.domain.dto.SuggestSearchDTO;
import com.nexus.nexusadminservice.map.domain.dto.SysRegionDTO;
import com.nexus.nexusadminservice.map.domain.entity.SysRegion;
import com.nexus.nexusadminservice.map.service.IMapProvider;
import com.nexus.nexusadminservice.map.service.INexusMapService;
import com.nexus.nexuscommoncache.util.CacheUtil;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommoncore.utils.PageUtil;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexuscommonredis.service.RedisService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@SuppressWarnings({"null"})
public class NexusMapServiceImpl implements INexusMapService{
    @Autowired
    MapDao dao;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    RedisService redisService;

    @Autowired
    Cache<String, Object> cache;

    @Autowired
    MapProviderImpl mapProviderImpl;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private IMapProvider mapProvider;

    @PostConstruct
    public void preLoadCityCache(){
        loadCity();
        loadPyCity();
        log.warn("已完成缓存预热");
    }

    private void loadCity(){
        List<SysRegionDTO> regions = new ArrayList<>();
        List<SysRegion> rets = dao.selectAllRegion();
        for(SysRegion region: rets){
            if(region.getLevel().equals(MapConstants.CITY_LEVEL)){  //从查询结果中过滤出所有城市
                SysRegionDTO item = new SysRegionDTO();
                BeanCopyUtil.copyProperties(region, item);
                regions.add(item);
            }
        }
        cacheUtil.setL2Cache(MapConstants.CACHE_MAP_CITY_KEY, regions, cache, redisService, 1000, TimeUnit.SECONDS);
    }

    private void loadPyCity(){
        //从数据库加载所有城市，并构建拼音分类列表，放进缓存
        Map<String, List<SysRegionDTO>> py_list = new HashMap<>();
        List<SysRegion> rets = dao.selectAllRegion();
        for(SysRegion region: rets){
            SysRegionDTO sysRegionDTO = new SysRegionDTO();
            BeanCopyUtil.copyProperties(region, sysRegionDTO);

            String key = region.getPinyin().charAt(0) + "";
            if(py_list.containsKey(key)){
                py_list.get(key).add(sysRegionDTO);
            }
            else{
                List<SysRegionDTO> list = new ArrayList<>();
                list.add(sysRegionDTO);
                py_list.put(key, list);
            }
        }
        cacheUtil.setL2Cache(MapConstants.CACHE_MAP_CITY_PINYIN_KEY, py_list, cache, redisService, 1000, TimeUnit.SECONDS);
    }

    @Override
    public List<SysRegionDTO> get_list() {
        return cacheUtil.getCache(MapConstants.CACHE_MAP_CITY_KEY, new TypeReference<List<SysRegionDTO>>(){}, cache, redisService);
    }

    @Override
    public Map<String, List<SysRegionDTO>> get_py_list() {
        return cacheUtil.getCache(MapConstants.CACHE_MAP_CITY_PINYIN_KEY, new TypeReference<Map<String, List<SysRegionDTO>>>() {}, cache, redisService);
    }

    @Override
    public List<SysRegionDTO> get_child_list(int parentId) {
        List<SysRegionDTO> list = new ArrayList<>();
        String key = MapConstants.CACHE_MAP_CITY_CHILDREN_KEY + parentId; //下级行政区域前缀 + parentId
        //先尝试从缓存中获取
        list = cacheUtil.getCache(key, new TypeReference<List<SysRegionDTO>>(){}, cache, redisService);
        if(list != null){
            return list;
        }

        //从数据库中检索
        list = new ArrayList<>();
        List<SysRegion> regions = dao.selectAllRegion();
        for(SysRegion region: regions){
            if(region.getParentId() != null && region.getParentId() == parentId){
                SysRegionDTO sysRegionDTO = new SysRegionDTO();
                BeanCopyUtil.copyProperties(region, sysRegionDTO);
                list.add(sysRegionDTO);
            }
        }

        //设置进缓存
        cacheUtil.setL2Cache(key, regions, cache, redisService, 1000, TimeUnit.SECONDS);
        return list;
    }

    /**
     * 通过关键词搜索地点
     * @param placeSearchReqDTO 请求
     * @return  查询结果
     */
    public BasePageDTO<SearchPoiDTO> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO){
        //构造 SuggestSearchDTO，用来调用 IMapProvider 中提供的腾讯位置服务方法
        SuggestSearchDTO suggestSearchDTO = new SuggestSearchDTO();
        BeanCopyUtil.copyProperties(placeSearchReqDTO, suggestSearchDTO);
        suggestSearchDTO.setId(placeSearchReqDTO.getId() + "");
        suggestSearchDTO.setPageIndex(placeSearchReqDTO.getPageNo());

        PoiListDTO poiListDTO;
        try{
            //调用 IMapProvider 中提供的查询地点方法
            poiListDTO = mapProviderImpl.searchQQMapPlaceByRegion(suggestSearchDTO);
        }
        catch(ServiceException e){
            log.warn("调用腾讯位置服务(searchQQMapPlaceByRegion)异常：", e);
            return null;
        }

        //将结果提取构造为需要的形式
        List<PoiDTO> poiDTOList = poiListDTO.getData();
        BasePageDTO<SearchPoiDTO> ret = new BasePageDTO<>();
        ret.setTotals(poiListDTO.getCount());
        ret.setTotalPages(PageUtil.getTotalPages(poiListDTO.getCount() == null ? 0 : poiListDTO.getCount(), placeSearchReqDTO.getPageSize()));
        if(poiDTOList == null){
            return null;
        }

        List<SearchPoiDTO> pageRet = new ArrayList<>();
        for(PoiDTO poiDTO: poiDTOList){
            SearchPoiDTO searchPoiDTO = new SearchPoiDTO();
            BeanUtils.copyProperties(poiDTO, searchPoiDTO);
            searchPoiDTO.setLongitude(poiDTO.getLocation().getLng());
            searchPoiDTO.setLatitude(poiDTO.getLocation().getLat());
            pageRet.add(searchPoiDTO);
        }
        ret.setList(pageRet);
        return ret;
    }

    /**
     * 根据经纬度来定位城市
     * @param locationReqDTO 经纬度信息
     * @return 城市信息
     */
    public RegionCityDTO getCityByLocation(LocationReqDTO locationReqDTO) {
        // 1 构建查询腾讯位置服务的入参
        LocationDTO locationDTO = new LocationDTO();
        BeanUtils.copyProperties(locationReqDTO, locationDTO);
        RegionCityDTO result = new RegionCityDTO();
        // 2 调用腾讯位置服务接口
        try{
            GeoResultDTO geoResultDTO =  mapProvider.getQQMapDistrictByLonLat(locationDTO);
            if (geoResultDTO != null && geoResultDTO.getResult() != null && geoResultDTO.getResult().getAd_info() != null) {
                String cityName = geoResultDTO.getResult().getAd_info().getCity();
                // 3 查缓存（获取城市名对应的其他信息，依赖数据库中的服务同步做出配置）
                List<SysRegionDTO> cache = CacheUtil.getCache(MapConstants.CACHE_MAP_CITY_CHILDREN_KEY, new TypeReference<List<SysRegionDTO>>(){}, caffeineCache, redisService);
                if(cache != null){
                    for (SysRegionDTO sysRegionDTO: cache) {
                        if (sysRegionDTO.getFullName().equals(cityName)) {
                            BeanUtils.copyProperties(sysRegionDTO, result);
                            return result;
                        }
                    }
                }
                else{
                    result.setName(cityName);
                    result.setFullName("在数据库中未能获取到城市完整信息");
                    result.setId((long)404);
                }
            }
            else{
                result.setFullName("位置服务未返回信息");
                result.setName("位置服务未返回信息");
                result.setId((long)404);
            }
        }
        catch(ServiceException e){
            log.warn("通过经纬度获取城市异常：", e);
        }
        return result;
    }

}
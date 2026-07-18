package com.nexus.nexusadminservice.config.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataDTO;
import com.nexus.nexusadminservice.config.dao.ConfigDataDao;
import com.nexus.nexusadminservice.config.domain.entity.SysDictionaryData;
import com.nexus.nexusadminservice.config.service.ISysDictionaryService;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDictionaryTypeServiceImpl implements ISysDictionaryService{
    @Autowired
    ConfigDataDao configDataDao;

    @Override
    public List<DictionaryDataDTO> selectDictDataByType(String typeKey) throws ServiceException{
        LambdaQueryWrapper<SysDictionaryData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictionaryData::getTypeKey, typeKey);
        List<SysDictionaryData> list = configDataDao.selectList(wrapper);
        if(list == null){
            throw new ServiceException("未查询到typeKey");
        }

        List<DictionaryDataDTO> rets = BeanCopyUtil.copyListProperties(list, DictionaryDataDTO::new);
        return rets;
    }

    @Override
    public Map<String, List<DictionaryDataDTO>> selectDictsDataByTypes(List<String> typeKeys) throws ServiceException{
        Map<String, List<DictionaryDataDTO>> map = new HashMap<>();

        for(String typeKey: typeKeys){
            LambdaQueryWrapper<SysDictionaryData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDictionaryData::getTypeKey, typeKey);
            List<SysDictionaryData> list = configDataDao.selectList(wrapper);
            if(list == null){
                throw new ServiceException("未查询到typeKey: ");
            }

            List<DictionaryDataDTO> item = BeanCopyUtil.copyListProperties(list, DictionaryDataDTO::new);
            map.put(typeKey, item);
        }
        return map;
    }

    @Override
    public DictionaryDataDTO selectDictDataByKey(String dataKey) {
        SysDictionaryData sysDictionaryData = configDataDao.selectOne(new LambdaQueryWrapper<SysDictionaryData>().eq(SysDictionaryData::getDataKey, dataKey));
        DictionaryDataDTO dictionaryDataDTO = new DictionaryDataDTO();
        BeanCopyUtil.copyProperties(sysDictionaryData, dictionaryDataDTO);
        return dictionaryDataDTO;
    }

    @Override
    public List<DictionaryDataDTO> selectDictDatasByKeys(List<String> dataKeys) {
        List<SysDictionaryData> list = configDataDao.selectList(new LambdaQueryWrapper<SysDictionaryData>().in(SysDictionaryData::getDataKey, dataKeys));

        List<DictionaryDataDTO> result = new ArrayList<>();
        for (SysDictionaryData sysDictionaryData : list) {
            DictionaryDataDTO dictionaryDataDTO = new DictionaryDataDTO();
            BeanCopyUtil.copyProperties(sysDictionaryData, dictionaryDataDTO);
            result.add(dictionaryDataDTO);
        }
        return result;
    }
}

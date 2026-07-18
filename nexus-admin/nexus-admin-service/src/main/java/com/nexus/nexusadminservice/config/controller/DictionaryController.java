package com.nexus.nexusadminservice.config.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataDTO;
import com.nexus.nexusadminapi.config.feign.DictionaryFeignClient;
import com.nexus.nexusadminservice.config.service.impl.SysDictionaryTypeServiceImpl;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.exception.ServiceException;

@RestController
public class DictionaryController implements DictionaryFeignClient {
    @Autowired
    SysDictionaryTypeServiceImpl sysDictionaryTypeServiceImpl;

    @Override
    public R<List<DictionaryDataDTO>> selectDictDataByType(String typeKey) throws ServiceException{
        return R.ok(sysDictionaryTypeServiceImpl.selectDictDataByType(typeKey));
    }

    @Override
    public R<Map<String, List<DictionaryDataDTO>>> selectDictDatasByTypes(List<String> typeKeys) throws ServiceException{
        return R.ok(sysDictionaryTypeServiceImpl.selectDictsDataByTypes(typeKeys));
    }

    @Override
    public R<DictionaryDataDTO> selectDictDataByKey(String dataKey) throws ServiceException{
        return R.ok(sysDictionaryTypeServiceImpl.selectDictDataByKey(dataKey));
    }

    @Override
    public R<List<DictionaryDataDTO>> selectDictDatasByKeys(List<String> dataKeys) throws ServiceException{
        return R.ok(sysDictionaryTypeServiceImpl.selectDictDatasByKeys(dataKeys));
    }

}

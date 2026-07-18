package com.nexus.nexusadminservice.config.service;

import java.util.List;
import java.util.Map;

import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataDTO;
import com.nexus.nexuscommondomain.exception.ServiceException;

public interface ISysDictionaryService {
    /**
     * 通过字典类型获取所有的字典数据
     * 
     * @param typeKey 字典类型
     * @return  字典数据集合
     * @throws ServiceException 
     */
    public List<DictionaryDataDTO> selectDictDataByType(String typeKey) throws ServiceException;

    /**
     * 通过多个字典类型获取对应的所有字典数据
     * 
     * @param typeKeys  要查询的字典类型列表  
     * @return          每个字典类型及其对应的所有字典数据
     * @throws ServiceException 
     */
    public Map<String, List<DictionaryDataDTO>> selectDictsDataByTypes(List<String> typeKeys) throws ServiceException;

    /**
     * 通过字典数据键获取字典数据
     * 
     * @param dataKey   字典数据键
     * @return          查询到的字典数据
     */
    public DictionaryDataDTO selectDictDataByKey(String dataKey);

    /**
     * 通过多个字典数据键获得对应的字典数据
     * 
     * @param dataKeys
     * @return
     */
    public List<DictionaryDataDTO> selectDictDatasByKeys(List<String> dataKeys);
} 
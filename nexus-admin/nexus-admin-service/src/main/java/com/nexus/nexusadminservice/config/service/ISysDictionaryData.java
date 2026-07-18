package com.nexus.nexusadminservice.config.service;

import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataListReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.DictionaryDataVO;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

public interface ISysDictionaryData {
    /**
     * 添加字典数据
     * 
     * @param dictionaryDataAddReqDTO
     * @return  字典数据 ID
     * @throws ServiceException 
     */
    public Long addData(DictionaryDataAddReqDTO dictionaryDataAddReqDTO) throws ServiceException;

    /**
     * 根据 TypeKey 查找字典数据
     * 
     * @param dictionaryDataListReqDTO
     * @return  字典数据列表
     */
    public BasePageVO<DictionaryDataVO> listData(DictionaryDataListReqDTO dictionaryDataListReqDTO);

    public Long editData(DictionaryDataEditReqDTO dictionaryDataEditReqDTO) throws ServiceException;
} 
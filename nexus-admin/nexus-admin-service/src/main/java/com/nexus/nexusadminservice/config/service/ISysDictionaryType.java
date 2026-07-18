package com.nexus.nexusadminservice.config.service;

import com.nexus.nexusadminapi.config.domain.dto.DictionaryTypeListReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryTypeWriteReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.DictionaryTypeVO;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

public interface ISysDictionaryType {
    /**
     * 添加字典类型
     * 
     * @param dictionaryTypeWriteReqDTO
     * @return  字典类型 ID
     * @throws ServiceException 
     */
    public Long addType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) throws ServiceException;

    /**
     * 获取所有字典类型
     * 
     * @param dictionaryTypeListReqDTO
     * @return  字典列表
     */
    public BasePageVO<DictionaryTypeVO> listType(DictionaryTypeListReqDTO dictionaryTypeListReqDTO);

    /**
     * 编辑字典类型
     * 
     * @param dictionaryTypeWriteReqDTO
     * @return  编辑后字典类型 ID
     * @throws ServiceException 
     */
    public Long editType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) throws ServiceException;
}
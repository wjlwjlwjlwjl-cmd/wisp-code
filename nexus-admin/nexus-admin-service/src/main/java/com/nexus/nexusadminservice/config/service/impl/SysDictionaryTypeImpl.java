package com.nexus.nexusadminservice.config.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryTypeListReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryTypeWriteReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.DictionaryTypeVO;
import com.nexus.nexusadminservice.config.dao.ConfigDataDao;
import com.nexus.nexusadminservice.config.dao.ConfigTypeDao;
import com.nexus.nexusadminservice.config.domain.entity.SysDictionaryType;
import com.nexus.nexusadminservice.config.service.ISysDictionaryType;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDictionaryTypeImpl implements ISysDictionaryType {
    @Autowired
    ConfigTypeDao configTypeDao;

    @Autowired
    ConfigDataDao configDataDao;

    @Override
    public Long addType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) throws ServiceException{
        LambdaQueryWrapper<SysDictionaryType> wrapper = new LambdaQueryWrapper<>();
        
        //字典类型键、值有一个冲突都不能插入
        wrapper.select(SysDictionaryType::getId)
            .eq(SysDictionaryType::getTypeKey, dictionaryTypeWriteReqDTO.getTypeKey())
            .or()
            .eq(SysDictionaryType::getValue, dictionaryTypeWriteReqDTO.getValue());
        SysDictionaryType sysDictionaryType = configTypeDao.selectOne(wrapper);
        if(sysDictionaryType != null){
            throw new ServiceException("字典类型键或值冲突");
        }

        sysDictionaryType = new SysDictionaryType();
        sysDictionaryType.setRemark(dictionaryTypeWriteReqDTO.getRemark());
        sysDictionaryType.setStatus(1);
        sysDictionaryType.setValue(dictionaryTypeWriteReqDTO.getValue());
        sysDictionaryType.setTypeKey(dictionaryTypeWriteReqDTO.getTypeKey());
        configTypeDao.insert(sysDictionaryType);
        return sysDictionaryType.getId();
    }

    @Override
    public BasePageVO<DictionaryTypeVO> listType(DictionaryTypeListReqDTO dictionaryTypeListReqDTO) {
        BasePageVO<DictionaryTypeVO> result = new BasePageVO<>();
        LambdaQueryWrapper<SysDictionaryType> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(dictionaryTypeListReqDTO.getTypeKey())){
            wrapper.eq(SysDictionaryType::getTypeKey, dictionaryTypeListReqDTO.getTypeKey());
        }
        if(StringUtils.isNotBlank(dictionaryTypeListReqDTO.getValue())){
            wrapper.likeRight(SysDictionaryType::getValue, dictionaryTypeListReqDTO.getValue());
        }

        //按照页数查询
        Page<SysDictionaryType> page = configTypeDao.selectPage(
            new Page<>(dictionaryTypeListReqDTO.getPageNo().longValue(), dictionaryTypeListReqDTO.getPageSize().longValue())
            , wrapper);
        result.setTotalPages(Integer.parseInt(String.valueOf(page.getPages())));
        result.setTotals(Integer.parseInt(String.valueOf(page.getTotal())));

        List<DictionaryTypeVO> list = new ArrayList<>();
        for(SysDictionaryType sysDictionaryType: page.getRecords()){
            DictionaryTypeVO dictionaryTypeVO = new DictionaryTypeVO();
            BeanCopyUtil.copyProperties(sysDictionaryType, dictionaryTypeVO);
            list.add(dictionaryTypeVO);
        }
        result.setList(list);
        return result;
    }

    @Override
    public Long editType(DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) throws ServiceException{
        SysDictionaryType sysDictionaryType = configTypeDao.selectOne(new LambdaQueryWrapper<SysDictionaryType>().eq(SysDictionaryType::getTypeKey, dictionaryTypeWriteReqDTO.getTypeKey()));
        if(sysDictionaryType == null){
            throw new ServiceException("editType fail: 字典类型键不存在");
        }

        SysDictionaryType sysDictionaryType2 = configTypeDao.selectOne(new LambdaQueryWrapper<SysDictionaryType>()
            .eq(SysDictionaryType::getValue, dictionaryTypeWriteReqDTO.getValue())
            .ne(SysDictionaryType::getTypeKey, dictionaryTypeWriteReqDTO.getTypeKey()));
        if(sysDictionaryType2 != null){
            throw new ServiceException("editType fail: 字典类型值已存在");
        }

        sysDictionaryType.setTypeKey(dictionaryTypeWriteReqDTO.getTypeKey());
        sysDictionaryType.setValue(dictionaryTypeWriteReqDTO.getValue());
        if(StringUtils.isNotBlank(dictionaryTypeWriteReqDTO.getRemark())){
            sysDictionaryType.setRemark(dictionaryTypeWriteReqDTO.getRemark());
        }
        configTypeDao.updateById(sysDictionaryType);
        return sysDictionaryType.getId();
    }

}

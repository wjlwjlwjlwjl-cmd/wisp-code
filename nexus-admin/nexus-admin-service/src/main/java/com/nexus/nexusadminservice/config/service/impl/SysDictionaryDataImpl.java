package com.nexus.nexusadminservice.config.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataListReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.DictionaryDataVO;
import com.nexus.nexusadminservice.config.dao.ConfigDataDao;
import com.nexus.nexusadminservice.config.dao.ConfigTypeDao;
import com.nexus.nexusadminservice.config.domain.entity.SysDictionaryData;
import com.nexus.nexusadminservice.config.domain.entity.SysDictionaryType;
import com.nexus.nexusadminservice.config.service.ISysDictionaryData;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDictionaryDataImpl implements ISysDictionaryData{
    @Autowired
    ConfigDataDao configDataDao;
    @Autowired
    ConfigTypeDao configTypeDao;

    @Override
    public Long addData(DictionaryDataAddReqDTO dictionaryDataAddReqDTO) throws ServiceException{
        //表中有维护联合索引（键-值）
        //检查字典类型是否存在
        LambdaQueryWrapper<SysDictionaryType> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysDictionaryType::getTypeKey, dictionaryDataAddReqDTO.getTypeKey());
        if(configTypeDao.selectOne(wrapper1) == null){
            throw new ServiceException("[addData] TypeKey 不存在");
        }

        //检查字典数据键或值是否存在
        LambdaQueryWrapper<SysDictionaryData> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(SysDictionaryData::getDataKey, dictionaryDataAddReqDTO.getDataKey())
            .or()
            .eq(SysDictionaryData::getValue, dictionaryDataAddReqDTO.getValue());
        if(configDataDao.selectOne(wrapper2) != null){
            throw new ServiceException("[addData] 字典数据键或值已存在");            
        }

        SysDictionaryData sysDictionaryData = new SysDictionaryData();
        sysDictionaryData.setDataKey(dictionaryDataAddReqDTO.getDataKey());
        sysDictionaryData.setTypeKey(dictionaryDataAddReqDTO.getTypeKey());
        sysDictionaryData.setValue(dictionaryDataAddReqDTO.getValue());
        if(StringUtils.isNotBlank(dictionaryDataAddReqDTO.getRemark())){
            sysDictionaryData.setRemark(dictionaryDataAddReqDTO.getRemark());
        }
        if(dictionaryDataAddReqDTO.getSort() != null){
            sysDictionaryData.setSort(dictionaryDataAddReqDTO.getSort());
        }
        configDataDao.insert(sysDictionaryData);
        return sysDictionaryData.getId();
    }

    @Override
    public BasePageVO<DictionaryDataVO> listData(DictionaryDataListReqDTO dictionaryDataListReqDTO) {
        BasePageVO<DictionaryDataVO> list = new BasePageVO<>();

        LambdaQueryWrapper<SysDictionaryData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictionaryData::getTypeKey, dictionaryDataListReqDTO.getTypeKey());
        if(StringUtils.isNotBlank(dictionaryDataListReqDTO.getValue())){
            wrapper.eq(SysDictionaryData::getValue, dictionaryDataListReqDTO.getValue());
        }
        wrapper.orderByAsc(SysDictionaryData::getSort);
        wrapper.orderByAsc(SysDictionaryData::getId);
        Page<SysDictionaryData> page = configDataDao.selectPage(new Page<>(dictionaryDataListReqDTO.getPageNo().longValue(), dictionaryDataListReqDTO.getPageSize()), wrapper);

        list.setTotalPages(Integer.parseInt(String.valueOf(page.getPages())));
        list.setTotals(Integer.parseInt(String.valueOf(page.getTotal())));
        List<DictionaryDataVO> items = new ArrayList<>();
        for(SysDictionaryData sysDictionaryData: page.getRecords()){
            DictionaryDataVO dictionaryDataVO = new DictionaryDataVO();
            BeanCopyUtil.copyProperties(sysDictionaryData, dictionaryDataVO);
            items.add(dictionaryDataVO);
        }
        list.setList(items);
        return list;
    }

    @Override
    public Long editData(DictionaryDataEditReqDTO dictionaryDataEditReqDTO) throws ServiceException {
        SysDictionaryData sysDictionaryData = new SysDictionaryData();

        LambdaQueryWrapper<SysDictionaryData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictionaryData::getDataKey, dictionaryDataEditReqDTO.getDataKey());
        sysDictionaryData = configDataDao.selectOne(wrapper);
        if(sysDictionaryData == null){
            throw new ServiceException("字典数据键不存在");
        }

        LambdaQueryWrapper<SysDictionaryData> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.ne(SysDictionaryData::getDataKey, dictionaryDataEditReqDTO.getDataKey())
            .eq(SysDictionaryData::getValue, dictionaryDataEditReqDTO.getValue());
        if(configDataDao.selectOne(wrapper2) != null){
            throw new ServiceException("字典数据已存在");
        }

        sysDictionaryData.setDataKey(dictionaryDataEditReqDTO.getDataKey());
        sysDictionaryData.setValue(dictionaryDataEditReqDTO.getValue());
        if(dictionaryDataEditReqDTO.getSort() != null){
            sysDictionaryData.setSort(dictionaryDataEditReqDTO.getSort());
        }
        if(StringUtils.isNotBlank(dictionaryDataEditReqDTO.getRemark())){
            sysDictionaryData.setRemark(dictionaryDataEditReqDTO.getRemark());
        }
        configDataDao.updateById(sysDictionaryData);
        Long id = sysDictionaryData.getId();
        return id;
    }
}

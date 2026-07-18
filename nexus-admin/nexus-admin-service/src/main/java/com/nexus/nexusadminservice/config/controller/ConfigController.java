package com.nexus.nexusadminservice.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryDataListReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryTypeListReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.DictionaryTypeWriteReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.DictionaryDataVO;
import com.nexus.nexusadminapi.config.domain.vo.DictionaryTypeVO;
import com.nexus.nexusadminservice.config.service.impl.SysDictionaryDataImpl;
import com.nexus.nexusadminservice.config.service.impl.SysDictionaryTypeImpl;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;


@RestController
public class ConfigController {
    @Autowired
    SysDictionaryTypeImpl sysDictionaryTypeImpl;
    @Autowired
    SysDictionaryDataImpl sysDictionaryDataImpl;

    @PostMapping("/dictionary_type/add")
    public R<Long> add_type(@RequestBody @Validated DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) throws ServiceException{
        return R.ok(sysDictionaryTypeImpl.editType(dictionaryTypeWriteReqDTO));
    }

    @GetMapping("/dictionary_type/list")
    public R<BasePageVO<DictionaryTypeVO>> listType(DictionaryTypeListReqDTO DictionaryTypeListReqDTO){
        return R.ok(sysDictionaryTypeImpl.listType(DictionaryTypeListReqDTO));
    }

    @PostMapping("/dictionary_type/edit")
    public R<Long> edit_type(@RequestBody @Validated DictionaryTypeWriteReqDTO dictionaryTypeWriteReqDTO) throws ServiceException{
        return R.ok(sysDictionaryTypeImpl.editType(dictionaryTypeWriteReqDTO));
    }

    @PostMapping("/dictionary_data/add")
    public R<Long> add_data(@RequestBody @Validated DictionaryDataAddReqDTO dictionaryDataAddReqDTO) throws ServiceException{
        return R.ok(sysDictionaryDataImpl.addData(dictionaryDataAddReqDTO));
    }

    @GetMapping("/dictionary_data/list")
    public R<BasePageVO<DictionaryDataVO>> listData(DictionaryDataListReqDTO dictionaryDataListReqDTO){
        return R.ok(sysDictionaryDataImpl.listData(dictionaryDataListReqDTO));
    }

    @PostMapping("/dictionary_data/edit")
    public R<Long> editData(@RequestBody @Validated DictionaryDataEditReqDTO dictionaryDataEditReqDTO) throws ServiceException{
        return R.ok(sysDictionaryDataImpl.editData(dictionaryDataEditReqDTO));
    }
}

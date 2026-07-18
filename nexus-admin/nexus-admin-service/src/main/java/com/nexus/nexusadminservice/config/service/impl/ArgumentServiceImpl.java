package com.nexus.nexusadminservice.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentListReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.ArgumentVO;
import com.nexus.nexusadminservice.config.dao.ArgumentDao;
import com.nexus.nexusadminservice.config.domain.entity.SysArgument;
import com.nexus.nexusadminservice.config.service.IArgumentService;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@SuppressWarnings({"null"})
public class ArgumentServiceImpl implements IArgumentService{
    @Autowired
    ArgumentDao argumentDao;

    @Override
    public Long add(ArgumentAddReqDTO argumentAddReqDTO) throws ServiceException {
        LambdaQueryWrapper<SysArgument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysArgument::getName, argumentAddReqDTO.getName())
            .or()
            .eq(SysArgument::getConfigKey, argumentAddReqDTO.getConfigKey());
        if(argumentDao.selectOne(wrapper) != null){
            throw new ServiceException("参数名称/键冲突");
        }

        SysArgument sysArgument = new SysArgument();
        sysArgument.setConfigKey(argumentAddReqDTO.getConfigKey());
        sysArgument.setName(argumentAddReqDTO.getName());
        sysArgument.setValue(argumentAddReqDTO.getValue());
        if(StringUtils.isNotBlank(argumentAddReqDTO.getRemark())){
            sysArgument.setRemark(argumentAddReqDTO.getRemark());
        }
        log.warn(sysArgument.getConfigKey());
        argumentDao.insert(sysArgument);

        return sysArgument.getId();
    }

    @Override
    public BasePageVO<ArgumentVO> list(ArgumentListReqDTO argumentListReqDTO) {
        BasePageVO<ArgumentVO> rets = new BasePageVO<>();
        LambdaQueryWrapper<SysArgument> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(argumentListReqDTO.getName())){
            wrapper.likeRight(SysArgument::getName, argumentListReqDTO.getName());
        }
        if(StringUtils.isNotBlank(argumentListReqDTO.getConfigKey())){
            wrapper.eq(SysArgument::getConfigKey, argumentListReqDTO.getConfigKey());
        }

        Page<SysArgument> pages = argumentDao.selectPage(new Page<>(argumentListReqDTO.getPageNo(), argumentListReqDTO.getPageSize()), wrapper);
        rets.setTotalPages(Integer.parseInt(String.valueOf(pages.getPages())));        
        rets.setTotals(Integer.parseInt(String.valueOf(pages.getTotal())));
        List<ArgumentVO> list = new ArrayList<>();
        for(SysArgument sysArgument: pages.getRecords()){
            ArgumentVO argumentVO = new ArgumentVO();
            BeanCopyUtil.copyProperties(sysArgument, argumentVO);
            list.add(argumentVO);
        }
        rets.setList(list);
        return rets;
    }

    @Override
    public Long edit(ArgumentEditReqDTO argumentEditReqDTO) throws ServiceException{
        LambdaQueryWrapper<SysArgument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysArgument::getConfigKey, argumentEditReqDTO.getConfigKey());
        SysArgument sysArgument = argumentDao.selectOne(wrapper); 
        if(sysArgument == null){
            throw new ServiceException("参数不存在");
        }
        sysArgument.setConfigKey(argumentEditReqDTO.getConfigKey());
        sysArgument.setName(argumentEditReqDTO.getName());
        sysArgument.setValue(argumentEditReqDTO.getValue());
        if(StringUtils.isNotBlank(argumentEditReqDTO.getRemark())){
            sysArgument.setRemark(argumentEditReqDTO.getRemark());
        }
        argumentDao.updateById(sysArgument);
        return sysArgument.getId();
    }

    @Override
    public ArgumentDTO getByConfigKey(String configKey) {
        // 1 根据参数业务主键查询参数对象
        SysArgument sysArgument = argumentDao.selectOne(new LambdaQueryWrapper<SysArgument>().eq(SysArgument::getConfigKey, configKey));
        // 2 做对象转换
        if (sysArgument != null) {
            ArgumentDTO argumentDTO = new ArgumentDTO();
            BeanCopyUtil.copyProperties(sysArgument, argumentDTO);
            return argumentDTO;
        }
        return null;
    }

    @Override
    public List<ArgumentDTO> getByConfigKeys(List<String> configKeys) {
        // 1 根据多个参数业务主键查询多个参数对象
        List<SysArgument> sysArguments = argumentDao.selectList(new LambdaQueryWrapper<SysArgument>().in(SysArgument::getConfigKey, configKeys));
        // 2 做对象转换
        if (!sysArguments.isEmpty()) {
            List<ArgumentDTO> result = new ArrayList<>();
            for (SysArgument sysArgument : sysArguments) {
                ArgumentDTO argumentDTO = new ArgumentDTO();
                BeanCopyUtil.copyProperties(sysArgument, argumentDTO);
                result.add(argumentDTO);
            }
            return result;
        }
        return null;
    }
}

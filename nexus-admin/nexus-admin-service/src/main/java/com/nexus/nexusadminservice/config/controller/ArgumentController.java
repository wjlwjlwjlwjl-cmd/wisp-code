package com.nexus.nexusadminservice.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusadminapi.config.domain.dto.ArgumentAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentListReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.ArgumentVO;
import com.nexus.nexusadminapi.config.feign.ArgumentFeignClient;
import com.nexus.nexusadminservice.config.service.impl.ArgumentServiceImpl;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

@RestController
public class ArgumentController implements ArgumentFeignClient{
    @Autowired
    ArgumentServiceImpl argumentServiceImpl;

    @Override
    public R<Long> add(ArgumentAddReqDTO argumentAddReqDTO) throws ServiceException {
        return R.ok(argumentServiceImpl.add(argumentAddReqDTO));
    }

    @Override
    public R<BasePageVO<ArgumentVO>> list(ArgumentListReqDTO argumentListReqDTO) {
        return R.ok(argumentServiceImpl.list(argumentListReqDTO));
    }

    @Override
    public R<Long> edit(ArgumentEditReqDTO argumentEditReqDTO) throws ServiceException{
        return R.ok(argumentServiceImpl.edit(argumentEditReqDTO));
    }

    @Override
    public ArgumentDTO getByConfigKey(String configKey) {
        return argumentServiceImpl.getByConfigKey(configKey);
    }

    @Override
    public List<ArgumentDTO> getByConfigKeys(List<String> configKeys) {
        return argumentServiceImpl.getByConfigKeys(configKeys);
    }
}

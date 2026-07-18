package com.nexus.nexusadminapi.config.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nexus.nexusadminapi.config.domain.dto.ArgumentAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentListReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.ArgumentVO;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

@FeignClient(value = "argumentFeignClient", contextId = "nexus-admin")
public interface ArgumentFeignClient {
    @PostMapping("/argument/add")
    R<Long> add(@RequestBody @Validated ArgumentAddReqDTO argumentAddReqDTO) throws ServiceException;

    @GetMapping("/argument/list")
    R<BasePageVO<ArgumentVO>> list(@Validated ArgumentListReqDTO argumentListReqDTO);

    @PostMapping("/argument/edit")
    R<Long> edit(@RequestBody @Validated ArgumentEditReqDTO argumentEditReqDTO) throws ServiceException;

    @GetMapping("/key")
    ArgumentDTO getByConfigKey(@RequestParam String configKey);

    @GetMapping("/keys")
    List<ArgumentDTO> getByConfigKeys(@RequestParam List<String> configKeys);
}

package com.nexus.nexusportalservice.controller;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.domain.dto.AppGenerateReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexusportalservice.domain.vo.AppGenerateRetVO;
import com.nexus.nexusportalservice.domain.vo.RequirementVO;
import com.nexus.nexusportalservice.service.impl.AppGenerateServiceImpl;
import com.nexus.nexusportalservice.service.impl.RequirementServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/wisp")
@RestController
@Slf4j
public class WispCodeController {

    private final RequirementServiceImpl requirementServiceImpl;
    private final AppGenerateServiceImpl appGenerateServiceImpl;

    WispCodeController(RequirementServiceImpl requirementServiceImpl, AppGenerateServiceImpl appGenerateServiceImpl) {
        this.requirementServiceImpl = requirementServiceImpl;
        this.appGenerateServiceImpl = appGenerateServiceImpl;
    }

    /**
     * 
     * @param input 用户需求
     * @return  生成结果(需求文档)
     */
    @PostMapping("/requirement/generate")
    public R<RequirementVO> generateRequirement(@RequestParam String input){
        return R.ok(requirementServiceImpl.requirementGenerate(input).convertToVO());
    }

    @PostMapping("/app/generate")
    public R<AppGenerateRetVO> generateApp(@RequestParam(value="appId") Long appId,
                                           @RequestParam(value="appDoc") String appDoc){
        //return R.ok(appGenerateServiceImpl.appGenerate(appId, appDoc).convertToVO());
        return R.ok();
    }

    private AppGenerateRetVO convert2VO(Long appId, OverAllState result) throws ServiceException {
        String status = result.value("status", String.class).orElse(null);
        if (!"SUCCESS".equals(status)) {
            String errorMsg = result.value("error", String.class).orElse(null);
            throw new ServiceException("工作流执行失败：" + errorMsg);
        }
        String appType = result.value("appType", String.class).orElse(null);
        String previewUrl = result.value("previewUrl", String.class).orElse(null);
        return new AppGenerateRetVO(appId, previewUrl, appType);
    }
}

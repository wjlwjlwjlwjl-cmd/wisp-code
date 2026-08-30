package com.nexus.nexusportalservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexusportalservice.domain.vo.AppGenerateRetVO;
import com.nexus.nexusportalservice.domain.vo.RequirementVO;
import com.nexus.nexusportalservice.service.impl.AppGenerateServiceImpl;
import com.nexus.nexusportalservice.service.impl.RequirementServiceImpl;

import lombok.extern.slf4j.Slf4j;

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
    public R<AppGenerateRetVO> generateApp(@RequestParam(value = "appId", required = false) Long appId, @RequestParam(value = "appDoc", required = false) String appDoc){
        return R.ok(appGenerateServiceImpl.appGenerate(appId, appDoc).convertToVO());
    }
}

package com.nexus.nexusportal.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexusportal.domain.vo.RequirementVO;
import com.nexus.nexusportal.service.impl.RequirementServiceImpl;

@RequestMapping("/wisp")
@RestController
public class WispCodeController {
    final RequirementServiceImpl requirementServiceImpl;

    WispCodeController(RequirementServiceImpl requirementServiceImpl) {
        this.requirementServiceImpl = requirementServiceImpl;
    }

    /**
     * 
     * @param input 用户需求
     * @return  生成结果(需求文档)
     */
    @PostMapping("/requirement/generate")
    public R<RequirementVO> generate(@RequestParam String input){
        return R.ok(requirementServiceImpl.requirementGenerate(input).convertToVO());
    }
}

package com.nexus.nexusportalservice.controller;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.domain.dto.AppGenerateReqDTO;
import com.nexus.nexusportalservice.domain.vo.AppVO;
import com.nexus.nexusportalservice.service.impl.AppBaseServiceImpl;
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
    private final AppBaseServiceImpl appBaseService;

    WispCodeController(RequirementServiceImpl requirementServiceImpl, AppGenerateServiceImpl appGenerateServiceImpl, AppBaseServiceImpl appBaseService) {
        this.requirementServiceImpl = requirementServiceImpl;
        this.appGenerateServiceImpl = appGenerateServiceImpl;
        this.appBaseService = appBaseService;
    }

    /**
     * 
     * @param input 用户需求
     * @return  生成结果(需求文档)
     */
    @PostMapping("/requirement/generate")
    public R<RequirementVO> generateRequirement(@RequestParam String input, @RequestHeader(value="Authorization") String token){
        return R.ok(requirementServiceImpl.requirementGenerate(input, token).convertToVO());
    }

    /**
     *
     * @param appId 生成的文档对应的 appid
     * @param appDoc 生成的文档
     * @return appId，应用预览链接
     * @throws Exception
     */
    @PostMapping("/app/generate")
    public R<AppGenerateRetVO> generateApp(@RequestParam(value="appId") Long appId,
                                           @RequestParam(value="appDoc") String appDoc) throws Exception {
        return R.ok(appGenerateServiceImpl.appGenerate(appId, appDoc).convertToVO());
    }

    /**
     *
     * @param token JwtToken + 查询页数
     * @return 数据库查询结果
     */
    @GetMapping("/app/list/mine")
    public BasePageVO<AppVO> listMyAppVO(@RequestHeader(value="Authorization") String token, @RequestParam(value="current")Integer current, @RequestParam(value="size")Integer pageSize){
        BasePageDTO<AppVO> basePageDTO = appBaseService.listMyApp(token, current, pageSize);
        BasePageVO<AppVO> basePageVO = new BasePageVO<>();
        BeanCopyUtil.copyProperties(basePageDTO, basePageVO);
        return basePageVO;
    }

    @GetMapping("/app/list/deploy")
    public BasePageVO<AppVO> listDeployAppVO(@RequestHeader(value="Authorization") String token, @RequestParam(value="current")Integer current, @RequestParam(value="size")Integer pageSize){
        BasePageDTO<AppVO> basePageDTO = appBaseService.listDeployApp(token, current, pageSize);
        BasePageVO<AppVO> basePageVO = new BasePageVO<>();
        BeanCopyUtil.copyProperties(basePageDTO, basePageVO);
        return basePageVO;
    }

    @GetMapping("/app/deploy")
    public Boolean deployApp(@RequestHeader("Authorization") String token, @RequestParam(value="appId") String appId){
        return true;
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

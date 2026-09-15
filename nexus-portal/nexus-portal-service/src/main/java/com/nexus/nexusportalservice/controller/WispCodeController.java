package com.nexus.nexusportalservice.controller;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.domain.dto.AppEditReqDTO;
import com.nexus.nexusportalservice.domain.dto.AppGenerateReqDTO;
import com.nexus.nexusportalservice.domain.dto.RequirementGenerateReqDTO;
import com.nexus.nexusportalservice.domain.dto.DeployAppDTO;
import com.nexus.nexusportalservice.domain.vo.*;
import com.nexus.nexusportalservice.service.impl.AppBaseServiceImpl;
import com.nexus.nexusportalservice.service.impl.AppEditServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexusportalservice.service.impl.AppGenerateServiceImpl;
import com.nexus.nexusportalservice.service.impl.RequirementServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/wisp")
@RestController
@Slf4j
public class WispCodeController {

    private final RequirementServiceImpl requirementServiceImpl;
    private final AppGenerateServiceImpl appGenerateServiceImpl;
    private final AppBaseServiceImpl appBaseService;
    private final AppEditServiceImpl appEditService;

    WispCodeController(RequirementServiceImpl requirementServiceImpl, AppGenerateServiceImpl appGenerateServiceImpl, AppBaseServiceImpl appBaseService, AppEditServiceImpl appEditService) {
        this.requirementServiceImpl = requirementServiceImpl;
        this.appGenerateServiceImpl = appGenerateServiceImpl;
        this.appBaseService = appBaseService;
        this.appEditService = appEditService;
    }

    /**
     * 
     * @param req 用户需求(input)
     * @return  生成结果(需求文档)
     */
    @PostMapping("/requirement/generate")
    public R<RequirementVO> generateRequirement(@RequestBody RequirementGenerateReqDTO req, @RequestHeader(value="Authorization") String token){
        return R.ok(requirementServiceImpl.requirementGenerate(req.getInput(), token).convertToVO());
    }

    /**
     *
     * @param req appId + appDoc（请求体）
     * @return appId，应用预览链接
     * @throws Exception
     */
    @PostMapping("/app/generate")
    public R<AppGenerateRetVO> generateApp(@RequestBody AppGenerateReqDTO req) throws Exception {
        return R.ok(appGenerateServiceImpl.appGenerate(req.getAppId(), req.getAppDoc()).convertToVO());
    }

    @GetMapping("/app/vs")
    public R<String> getVscodeUrl(@RequestHeader(value="Authorization") String token, @RequestParam(value="appId") Long appId){
        return R.ok(appEditService.vscodeAppEdit(token, appId));
    }

    @GetMapping("/app/vs/confirm")
    public R<Boolean> confirmVscodeEdit(@RequestHeader(value="Authorization") String token, @RequestParam(value="appId")String appId){
        return R.ok(appEditService.confirmVscodeEdit(token, appId));
    }

    /**
     *
     * @param token JwtToken + 查询页数
     * @return 数据库查询结果
     */
    @GetMapping("/app/list/mine")
    public R<BasePageVO<AppVO>> listMyAppVO(@RequestHeader(value="Authorization") String token, @RequestParam(value="current")Integer current, @RequestParam(value="size")Integer pageSize){
        BasePageDTO<AppVO> basePageDTO = appBaseService.listMyApp(token, current, pageSize);
        BasePageVO<AppVO> basePageVO = new BasePageVO<>();
        BeanCopyUtil.copyProperties(basePageDTO, basePageVO);
        return R.ok(basePageVO);
    }

    /**
     * 列出来所有已经部署的应用
     *
     * @param token jwt token
     * @param current 当前分页
     * @param pageSize 分页大小
     * @return 应用页
     */
    @GetMapping("/app/list/deploy")
    public R<BasePageVO<AppVO>> listDeployAppVO(@RequestHeader(value="Authorization") String token, @RequestParam(value="current")Integer current, @RequestParam(value="size")Integer pageSize){
        BasePageDTO<AppVO> basePageDTO = appBaseService.listDeployApp(token, current, pageSize);
        BasePageVO<AppVO> basePageVO = new BasePageVO<>();
        BeanCopyUtil.copyProperties(basePageDTO, basePageVO);
        return R.ok(basePageVO);
    }

    /**
     * 部署一个应用
     * @param token jwt token
     * @param appId 应用id
     * @return 是否部署成功
     */
    @GetMapping("/app/deploy")
    public R<DeployAppVO> deployApp(@RequestHeader("Authorization") String token, @RequestParam(value="appId") String appId){
        DeployAppVO deployAppVO = appBaseService.appDeploy(token, appId, true).convert2VO();
        return R.ok(deployAppVO, "部署成功");
    }

    /**
     * 取消部署应用
     *
     * @param token jwt token
     * @param appId 应用 id
     * @return 是否取消部署成功
     */
    @GetMapping("/app/deploy/cancel")
    public R<DeployAppVO> deployAppCancel(@RequestHeader("Authorization") String token, @RequestParam(value="appId") String appId){
        DeployAppVO deployAppVO = appBaseService.appDeploy(token, appId, false).convert2VO();
        return R.ok(deployAppVO, "取消部署成功");
    }

    /**
     * 获取应用信息
     *
     * @param appId 应用 Id
     * @return 应用信息
     */
    @GetMapping("/app/detail")
    public R<AppVO> appDetail(@RequestParam(value="appId") String appId){
        return R.ok(appBaseService.appDetail(appId).convert2VO());
    }

    /**
     *
     * @param appId 应用 Id
     * @return 应用历史，0 为 用户，1 为 ai
     */
    @GetMapping("/app/history")
    public R<List<ChatHistoryVO>> appHistory(@RequestParam String appId){
        return R.ok(BeanCopyUtil.copyListProperties(appBaseService.appHistory(appId), ChatHistoryVO::new));
    }

    /**
     *
     * @param req appId + newPrompt（请求体）
     * @return 修改后信息（同创建)
     * @throws Exception Exception
     */
    @PostMapping("/app/edit")
    public R<AppGenerateRetVO> appEdit(@RequestBody AppEditReqDTO req) throws Exception{
        return R.ok(appEditService.appEdit(req.getAppId(), req.getNewPrompt()).convertToVO());
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

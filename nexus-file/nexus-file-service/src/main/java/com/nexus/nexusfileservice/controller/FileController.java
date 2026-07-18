package com.nexus.nexusfileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexusfileservice.domain.dto.FileDTO;
import com.nexus.nexusfileservice.domain.dto.SignDTO;
import com.nexus.nexusfileservice.domain.vo.FileVO;
import com.nexus.nexusfileservice.domain.vo.SignVO;
import com.nexus.nexusfileservice.service.FileServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({"null"})
@Controller
public class FileController {
    @Autowired    
    FileServiceImpl fileService;

    /**
     * Web 端将文件交给服务器，随后由服务器上传到 OSS
     * 
     * @param   file 要上传的文件
     * @return  上传结果
     */
    @ResponseBody
    @PostMapping("/upload")
    public R<FileVO> upload(MultipartFile file){
        FileDTO fileDTO = fileService.upload(file);
        FileVO fileVO = new FileVO();
        BeanCopyUtil.copyProperties(fileDTO, fileVO);
        return R.ok(fileVO);
    }

    /**
     * Web 端把需求交给服务器，服务器到 STS 申请临时访问凭证并由此计算出签名返回给 Web 端，Web 端直接通过签名向 OSS 存储
     * 
     * @return   获取结果
     */
    @ResponseBody
    @RequestMapping("/sign")
    public R<SignVO> getSign(){
        SignDTO signDTO = fileService.getSign();
        SignVO signVO = new SignVO();
        BeanCopyUtil.copyProperties(signDTO, signVO);
        return R.ok(signVO);
    }
    
    @RequestMapping("/submit")
    public String submit(){
        return "/submit";
    }
}

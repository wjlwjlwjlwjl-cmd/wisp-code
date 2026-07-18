package com.nexus.nexusfileservice.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.lang.Exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alibaba.nacos.common.codec.Base64;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.nexus.nexusfileservice.config.OSSAutoConfiguration;
import com.nexus.nexusfileservice.config.OSSProperties;
import com.nexus.nexusfileservice.constants.OSSCustomConstants;
import com.nexus.nexusfileservice.domain.dto.FileDTO;
import com.nexus.nexusfileservice.domain.dto.SignDTO;
import com.nexus.nexusfileservice.service.impl.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings({ "null" })
public class FileServiceImpl implements IFileService {
    @Autowired
    OSSAutoConfiguration ossAutoConfiguration;

    @Autowired
    OSSProperties ossProperties; // 从 Nacos 中读取的 oss 配置信息

    public FileDTO upload(MultipartFile file) {
        FileDTO fileDTO = new FileDTO();

        try {
            String originalFileName = file.getOriginalFilename();
            String extName = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
            String objectName = ossProperties.getPathPrefix() + UUID.randomUUID() + '.' + extName;

            String bucketName = ossProperties.getBucketName();

            OSSClient client = ossAutoConfiguration.ossClient(ossProperties);
            client.putObject(bucketName, objectName, new ByteArrayInputStream(file.getBytes()));

            fileDTO.setKey(objectName);
            fileDTO.setUrl(ossProperties.getBaseUrl());
            fileDTO.setName(new File(objectName).getName());
        } catch (ClientException e) {
            log.warn("[oss] upload fail: ", e);
        } catch (IOException e) {
            log.warn("[oss] MultipartFile get byte fail: ", e);
        }

        return fileDTO;
    }

    public SignDTO getSign() {
        SignDTO signDTO = new SignDTO();
        signDTO.setHost(ossProperties.getBaseUrl());
        signDTO.setPathPrefix(ossProperties.getPathPrefix());

        String accesskeyid = ossProperties.getAccessKeyId();
        String accesskeysecret = ossProperties.getAccessKeySecret();

        Instant now = Instant.now();

        try {
            // 步骤1：创建policy。
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> policy = new HashMap<>(); //配置过期时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OSSCustomConstants.SIGN_EXPIRE_TIME_FORMAT).withZone(java.time.ZoneOffset.UTC);
            String expireString = formatter.format(now.plusMillis(ossProperties.getExpre()));
            policy.put("expiration", expireString); 

            List<Object> conditions = new ArrayList<>();

            Map<String, String> bucketCondition = new HashMap<>(); //设置空间名称
            bucketCondition.put("bucket", ossProperties.getBucketName());
            conditions.add(bucketCondition);

            Map<String, String> signatureVersionCondition = new HashMap<>(); //设置签名，固定值为 OSS4-HMAC-SHA256
            signatureVersionCondition.put("x-oss-signature-version", "OSS4-HMAC-SHA256");
            conditions.add(signatureVersionCondition);

            Map<String, String> credentialCondition = new HashMap<>(); //设置 xOSSCredential
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(OSSCustomConstants.SIGN_DATE_FORMAT).withZone(java.time.ZoneOffset.UTC);
            String date = formatter2.format(now);
            String xOSSCredential = accesskeyid + "/" + date + ossProperties.getRegion() + "/oss/aliyun_v4_request";
            signDTO.setXOSSCredential(xOSSCredential);
            credentialCondition.put("x-oss-credential", xOSSCredential);
            conditions.add(credentialCondition);

            Map<String, String> dateCondition = new HashMap<>(); //设置 xOSSDate
            DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern(OSSCustomConstants.SIGN_REQUEST_TIME_FORMAT).withZone(java.time.ZoneOffset.UTC);
            String dateConditionStr = formatter3.format(now);
            dateCondition.put("x-oss-date", dateConditionStr);
            conditions.add(dateCondition);
            signDTO.setXOSSDate(dateConditionStr);

            conditions.add(Arrays.asList("content-length-range", 1, 10));
            conditions.add(Arrays.asList("eq", "$success_action_status", "201"));
            conditions.add(Arrays.asList("starts-with", "$key", "user/eric/"));
            conditions.add(Arrays.asList("in", "$content-type", Arrays.asList("image/jpg", "image/png")));
            conditions.add(Arrays.asList("not-in", "$cache-control", Arrays.asList("no-cache")));

            policy.put("conditions", conditions);

            String jsonPolicy = mapper.writeValueAsString(policy); //将 Map<String, Object> 的 policy 进行序列化
            // 步骤2：构造待签名字符串（StringToSign）。
            String policyBase64 = new String(Base64.encodeBase64(jsonPolicy.getBytes()));
            signDTO.setPolicy(policyBase64);

            // 步骤3：计算SigningKey。
            byte[] dateKey = hmacsha256(("aliyun_v4" + accesskeysecret).getBytes(), dateConditionStr);
            byte[] dateRegionKey = hmacsha256(dateKey, ossProperties.getRegion());
            byte[] dateRegionServiceKey = hmacsha256(dateRegionKey, "oss");
            byte[] signingKey = hmacsha256(dateRegionServiceKey, "aliyun_v4_request");

            // 步骤4：计算Signature。
            byte[] result = hmacsha256(signingKey, policyBase64);
            String signature = BinaryUtil.toHex(result);
            signDTO.setSignature(signature);
        } catch (JsonProcessingException e) {
            log.warn("构造待签名字符串失败：", e);
        }
        return signDTO;
    }

    private static byte[] hmacsha256(byte[] key, String data) {
        try {
            // 初始化HMAC密钥规格，指定算法为HMAC-SHA256并使用提供的密钥。
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");

            // 获取Mac实例，并通过getInstance方法指定使用HMAC-SHA256算法。
            Mac mac = Mac.getInstance("HmacSHA256");
            // 使用密钥初始化Mac对象。
            mac.init(secretKeySpec);

            // 执行HMAC计算，通过doFinal方法接收需要计算的数据并返回计算结果的数组。
            byte[] hmacBytes = mac.doFinal(data.getBytes());

            return hmacBytes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

}

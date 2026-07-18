package com.nexus.nexusadminservice.config.service;

import java.util.List;

import com.nexus.nexusadminapi.config.domain.dto.ArgumentAddReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentEditReqDTO;
import com.nexus.nexusadminapi.config.domain.dto.ArgumentListReqDTO;
import com.nexus.nexusadminapi.config.domain.vo.ArgumentVO;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

public interface IArgumentService {
    /**
     * 添加参数配置
     * 
     * @param argumentAddReqDTO
     * @return  添加参数配置对应 id
     * @throws ServiceException 
     */
    public Long add(ArgumentAddReqDTO argumentAddReqDTO) throws ServiceException;

    /**
     * 获取参数配置（精确匹配 configKey，name 模糊匹配）
     * 
     * @param argumentListReqDTO
     * @return
     */
    public BasePageVO<ArgumentVO> list(ArgumentListReqDTO argumentListReqDTO);

    public Long edit(ArgumentEditReqDTO argumentEditReqDTO) throws ServiceException;

    /**
     * 根据参数键查询参数对象
     * @param configKey 参数键
     * @return 参数对象
     */
    ArgumentDTO getByConfigKey(String configKey);

    /**
     * 根据多个参数键查询多个参数对象
     * @param configKeys 多个参数键
     * @return 多个参数对象
     */
    List<ArgumentDTO> getByConfigKeys(List<String> configKeys);
}

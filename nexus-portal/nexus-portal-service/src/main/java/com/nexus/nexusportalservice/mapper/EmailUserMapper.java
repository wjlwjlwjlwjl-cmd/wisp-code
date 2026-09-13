package com.nexus.nexusportalservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusportalservice.domain.entity.EmailUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailUserMapper extends BaseMapper<EmailUser> {
}

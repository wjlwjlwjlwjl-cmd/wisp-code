package com.nexus.nexusportalservice.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chat_history")
public class Memory {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private Long appId; //应用主键Id，conversationId
    private int msgRole; //消息角色：0 用户，1 ai
    private String content; //消息内容
}

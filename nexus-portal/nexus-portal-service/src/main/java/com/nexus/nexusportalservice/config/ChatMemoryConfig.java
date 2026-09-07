package com.nexus.nexusportalservice.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommonredis.service.RedisService;
import com.nexus.nexusportalservice.domain.dto.ChatHistoryDTO;
import com.nexus.nexusportalservice.domain.entity.Memory;
import com.nexus.nexusportalservice.enums.MessageTypeEnum;
import com.nexus.nexusportalservice.mapper.MemoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ChatMemoryConfig implements ChatMemory {
    @Value("${chat.memory.maxLen: 5}")
    int maxLen;

    @Value("${chat.memory.ttl: 24}")
    int ttl;

    @Autowired
    RedisService redisService;

    @Autowired
    MemoryMapper memoryMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {
        String listKey = getChatCacheKey(conversationId);
        for(Message message: messages){
            String messageType = message.getMessageType().getValue();
            if(messageType.equals(MessageTypeEnum.ASSISTANT.getAppDesc())){
                persist(conversationId, message, 1, listKey);
            }
            else if(messageType.equals(MessageTypeEnum.USER.getAppDesc())){
                persist(conversationId, message, 0, listKey);
            }
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        List<ChatHistoryDTO> messages = new ArrayList<>();
        String listKey = getChatCacheKey(conversationId);
        //先尝试从 Redis 中获取
        List<ChatHistoryDTO> cachedList = redisService.getCacheList(listKey, ChatHistoryDTO.class);
        if(!cachedList.isEmpty()){
            return toAiMessage(cachedList);
        }
        //Redis中没有，那么就从 Mysql 中获取
        List<Memory> memories = memoryMapper.selectList(new LambdaQueryWrapper<Memory>().eq(Memory::getAppId, conversationId).last("limit " + maxLen).orderByAsc(Memory::getId));
        List<ChatHistoryDTO> listToCache = BeanCopyUtil.copyListProperties(memories, ChatHistoryDTO::new);
        //更新到 Redis
        if(!listToCache.isEmpty()){
            redisService.setCacheList(listKey, listToCache);
            redisService.expire(listKey, 24, TimeUnit.HOURS);
        }

        return toAiMessage(listToCache);
    }

    @Override
    public void clear(String conversationId) {
        String listKey = getChatCacheKey(conversationId);
        //清空 redis
        redisService.removeForAllList(listKey);
        //清空 mysql
        memoryMapper.delete(new LambdaQueryWrapper<Memory>().eq(Memory::getAppId, conversationId));
    }

    private void persist(String conversationId, Message message, int appType, String listKey){
        //添加到数据库
        Memory memory = new Memory();
        memory.setAppId(Long.valueOf(conversationId));
        memory.setMsgRole(appType);
        memory.setContent(message.getText());
        memoryMapper.insert(memory);

        //添加到 redis
        ChatHistoryDTO chatHistoryDTO = new ChatHistoryDTO();
        BeanCopyUtil.copyProperties(memory, chatHistoryDTO);
        redisService.rightPushForList(listKey, chatHistoryDTO);
        redisService.trimList(listKey, maxLen);
        redisService.expire(listKey, ttl, TimeUnit.HOURS);
    }

    private String getChatCacheKey(String conversationId){
        return "chat:history:key:" + conversationId;
    }

    private List<Message> toAiMessage(List<ChatHistoryDTO> chatHistoryDTOs){
        List<Message> rets = new ArrayList<>();
        for(ChatHistoryDTO chatHistoryDTO: chatHistoryDTOs){
            if(chatHistoryDTO.getMsgRole() == MessageTypeEnum.USER.getAppType()){
                rets.add(new UserMessage(chatHistoryDTO.getContent()));
            }
            else{
                rets.add(new AssistantMessage(chatHistoryDTO.getContent()));
            }
        }
        return rets;
    }
}

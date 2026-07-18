package com.nexus.nexuscommondomain.constants;

import lombok.Getter;

/**
 * 当线程池被打满时的执行策略
 * 
 * RejectType
 */
@Getter
public enum RejectType{
    AbortPolicy(1),             // 默认，直接抛出 RejectedExecutionException
    DiscardPolicy(2),           // 静默丢弃当前新任务，无异常
    DiscardOldestPolicy(3),     // 丢弃队列最老未执行任务，重新提交当前任务
    CallerRunsPolicy(4);        // 有提交任务的线程执行任务

    private Integer handler;

    RejectType(int handler){
        this.handler = handler;
    }
}
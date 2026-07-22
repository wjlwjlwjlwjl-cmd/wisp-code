package com.nexus.nexuscommoncore.config;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/*public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler)
*/

@EnableAsync
@Configuration
public class ThreadPoolConfig {
    @Value("${thread.pool-executor.corePoolSize: 5}")
    private int corePoolSize;                       //核心线程数

    @Value("${thread.pool-executor.maximumPoolSize: 10}")
    private int maximumPoolSize;                    //最大线程数

    @Value("${thread.pool-executor.keepAliveSeconds: 60}")
    private long keepAliveSeconds;                  //线程最大空闲存活时间

    @Value("${thread.pool-executor.queueCapacity: 100}")
    private int queueCapacity;                      //任务队列大小

    @Value("${thread.pool-executor.prefixName: thread-service-}")
    private String prefixName;                      //线程名称前缀

    @Value("${thread.pool-executor.rejectHandler}")
    private int rejectHandler;                      //线程池被打满时的拒绝策略

    @Bean("threadPoolExecutor")
    public Executor getThreadPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize);
        executor.setKeepAliveSeconds(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefixName);
        executor.setRejectedExecutionHandler(gExecutionHandler(rejectHandler));
        return executor;
    }

    private RejectedExecutionHandler gExecutionHandler(int option){
        if(option == 1){
            return new ThreadPoolExecutor.AbortPolicy();
        }
        else if(option == 2){
            return new ThreadPoolExecutor.DiscardPolicy();
        }
        else if(option == 3){
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
        else{
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    }
}
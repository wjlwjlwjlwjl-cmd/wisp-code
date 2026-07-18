package com.nexus.nexusgateway.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.nexus.nexuscommoncore.utils.JsonUtil;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.ResultCode;
import com.nexus.nexuscommondomain.exception.ServiceException;

@Slf4j
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    /**
     * 网关统一异常处理，捕获服务路径错误、自定义异常及其他非服务接口错误
     * 
     * @param exchange ServerWebExchange
     * @param ex 异常信息
     * @return 无
     */
    @Override
    public @NonNull Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if(response.isCommitted()){
            return Mono.error(ex); //响应已经到达客户端，无法再进行修改
        }
        int errCode = ResultCode.ERROR.getCode();
        String errMsg = ResultCode.ERROR.getMsg();
        if(ex instanceof NoResourceFoundException){
            errCode = ResultCode.SERVICE_NOT_FOUND.getCode();
            errMsg = ResultCode.SERVICE_NOT_FOUND.getMsg();
        }
        else if(ex instanceof ServiceException){
            errCode = ((ServiceException)ex).getCode();
            errMsg = ex.getMessage();
        }

        //和服务处的统一异常处理相同，取错误码的前三位作为 http 响应状态码
        int httpCode = Integer.parseInt(String.valueOf(errCode).substring(0, 3));
        log.warn("Gateway Captured Exception, errCode: {}, errMsg: {}", errCode, errMsg);
        webFluxResponseWriter(response, httpCode, errCode, errMsg);

        return webFluxResponseWriter(response, httpCode, errCode, errMsg);
    }

    //根据 handle 获得的信息获取更多返回响应配置
    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, int httpCode, int errCode, Object errMsg){
        return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE, httpCode, errCode, errMsg);
    }

    //最后整体设置响应信息，响应格式，http状态码，错误码，错误信息
    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType, int httpCode, int errCode, Object errMsg){
        response.setStatusCode(HttpStatus.valueOf(httpCode));
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        R<?> result = R.fail(errCode, (String)errMsg);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JsonUtil.object2String(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }
}

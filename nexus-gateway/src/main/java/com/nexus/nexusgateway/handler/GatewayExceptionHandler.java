package com.nexus.nexusgateway.handler;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.ResultCode;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexuscommoncore.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
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
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public @NonNull Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        int errCode = ResultCode.ERROR.getCode();
        String errMsg = ResultCode.ERROR.getMsg();

        if (ex instanceof NoResourceFoundException) {
            errCode = ResultCode.SERVICE_NOT_FOUND.getCode();
            errMsg = ResultCode.SERVICE_NOT_FOUND.getMsg();
        } else if (ex instanceof ServiceException) {
            errCode = ((ServiceException) ex).getCode();
            errMsg = ex.getMessage();
        }

        // 安全解析http状态码
        int httpCode;
        String codeStr = String.valueOf(errCode);
        if (codeStr.length() >= 3) {
            int tempCode;
            try {
                tempCode = Integer.parseInt(codeStr.substring(0, 3));
                HttpStatus.valueOf(tempCode);
                httpCode = tempCode;
            } catch (Exception e) {
                httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
        } else {
            httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        log.warn("Gateway Captured Exception, errCode: {}, errMsg: {}", errCode, errMsg, ex);
        return webFluxResponseWriter(response, httpCode, errCode, errMsg);
    }

    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, int httpCode, int errCode, Object errMsg) {
        return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE, httpCode, errCode, errMsg);
    }

    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType, int httpCode, int errCode, Object errMsg) {
        response.setStatusCode(HttpStatus.valueOf(httpCode));
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        R<?> result = R.fail(errCode, (String) errMsg);
        byte[] bytes = JsonUtil.object2String(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(dataBuffer));
    }
}

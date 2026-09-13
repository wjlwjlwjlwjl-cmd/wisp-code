package com.nexus.nexuscommonsecurity.handler;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.ResultCode;
import com.nexus.nexuscommondomain.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public <T> R<T> handleMethodNotAllowed(
            MethodNotAllowedException e,
            ServerWebExchange exchange) {

        String requestURI = exchange.getRequest().getURI().getPath();

        log.warn(
                "Request Address: '{}', Request Method not supported, method:{}",
                requestURI,
                exchange.getRequest().getMethod()
        );

        exchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);

        return R.fail(
                ResultCode.REQUEST_METHOD_NOT_SUPPORTED.getCode(),
                ResultCode.REQUEST_METHOD_NOT_SUPPORTED.getMsg()
        );
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            ServerWebExchange exchange) {

        String requestURI = exchange.getRequest().getURI().getPath();

        log.warn(
                "Request Address: '{}', Method Argument Type Mismatch, field:{}, value:{}",
                requestURI,
                e.getName(),
                e.getValue()
        );

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

        return R.fail(
                ResultCode.PARA_TYPE_MISMATCH.getCode(),
                ResultCode.PARA_TYPE_MISMATCH.getMsg()
        );
    }

    /**
     * 业务自定义异常
     */
    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(
            ServiceException e,
            ServerWebExchange exchange) {

        String requestURI = exchange.getRequest().getURI().getPath();

        log.warn(
                "Request Address: '{}', ServiceException Occurred, code:{}, msg:{}, more Info: {}",
                requestURI,
                e.getCode(),
                e.getMessage(),
                e.getStackTrace()
        );

        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(
            RuntimeException e,
            ServerWebExchange exchange) {

        String requestURI = exchange.getRequest().getURI().getPath();

        log.error(
                "Request Address: '{}', RuntimeException Occurred",
                requestURI,
                e
        );

        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        return R.fail(
                ResultCode.ERROR.getCode(),
                ResultCode.ERROR.getMsg()
        );
    }

    /**
     * RequestBody 参数校验异常
     *
     * WebFlux 对应 WebExchangeBindException
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public R<?> handleWebExchangeBindException(
            WebExchangeBindException e,
            ServerWebExchange exchange) {

        String requestURI = exchange.getRequest().getURI().getPath();

        log.warn(
                "Request Address: '{}', RequestBody parameter validation failed",
                requestURI
        );

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

        String message = getAllErrorMsg(e);

        return R.fail(
                ResultCode.ERROR.getCode(),
                message
        );
    }

    /**
     * @RequestParam / @PathVariable 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintViolationException(
            ConstraintViolationException e,
            ServerWebExchange exchange) {

        String requestURI = exchange.getRequest().getURI().getPath();

        log.warn(
                "Request Address: '{}', RequestParam/PathVariable parameter validation failed",
                requestURI
        );

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

        String message = getConstraintViolationMsg(e);

        return R.fail(
                ResultCode.ERROR.getCode(),
                message
        );
    }

    /**
     * WebFlux 参数绑定错误信息
     */
    private String getAllErrorMsg(WebExchangeBindException e) {

        StringBuilder sb = new StringBuilder();

        e.getAllErrors().forEach(error ->
                sb.append(error.getDefaultMessage()).append("; ")
        );

        return sb.toString().trim();
    }

    /**
     * ConstraintViolation 错误信息
     */
    private String getConstraintViolationMsg(
            ConstraintViolationException e) {

        StringBuilder sb = new StringBuilder();

        Set<ConstraintViolation<?>> violationSet =
                e.getConstraintViolations();

        for (ConstraintViolation<?> violation : violationSet) {
            sb.append(violation.getMessage()).append("; ");
        }

        return sb.toString().trim();
    }
}
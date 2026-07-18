package com.nexus.nexuscommonsecurity.handler;

import java.util.List;

import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.ResultCode;
import com.nexus.nexuscommondomain.exception.ServiceException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 
     * @param response Http 响应
     * @param code     完整状态码，取前三位作为 Http 响应状态码
     */
    private static void setResponseCode(HttpServletResponse response, int code) {
        String errCode = (code + "").substring(0, 3);
        response.setStatus(Integer.parseInt(errCode));
    }

    /**
     * 
     * @param <T>           统一返回结果类型模板
     * @param e             请求方法不支持异常
     * @param request       Http 请求
     * @param response      Http 响应
     * @return              统一异常处理返回结果
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public static <T> R<T> httpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request, HttpServletResponse response) {
        setResponseCode(response, ResultCode.REQUEST_METNHOD_NOT_SUPPORTED.getCode());
        return R.fail(ResultCode.REQUEST_METNHOD_NOT_SUPPORTED.getCode(), "Request Method not supported");
    }

    /**
     * 类型不匹配异常
     *
     * @param e         异常信息
     * @param response  响应
     * @return          不匹配结果
     */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public R<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
            HttpServletResponse response) {
        log.error("Method Argument Type Mismatch", e);
        setResponseCode(response, ResultCode.PARA_TYPE_MISMATCH.getCode());
        return R.fail(ResultCode.PARA_TYPE_MISMATCH.getCode(),
                ResultCode.PARA_TYPE_MISMATCH.getMsg());
    }

    /**
     * url未找到异常
     *
     * @param e         异常信息
     * @param response  响应
     * @return          异常结果
     */
    @ExceptionHandler({ NoResourceFoundException.class })
    public R<?> handleMethodNoResourceFoundException(NoResourceFoundException e,
            HttpServletResponse response) {
        log.error("No Resource Found for URL", e);
        setResponseCode(response, ResultCode.URL_NOT_FOUND.getCode());
        return R.fail(ResultCode.URL_NOT_FOUND.getCode(),
                ResultCode.URL_NOT_FOUND.getMsg());
    }

    /**
     * 拦截运行时异常
     *
     * @param e         异常信息
     * @param request   请求信息
     * @param response  响应信息
     * @return          响应结果
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request,
            HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("Request Address: '{}', RuntimeException Occured: {}", requestURI, e);
        setResponseCode(response, ResultCode.ERROR.getCode());
        return R.fail(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg());
    }

    /**
     * 系统异常
     * 
     * @param e         通用异常信息
     * @param request   请求
     * @param response  响应
     * @return          响应结果
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("Requrest Address: '{}', Exception Occured: {}.", requestURI, e);
        setResponseCode(response, ResultCode.ERROR.getCode());
        return R.fail(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg());
    }

    /**
     * 
     * @param e         服务异常
     * @param request   Http 请求
     * @param response  Http 响应
     * @return          响应结果
     */
    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(ServiceException e, HttpServletRequest request, 
        HttpServletResponse response){
        String requestURI = request.getRequestURI();
        log.error("Requrest Address: '{}', ServiceException Occured: {}.", requestURI, e);
        setResponseCode(response, ResultCode.ERROR.getCode());
        return R.fail(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg());
    }

    /**
     * RequestBody 传递参数时发生异常（POST、PUT等）
     * 
     * @param e         参数校验失败异常
     * @param request   Http 请求
     * @param response  Http 响应
     * @return          响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response){
        String requestURI = request.getRequestURI();
        log.error("Request Address{}, Arguments in Request Body Break the Law: {}.", requestURI, e);
        setResponseCode(response, ResultCode.ERROR.getCode());

        return R.fail(ResultCode.ERROR.getCode(), getAllErrorMsg(e));
    }

    /**
     * 捕获 GET 等不通过 RequestBody 传递参数的服务发生参数校验一场的捕获
     * 
     * @param e         异常
     * @param request   Http 请求 
     * @param response  Http 响应
     * @return          响应结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response){
        String requestURI = request.getRequestURI();
        log.error("Request Address{}, Arguments in RequestParams、PathVariables or other Places Break the Law: {}", requestURI, e);
        setResponseCode(response, ResultCode.ERROR.getCode());
        return R.fail(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg());
    }

    private String getAllErrorMsg(MethodArgumentNotValidException e){
        StringBuilder stringBuilder = new StringBuilder();
        List<ObjectError> errors = e.getAllErrors();
        for(ObjectError error: errors){
            stringBuilder.append(error.toString() + " || ");
        }
        return stringBuilder.toString();
    }
}

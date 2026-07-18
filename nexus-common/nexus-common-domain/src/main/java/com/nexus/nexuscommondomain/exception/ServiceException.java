package com.nexus.nexuscommondomain.exception;

import com.nexus.nexuscommondomain.domain.ResultCode;

public class ServiceException extends Exception{
    /**
     * 错误代码
     */
    private int errCode;
    
    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * ServiceException 构造方法（推荐方式）
     * 
     * @param resultCode
     */
    public ServiceException(ResultCode resultCode){
        this.errCode = resultCode.getCode();
        this.errMsg = resultCode.getMsg();
    }

    /**
     * ServiceException 构造方法（定制错误码、错误信息）
     * 
     * @param errCode
     * @param errMsg
     */
    public ServiceException(int errCode, String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    public ServiceException(String errMsg, int errCode){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    /**
     * ServiceException 构造方法（使用通用错误码）
     * 
     * @param errMsg 错误信息
     */
    public ServiceException(String errMsg){
        this.errCode = ResultCode.ERROR.getCode();
        this.errMsg = errMsg;
    }

    public int getCode(){
        return errCode;
    }
    public String getMsg(){
        return errMsg;
    }
}
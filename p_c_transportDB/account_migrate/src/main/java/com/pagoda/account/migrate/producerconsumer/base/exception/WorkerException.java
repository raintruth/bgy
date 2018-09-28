package com.pagoda.account.migrate.producerconsumer.base.exception;

import lombok.Getter;

/**
 * @ClassName WorkerException
 * @author wfg
 * @Date: 2018/9/17 9:47
 * @Description: TODD
 * @Version 1.0
 */
@Getter
public class WorkerException extends RuntimeException{

    private static final long serialVersionUID = 6401507641287L;

    /** 异常代码 */
    protected Integer code;

    /** 异常消息 */
    protected String message;

    public WorkerException() {
        super();
    }

    public WorkerException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    public WorkerException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public WorkerException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "errorCode: " + code + ", message: " + message;
    }

}

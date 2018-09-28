package com.pagoda.account.migrate.producerconsumer.base.normal;

import java.io.Serializable;
import java.util.Objects;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author wfg
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 12345L;
    public static final String RESULT_PATTERN = "【执行结果】：{getExeResult}! msg: ({message}), 【总共耗时】：({intervalMs})秒, 【工作耗时】：({workingIntervalMs})秒, 【等待耗时】：({waitIntervalMs})秒, 【工作信息】: ({workingInfo}), 【异常信息】: ({workingExceptionInfo})。";

    private String message;

    private int code;

    private T data;

    /**
     * 工作的信息
     */
    private StringBuilder workingInfo;
    /**
     * 出现失败的信息
     */
    private StringBuilder workingExceptionInfo;
    /**
     * 耗时Ms
     */
    private long intervalMs;

    /**
     * 实际工作时间Ms
     */
    private long workingIntervalMs;


    /**
     * 实际工作时间Ms
     */
    private long waitIntervalMs;

    {
        workingInfo = new StringBuilder();
        workingExceptionInfo = new StringBuilder();

    }

    public Result() {
        super();
    }


    /**
     * 获取 格式化的结果
     * @return
     */
    public String getBeautyResult() {
        //ResultCode.statusOf(code)
        String beautyResult = RESULT_PATTERN.replace("{getExeResult}", getExeResult()).replace("{message}", message)
                .replace("{intervalMs}", intervalMs/1000.0 + "").replace("{workingIntervalMs}", workingIntervalMs/1000.0 + "").replace("{waitIntervalMs}", waitIntervalMs/1000.0 + "")
                .replace("{workingInfo}", workingInfo.toString()).replace("{workingExceptionInfo}", workingExceptionInfo.toString());
        return beautyResult;
    }


    public boolean isSuccess() {
        return Objects.equals(String.valueOf(code), ResultCode.SUCCESS.getSCode());
    }

    public String getExeResult() {
        return isSuccess() ? "SUCCESS" : "FAIL";
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public StringBuilder getWorkingInfo() {
        return workingInfo;
    }

    public Result setWorkingInfo(StringBuilder workingInfo) {
        this.workingInfo = workingInfo;
        return this;
    }

    public StringBuilder getWorkingExceptionInfo() {
        return workingExceptionInfo;
    }

    public Result setWorkingExceptionInfo(StringBuilder workingExceptionInfo) {
        this.workingExceptionInfo = workingExceptionInfo;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public long getIntervalMs() {
        return intervalMs;
    }

    public Result setIntervalMs(long intervalMs) {
        this.intervalMs = intervalMs;
        return this;
    }
    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    public long getWorkingIntervalMs() {
        return workingIntervalMs;
    }

    public Result setWorkingIntervalMs(long workingIntervalMs) {
        this.workingIntervalMs = workingIntervalMs;
        return this;
    }

    public long getWaitIntervalMs() {
        return waitIntervalMs;
    }

    public Result setWaitIntervalMs(long waitIntervalMs) {
        this.waitIntervalMs = waitIntervalMs;
        return this;
    }

}

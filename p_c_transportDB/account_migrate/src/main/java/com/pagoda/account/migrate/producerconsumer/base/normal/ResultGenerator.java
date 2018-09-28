package com.pagoda.account.migrate.producerconsumer.base.normal;


/**
 * @author wfg
 * 响应结果生成工具
 */
public class ResultGenerator {

    private ResultGenerator() {
    }

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMsg());
    }

    public static Result genSuccessResult(Object data) {
        return genSuccessResult(data, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static Result genSuccessResult(Object data, Integer code, String message) {
        return new Result()
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }

    public static Result genEmptyResult() {
        return genSuccessResult();
    }
    public static Result genEmptyDataResult() {
        return genFailResult(ResultCode.EMPTY);
    }
    public static Result genFailResult(ResultCode resultCode) {
        return genFailResult(resultCode.getCode(), resultCode.getMsg());
    }

    public static Result genFailResult(Integer code, String message) {
        return new Result()
                .setCode(code)
                .setMessage(message);
    }

    public static Result genResult(Result result, ResultCode resultCode, long intervalMs) {
        if(result == null ){
            return genFailResult(resultCode).setIntervalMs(intervalMs);
        }else {
            return result.setCode(resultCode.getCode()).setMessage(resultCode.getMsg()).setIntervalMs(intervalMs);
        }
    }
}

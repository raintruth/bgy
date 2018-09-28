package com.pagoda.account.migrate.producerconsumer.base.impl;

import cn.hutool.core.date.TimeInterval;
import com.pagoda.account.migrate.producerconsumer.base.IWorker;
import com.pagoda.account.migrate.producerconsumer.base.normal.Result;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
public abstract class Worker implements Callable<Result>, IWorker {

    public static final String IDENTITY_PATTERN = "[编号：{taskNum}-{roleType}]";

    private int taskNum;
    private String roleType;
    //TODO 名称 工作状态  未开始, 等待中, 工作中, 完成
    private String name;
    private String status;

    protected Map<String, Object> goodsMap;
    /**
     * goodsMap中主包裹 的mapkey
     */
    protected String goodsMapMainDataMapKey;


    private Result result;
    private boolean isExceptionReTry;
    private int maxReTryTimes;

    /**
     * 效率分析工具
     */
    private TimeInterval interval;
    private TimeInterval waitInterval;
    private TimeInterval workingInterval;

    /**
     * 管理主类
     */
    private BaseProdConsManager prodConsManager;

    //TODO 后期改为配置文件
    {
        result = ResultGenerator.genEmptyResult();
        interval = new TimeInterval();
        waitInterval = new TimeInterval();
        workingInterval = new TimeInterval();
        isExceptionReTry = true;
        maxReTryTimes = 3;
    }


    public Worker() {

    }
    public Worker(String roleType) {
        this.roleType = roleType;
    }

    @Override
    public int getStatus(){

        return 0;
    }

    public boolean isWorkingSuccess(ResultCode resultCode){
        return Objects.equals(resultCode.getSCode(), ResultCode.SUCCESS.getSCode());

    }

    @Override
    public Result call() throws Exception{
        interval.start();
        ResultCode resultCode = ResultCode.FAIL;
        log.info("{}进入准备  ing-- before Storage数量={} 个 ", getIdentity(), prodConsManager.getStorage().getQueues().size());
        try {
            // 调用前的业务处理
            preHandle();
            resultCode = handle();
            // 调用后的业务处理
            afterHandle();
        } catch (Exception e) {
            log.error("{} 执行call 发生异常，errorMsg={} ", getIdentity(), e.toString());
        }
        long intervalMs = interval.intervalMs();
        log.info("{} 总共花费{}秒", getIdentity(), intervalMs/1000.0);
        return ResultGenerator.genResult(result, resultCode, intervalMs);

    }



    protected ResultCode dealWorkingException(Exception workEx) {
        ResultCode resultCode = ResultCode.FAIL;
        StringBuilder WorkingExceptionInfo = this.getResult().getWorkingExceptionInfo();

        int handleTime = 0;
        // 自定义的异常方法
        boolean isSuccess = false;
        if(isExceptionReTry){
            do{

                try {
                    handleTime++;

                    log.info("{} 等待{}秒后重试...", getIdentity(), handleTime);
                    WorkingExceptionInfo.append("<").append(getIdentity()).append("发生异常 等待").append(handleTime).append("秒后重试...").append("workEx=").append(workEx.toString()).append(">");
                    Thread.sleep(1000L * handleTime);
                    resultCode = working();
                    isSuccess = true;

                } catch (InterruptedException ex) {
                    log.error(" {}  发生中断异常, errorMsg={}", getIdentity(), ex.toString());
                    WorkingExceptionInfo.append("<").append(getIdentity()).append("发生中断异常 ").append("errorMsg=").append(ex.toString()).append(">");
                    Thread.currentThread().interrupt();
                } catch (Exception e1) {
                    log.error(" {} 第{}次重试 执行发生异常, errorMsg={}", getIdentity(), handleTime, e1.toString());
                    WorkingExceptionInfo.append("<").append(getIdentity()).append("第").append(handleTime).append("次重试 ").append("执行发生异常 等待").append("errorMsg=").append(e1.toString()).append(">");

                }
            }while ( !isSuccess && handleTime < maxReTryTimes);

        }
        //如果 重试结果失败，则将拿到的货物返还至 exeFailStorage中
        if(!isSuccess){
            log.info("{} 第{}次重试全部失败！", getIdentity(), handleTime);
            WorkingExceptionInfo.append("<").append(getIdentity()).append("第").append(handleTime).append("次重试 ").append("全部失败！ ").append(">");

            allRetryFailDo();
        }else{
            log.info("{} 第{}次重试成功！", getIdentity(), handleTime);
            WorkingExceptionInfo.append("<").append(getIdentity()).append("第").append(handleTime).append("次重试 ").append("成功！ ").append(">");
        }
        log.info("{}执行发生异常 处理完毕， 一共重试{}次, 最后结果={}", getIdentity(), handleTime, isSuccess ? "成功！" : "失败！");
        WorkingExceptionInfo.append("<").append(getIdentity()).append("执行发生异常 处理完毕， 一共重试").append(handleTime).append("次, 最后结果=").append(isSuccess ? "成功！" : "失败！").append(">");

        return resultCode;
    }


    public String getIdentity() {
        return getIdentity(taskNum, roleType);
    }

    public static String getIdentity(int taskNum, String roleType) {
        return IDENTITY_PATTERN.replace("{taskNum}", String.valueOf(taskNum)).replace("{roleType}", roleType);
    }



    /**
     * 执行主方法
     * @return 状态枚举
     */
    protected abstract ResultCode handle();

    /**
     * 所有重试都失败了怎么做
     */
    protected abstract void allRetryFailDo();

    protected void preHandle(){
        log.info("{} 进入 preHandle ---", getIdentity());
    }

    protected void afterHandle() {
        log.info("{} 进入 afterHandle ---", getIdentity());
    }


    public Worker registerProdConsManager(final BaseProdConsManager prodConsManager) {
        this.prodConsManager = prodConsManager;
        return this;
    }


    public int getTaskNum() {
        return taskNum;
    }

    public Worker setTaskNum(int taskNum) {
        this.taskNum = taskNum;
        return this;
    }

    public String getRoleType() {
        return roleType;
    }

    public Worker setRoleType(String roleType) {
        this.roleType = roleType;
        return this;
    }

    public Map<String, Object> getGoodsMap() {
        return goodsMap;
    }

    public Worker setGoodsMap(Map<String, Object> goodsMap) {
        this.goodsMap = goodsMap;
        return this;
    }

    public String getGoodsMapMainDataMapKey() {
        return goodsMapMainDataMapKey;
    }

    public Worker setGoodsMapMainDataMapKey(String goodsMapMainDataMapKey) {
        this.goodsMapMainDataMapKey = goodsMapMainDataMapKey;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public Worker setResult(Result result) {
        this.result = result;
        return this;
    }

    public boolean isExceptionReTry() {
        return isExceptionReTry;
    }

    public Worker setExceptionReTry(boolean exceptionReTry) {
        isExceptionReTry = exceptionReTry;
        return this;
    }

    public int getMaxReTryTimes() {
        return maxReTryTimes;
    }

    public Worker setMaxReTryTimes(int maxReTryTimes) {
        this.maxReTryTimes = maxReTryTimes;
        return this;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public Worker setInterval(TimeInterval interval) {
        this.interval = interval;
        return this;
    }

    public TimeInterval getWaitInterval() {
        return waitInterval;
    }

    public Worker setWaitInterval(TimeInterval waitInterval) {
        this.waitInterval = waitInterval;
        return this;
    }

    public TimeInterval getWorkingInterval() {
        return workingInterval;
    }



    public BaseProdConsManager getProdConsManager() {
        return prodConsManager;
    }

    public Worker setProdConsManager(final BaseProdConsManager prodConsManager) {
        this.prodConsManager = prodConsManager;
        return this;
    }


}


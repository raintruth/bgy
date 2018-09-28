package com.pagoda.account.migrate.producerconsumer.base.impl;

import com.pagoda.account.migrate.producerconsumer.base.IProducer;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */

@Slf4j
public abstract class BaseProducer extends Worker implements IProducer {

    /**
     * 生产者标识名称
     */
    public static final String PRODUCER_IDENTITY_NAME = "Producer";



    public BaseProducer() {
        super(PRODUCER_IDENTITY_NAME);
    }


    public static String getIdentity(int taskNum) {
        return getIdentity(taskNum, PRODUCER_IDENTITY_NAME);
    }

    @Override
    public ResultCode working(){
        ResultCode resultCode;
        this.goodsMap = productGoods();
        recordWorkingInfo(this.getResult().getWorkingInfo(), this.goodsMap);
        if(goodsMap == null){
            log.info("{} 生产包裹为空 已停止！", getIdentity());
            resultCode = ResultCode.EMPTY;
        }else {
            List list = (List)goodsMap.get(this.goodsMapMainDataMapKey);
            if(list == null || list.isEmpty() ){
                log.info("{} 生产的包裹(主包为空) 已停止！", getIdentity());
                resultCode = ResultCode.EMPTY;
            }else {
                resultCode = ResultCode.SUCCESS;
            }

        }

        return resultCode;
    }

    @Override
    protected ResultCode handle() {
        ResultCode resultCode;
        this.getWorkingInterval().start();
        try {

            resultCode = working();
        } catch (Exception workEx) {
            log.error("{} 第{}次生产货物 发生异常！errorMsg={}", getIdentity(), 1, workEx.toString());
            resultCode = dealWorkingException(workEx);
            //子类可以覆盖的方法
            onException(workEx);
        }
        //保存实际工作的时间
        this.getResult().setWorkingIntervalMs(getWorkingInterval().intervalMs());
        //效率分析
        this.getWaitInterval().start();
        int mainDataSize = 0;
        try {
            //if(isWorkingSuccess(resultCode)){
            if(goodsMap != null){
                Object mainData = goodsMap.get(this.goodsMapMainDataMapKey);
                if(mainData != null){
                    List list = (List)mainData;
                    mainDataSize = list.size();

                }
                log.info(" {} 将生产的[货物包({}小包), 主包件数={}]放到主仓库中ing ---", getIdentity(), goodsMap.keySet().size(), mainDataSize);
                this.getProdConsManager().getStorage().getQueues().put(goodsMap);
                log.info(" {} 将生产的[货物包({}小包), 主包件数={}]放到主仓库中 成功！ ---", getIdentity(), goodsMap.keySet().size(), mainDataSize);
                resultCode = ResultCode.SUCCESS;
            }
        } catch (InterruptedException e) {
            log.error(" {} 将生产的[货物包({}小包), 主包件数={}]放到主仓库中 失败！，errorMsg={} ---", getIdentity(), goodsMap.keySet().size(), e.toString(), mainDataSize);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(" {} 将生产的[货物包({}小包), 主包件数={}]放到主仓库中 异常 ---", getIdentity(), goodsMap.keySet().size(), mainDataSize);
        } finally {

            this.getProdConsManager().getProducerGate().countDown();
        }
        this.getResult().setWaitIntervalMs(getWaitInterval().intervalMs());
        return resultCode;
    }


    @Override
    protected void allRetryFailDo() {
        try {
            //TODO 另开一个生产者 否则引起死锁循环
            //getStorage().getExeFailQueues().take();
            log.info("TODO {} 生产失败！将从exeFailStorage中直接拿取货物 ---", getIdentity());
        } catch (Exception e) {
            log.error(" {} 生产失败！将从exeFailStorage中直接拿取货发生异常, errorMsg={}", getIdentity(), e.toString());
        }
    }


    protected abstract void recordWorkingInfo(StringBuilder workingInfo, Map<String,Object> goodsMap);

    protected void onException(Exception e) {
        log.info("{}进入onException，异常信息, e={}--------", getIdentity(), e);
    }


}

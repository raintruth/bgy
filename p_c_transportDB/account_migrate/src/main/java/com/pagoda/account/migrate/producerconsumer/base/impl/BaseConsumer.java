package com.pagoda.account.migrate.producerconsumer.base.impl;

import com.pagoda.account.migrate.producerconsumer.base.IConsumer;
import com.pagoda.account.migrate.producerconsumer.base.normal.ResultCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author  wfg
 * Version  1.0.0
 * @since   2018教师节 下午2:28:58
 */

@Slf4j
public abstract class BaseConsumer extends Worker implements IConsumer {
    public static final String CONSUMER_IDENTITY_NAME = "Consumer";


    public BaseConsumer() {
        super(CONSUMER_IDENTITY_NAME);
    }


    public static String getIdentity(int taskNum) {
        return getIdentity(taskNum, CONSUMER_IDENTITY_NAME);
    }


    @Override
    public ResultCode working(){

        return consumeGoods(goodsMap);

    }

    @Override
    protected ResultCode handle() {
        ResultCode resultCode = ResultCode.FAIL;
        //效率分析
        this.getWaitInterval().start();
        try {
            this.goodsMap = tryGetStorage();
            if(goodsMap == null) {

                log.info("{} 从仓库中拿取的包裹 为空 已停止继续工作！", getIdentity());
                resultCode = ResultCode.EMPTY;
            }else {
                List list = (List)goodsMap.get(this.goodsMapMainDataMapKey);
                if(list == null || list.isEmpty() ){
                    log.info("{} 从仓库中拿取的包裹(主包为空) 已停止继续工作！", getIdentity());
                    resultCode = ResultCode.EMPTY;
                }else {

                    resultCode = ResultCode.SUCCESS;
                }
            }
        } catch (Exception workEx) {
            log.error("{} 获取货物 发生异常！errorMsg={}", getIdentity(), workEx.toString());

        }
        this.getResult().setWaitIntervalMs(getWaitInterval().intervalMs());

        this.getWorkingInterval().start();
        try {

            //if(isWorkingSuccess(resultCode)){
            if(goodsMap != null){
                resultCode = working();
                recordWorkingInfo(this.getResult().getWorkingInfo(), this.goodsMap);
            }
        } catch (Exception e) {
            log.error("{} 第{}次 消费货物 发生异常！errorMsg={}", getIdentity(), 1, e.toString());
            resultCode = dealWorkingException(e);
            //子类可以覆盖的方法
            onException(e);
        } finally {

            this.getProdConsManager().getConsumerGate().countDown();
        }

        //保存实际工作的时间
        this.getResult().setWorkingIntervalMs(getWorkingInterval().intervalMs());
        return resultCode;
    }

    private Map<String, Object> tryGetStorage() {

        Map<String, Object> goodsMap = null;
        while(this.getProdConsManager().getProducerGate().getCount() > 0 || !this.getProdConsManager().getStorage().isEmpty()){
            //预测剩余时间
            //this.getProdConsManager().forecastRemianTimes();

            try {
                log.info(" {} 从主仓库中 尝试拿取生产的包裹 ing ---", getIdentity());
                //take 取不到数据会一直阻塞，特别是在生产数据为空时、不好 Map<String, Object> storageInfoMap = (Map<String, Object>) queue.take();
                goodsMap = this.getProdConsManager().getStorage().poll(5, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                log.error(" {} 从主仓库中 尝试拿取生产的包裹 发生异常, errorMsg={}", getIdentity(), e.toString());
                Thread.currentThread().interrupt();
            }
            if(goodsMap != null){
                int mainDataSize = 0;
                List list = (List)goodsMap.get(this.goodsMapMainDataMapKey);
                if(list != null && !list.isEmpty() ){
                    mainDataSize = list.size();
                }
                log.info(" {} 从主仓库中 尝试拿取生产的包裹 成功, 货物包({}小包, 主包件数{})---", getIdentity(), goodsMap.keySet().size(), mainDataSize);
                break;
            }else {
                try {

                    log.info("{}等了5秒没有发现货物，等待1秒继续--", getIdentity());
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return goodsMap;
    }



    @Override
    protected void allRetryFailDo() {
        try {
            //TODO 发送信息到 邮箱
            log.info("{} 消费失败！将货物{}返还至 exeFailStorage中---", getIdentity(), goodsMap.keySet().toString());
            this.getProdConsManager().getStorage().getExeFailQueues().put(goodsMap);
        } catch (InterruptedException e) {
            log.error("{} 将货物{}返还至 exeFailStorage中发生异常, errorMsg={}", getIdentity(), goodsMap.keySet().toString(), e.toString());
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void recordWorkingInfo(StringBuilder workingInfo, Map<String,Object> goodsMap);

    protected void onException(Exception e) {
        log.info("{}进入onException，异常信息, e={}--------", getIdentity(), e);
    }


}

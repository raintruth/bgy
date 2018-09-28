package com.pagoda.account.migrate.producerconsumer.old.memberaccount;

import com.pagoda.account.migrate.service.member.IMemberAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;

public class ProductAndConsumerTest {

    @Autowired
    private IMemberAccountService memberAccountService;

    public static void main(String[] args) {
      //  BlockingQueue publicBoxQueue= new LinkedBlockingQueue(2);   //定义了一个大小为5的盒子


        //由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
        final RejectedExecutionHandler handler1 = (r, executor) -> {
            System.out.println("太忙了,ProductThread "+ ((ProductThread) r).toString() + executor.toString());
            //System.out.println("太忙了,把该订单交给调度线程池逐一处理" + ((ProductThread) r).getMsg());
            // msgQueue.offer(((DBThread) r).getMsg());
            if (!executor.isShutdown()) {
                System.out.println("RejectedExecutionHandler 尝试 重试 TaskNum " + ((ProductThread) r).getTaskNum());
                r.run();
            }
        };
        final RejectedExecutionHandler handler2 = (r, executor) -> {
            System.out.println("太忙了,UserConsumer "+ ((ConsumerThread) r).toString()+ executor.toString());
            //System.out.println("太忙了,把该订单交给调度线程池逐一处理" + ((DBThread) r).getMsg());
            // msgQueue.offer(((DBThread) r).getMsg());

            if (!executor.isShutdown()) {

                r.run();
            }
        };

        BlockingQueue publicBoxQueue= new ArrayBlockingQueue(4);   //定义了一个大小为5的盒子
        //为多生产者和多消费者分别开创的线程池
        ThreadPoolExecutor productPool =
                new ThreadPoolExecutor(2, 5, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2),handler1);//new ThreadPoolExecutor.CallerRunsPolicy()
        ThreadPoolExecutor consumerPool =
                new ThreadPoolExecutor(4, 6, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(3), handler2);

        System.out.println("start");

        for (int i = 0; i < 50; i++) {
            //productPool.execute(new ProductThread(i, publicBoxQueue, memberAccountService));
            // consumerPool.execute(new UserConsumer(publicBoxQueue));
        }
        System.out.println("生产者 任务添加完毕");

        productPool.shutdown();
        consumerPool.shutdown();
        try {
            productPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            consumerPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("end");
    }
}
package com.example.httpsdemo.utils;

import android.util.Log
import java.util.concurrent.*

/**
 * 默认的线程池
 * @author liuhaichao
 */
internal object DefaultThreadPool {

    private lateinit var defaultThreadPool: ThreadPoolExecutor

    private val rejectedExecutionHandler =
        RejectedExecutionHandler { r: Runnable, executor: ThreadPoolExecutor ->
            Log.e(
                "拒绝策略",
                "executor.poolSize=${executor.poolSize},${executor.activeCount}," +
                        "executor.corePoolSize=${executor.corePoolSize}," +
                        "executor.taskCount=${executor.taskCount}",
            )
        }

    private fun getThreadPoolExecutor(): ThreadPoolExecutor {
        if (!this::defaultThreadPool.isInitialized || defaultThreadPool.isShutdown) {
            defaultThreadPool = createThreadPoolExecutor()
        }
        return defaultThreadPool
    }

    fun execute(command: Runnable) {
        getThreadPoolExecutor().execute(command)
    }

    private fun createThreadPoolExecutor(): ThreadPoolExecutor {
        return ThreadPoolExecutor(
            1,//线程池核心线程数最大值。
            1,//线程池最大线程数大小。
            0,//线程池中非核心线程空闲的存活时间大小。
            TimeUnit.SECONDS,//线程空闲存活时间单位。
            LinkedBlockingQueue(100),//存放任务的阻塞队列
            Executors.defaultThreadFactory(),//创建新线程的工厂，所有线程都是通过该工厂创建的，有默认实现。
            rejectedExecutionHandler//线程池的拒绝策略。
        )
    }

    fun shutdown() {
        //不再接收新任务，线程池会把正在执行的任务和队列中等待的任务都执行完毕再关闭
        getThreadPoolExecutor().shutdown()
    }

}

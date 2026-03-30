package com.example.e_commerce.utils;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ThreadPoolUtil {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolUtil.class);
    private static final int CORE = 8;
    private static final int MAX = 16;
    private static final long KEEP_ALIVE = 60L;
    private static final int QUEUE = 2000;
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger i = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("user-pool-" + i.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    };
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            CORE, MAX, KEEP_ALIVE, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE),
            THREAD_FACTORY,
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    public static void execute(Runnable task) {
        EXECUTOR.execute(task);
    }
    public static <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR.submit(task);
    }
    public static ThreadPoolExecutor getExecutor() {
        return EXECUTOR;
    }
    // Graceful shutdown methods
    public static void shutdown() {
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(60, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
                if (!EXECUTOR.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.warn("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    public static void shutdownNow() {
        EXECUTOR.shutdownNow();
    }
}
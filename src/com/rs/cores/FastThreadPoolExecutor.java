package com.rs.cores;

import com.rs.utils.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.*;

/**
 * The fast executer service. This is a subtyped
 * {@link ThreadPoolExecutor} which is adapted
 * for logging game errors.
 *
 * @ausky David O'Neill
 */
final class FastThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * Construct a {@link FastThreadPoolExecutor} object backed
     * by a {@link FastThreadFactory}. This thread pool executor
     * is cached, meaning that new threads will only be added
     * to the pool as necessary, and existing threads will be reused
     * if they are idle yet still alive. Threads in the pool
     * which haven't been used for 60 seconds are removed from the pool.
     * @param maxPoolSize the maximum number of threads to hold in the pool
     * @param factory the {@code ThreadFactory}
     */
    FastThreadPoolExecutor(int maxPoolSize, ThreadFactory factory) {
        super(  0, maxPoolSize,
                60L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                factory);
        Logger.log("FastThreadPoolExecutor open. Cached w/ max thread pool size: " + maxPoolSize);
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if(t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            Logger.log("FastThreadPoolExecutor caught an exception.");
            Logger.threadFatal(sw.toString());
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Logger.log("FastThreadPoolExecutor closing. No longer queueing tasks.");
    }
}

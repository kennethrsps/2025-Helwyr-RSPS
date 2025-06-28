/*package com.rs.cores;

import com.rs.utils.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.*;

*//**
 * A subtyped {@link ScheduledThreadPoolExecutor} with error logging
 * cabailities.
 * @ausky David O'Neill
 *//*
final class SlowThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    *//**
     * Construct a {@link SlowThreadPoolExecutor} object backed
     * by a {@link SlowThreadFactory}.
     * @param corePoolSize the number of threads to hold in the pool
     * @param threadFactory the {@code ThreadFactory}
     *//*
    SlowThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
        Logger.log("SlowThreadPoolExecutor open. Fixed thread pool size: " + corePoolSize);
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if(t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            Logger.log("SlowThreadPoolExecutor caught an exception.");
            //Logger.threadFatal(sw.toString());
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Logger.log("SlowThreadPoolExecutor closing. No longer queueing tasks.");
    }
}
*/
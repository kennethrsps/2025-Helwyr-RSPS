package com.rs.cores;

import com.rs.utils.Logger;

/**
 * A hidden exception handler for logging silent thread death from the slow executor pool.
 * @ausky David O'Neill (dlo3)
 */
final class SlowThreadHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Logger.threadFatal("(" + thread.getName() + ", slow pool) - Printing trace");
        throwable.printStackTrace();
    }

}

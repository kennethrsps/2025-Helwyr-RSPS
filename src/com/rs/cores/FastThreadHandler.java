package com.rs.cores;


/**
 * @ausky David O'Neill
 */
final class FastThreadHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
      //  Logger.threadFatal("(" + thread.getName() + ", fast pool) - Printing trace");
        throwable.printStackTrace();
    }
}
